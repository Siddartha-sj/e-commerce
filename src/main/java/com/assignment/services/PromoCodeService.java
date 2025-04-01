package com.assignment.services;

import com.assignment.DTO.PromoCodeDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

public interface PromoCodeService {
    ResponseEntity<Object> createPromoCode(PromoCodeDTO promoCodeDTO, BindingResult bindingResult);

    void deactivateExpiredPromoCodes();

    ResponseEntity<Object> findAllPromoCode();
}
