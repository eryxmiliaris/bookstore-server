package com.vb.bookstore.repositories;

import com.vb.bookstore.entities.Ebook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EbookRepository extends JpaRepository<Ebook, Long> {
    List<Ebook> findByDiscountEndDateBefore(LocalDateTime dateTime);
}
