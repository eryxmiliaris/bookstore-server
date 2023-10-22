package com.vb.bookstore.controllers;

import com.vb.bookstore.entities.Book;
import com.vb.bookstore.entities.User;
import com.vb.bookstore.entities.Wishlist;
import com.vb.bookstore.payloads.MessageResponse;
import com.vb.bookstore.payloads.auth.UserDTO;
import com.vb.bookstore.payloads.user.WishlistDTO;
import com.vb.bookstore.repositories.BookRepository;
import com.vb.bookstore.repositories.UserRepository;
import com.vb.bookstore.repositories.WishlistRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final WishlistRepository wishlistRepository;

    @GetMapping("/info")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserDTO> getUserInfo(HttpServletRequest request) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDTO userDTO = modelMapper.map(userDetails, UserDTO.class);
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        userDTO.setRoles(roles);

        return ResponseEntity.ok()
                .body(userDTO);
    }

    @GetMapping("/wishlist")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<WishlistDTO>> getWishlist() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow();
        List<Wishlist> wishlists = wishlistRepository.findByUserId(user.getId());
        List<WishlistDTO> wishlistDTOS = wishlists.stream().map((element) -> {
            WishlistDTO mapped = modelMapper.map(element, WishlistDTO.class);
            mapped.setBookId(element.getBook().getId());
            return mapped;
        }).toList();
        return ResponseEntity.ok(wishlistDTOS);
    }

    @PostMapping("/wishlist")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> addBookToWishlist(
            @Valid
            @RequestBody
            WishlistDTO request
    ) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow();
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow();
        Optional<Wishlist> wishlistItem = wishlistRepository.findByUserIdAndBookIdAndBookTypeAndPaperBookId(user.getId(), book.getId(), request.getBookType(), request.getPaperBookId());
        if (wishlistItem.isPresent()) {
            return new ResponseEntity<>(new MessageResponse(false, "User already has this book in his wishlist!"), HttpStatus.CONFLICT);
        }
        Wishlist newWishlistItem = new Wishlist();
        newWishlistItem.setBookType(request.getBookType());
        newWishlistItem.setPaperBookId(request.getPaperBookId());
        newWishlistItem.setUser(user);
        newWishlistItem.setBook(book);
        wishlistRepository.save(newWishlistItem);
        return ResponseEntity.ok(new MessageResponse(true, "Book has been added to your wishlist successfully!"));
    }

    @DeleteMapping("/wishlist/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> deleteBookFromWishlist(@PathVariable Long id) {
        Wishlist wishlistItem = wishlistRepository.findById(id)
                .orElseThrow();
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow();
        if (user.getUsername().equals(userDetails.getUsername())) {
            wishlistRepository.delete(wishlistItem);
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse(false, "Missing access"));
        }
        return ResponseEntity.ok(new MessageResponse(true, "Book was removed from your wishlist!"));
    }
}
