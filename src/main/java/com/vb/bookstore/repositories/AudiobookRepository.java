package com.vb.bookstore.repositories;

import com.vb.bookstore.entities.Audiobook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AudiobookRepository extends JpaRepository<Audiobook, Long> {
}
