package com.assignment.controller;

import com.assignment.DTO.CategoryDTO;
import com.assignment.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/category")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Object> findAllCategory() {

        return categoryService.findAllCategory();
    }

    @PostMapping("/category")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Object> saveCategory(@Valid @RequestBody CategoryDTO categoryDTO, BindingResult bindingResult) {
        return categoryService.saveCategory(categoryDTO, bindingResult);
    }

    @DeleteMapping("/category/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Object> deleteCategory(@PathVariable int id) {
        return categoryService.deleteCategory(id);
    }

    @PutMapping("/category/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Object> updateCategory(@PathVariable int id, @Valid @RequestBody CategoryDTO categoryDTO, BindingResult bindingResult) {
        return categoryService.updateCategory(id, categoryDTO, bindingResult);
    }
}
