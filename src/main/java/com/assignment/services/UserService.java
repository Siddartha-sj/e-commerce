package com.assignment.services;

import com.assignment.DTO.UserRegistrationDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

public interface UserService {


    public ResponseEntity<Object> save(@Valid UserRegistrationDTO userRegistrationDTO, BindingResult bindingResult);

    public ResponseEntity<Object> login(String username, String password);

    public ResponseEntity<Object> findAllProduct();

    public ResponseEntity<Object> addProductToCart(int userId, int productId, int quantity);

    public ResponseEntity<Object> removeProductFromCart(int userId, int productId);

    public ResponseEntity<Object> placeOrder(int userId, String promoCode);


}
