package com.assignment.implmentation;

import com.assignment.DTO.ProductDTO;
import com.assignment.entites.Category;
import com.assignment.entites.Product;
import com.assignment.exception.ResourceNotFoundException;
import com.assignment.repository.CategoryRepository;
import com.assignment.repository.ProductRepository;
import com.assignment.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of the ProductService interface.
 * <p>
 * This class provides services for managing products, including adding, updating,
 * deleting, and retrieving product details.
 * </p>
 */
@Service
public class ProductServiceImp implements ProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CategoryRepository categoryRepository;

    /**
     * Saves a new product into the database.
     * <p>
     * Validates the product data and ensures the associated category exists.
     * </p>
     *
     * @param productDTO    The DTO containing product details.
     * @param bindingResult The result of input validation.
     * @return ResponseEntity containing success message or validation errors.
     */
    @Override
    public ResponseEntity<Object> saveProduct(ProductDTO productDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> {
                errorMap.put(error.getField(), error.getDefaultMessage());
            });
            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
        }


        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found!"));

        Product product = new Product();
        product.setSku(productDTO.getSku());
        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        product.setImageUrl(productDTO.getImageUrl());
        product.setCategory(category);  // Set the found category
        product.setActive(productDTO.isActive());

        // Save the product
        productRepository.save(product);

        // Return success response

        return ResponseEntity.ok(Map.of("message", "Product successfully inserted"));

    }

    /**
     * Updates an existing product in the database.
     * <p>
     * Ensures the product exists before updating its details.
     * </p>
     *
     * @param productDTO    The DTO containing updated product details.
     * @param bindingResult The result of input validation.
     * @return ResponseEntity containing success message or validation errors.
     */

    @Override
    public ResponseEntity<Object> updateProduct(ProductDTO productDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> {
                errorMap.put(error.getField(), error.getDefaultMessage());
            });
            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
        }


        Product existingProduct = productRepository.findById(productDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Product with productId not found!"));

        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found!"));

        // The product exists, so update it
        existingProduct.setSku(productDTO.getSku());
        existingProduct.setName(productDTO.getName());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setImageUrl(productDTO.getImageUrl());
        existingProduct.setCategory(category);
        existingProduct.setActive(productDTO.isActive());

        // Save the updated product (this will update the existing one)
        productRepository.save(existingProduct);
        return ResponseEntity.ok(Map.of("message", "Product updated successfully"));
    }

    /**
     * Soft deletes a product by marking it as inactive and deleted.
     *
     * @param id The ID of the product to be deleted.
     * @return ResponseEntity containing success message.
     */
    @Override
    public ResponseEntity<Object> deleteProduct(int id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found!"));

        product.setDeleted(true);
        product.setActive(false);
        productRepository.save(product);

        return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
    }


    /**
     * Retrieves all products from the database.
     *
     * @return ResponseEntity containing a list of all products.
     */
    @Override
    public ResponseEntity<Object> findAllProduct() {
        // Fetch the list of products from the database
        List<Product> products = productRepository.findAll();

        // Convert the list of products to a list of ProductDTO manually
        List<ProductDTO> productDTOs = products.stream().map(product -> {
            ProductDTO dto = new ProductDTO();
            dto.setId(product.getId());
            dto.setSku(product.getSku());
            dto.setName(product.getName());
            dto.setPrice(product.getPrice());
            dto.setImageUrl(product.getImageUrl());
            dto.setCategoryId(product.getCategory().getId());
            // ✅ Set category name dynamically
            dto.setCategoryName(product.getCategory().getName());

            dto.setActive(product.getActive());
            return dto;
        }).collect(Collectors.toList());

        // Prepare the response with the DTO list
        return ResponseEntity.ok(Map.of("products", productDTOs));
    }
}
