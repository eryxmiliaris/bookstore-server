package com.vb.bookstore.repositories;

import com.vb.bookstore.entities.Book;
import com.vb.bookstore.entities.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    List<Wishlist> findByBook(Book book);
    Optional<Wishlist> findByUserIdAndBookIdAndBookTypeAndPaperBookId(Long userId, Long bookId, String bookType, Long paperBookId);
    List<Wishlist> findByUserId(Long userId);
}
