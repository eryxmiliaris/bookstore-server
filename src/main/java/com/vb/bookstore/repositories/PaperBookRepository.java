package com.vb.bookstore.repositories;

import com.vb.bookstore.entities.PaperBook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaperBookRepository extends JpaRepository<PaperBook, Long> {
}
