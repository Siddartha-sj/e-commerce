package com.assignment.implmentation;

import com.assignment.config.JWTService;
import com.assignment.entites.Cart;
import com.assignment.entites.CartItem;
import com.assignment.entites.Product;
import com.assignment.entites.User;
import com.assignment.exception.InvalidDataException;
import com.assignment.exception.ResourceNotFoundException;
import com.assignment.repository.CartItemRepository;
import com.assignment.repository.CartRepository;
import com.assignment.repository.ProductRepository;
import com.assignment.repository.UserRepository;
import com.assignment.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of the CartService interface.
 * This class provides functionality for adding and removing products from a user's cart.
 */
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private UserRepository userRepository;


    /**
     * Adds a product to the user's cart.
     * If the product is already present in the cart, its quantity is updated.
     * Otherwise, a new CartItem is created and added to the cart.
     *
     * @param authorizationHeader The JWT token passed in the Authorization header.
     * @param productId           The ID of the product to be added to the cart.
     * @param quantity            The quantity of the product to be added.
     * @return ResponseEntity with a success message indicating whether the product
     * was added or its quantity was updated.
     * @throws ResourceNotFoundException if the user, product, or cart is not found.
     * @throws InvalidDataException      if the product is inactive or if there is
     *                                   insufficient stock.
     */
    @Override
    public ResponseEntity<Object> addProductToCart(String authorizationHeader, int productId, int quantity) {


        String token = authorizationHeader.replace("Bearer ", "");

        String username = jwtService.extractUserName(token);
        User currentUser = userRepository.findByUsername(username);

        // Fetch user and product
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found!"));

        // Check if product is active
        if (!product.getActive()) {
            throw new InvalidDataException("This product is no longer available.");
        }

        // Check stock availability
        int availableStock = product.getSku();
        if (availableStock < quantity) {
            throw new InvalidDataException("Not enough stock available for this product.");
        }

        // Deduct the quantity from the stock
        product.setSku(availableStock - quantity);

        // Save the updated product back to the repository to reflect the new stock
        productRepository.save(product);

        // Get user's cart
        Cart cart = cartRepository.findByUser(user);
        if (cart == null) {
            throw new ResourceNotFoundException("Cart not found for the user!");
        }

        // Check if the product already exists in the cart
        Optional<CartItem> existingCartItemOptional = cartItemRepository.findByCartAndProduct(cart, product);

        if (existingCartItemOptional.isPresent()) {
            // If product exists, update the quantity
            CartItem existingCartItem = existingCartItemOptional.get();
            existingCartItem.setQuantity(existingCartItem.getQuantity() + quantity);
            cartItemRepository.save(existingCartItem);
            return ResponseEntity.ok(Map.of("message", "Product quantity updated in cart."));
        } else {
            // If product does not exist, create a new cart item
            CartItem newCartItem = new CartItem();
            newCartItem.setCart(cart);
            newCartItem.setProduct(product);
            newCartItem.setQuantity(quantity);
            cartItemRepository.save(newCartItem);
            return ResponseEntity.ok(Map.of("message", "Product added to cart."));
        }
    }

    /**
     * Removes a product from the user's cart.
     * If the product is present in the cart, it is removed, and the stock
     * quantity is updated back to the product table.
     *
     * @param authorizationHeader The JWT token passed in the Authorization header.
     * @param productId           The ID of the product to be removed from the cart.
     * @return ResponseEntity with a success message indicating that the product
     * was removed and the stock was updated.
     * @throws ResourceNotFoundException if the user, product, cart, or cart item
     *                                   is not found.
     */
    @Override
    public ResponseEntity<Object> removeProductFromCart(String authorizationHeader, int productId) {


        String token = authorizationHeader.replace("Bearer ", "");

        String username = jwtService.extractUserName(token);
        User currentUser = userRepository.findByUsername(username);

        // Fetch user and product
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found!"));

        // Get user's cart
        Cart cart = cartRepository.findByUser(user);
        if (cart == null) {
            throw new ResourceNotFoundException("Cart not found for the user!");
        }


        // Check if product is in the cart
        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found in the cart!"));


        // Retrieve the quantity of the product in the cart
        int quantityInCart = cartItem.getQuantity();

        // Remove the CartItem from the cart
        cartItemRepository.delete(cartItem);

        // Update the product's quantity back to the Product table
        product.setSku(product.getSku() + quantityInCart);
        productRepository.save(product);

        // Create response message and return
        return ResponseEntity.ok(Map.of("message", "Product removed from cart."));
    }

    @Override
    public ResponseEntity<Object> viewCart(String authorizationHeader) {
        // Extract username from JWT token
        String token = authorizationHeader.replace("Bearer ", "");
        String username = jwtService.extractUserName(token);
        User user = userRepository.findByUsername(username);

        // Fetch user's cart
        Cart cart = cartRepository.findByUser(user);
        if (cart == null || cart.getCartItems().isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "Cart is empty."));
        }

        // Convert cart items into a structured response
        List<Map<String, Object>> cartItems = cart.getCartItems().stream().map(item -> new HashMap<String, Object>() {{
            put("productId", item.getProduct().getId());
            put("productName", item.getProduct().getName());
            put("price", item.getProduct().getPrice());
            put("quantity", item.getQuantity());
            put("imageUrl", item.getProduct().getImageUrl());
        }}).collect(Collectors.toList());


        return ResponseEntity.ok(Map.of("cartItems", cartItems));
    }

}
