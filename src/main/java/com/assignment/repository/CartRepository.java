package com.assignment.repository;

import com.assignment.entites.Cart;
import com.assignment.entites.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {

    Cart findByUser(User user);
}
