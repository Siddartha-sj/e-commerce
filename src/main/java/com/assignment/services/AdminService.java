package com.assignment.services;

import com.assignment.DTO.CategoryDTO;
import com.assignment.DTO.ProductDTO;
import com.assignment.DTO.PromoCodeDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

public interface AdminService {


    ResponseEntity<Object> saveProduct(ProductDTO productDTO, BindingResult bindingResult);


    ResponseEntity<Object> updateProduct(ProductDTO productDTO, BindingResult bindingResult);

    public ResponseEntity<Object> deleteProduct(int id);

    public ResponseEntity<Object> findAllProduct();

    ResponseEntity<Object> saveCategory(CategoryDTO categoryDTO, BindingResult bindingResult);

    public ResponseEntity<Object> deleteCategory(int id);

    public ResponseEntity<Object> findAllUsers();

    public ResponseEntity<Object> findUserById(int id);

    public ResponseEntity<Object> deleteUser(int id);

    public ResponseEntity<Object> updateWallet(int id, double value);

    ResponseEntity<Object> createPromoCode(PromoCodeDTO promoCodeDTO, BindingResult bindingResult);
}
