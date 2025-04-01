package com.assignment.DTO;

import com.assignment.entites.Wallet;

public class UserDTO {
    private int id;
    private String username;
    private String email;
    private String roleName; // For Role information
    private boolean hasWallet; // To indicate if the user has an associated wallet
    private String address;
    private String phoneNumber;
    private double walletBalance; // Only wallet balance


    public UserDTO()
    {

    }
    // Constructor
    public UserDTO(int id, String username, String email, String roleName, boolean hasWallet, String address, String phoneNumber, double walletBalance) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.roleName = roleName;
        this.hasWallet = hasWallet;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.walletBalance = walletBalance;
    }

    public double getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(double walletBalance) {
        this.walletBalance = walletBalance;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public boolean isHasWallet() {
        return hasWallet;
    }

    public void setHasWallet(boolean hasWallet) {
        this.hasWallet = hasWallet;
    }
}
