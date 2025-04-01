package com.assignment.controller;

import com.assignment.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add/{productId}/{quantity}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Object> addProductToCart(@RequestHeader("Authorization") String authorizationHeader,
                                                   @PathVariable int productId,
                                                   @PathVariable int quantity) {
        return cartService.addProductToCart(authorizationHeader, productId, quantity);
    }

    @DeleteMapping("/remove/{productId}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Object> removeProductFromCart(@RequestHeader("Authorization") String authorizationHeader,
                                                        @PathVariable int productId) {
        return cartService.removeProductFromCart(authorizationHeader, productId);
    }

    @GetMapping("/view")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Object> viewCart(@RequestHeader("Authorization") String authorizationHeader) {
        return cartService.viewCart(authorizationHeader);
    }
}
