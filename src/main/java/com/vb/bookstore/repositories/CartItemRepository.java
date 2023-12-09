package com.vb.bookstore.repositories;

import com.vb.bookstore.entities.Book;
import com.vb.bookstore.entities.Cart;
import com.vb.bookstore.entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByBookTypeAndBook_IdIn(String bookType, List<Long> ids);
    Optional<CartItem> findByCartAndBookAndBookTypeAndPaperBookId(Cart cart, Book book, String bookType, Long paperBookId);
    List<CartItem> findByPaperBookIdIn(List<Long> ids);
}
