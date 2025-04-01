package com.assignment.controller;

import com.assignment.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/place/{promoCode}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Object> placeOrder(@RequestHeader("Authorization") String authorizationHeader,
                                             @PathVariable(required = false) String promoCode) {
        return orderService.placeOrder(authorizationHeader, promoCode);
    }

    @PostMapping("/place")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Object> placeOrderWithoutPromo(@RequestHeader("Authorization") String authorizationHeader) {
        return orderService.placeOrder(authorizationHeader, null);
    }

    @DeleteMapping("/cancel/{orderId}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Object> cancelOrder(@RequestHeader("Authorization") String authorizationHeader,
                                              @PathVariable int orderId) {
        return orderService.cancelOrder(authorizationHeader, orderId);
    }

    @GetMapping("/history")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Object> getOrders(@RequestHeader("Authorization") String authorizationHeader) {
        return orderService.getOrders(authorizationHeader);
    }
}
