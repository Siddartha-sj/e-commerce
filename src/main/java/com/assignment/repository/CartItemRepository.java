package com.assignment.repository;

import com.assignment.entites.Cart;
import com.assignment.entites.CartItem;
import com.assignment.entites.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
}
