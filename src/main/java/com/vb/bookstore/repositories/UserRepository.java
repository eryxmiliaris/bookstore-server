package com.vb.bookstore.repositories;

import com.vb.bookstore.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    Optional<User> findByResetToken(String resetToken);
    List<User> findByActiveSubscriptionEndDateLessThan(LocalDate now);
}
