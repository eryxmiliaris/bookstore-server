package com.vb.bookstore.repositories;

import com.vb.bookstore.entities.Roles;
import com.vb.bookstore.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(Roles name);
}
