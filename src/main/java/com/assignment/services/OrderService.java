package com.assignment.services;

import org.springframework.http.ResponseEntity;

public interface OrderService {
    ResponseEntity<Object> placeOrder(String authorizationHeader, String promoCode);

    ResponseEntity<Object> cancelOrder(String authorizationHeader, int orderId);

    ResponseEntity<Object> getOrders(String authorizationHeader);
}
