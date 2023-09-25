package com.vb.bookstore.repositories;

import com.vb.bookstore.entities.EBook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EBookRepository extends JpaRepository<EBook, Long> {
}
