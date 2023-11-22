package com.vb.bookstore.repositories;

import com.vb.bookstore.entities.Address;
import com.vb.bookstore.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUser(User user);
}
