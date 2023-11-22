package com.vb.bookstore.repositories;

import com.vb.bookstore.entities.LibraryCollection;
import com.vb.bookstore.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LibraryCollectionRepository extends JpaRepository<LibraryCollection, Long> {
    List<LibraryCollection> findByUser(User user);
}
