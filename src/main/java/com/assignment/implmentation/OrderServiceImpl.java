package com.assignment.implmentation;

import com.assignment.DTO.OrderDTO;
import com.assignment.config.JWTService;
import com.assignment.entites.*;
import com.assignment.exception.InvalidDataException;
import com.assignment.exception.ResourceNotFoundException;
import com.assignment.repository.*;
import com.assignment.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of the OrderService interface.
 * <p>
 * This class handles placing, cancelling, and retrieving orders.
 * It performs necessary validations and manages order-related operations.
 * </p>
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private OrderItemRepository orderItemRepository;


    @Autowired
    private JWTService jwtService;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private PromoCodeRepository promoCodeRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderAuditRepository orderAuditRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Places an order for the current authenticated user.
     * Validates user, cart, and promo code before placing the order. Deducts wallet balance
     * and records the transaction, then creates order and order items.
     *
     * @param authorizationHeader Authorization header containing the JWT token.
     * @param promoCode           Optional promo code for discount.
     * @return ResponseEntity containing success message and order ID.
     */
    @Override
    public ResponseEntity<Object> placeOrder(String authorizationHeader, String promoCode) {


        String token = authorizationHeader.replace("Bearer ", "");
        String username = jwtService.extractUserName(token);
        User currentUser = userRepository.findByUsername(username);

        // ✅ Check if user exists
        if (currentUser == null) {
            throw new ResourceNotFoundException("User not found!");
        }

        // ✅ Get user with address and phone check
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

        if (user.getAddress() == null || user.getAddress().isEmpty()) {
            throw new InvalidDataException("Address is required to place an order!");
        }

        if (user.getPhoneNumber() == null || user.getPhoneNumber().isEmpty()) {
            throw new InvalidDataException("Phone number is required to place an order!");
        }

        // ✅ Get user's wallet and cart
        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("User wallet not found!"));

        Cart cart = Optional.ofNullable(cartRepository.findByUser(user))
                .orElseThrow(() -> new InvalidDataException("Cart not found!"));

        if (cart.getCartItems().isEmpty()) {
            throw new InvalidDataException("Cart is empty!");
        }

        // ✅ Calculate total amount
        double totalAmount = 0.0;
        for (CartItem cartItem : cart.getCartItems()) {
            totalAmount += cartItem.getProduct().getPrice() * cartItem.getQuantity();
        }

        // ✅ Check and apply promo code if provided
        PromoCode activePromoCode = null;
        if (promoCode != null && !promoCode.trim().isEmpty()) {
            activePromoCode = promoCodeRepository.findByCode(promoCode)
                    .orElseThrow(() -> new ResourceNotFoundException("Promo code not found!"));

            if (!activePromoCode.getActive()) {
                throw new InvalidDataException("Promo code is not active!");
            }

            if (activePromoCode.getExpiryDate() != null && activePromoCode.getExpiryDate().isBefore(LocalDate.now())) {
                throw new InvalidDataException("Promo code has expired!");
            }

            if (totalAmount < activePromoCode.getMinOrderAmount()) {
                throw new InvalidDataException("Total amount does not meet the minimum order amount for this promo code!");
            }

            // ✅ Apply the discount if applicable
            if (activePromoCode.getProductSpecific() && activePromoCode.getProduct() != null) {
                for (CartItem cartItem : cart.getCartItems()) {
                    if (cartItem.getProduct().equals(activePromoCode.getProduct())) {
                        totalAmount -= cartItem.getProduct().getPrice() * cartItem.getQuantity() * (activePromoCode.getDiscountPercentage() / 100);
                        break; // Apply discount only once
                    }
                }
            } else {
                // ✅ Apply general discount if promo code is valid
                totalAmount -= totalAmount * (activePromoCode.getDiscountPercentage() / 100);
            }
        }

        // ✅ Create transaction (initially set as failed)
        Transaction transaction = new Transaction();
        transaction.setAmount(totalAmount);
        transaction.setTransactionStatus(false);
        transaction.setTransactionDate(LocalDate.now());
        transactionRepository.save(transaction);

        // ✅ Check if wallet balance is sufficient
        if (wallet.getBalance() < totalAmount) {
            throw new InvalidDataException("Insufficient wallet balance!");
        }

        // ✅ Deduct order total from wallet balance
        wallet.setBalance(wallet.getBalance() - totalAmount);
        walletRepository.save(wallet);

        // ✅ Create the order
        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(totalAmount);
        order.setStatus("PLACED");
        order.setOrderDate(LocalDate.now());
        order.setAddress(user.getAddress());
        order.setPhoneNumber(user.getPhoneNumber());

        // ✅ Set promo code if applied
        if (activePromoCode != null) {
            order.setPromoCode(activePromoCode);
        }

        orderRepository.save(order);

        // ✅ Save audit log
        saveOrderAudit(order, user, "ORDER_PLACED", "Order successfully placed with total amount: " + totalAmount);

        // ✅ Create OrderItems for each item in the cart
        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());
            orderItemRepository.save(orderItem);
        }

        // ✅ Create payment record
        Payment payment = new Payment();
        payment.setAmountPaid(totalAmount);
        payment.setPaymentDate(LocalDate.now());
        payment.setPaymentMethod("WALLET");
        payment.setTransaction(transaction);
        paymentRepository.save(payment);

        // ✅ Link payment to order
        order.setPayment(payment);
        orderRepository.save(order);

        // ✅ Update transaction status to success
        transaction.setTransactionStatus(true);
        transactionRepository.save(transaction);

        // ✅ Clear the user's cart after order
        cart.getCartItems().clear();
        cartRepository.save(cart);

        // ✅ Return success message
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Order placed successfully!",
                "orderId", order.getId()
        ));
    }

    // ✅ Save order audit log
    private void saveOrderAudit(Order order, User user, String action, String details) {
        OrderAudit audit = new OrderAudit();
        audit.setOrder(order);
        audit.setUser(user);
        audit.setAction(action);
        audit.setDetails(details);
        orderAuditRepository.save(audit);
    }

    /**
     * Cancels an order by its ID and processes a refund to the user's wallet.
     * <p>
     * Validates user authorization and order status before initiating cancellation.
     * </p>
     *
     * @param authorizationHeader Authorization header containing the JWT token.
     * @param orderId             ID of the order to be cancelled.
     * @return ResponseEntity containing success message and order ID.
     */
    @Override
    public ResponseEntity<Object> cancelOrder(String authorizationHeader, int orderId) {

        String token = authorizationHeader.replace("Bearer ", "");
        String username = jwtService.extractUserName(token);
        User currentUser = userRepository.findByUsername(username);

        if (currentUser == null) {
            throw new ResourceNotFoundException("User not found!");
        }
        // Fetch the order by ID
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found!"));

        // Ensure the order belongs to the current user
        if (!order.getUser().equals(currentUser)) {
            throw new InvalidDataException("You are not authorized to cancel this order!");
        }

        // Check if the order is already cancelled
        if (order.getStatus().equals("CANCELLED")) {
            throw new InvalidDataException("This order has already been cancelled!");
        }


        // Optionally, allow cancellation even if the order is completed
        if (order.getStatus().equals("PLACED")) {
            // Change the order status to "CANCELLED"
            order.setStatus("CANCELLED");
            order.setOrderDate(LocalDate.now());  // Set cancellation date
            orderRepository.save(order);  // Save the updated order status
        }

        // Refund the wallet balance
        Wallet wallet = walletRepository.findByUser(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("User wallet not found!"));
        wallet.setBalance(wallet.getBalance() + order.getTotalAmount());
        walletRepository.save(wallet);


        // Update the product stock in the cart items of the cancelled order
        for (OrderItem orderItem : order.getOrderItems()) {
            Product product = orderItem.getProduct();
            product.setSku(product.getSku() + orderItem.getQuantity()); // Increase the stock
            productRepository.save(product); // Save the updated product stock
        }

        // Create a transaction to log the refund (optional)
        Transaction refundTransaction = new Transaction();
        refundTransaction.setAmount(order.getTotalAmount());
        refundTransaction.setTransactionStatus(true);
        refundTransaction.setTransactionDate(LocalDate.now());
        transactionRepository.save(refundTransaction);

        // Optionally, save an audit log for the cancellation
        saveOrderAudit(order, currentUser, "ORDER_CANCELLED", "Order has been cancelled and refunded.");

        return ResponseEntity.ok(Map.of(
                "message", "Order cancelled successfully and amount refunded to your wallet.",
                "orderId", order.getId()
        ));
    }

    /**
     * Retrieves all orders for the authenticated user.
     * <p>
     * The orders are mapped into OrderDTO objects for a simplified response.
     * </p>
     *
     * @param authorizationHeader The HTTP Authorization header containing the Bearer token.
     * @return ResponseEntity containing a list of orders or a message if no orders exist.
     */
    @Override
    public ResponseEntity<Object> getOrders(String authorizationHeader) {

        // Extract the token and get the username
        String token = authorizationHeader.replace("Bearer ", "");
        String username = jwtService.extractUserName(token);

        // Fetch user from the repository
        User currentUser = userRepository.findByUsername(username);

        if (currentUser == null) {
            throw new ResourceNotFoundException("User not found!");
        }

        // Fetch all orders associated with the user
        List<Order> orders = orderRepository.findByUser(currentUser);

        // Check if the user has no orders
        if (orders.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "No orders found!"));
        }


        // Map the orders to OrderDTO
        List<OrderDTO> orderDTOs = orders.stream().map(order ->
                new OrderDTO(order.getId(), order.getTotalAmount(), order.getStatus(), order.getOrderDate())
        ).collect(Collectors.toList());

        // Return the list of OrderDTOs
        return ResponseEntity.ok(Map.of("orders", orderDTOs));
    }
}
