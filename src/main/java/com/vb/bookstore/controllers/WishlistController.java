package com.vb.bookstore.controllers;

import com.vb.bookstore.payloads.MessageResponse;
import com.vb.bookstore.payloads.wishlist.WishlistDTO;
import com.vb.bookstore.services.WishlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {
    private final WishlistService wishlistService;
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<WishlistDTO>> getWishlist() {
        List<WishlistDTO> wishlistDTOS = wishlistService.getWishlist();

        return ResponseEntity.ok(wishlistDTOS);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> addBookToWishlist(
            @Valid
            @RequestBody
            WishlistDTO request
    ) {
        MessageResponse messageResponse = wishlistService.addBookToWishlist(request);
        return ResponseEntity.ok(messageResponse);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> deleteBookFromWishlist(@PathVariable Long id) {
        MessageResponse messageResponse = wishlistService.deleteBookFromWishlist(id);
        return ResponseEntity.ok(messageResponse);
    }
}
