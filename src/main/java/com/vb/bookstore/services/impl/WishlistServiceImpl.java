package com.vb.bookstore.services.impl;

import com.vb.bookstore.entities.*;
import com.vb.bookstore.exceptions.ApiRequestException;
import com.vb.bookstore.exceptions.ResourceNotFoundException;
import com.vb.bookstore.payloads.MessageResponse;
import com.vb.bookstore.payloads.wishlist.WishlistDTO;
import com.vb.bookstore.repositories.BookRepository;
import com.vb.bookstore.repositories.CartItemRepository;
import com.vb.bookstore.repositories.OrderItemRepository;
import com.vb.bookstore.repositories.WishlistRepository;
import com.vb.bookstore.services.UserService;
import com.vb.bookstore.services.WishlistService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {
    private final ModelMapper modelMapper;

    private final UserService userService;

    private final WishlistRepository wishlistRepository;
    private final BookRepository bookRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;

    public List<WishlistDTO> getWishlist() {
        User user = userService.getCurrentUser();

        List<Wishlist> wishlists = wishlistRepository.findByUserId(user.getId());
        List<WishlistDTO> wishlistDTOS = wishlists.stream().map((element) -> {
            WishlistDTO mapped = modelMapper.map(element, WishlistDTO.class);
            mapped.setBookId(element.getBook().getId());
            return mapped;
        }).toList();

        return wishlistDTOS;
    }

    public MessageResponse addBookToWishlist(WishlistDTO request) {
        User user = userService.getCurrentUser();
        Cart cart = user.getCart();
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", request.getBookId()));

        if (!request.getBookType().equals("Paper book")) {
            request.setPaperBookId(null);
        }

        Optional<CartItem> existingCartItem = cartItemRepository.findByCartAndBookAndBookTypeAndPaperBookId(cart, book, request.getBookType(), request.getPaperBookId());
        if (existingCartItem.isPresent()) {
            throw new ApiRequestException("This book is already in your cart!", HttpStatus.CONFLICT);
        }

        if (!request.getBookType().equals("Paper book")) {
            Optional<OrderItem> existingOrderItem = orderItemRepository.findByOrder_UserAndBookAndBookType(user, book, request.getBookType());
            if (existingOrderItem.isPresent()) {
                throw new ApiRequestException("You already own this book!", HttpStatus.BAD_REQUEST);
            }
        }

        Optional<Wishlist> wishlistItem = wishlistRepository.findByUserIdAndBookIdAndBookTypeAndPaperBookId(user.getId(), book.getId(), request.getBookType(), request.getPaperBookId());
        if (wishlistItem.isPresent()) {
            throw new ApiRequestException("User already has this book in his wishlist!", HttpStatus.CONFLICT);
        }
        Wishlist newWishlistItem = new Wishlist();
        newWishlistItem.setBookType(request.getBookType());
        newWishlistItem.setPaperBookId(request.getPaperBookId());
        newWishlistItem.setUser(user);
        newWishlistItem.setBook(book);
        wishlistRepository.save(newWishlistItem);

        return new MessageResponse(true, "Book has been added to your wishlist successfully!");
    }

    public MessageResponse deleteBookFromWishlist(Long id) {
        Wishlist wishlistItem = wishlistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist item", "id", id));
        User user = userService.getCurrentUser();
        if (user.getUsername().equals(wishlistItem.getUser().getUsername())) {
            wishlistRepository.delete(wishlistItem);
        } else {
            throw new ApiRequestException("Missing access", HttpStatus.FORBIDDEN);
        }
        return new MessageResponse(true, "Book was removed from your wishlist!");
    }
}
