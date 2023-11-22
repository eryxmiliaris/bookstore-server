package com.vb.bookstore.repositories;

import com.vb.bookstore.entities.Book;
import com.vb.bookstore.entities.Review;
import com.vb.bookstore.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByUserAndBook(User user, Book book);

    Page<Review> findByUser_UsernameContains(String username, Pageable pageDetails);

    List<Review> findByUser(User user);
}
