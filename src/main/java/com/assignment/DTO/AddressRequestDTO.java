package com.assignment.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public class AddressRequestDTO {

    @NotBlank(message = "Address is required")
    @NotEmpty(message = "Address cannot be empty")
    private String address;

    @NotBlank(message = "PhoneNumber is required")
    @NotEmpty(message = "PhoneNumber cannot be empty")
    private String phoneNumber;

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
}
