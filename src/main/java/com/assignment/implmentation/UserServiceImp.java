package com.assignment.implmentation;

import com.assignment.DTO.UserRegistrationDTO;
import com.assignment.entites.*;
import com.assignment.repository.*;
import com.assignment.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.*;

@Service
public class UserServiceImp implements UserService {

    //   @Autowired
    //   BCryptPasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    PromoCodeRepository promoCodeRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    PaymentRepository paymentRepository;


    @Override
    public ResponseEntity<Object> save(UserRegistrationDTO userRegistrationDTO, BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> {
                errorMap.put(error.getField(), error.getDefaultMessage());
            });
            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
        }
        Map<String, Object> map = new HashMap<>();

        User user = new User();
        user.setUsername(userRegistrationDTO.getUsername());
        user.setPassword(userRegistrationDTO.getPassword());
        user.setEmail(userRegistrationDTO.getEmail());
        user.setRole(roleRepository.findById(1).get());

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        Cart cart = new Cart();
        cart.setUser(user);

        userRepository.save(user);
        cartRepository.save(cart);
        walletRepository.save(wallet);

        map.put("message", "successfully inserted");

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> login(String username, String password) {
        // Fetch the user by username
        Map<String, Object> map = new HashMap<>();
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            // Check if password matches using BCrypt
            // return passwordEncoder.matches(password, user.getPassword());
            String currentUsername = user.getUsername();
            String currentPassword = user.getPassword();
            if (currentUsername.equals(username) && currentPassword.equals(password)) {
                map.put("message", "Login Successful!");
                return new ResponseEntity<>(map, HttpStatus.OK);
            }
        }
        map.put("message", "Login Failed!");
        return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);  // Return false if the user is not found
    }

    @Override
    public ResponseEntity<Object> findAllProduct() {
        Map<String, Object> map = new HashMap<>();
        List<Product> list = productRepository.findAll();
        map.put("Products", list);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> addProductToCart(int userId, int productId, int quantity) {
        Map<String, Object> map = new HashMap<>();

        // Fetch user and product
        Optional<User> userOptional = userRepository.findById(userId);
        Optional<Product> productOptional = productRepository.findById(productId);

        if (userOptional.isEmpty() || productOptional.isEmpty()) {
            map.put("message", "User or Product not found!");
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        }

        User user = userOptional.get();
        Product product = productOptional.get();

        // Check if product is active
        if (!product.getActive()) {
            map.put("message", "This product is no longer available.");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }

        // Check if there is enough stock for the requested quantity
        int availableStock = product.getSku();  // Assuming 'getStock()' gives the available stock
        if (availableStock < quantity) {
            map.put("message", "Not enough stock available for this product.");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }

        // Deduct the quantity from the stock
        product.setSku(availableStock - quantity);

        // Save the updated product back to the repository to reflect the new stock
        productRepository.save(product);

        // Find the user's existing cart
        Cart cart = cartRepository.findByUser(user);

        if (cart == null) {
            map.put("message", "Cart not found for the user!");
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        }

        // Check if the product already exists in the cart
        Optional<CartItem> existingCartItemOptional = cartItemRepository.findByCartAndProduct(cart, product);

        if (existingCartItemOptional.isPresent()) {
            // If product exists, update the quantity
            CartItem existingCartItem = existingCartItemOptional.get();
            existingCartItem.setQuantity(existingCartItem.getQuantity() + quantity);
            cartItemRepository.save(existingCartItem);
            map.put("message", "Product quantity updated in cart.");
            return new ResponseEntity<>(map, HttpStatus.OK);
        } else {
            // If product does not exist, create a new cart item
            CartItem newCartItem = new CartItem();
            newCartItem.setCart(cart);
            newCartItem.setProduct(product);
            newCartItem.setQuantity(quantity);
            cartItemRepository.save(newCartItem);
            map.put("message", "Product added to cart.");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity<Object> removeProductFromCart(int userId, int productId) {
        // Initialize response map
        Map<String, Object> map = new HashMap<>();

        // Fetch the user and product from the database
        Optional<User> userOptional = userRepository.findById(userId);
        Optional<Product> productOptional = productRepository.findById(productId);

        // Check if user or product exist
        if (userOptional.isEmpty() || productOptional.isEmpty()) {
            map.put("message", "User or Product not found!");
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        }

        User user = userOptional.get();
        Product product = productOptional.get();

        // Find the user's existing cart
        Cart cart = cartRepository.findByUser(user);

        // Check if cart exists
        if (cart == null) {
            map.put("message", "Cart not found for the user!");
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        }

        // Find the CartItem for the given product
        Optional<CartItem> cartItemOptional = cartItemRepository.findByCartAndProduct(cart, product);

        // Check if product exists in the cart
        if (cartItemOptional.isEmpty()) {
            map.put("message", "Product not found in the cart!");
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        }

        // Retrieve the CartItem
        CartItem cartItem = cartItemOptional.get();

        // Retrieve the quantity of the product in the cart
        int quantityInCart = cartItem.getQuantity();

        // Remove the CartItem from the cart
        cartItemRepository.delete(cartItem);

        // Update the product's quantity back to the Product table
        product.setSku(product.getSku() + quantityInCart);
        productRepository.save(product);

        // Create response message
        map.put("message", "Product removed from cart and stock updated.");

        // Return successful response
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> placeOrder(int userId, String promoCode) {

        Map<String, Object> map = new HashMap<>();

        // Fetch the user's details and wallet
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            map.put("message", "User not found!");
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        }

        User user = userOptional.get();

        // Fetch user's wallet
        Optional<Wallet> walletOptional = walletRepository.findByUser(user);

        if (walletOptional.isEmpty()) {
            map.put("message", "User wallet not found!");
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        }

        Wallet wallet = walletOptional.get();

        Cart cart = cartRepository.findByUser(user);

        if (cart == null || cart.getCartItems().isEmpty()) {
            map.put("message", "Cart is empty!");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }

        // Calculate the total amount for the order
        double totalAmount = 0.0;
        for (CartItem cartItem : cart.getCartItems()) {
            totalAmount += cartItem.getProduct().getPrice() * cartItem.getQuantity();
        }

        // Check if the promo code is applicable
        PromoCode activePromoCode = null;
        if (promoCode != null && !promoCode.isEmpty()) {
            Optional<PromoCode> promoCodeOptional = promoCodeRepository.findByCode(promoCode);
            if (promoCodeOptional.isPresent()) {
                activePromoCode = promoCodeOptional.get();

                // Check if the promo code is valid and active
                if (!activePromoCode.getActive()) {
                    map.put("message", "Promo code is not active!");
                    return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
                }

                // Check if the promo code has expired
                if (activePromoCode.getExpiryDate() != null && activePromoCode.getExpiryDate().isBefore(LocalDate.now())) {
                    map.put("message", "Promo code has expired!");
                    return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
                }

                // Check if the total amount meets the minimum order amount required by the promo code
                if (totalAmount < activePromoCode.getMinOrderAmount()) {
                    map.put("message", "Total amount does not meet the minimum order amount for this promo code!");
                    return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
                }

                // Apply the discount if the promo code is applicable
                if (activePromoCode.getProductSpecific() && activePromoCode.getProduct() != null) {
                    // Apply discount only to the specific product
                    for (CartItem cartItem : cart.getCartItems()) {
                        if (cartItem.getProduct().equals(activePromoCode.getProduct())) {
                            totalAmount -= cartItem.getProduct().getPrice() * cartItem.getQuantity() * (activePromoCode.getDiscountPercentage() / 100);
                            break;  // Only apply discount once for product-specific promo codes
                        }
                    }
                } else {
                    // Apply discount for general promo codes
                    totalAmount -= totalAmount * (activePromoCode.getDiscountPercentage() / 100);
                }
            }
        }

        // Record the transaction even if the wallet balance is insufficient
        Transaction transaction = new Transaction();
        transaction.setAmount(totalAmount);
        transaction.setTransactionStatus(false);  // Set the transaction status as failed initially
        transaction.setTransactionDate(LocalDate.now());
        transactionRepository.save(transaction);  // Save the transaction regardless of success

        // Check if the user has sufficient balance in the wallet
        if (wallet.getBalance() < totalAmount) {
            map.put("message", "Insufficient wallet balance!");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }

        // Deduct the order total from the wallet balance
        wallet.setBalance(wallet.getBalance() - totalAmount);
        walletRepository.save(wallet); // Save the updated wallet balance

        // Create the order
        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(totalAmount);
        order.setStatus("PLACED");
        order.setOrderDate(LocalDate.now());

        // Set the promo code if it exists
        if (activePromoCode != null) {
            order.setPromoCode(activePromoCode);
        }

        // Save the order to the database (orderRepository is assumed to exist)
        orderRepository.save(order);

        // Create the payment record
        Payment payment = new Payment();
        payment.setAmountPaid(totalAmount);
        payment.setPaymentDate(LocalDate.now());
        payment.setPaymentMethod("WALLET");  // Assuming payment method is wallet
        payment.setTransaction(transaction);  // Link the transaction to the payment

        // Save the payment
        paymentRepository.save(payment);

        // Set the payment on the order
        order.setPayment(payment);
        orderRepository.save(order);  // Save the order with the payment information

        // Set the transaction status to true since the order was successfully placed
        transaction.setTransactionStatus(true);
        transactionRepository.save(transaction);  // Update the transaction status

        // Empty the user's cart after successful order placement
        cart.getCartItems().clear();  // Clear all cart items
        cartRepository.save(cart);  // Save the updated cart

        // Return a success message
        map.put("message", "Order placed successfully!");
        map.put("orderId", order.getId());
        return new ResponseEntity<>(map, HttpStatus.CREATED);
    }
}

