package com.vb.bookstore.services.impl;

import com.vb.bookstore.config.AppConstants;
import com.vb.bookstore.entities.*;
import com.vb.bookstore.exceptions.ApiRequestException;
import com.vb.bookstore.exceptions.ResourceNotFoundException;
import com.vb.bookstore.payloads.MessageResponse;
import com.vb.bookstore.payloads.cart.*;
import com.vb.bookstore.payloads.wishlist.WishlistDTO;
import com.vb.bookstore.repositories.*;
import com.vb.bookstore.services.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final ModelMapper modelMapper;

    private final WishlistService wishlistService;
    private final UserService userService;
    private final OrderService orderService;
    private final PaymentService paymentService;

    private final PaperBookRepository paperBookRepository;
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final BookRepository bookRepository;
    private final WishlistRepository wishlistRepository;
    private final PromoCodeRepository promoCodeRepository;
    private final AddressRepository addressRepository;
    private final ShippingMethodRepository shippingMethodRepository;
    private final OrderItemRepository orderItemRepository;

    public CartDTO getCart() {
        User user = userService.getCurrentUser();
        Cart cart = user.getCart();
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        cartDTO.setCartItems(cart.getCartItems().stream().
                map((cartItem) -> {
                    CartItemDTO cartItemDTO = modelMapper.map(cartItem, CartItemDTO.class);
                    cartItemDTO.setBookId(cartItem.getBook().getId());
                    return cartItemDTO;
                }).toList());
        if (cart.getPromoCode() == null) {
            cartDTO.setPromoCode(null);
        } else {
            cartDTO.setPromoCode(cart.getPromoCode().getCode());
        }
        return cartDTO;
    }

    public void checkCartPaymentStatus(Cart cart) {
        if (cart.getPaymentStatus() != null) {
            MessageResponse messageResponse = paymentService.checkPaymentStatus(cart.getPaymentId());
            if (!messageResponse.isSuccess()) {
                cart.setPaymentStatus(null);
                cart.setPaymentId(null);
                cart.setPaymentRedirectUrl(null);
            } else {
                orderService.order();
            }
        }
    }

    public MessageResponse addBookToCart(AddToCartRequest request) {
        User user = userService.getCurrentUser();

        Cart cart = user.getCart();
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", request.getBookId()));

        checkCartPaymentStatus(cart);

        if (!request.getBookType().equals(AppConstants.PAPER_BOOK)) {
            request.setPaperBookId(null);
        }

        Optional<CartItem> existingCartItem = cartItemRepository.findByCartAndBookAndBookTypeAndPaperBookId(cart, book, request.getBookType(), request.getPaperBookId());
        if (existingCartItem.isPresent()) {
            throw new ApiRequestException("You already have this book in your cart!", HttpStatus.CONFLICT);
        }

        if (!request.getBookType().equals(AppConstants.PAPER_BOOK)) {
            Optional<OrderItem> existingOrderItem = orderItemRepository.findByOrder_UserAndBookAndBookType(user, book, request.getBookType());
            if (existingOrderItem.isPresent()) {
                throw new ApiRequestException("You already own this book!", HttpStatus.BAD_REQUEST);
            }
        }

        CartItem cartItem = new CartItem();

        switch (request.getBookType()) {
            case AppConstants.PAPER_BOOK -> {
                PaperBook pb = paperBookRepository.findById(request.getPaperBookId())
                        .orElseThrow(() -> new ResourceNotFoundException("Paper book", "id", request.getPaperBookId()));
                if (!book.getPaperBooks().contains(pb)) {
                    throw new ApiRequestException("Paper book doesn't belong to the given book", HttpStatus.CONFLICT);
                }
                if (!pb.getIsAvailable()) {
                    throw new ApiRequestException("Paper book is not available", HttpStatus.BAD_REQUEST);
                }
                cartItem.setPaperBookId(request.getPaperBookId());
                cartItem.setPrice(pb.getPrice());
                cartItem.setHasDiscount(pb.getHasDiscount());
                cartItem.setPriceWithDiscount(pb.getPriceWithDiscount());
                cartItem.setQuantity(1);
                cartItem.setTotalPrice(pb.getPriceWithDiscount());
                cart.setShippingMethod(shippingMethodRepository.findAll().get(0));
                if (user.getAddresses().size() > 0) {
                    cart.setAddress(user.getAddresses().get(0));
                }
            }
            case AppConstants.EBOOK -> {
                if (book.getEbook() == null) {
                    throw new ApiRequestException("Given book doesn't have an assigned ebook", HttpStatus.NOT_FOUND);
                }
                cartItem.setPrice(book.getEbook().getPrice());
                cartItem.setHasDiscount(book.getEbook().getHasDiscount());
                cartItem.setPriceWithDiscount(book.getEbook().getPriceWithDiscount());
                cartItem.setTotalPrice(book.getEbook().getPriceWithDiscount());
                cartItem.setQuantity(1);
            }
            case AppConstants.AUDIOBOOK -> {
                if (book.getAudiobook() == null) {
                    throw new ApiRequestException("Given book doesn't have an assigned audiobook", HttpStatus.NOT_FOUND);
                }
                cartItem.setPrice(book.getAudiobook().getPrice());
                cartItem.setHasDiscount(book.getAudiobook().getHasDiscount());
                cartItem.setPriceWithDiscount(book.getAudiobook().getPriceWithDiscount());
                cartItem.setTotalPrice(book.getAudiobook().getPriceWithDiscount());
                cartItem.setQuantity(1);
            }
        }

        cartItem.setCart(cart);
        cartItem.setBook(book);
        cartItem.setBookType(request.getBookType());

        cart.addItem(cartItem);
        cartRepository.save(cart);

        Optional<Wishlist> wishlist = wishlistRepository.findByUserIdAndBookIdAndBookTypeAndPaperBookId(user.getId(), book.getId(), request.getBookType(), request.getPaperBookId());
        wishlist.ifPresent(wishlistRepository::delete);

        return new MessageResponse(true, "Book has been added to the cart successfully!");
    }

    public MessageResponse deleteBookFromCart(Long id) {
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", id));
        Cart cart = cartItem.getCart();
        User user = userService.getCurrentUser();

        checkCartPaymentStatus(cart);

        if (user != cart.getUser()) {
            throw new ApiRequestException("Missing access", HttpStatus.FORBIDDEN);
        }
        cart.removeItem(cartItem);
        cartItemRepository.delete(cartItem);
        cartRepository.save(cart);
        return new MessageResponse(true, "Book has been successfully deleted from your cart!");
    }

    public MessageResponse updatePaperBookQuantity(Long id, Integer newQuantity) {
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", id));
        Cart cart = cartItem.getCart();
        User user = userService.getCurrentUser();
        if (user != cart.getUser()) {
            throw new ApiRequestException("Missing access", HttpStatus.FORBIDDEN);
        }
        if (cartItem.getPaperBookId() == null) {
            throw new ApiRequestException("Given cart item doesn't contain paper book!", HttpStatus.CONFLICT);
        }

        cartItem.setQuantity(newQuantity);
        cart.updateTotalPrice();
        cartRepository.save(cart);

        return new MessageResponse(true, "Book quantity has been updated successfully!");
    }

    public MessageResponse moveToWishlist(MoveToWishlistRequest request) {
        User user = userService.getCurrentUser();
        Cart cart = user.getCart();

        checkCartPaymentStatus(cart);

        CartItem cartItem = cartItemRepository.findById(request.getCartItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart item", "id", request.getCartItemId()));

        if (cartItem.getCart() != cart) {
            throw new ApiRequestException("Missing access", HttpStatus.FORBIDDEN);
        }

        cart.removeItem(cartItem);
        cartItemRepository.delete(cartItem);
        WishlistDTO wishlistDTO = new WishlistDTO();
        wishlistDTO.setBookId(request.getBookId());
        wishlistDTO.setBookType(request.getBookType());
        wishlistDTO.setPaperBookId(request.getPaperBookId());
        wishlistService.addBookToWishlist(wishlistDTO);
        cartRepository.save(cart);
        return new MessageResponse(true, "Book has been successfully moved to wishlist!");
    }

    public List<ShippingMethodDTO> getShippingMethods() {
        List<ShippingMethod> all = shippingMethodRepository.findAll();
        List<ShippingMethodDTO> shippingMethodDTOS = all.stream().map((element) -> modelMapper.map(element, ShippingMethodDTO.class)).toList();
        return shippingMethodDTOS;
    }

    public MessageResponse updateAddress(Long addressId) {
        User user = userService.getCurrentUser();
        Cart cart = user.getCart();

        checkCartPaymentStatus(cart);

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));
        if (address.getUser() != user) {
            throw new ApiRequestException("Missing access", HttpStatus.FORBIDDEN);
        }
        cart.setAddress(address);
        cartRepository.save(cart);
        return new MessageResponse(true, "Address has been updated successfully");
    }

    public MessageResponse updateShippingMethod(Long shippingMethodId) {
        User user = userService.getCurrentUser();
        Cart cart = user.getCart();

        checkCartPaymentStatus(cart);

        ShippingMethod shippingMethod = shippingMethodRepository.findById(shippingMethodId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipping method", "id", shippingMethodId));
        cart.setShippingMethod(shippingMethod);
        cartRepository.save(cart);
        return new MessageResponse(true, "Shipping method has been updated successfully");
    }
}
