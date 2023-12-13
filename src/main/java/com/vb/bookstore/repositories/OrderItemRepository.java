package com.vb.bookstore.repositories;

import com.vb.bookstore.entities.Book;
import com.vb.bookstore.entities.OrderItem;
import com.vb.bookstore.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    Optional<OrderItem> findByOrder_UserAndBookAndBookType(User user, Book book, String bookType);
    boolean existsByOrder_UserAndBook(User user, Book book);
    Optional<List<OrderItem>> findByOrder_UserAndBook(User user, Book book);
    List<OrderItem> findByOrder_User(User user);
}
