package com.assignment.controller;

import com.assignment.DTO.UserRegistrationDTO;
import com.assignment.implmentation.UserServiceImp;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/ecommerce/user")
@CrossOrigin
public class UserController {

    @Autowired
    UserServiceImp userServiceImp;

    @PostMapping("/register")
    public ResponseEntity<Object> save(@Valid @RequestBody UserRegistrationDTO userRegistrationDTO, BindingResult bindingResult) {

        return userServiceImp.save(userRegistrationDTO, bindingResult);
    }

    @PostMapping("/login/{username},{password}")
    public ResponseEntity<Object> login(@PathVariable String username, @PathVariable String password) {
        return userServiceImp.login(username, password);

    }

    @GetMapping("/home")
    public ResponseEntity<Object> findAllProduct() {
        return userServiceImp.findAllProduct();
    }


    @PostMapping("/addtocart/{userId},{productId},{quantity}")
    public ResponseEntity<Object> addProductToCart(@PathVariable int userId, @PathVariable int productId, @PathVariable int quantity) {

        return userServiceImp.addProductToCart(userId, productId, quantity);
    }

    @PutMapping("/removefromcart/{userId},{productId}")
    public ResponseEntity<Object> removeProductFromCart(@PathVariable int userId, @PathVariable int productId) {
        return userServiceImp.removeProductFromCart(userId, productId);
    }

    @PostMapping("/order/{userId},{promoCode}")
    public ResponseEntity<Object> placeOrder(@PathVariable int userId, @PathVariable String promoCode) {
        return userServiceImp.placeOrder(userId, promoCode);
    }


}
