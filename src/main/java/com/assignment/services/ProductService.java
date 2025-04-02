package com.assignment.services;

import com.assignment.DTO.ProductDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

public interface ProductService {
    ResponseEntity<Object> saveProduct(ProductDTO productDTO, BindingResult bindingResult);

    ResponseEntity<Object> updateProduct(ProductDTO productDTO, BindingResult bindingResult);

    ResponseEntity<Object> deleteProduct(int id);

    ResponseEntity<Object> findAllProduct();
}
