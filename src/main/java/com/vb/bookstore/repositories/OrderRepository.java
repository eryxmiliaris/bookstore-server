package com.vb.bookstore.repositories;

import com.vb.bookstore.entities.Book;
import com.vb.bookstore.entities.Order;
import com.vb.bookstore.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByOrderItems_BookAndOrderDateAfter(Book book, LocalDate orderDate);
    List<Order> findByUserOrderByIdDesc(User user);
    Page<Order> findById(Long id, Pageable pageable);
    Page<Order> findAllByOrderByIdDesc(Pageable pageDetails);
}
