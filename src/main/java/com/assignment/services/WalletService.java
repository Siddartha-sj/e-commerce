package com.assignment.services;

import org.springframework.http.ResponseEntity;

public interface WalletService {
    ResponseEntity<Object> updateWallet(int id, double value);
}
