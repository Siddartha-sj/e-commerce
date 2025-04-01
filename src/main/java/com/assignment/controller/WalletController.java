package com.assignment.controller;

import com.assignment.services.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class WalletController {

    @Autowired
    private WalletService walletService;

    @PutMapping("/wallet/{id}/{value}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Object> updateWallet(@PathVariable int id, @PathVariable double value) {
        return walletService.updateWallet(id, value);
    }
}
