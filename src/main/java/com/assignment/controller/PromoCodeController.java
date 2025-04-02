package com.assignment.controller;

import com.assignment.DTO.PromoCodeDTO;
import com.assignment.services.PromoCodeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class PromoCodeController {

    @Autowired
    private PromoCodeService promoCodeService;

    @PostMapping("/promocode")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Object> createPromoCode(@Valid @RequestBody PromoCodeDTO promoCodeDTO, BindingResult bindingResult) {
        return promoCodeService.createPromoCode(promoCodeDTO, bindingResult);
    }
    @GetMapping("/promocode")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ADMIN_USER')")
    public ResponseEntity<Object> getPromoCode() {
        return promoCodeService.findAllPromoCode();
    }

}
