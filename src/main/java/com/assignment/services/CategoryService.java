package com.assignment.services;

import com.assignment.DTO.CategoryDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

public interface CategoryService {
    ResponseEntity<Object> saveCategory(CategoryDTO categoryDTO, BindingResult bindingResult);

    ResponseEntity<Object> deleteCategory(int id);

    ResponseEntity<Object> findAllCategory();

    ResponseEntity<Object> updateCategory(int id, @Valid CategoryDTO categoryDTO, BindingResult bindingResult);
}
