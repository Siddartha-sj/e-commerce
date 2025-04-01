package com.assignment.implmentation;

import com.assignment.DTO.CategoryDTO;
import com.assignment.entites.Category;
import com.assignment.exception.ResourceNotFoundException;
import com.assignment.repository.CategoryRepository;
import com.assignment.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the CategoryService interface.
 * <p>
 * This class provides functionality for adding and deleting categories.
 * </p>
 */

@Service
public class CategoryServiceImp implements CategoryService {

    @Autowired
    CategoryRepository categoryRepository;

    /**
     * Saves a new category based on the provided CategoryDTO.
     * Validates the provided category data and creates a new category if the
     * validation passes.
     *
     * @param categoryDTO   The data transfer object containing the category name.
     * @param bindingResult The result of binding the request parameters.
     *                      Used to check for validation errors.
     * @return ResponseEntity containing a success message if the category is
     * added, or an error map if validation fails.
     */
    @Override
    public ResponseEntity<Object> saveCategory(CategoryDTO categoryDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> {
                errorMap.put(error.getField(), error.getDefaultMessage());
            });
            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
        }

        // Create a new Category object and set its name
        Category category = new Category();
        category.setName(categoryDTO.getName());

        // Save the category
        categoryRepository.save(category);

        // Return success response
        return ResponseEntity.ok(Map.of("message", "Category Added!"));
    }


    /**
     * Soft deletes a category by setting its `deleted` flag to true.
     * <p>
     * If the category exists, it is marked as deleted and updated in the repository.
     * </p>
     *
     * @param id The ID of the category to be deleted.
     * @return ResponseEntity containing a success message if the category
     * is found and marked as deleted.
     * @throws ResourceNotFoundException if the category is not found.
     */
    @Override
    public ResponseEntity<Object> deleteCategory(int id) {

        // Fetch category by ID, throw exception if not found
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category Not Found!"));

        // Mark category as deleted and save it
        category.setDeleted(true);
        categoryRepository.save(category);

        // Return success message
        return ResponseEntity.ok(Map.of("message", "Category Deleted!"));


    }

    @Override
    public ResponseEntity<Object> findAllCategory() {
        List<Category> categories = categoryRepository.findAll();

        // Check if the list is empty
        if (categories.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "No categories found"));
        }

        // Map Category to CategoryDTO directly in the stream
        List<CategoryDTO> categoryDTOs = categories.stream()
                .map(category -> {
                    CategoryDTO dto = new CategoryDTO();
                    dto.setName(category.getName());
                    return dto;
                })
                .toList();

        // Return the response
        return ResponseEntity.ok(Map.of("data", categoryDTOs));
    }

    @Override
    public ResponseEntity<Object> updateCategory(int id, CategoryDTO categoryDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> {
                errorMap.put(error.getField(), error.getDefaultMessage());
            });
            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
        }

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category Not Found!"));
        category.setName(categoryDTO.getName());
        categoryRepository.save(category);

        return ResponseEntity.ok(Map.of("message", "Category Updated!"));
    }

}
