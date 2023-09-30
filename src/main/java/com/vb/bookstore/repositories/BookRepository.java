package com.vb.bookstore.repositories;

import com.vb.bookstore.entities.Book;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByCategories_CategoryNameIn(List<String> categories, Sort sortBy);
}
