package com.vb.bookstore.repositories;

import com.vb.bookstore.entities.BookNote;
import com.vb.bookstore.entities.LibraryItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookNoteRepository extends JpaRepository<BookNote, Long> {
    List<BookNote> findByLibraryItem(LibraryItem libraryItem);
}
