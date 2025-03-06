package com.assignment.implmentation;

import com.assignment.DTO.CategoryDTO;
import com.assignment.DTO.ProductDTO;
import com.assignment.DTO.PromoCodeDTO;
import com.assignment.entites.*;
import com.assignment.repository.*;
import com.assignment.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AdminServiceImp implements AdminService {

    //   @Autowired
    //   BCryptPasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    PromoCodeRepository promoCodeRepository;



    @Override
    public ResponseEntity<Object> saveProduct(ProductDTO productDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // Return a bad request response with error messages
            Map<String, Object> errors = new HashMap<>();
            bindingResult.getAllErrors().forEach(error -> {
                errors.put(error.getCode(), error.getDefaultMessage());
            });
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        Map<String, Object> map = new HashMap<>();

        Optional<Category> categoryOpt = categoryRepository.findById(productDTO.getCategoryId());

        // If the category is not found, return an error map and BAD_REQUEST status
        if (categoryOpt.isEmpty()) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("message", "Category not found");
            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
        }

        // If category is found, proceed with saving the product
        Category category = categoryOpt.get();

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

        map.put("message", "Product successfully inserted");
        return new ResponseEntity<>(map, HttpStatus.OK);

    }

    @Override
    public ResponseEntity<Object> updateProduct(ProductDTO productDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // Collect all validation errors and return them in the response
            Map<String, String> errors = new HashMap<>();
            bindingResult.getAllErrors().forEach(error -> {
                errors.put(error.getObjectName(), error.getDefaultMessage());
            });
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        Map<String, Object> map = new HashMap<>();
        Product product = new Product();
        product.setId(productDTO.getId());
        product.setSku(productDTO.getSku());
        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        product.setImageUrl(productDTO.getImageUrl());
        product.setActive(productDTO.isActive());

        // Check if category exists
        Optional<Category> category = categoryRepository.findById(productDTO.getCategoryId());
        if (!category.isPresent()) {
            map.put("message", "Category does not exist");
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        }
        product.setCategory(category.get());

        // Proceed with finding and updating the product
        Optional<Product> currentProduct = productRepository.findById(product.getId());

        if (currentProduct.isPresent()) {
            // The product exists, so update it
            Product existingProduct = currentProduct.get();
            existingProduct.setSku(product.getSku());
            existingProduct.setName(product.getName());
            existingProduct.setPrice(product.getPrice());
            existingProduct.setImageUrl(product.getImageUrl());
            existingProduct.setCategory(product.getCategory());
            existingProduct.setActive(product.getActive());

            // Save the updated product (this will update the existing one)
            productRepository.save(existingProduct);
        } else {
            map.put("message","Product not found!");
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        }

        map.put("message", "Updated");
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> deleteProduct(int id) {
        Map<String, Object> map = new HashMap<>();
        Optional<Product> product = productRepository.findById(id);
        if(product.isPresent()) {
            Product currentProduct = product.get();
            currentProduct.setDeleted(true);
            currentProduct.setActive(false);
            productRepository.save(currentProduct);
            map.put("message", "Product Deleted!");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
        else {
            map.put("message","Product Not Found!");
            return new ResponseEntity<>(map,HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<Object> findAllProduct() {
        Map<String, Object> map = new HashMap<>();
        List<Product> list = productRepository.findAll();
        map.put("Data",list);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> saveCategory(CategoryDTO categoryDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // Return a bad request response with error messages
            Map<String, Object> errors = new HashMap<>();
            bindingResult.getAllErrors().forEach(error -> {
                errors.put(error.getCode(), error.getDefaultMessage());
            });
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        Map<String, Object> map = new HashMap<>();
        Category category = new Category();
        category.setName(categoryDTO.getName());

        // Save the category
        categoryRepository.save(category);

        // Return success response
        map.put("message", "Category Added!");
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> deleteCategory(int id) {
        Optional<Category> category = categoryRepository.findById(id);
        Map<String, Object> map = new HashMap<>();
        if(category.isPresent())
        {
            Category currentCategory = category.get();
            currentCategory.setDeleted(true);
            categoryRepository.save(currentCategory);
            map.put("message","Category Deleted!");
            return new ResponseEntity<>(map,HttpStatus.OK);
        }
        else {
            map.put("message","Category Not Found!");
            return new ResponseEntity<>(map,HttpStatus.NOT_FOUND);
        }

    }

    @Override
    public ResponseEntity<Object> findAllUsers() {
        Map<String, Object> map = new HashMap<>();
        List<User> list = userRepository.findAll();
        map.put("Data", list);
        return new ResponseEntity<>(map, HttpStatus.OK);

    }

    @Override
    public ResponseEntity<Object> findUserById(int id) {
        // Fetch the user using the repository
        Optional<User> user = userRepository.findById(id);
        Map<String, Object> map = new HashMap<>();
        // Check if the user is present
        if (user.isPresent()) {
            map.put("User",user.get());
            return new ResponseEntity<>(map, HttpStatus.OK);  // Return the user data with 200 OK status
        } else {
            // If the user is not found, return a string with 404 Not Found status
            map.put("message","User Not Found!");
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<Object> deleteUser(int id) {
        Map<String, Object> map = new HashMap<>();
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            User currentUser = user.get();
            currentUser.setDeleted(true);
            userRepository.save(currentUser);
            map.put("message","User Deleted!");
            return new ResponseEntity<>(map,HttpStatus.OK);

        } else {
            map.put("message","User Not Found!");
            return new ResponseEntity<>(map,HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<Object> updateWallet(int id, double value) {
        Map<String, Object> map = new HashMap<>();
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            User currentUser = user.get();
            Wallet wallet = currentUser.getWallet();
            double currentBalance = wallet.getBalance();
            wallet.setBalance(currentBalance + value);

            walletRepository.save(wallet);
            map.put("message","Balance Added");

            return new ResponseEntity<>(map,HttpStatus.OK);

        } else {
            map.put("message","User Not Found!");
            return new ResponseEntity<>(map,HttpStatus.NOT_FOUND);
        }

    }

    @Override
    public ResponseEntity<Object> createPromoCode(PromoCodeDTO promoCodeDTO, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            // Return a bad request response with error messages
            Map<String, Object> errors = new HashMap<>();
            bindingResult.getAllErrors().forEach(error -> {
                errors.put(error.getCode(), error.getDefaultMessage());
            });
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        Map<String, Object> map = new HashMap<>();
        LocalDate expiryDateParsed = LocalDate.parse(promoCodeDTO.getExpiryDate());
        PromoCode promoCode = new PromoCode();
        promoCode.setCode(promoCodeDTO.getCode());
        promoCode.setDiscountPercentage(promoCodeDTO.getDiscountPercentage());
        promoCode.setMinOrderAmount(promoCodeDTO.getMinOrderAmount());
        promoCode.setProductSpecific(promoCodeDTO.getIsProductSpecific());
        promoCode.setActive(promoCodeDTO.getIsActive());
        promoCode.setExpiryDate(expiryDateParsed);


        // If the promo code is product-specific, set the product
        if (promoCodeDTO.getIsProductSpecific() && promoCodeDTO.getProductId() != null) {
            Optional<Product> product = productRepository.findById(promoCodeDTO.getProductId());
            if (product.isPresent()) {
                promoCode.setProduct(product.get());
            } else {
                map.put("message","Product with ID  does not exist.");
                return new ResponseEntity<>(map,HttpStatus.NOT_FOUND);
            }
        }

        // Save the promo code to the database
        promoCodeRepository.save(promoCode);
        map.put("message","Promocode Created");
        return new ResponseEntity<>(map,HttpStatus.OK);
    }


}
