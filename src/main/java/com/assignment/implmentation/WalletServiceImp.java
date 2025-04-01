package com.assignment.implmentation;

import com.assignment.entites.User;
import com.assignment.entites.Wallet;
import com.assignment.exception.ResourceNotFoundException;
import com.assignment.repository.UserRepository;
import com.assignment.repository.WalletRepository;
import com.assignment.services.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;


/**
 * Implementation of the WalletService interface.
 * <p>
 * This service provides functionality to manage user wallet operations,
 * such as updating the wallet balance.
 * </p>
 */
@Service
public class WalletServiceImp implements WalletService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    WalletRepository walletRepository;


    /**
     * Updates the balance of a user's wallet by adding a specified value.
     * <p>
     * This method retrieves the user by their ID and accesses their wallet.
     * It adds the specified value to the current balance and saves the updated
     * wallet to the repository.
     * </p>
     *
     * @param id    The ID of the user whose wallet balance is being updated.
     * @param value The amount to be added to the current wallet balance.
     * @return ResponseEntity containing a success message or error information.
     * @throws ResourceNotFoundException If the user with the specified ID is not found.
     */
    @Override
    public ResponseEntity<Object> updateWallet(int id, double value) {
        // Find user by ID or throw ResourceNotFoundException if not found
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        // Retrieve the user's wallet
        Wallet wallet = user.getWallet();
        double currentBalance = wallet.getBalance();

        // Update wallet balance
        wallet.setBalance(currentBalance + value);

        walletRepository.save(wallet);
        return ResponseEntity.ok(Map.of("message", "Balance added successfully!"));
    }
}
