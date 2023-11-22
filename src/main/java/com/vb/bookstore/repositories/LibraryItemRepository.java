package com.vb.bookstore.repositories;

import com.vb.bookstore.entities.Book;
import com.vb.bookstore.entities.LibraryCollection;
import com.vb.bookstore.entities.LibraryItem;
import com.vb.bookstore.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LibraryItemRepository extends JpaRepository<LibraryItem, Long> {
    List<LibraryItem> findByUserAndLibraryCollection(User user, LibraryCollection collection);

    List<LibraryItem> findByUser(User user);

    List<LibraryItem> findByUserAndIsSubscriptionItem(User user, Boolean isSubscriptionItem);

    Optional<LibraryItem> findByUserAndBookAndBookType(User user, Book book, String bookType);
}
