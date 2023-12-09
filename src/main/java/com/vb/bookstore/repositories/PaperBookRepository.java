package com.vb.bookstore.repositories;

import com.vb.bookstore.entities.PaperBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PaperBookRepository extends JpaRepository<PaperBook, Long> {
    List<PaperBook> findByDiscountEndDateBefore(LocalDateTime date);
}
