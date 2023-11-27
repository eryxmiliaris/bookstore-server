package com.vb.bookstore.repositories;

import com.vb.bookstore.entities.Ebook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EbookRepository extends JpaRepository<Ebook, Long> {
}
