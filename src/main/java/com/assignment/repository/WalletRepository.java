package com.assignment.repository;

import com.assignment.entites.User;
import com.assignment.entites.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet,Integer> {
    Optional<Wallet> findByUser(User user);
}
