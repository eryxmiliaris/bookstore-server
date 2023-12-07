package com.vb.bookstore.services.impl;

import com.vb.bookstore.entities.Book;
import com.vb.bookstore.entities.Review;
import com.vb.bookstore.entities.User;
import com.vb.bookstore.exceptions.ApiRequestException;
import com.vb.bookstore.exceptions.ResourceNotFoundException;
import com.vb.bookstore.payloads.MessageResponse;
import com.vb.bookstore.payloads.books.ReviewDTO;
import com.vb.bookstore.repositories.BookRepository;
import com.vb.bookstore.repositories.OrderItemRepository;
import com.vb.bookstore.repositories.ReviewRepository;
import com.vb.bookstore.services.ReviewService;
import com.vb.bookstore.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final UserService userService;

    private final BookRepository bookRepository;
    private final OrderItemRepository orderItemRepository;
    private final ReviewRepository reviewRepository;

    public MessageResponse addReview(Long bookId, ReviewDTO request) {
        User user = userService.getCurrentUser();
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", bookId));
        if (!orderItemRepository.existsByOrder_UserAndBook(user, book)) {
            throw new ApiRequestException("User does not own given book", HttpStatus.FORBIDDEN);
        }
        if (reviewRepository.findByUserAndBook(user, book).isPresent()) {
            throw new ApiRequestException("User has already added review for this book", HttpStatus.FORBIDDEN);
        }
        Review review = new Review();
        review.setUser(user);
        review.setBook(book);
        review.setRating(request.getRating());
        review.setPublicationDate(LocalDateTime.now());
        review.setText(request.getText());

        book.addReview(review);

        bookRepository.save(book);

        return new MessageResponse(true, "Review has been successfully added");
    }

    public MessageResponse updateReview(Long id, ReviewDTO request) {
        User user = userService.getCurrentUser();
        boolean isAdmin = userService.currentUserIsAdmin();
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));
        if (isAdmin || review.getUser() == user) {
            review.setRating(request.getRating());
            review.setText(request.getText());
            reviewRepository.save(review);
        } else {
            throw new ApiRequestException("Missing access", HttpStatus.FORBIDDEN);
        }
        return new MessageResponse(true, "Review has been updated successfully");
    }

    public MessageResponse deleteReview(Long id) {
        User user = userService.getCurrentUser();
        boolean isAdmin = userService.currentUserIsAdmin();
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));
        if (isAdmin || review.getUser() == user) {
            Book book = review.getBook();
            book.deleteReview(review);
            bookRepository.save(book);
            reviewRepository.deleteById(review.getId());
        } else {
            throw new ApiRequestException("Missing access", HttpStatus.FORBIDDEN);
        }
        return new MessageResponse(true, "Review has been deleted successfully");
    }
}
