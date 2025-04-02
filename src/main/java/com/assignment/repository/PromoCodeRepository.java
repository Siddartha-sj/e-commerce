package com.assignment.repository;

import com.assignment.entites.PromoCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PromoCodeRepository extends JpaRepository<PromoCode, Integer> {
    Optional<PromoCode> findByCode(String promoCode);

    List<PromoCode> findByExpiryDateBeforeAndIsActiveTrue(LocalDate currentDate);
}
