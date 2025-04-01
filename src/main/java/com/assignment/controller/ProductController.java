package com.assignment.controller;

import com.assignment.DTO.ProductDTO;
import com.assignment.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("http://localhost:3000")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/product")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Object> saveProduct(@Valid @RequestBody ProductDTO productDTO, BindingResult bindingResult) {
        return productService.saveProduct(productDTO, bindingResult);
    }

    @PutMapping("/product")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Object> updateProduct(@Valid @RequestBody ProductDTO productDTO, BindingResult bindingResult) {
        return productService.updateProduct(productDTO, bindingResult);
    }

    @DeleteMapping("/product/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Object> deleteProduct(@PathVariable int id) {
        return productService.deleteProduct(id);
    }

    @GetMapping("/products")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Object> findAllProducts() {
        return productService.findAllProduct();
    }


}
