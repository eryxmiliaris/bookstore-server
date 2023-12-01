package com.vb.bookstore.services;

import com.vb.bookstore.payloads.MessageResponse;
import com.vb.bookstore.payloads.books.ReviewDTO;

public interface ReviewService {
    MessageResponse addReview(Long bookId, ReviewDTO request);

    MessageResponse deleteReview(Long id);
}
