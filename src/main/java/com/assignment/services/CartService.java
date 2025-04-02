package com.assignment.services;

import org.springframework.http.ResponseEntity;

public interface CartService {
    ResponseEntity<Object> addProductToCart(String authorizationHeader, int productId, int quantity);

    ResponseEntity<Object> removeProductFromCart(String authorizationHeader, int productId);

    ResponseEntity<Object> viewCart(String authorizationHeader);
}
