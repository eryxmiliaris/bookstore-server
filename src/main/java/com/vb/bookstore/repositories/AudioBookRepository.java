package com.vb.bookstore.repositories;

import com.vb.bookstore.entities.AudioBook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AudioBookRepository extends JpaRepository<AudioBook, Long> {
}
