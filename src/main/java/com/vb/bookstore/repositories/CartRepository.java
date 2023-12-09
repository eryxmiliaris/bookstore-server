package com.vb.bookstore.repositories;

import com.vb.bookstore.entities.Cart;
import com.vb.bookstore.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
}
