package com.vb.bookstore.repositories;

import com.vb.bookstore.entities.Audiobook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AudiobookRepository extends JpaRepository<Audiobook, Long> {
    List<Audiobook> findByDiscountEndDateBefore(LocalDateTime date);
}
