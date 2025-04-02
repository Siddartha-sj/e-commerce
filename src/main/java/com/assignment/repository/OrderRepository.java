package com.assignment.repository;

import com.assignment.entites.Order;
import com.assignment.entites.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUser(User currentUser);
}
