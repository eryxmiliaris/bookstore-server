package com.vb.bookstore.repositories;

import com.vb.bookstore.entities.PromoCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PromoCodeRepository extends JpaRepository<PromoCode, Long> {
    List<PromoCode> findByEndDateBeforeAndIsActiveTrue(LocalDateTime endDate);
    Optional<PromoCode> findByCode(String code);
}
