package com.assignment.controller;

import com.assignment.DTO.CategoryDTO;
import com.assignment.DTO.ProductDTO;
import com.assignment.DTO.PromoCodeDTO;
import com.assignment.implmentation.AdminServiceImp;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ecommerce/admin")
@CrossOrigin
public class AdminController {

    @Autowired
    AdminServiceImp adminServiceImp;

    @PostMapping("/product")
    public ResponseEntity<Object> saveProduct(@Valid @RequestBody ProductDTO productDTO, BindingResult bindingResult) {
        // Check if there are validation errors

        return adminServiceImp.saveProduct(productDTO,bindingResult);
    }


    @GetMapping("/product")
    public ResponseEntity<Object> findAllProduct() {
        return adminServiceImp.findAllProduct();

    }

    @PutMapping("/product")
    public ResponseEntity<Object> updateProduct(@RequestBody @Valid ProductDTO productDTO, BindingResult bindingResult) {
        return adminServiceImp.updateProduct(productDTO,bindingResult);
    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity<Object> deleteProduct(@PathVariable int id) {

        return adminServiceImp.deleteProduct(id);
    }

    @PostMapping("/category")
    public ResponseEntity<Object> saveCategory(@RequestBody @Valid CategoryDTO categoryDTO, BindingResult bindingResult) {

        return adminServiceImp.saveCategory(categoryDTO,bindingResult);
    }

    @DeleteMapping("/category/{id}")
    public ResponseEntity<Object> deleteCategory(@PathVariable int id) {

        return adminServiceImp.deleteCategory(id);
    }

    @GetMapping("/user")
    public ResponseEntity<Object> findAllUsers() {

        return adminServiceImp.findAllUsers();
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable int id) {

        return adminServiceImp.deleteUser(id);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<Object> findUserById(@PathVariable int id) {

        return adminServiceImp.findUserById(id);
    }

    @PutMapping("/wallet/{id},{value}")
    public ResponseEntity<Object> updateWallet(@PathVariable int id, @PathVariable double value) {
        return adminServiceImp.updateWallet(id, value);

    }

    @PostMapping("/promocode")
    public ResponseEntity<Object> createPromoCode(@Valid @RequestBody PromoCodeDTO promoCodeDTO, BindingResult bindingResult) {

        return adminServiceImp.createPromoCode(promoCodeDTO,bindingResult);

    }
}
