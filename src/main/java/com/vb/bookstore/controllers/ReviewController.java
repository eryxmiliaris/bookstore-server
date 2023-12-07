package com.vb.bookstore.controllers;

import com.vb.bookstore.payloads.MessageResponse;
import com.vb.bookstore.payloads.books.ReviewDTO;
import com.vb.bookstore.services.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> addReview(
            @RequestParam Long bookId,
            @RequestBody @Valid ReviewDTO request
    ) {
        MessageResponse messageResponse = reviewService.addReview(bookId, request);
        return ResponseEntity.ok(messageResponse);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> updateReview(
            @PathVariable Long id,
            @RequestBody @Valid ReviewDTO request
    ) {
        MessageResponse messageResponse = reviewService.updateReview(id, request);
        return ResponseEntity.ok(messageResponse);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> deleteReview(
            @PathVariable Long id
    ) {
        MessageResponse messageResponse = reviewService.deleteReview(id);
        return ResponseEntity.ok(messageResponse);
    }
}
