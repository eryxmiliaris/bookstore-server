package com.vb.bookstore.services;

import com.vb.bookstore.entities.*;
import com.vb.bookstore.exceptions.ApiRequestException;
import com.vb.bookstore.exceptions.ResourceNotFoundException;
import com.vb.bookstore.payloads.MessageResponse;
import com.vb.bookstore.payloads.cart.*;
import com.vb.bookstore.payloads.wishlist.WishlistDTO;
import com.vb.bookstore.repositories.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CartService {

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
        Cart cart = cartRepository.findCartByUser(user);
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

    private void checkPaymentStatus(Cart cart) {
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

        Cart cart = cartRepository.findCartByUser(user);
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", request.getBookId()));

        checkPaymentStatus(cart);

        if (!request.getBookType().equals("Paper book")) {
            request.setPaperBookId(null);
        }

        Optional<CartItem> existingCartItem = cartItemRepository.findByCartAndBookAndBookTypeAndPaperBookId(cart, book, request.getBookType(), request.getPaperBookId());
        if (existingCartItem.isPresent()) {
            throw new ApiRequestException("You already have this book in your cart!", HttpStatus.CONFLICT);
        }

        if (!request.getBookType().equals("Paper book")) {
            Optional<OrderItem> existingOrderItem = orderItemRepository.findByOrder_UserAndBookAndBookType(user, book, request.getBookType());
            if (existingOrderItem.isPresent()) {
                throw new ApiRequestException("You already own this book!", HttpStatus.BAD_REQUEST);
            }
        }

        CartItem cartItem = new CartItem();

        switch (request.getBookType()) {
            case "Paper book" -> {
                PaperBook pb = paperBookRepository.findById(request.getPaperBookId())
                        .orElseThrow(() -> new ResourceNotFoundException("Paper book", "id", request.getPaperBookId()));
                if (!book.getPaperBooks().contains(pb)) {
                    throw new ApiRequestException("Paper book doesn't belong to the given book", HttpStatus.CONFLICT);
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
            case "Ebook" -> {
                if (book.getEbook() == null) {
                    throw new ApiRequestException("Given book doesn't have an assigned ebook", HttpStatus.NOT_FOUND);
                }
                cartItem.setPrice(book.getEbook().getPrice());
                cartItem.setHasDiscount(book.getEbook().getHasDiscount());
                cartItem.setPriceWithDiscount(book.getEbook().getPriceWithDiscount());
                cartItem.setTotalPrice(book.getEbook().getPriceWithDiscount());
                cartItem.setQuantity(1);
            }
            case "Audiobook" -> {
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

        checkPaymentStatus(cart);

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

        checkPaymentStatus(cart);

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

    public MessageResponse applyPromoCode(String code) {
        User user = userService.getCurrentUser();
        Cart cart = user.getCart();

        checkPaymentStatus(cart);

        PromoCode promoCode = promoCodeRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Promo code", "code", code));
        if (!promoCode.getIsActive()) {
            throw new ApiRequestException("Promo code is inactive!", HttpStatus.BAD_REQUEST);
        }
        if (user.getUsedPromoCodes().contains(promoCode)) {
            throw new ApiRequestException("You have already used this promo code!", HttpStatus.CONFLICT);
        }
        cart.setPromoCode(promoCode);
        cartRepository.save(cart);
        return new MessageResponse(true, "Promo code was successfully applied!");
    }

    public List<ShippingMethodDTO> getShippingMethods() {
        List<ShippingMethod> all = shippingMethodRepository.findAll();
        List<ShippingMethodDTO> shippingMethodDTOS = all.stream().map((element) -> modelMapper.map(element, ShippingMethodDTO.class)).toList();
        return shippingMethodDTOS;
    }

    public MessageResponse removePromoCode() {
        User user = userService.getCurrentUser();
        Cart cart = user.getCart();

        checkPaymentStatus(cart);

        cart.setPromoCode(null);
        cartRepository.save(cart);
        return new MessageResponse(true, "Promo code has been successfully removed");
    }

    public MessageResponse updateAddress(Long addressId) {
        User user = userService.getCurrentUser();
        Cart cart = user.getCart();

        checkPaymentStatus(cart);

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

        checkPaymentStatus(cart);

        ShippingMethod shippingMethod = shippingMethodRepository.findById(shippingMethodId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipping method", "id", shippingMethodId));
        cart.setShippingMethod(shippingMethod);
        cartRepository.save(cart);
        return new MessageResponse(true, "Shipping method has been updated successfully");
    }
}
