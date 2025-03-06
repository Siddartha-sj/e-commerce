package com.assignment.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public class UserRegistrationDTO {

        @NotBlank(message = "Username is required")
        @NotEmpty(message = "Username cannot be empty")
        private String username;

        @NotBlank(message = "Password is required")
        @NotEmpty(message = "Password cannot be empty")
        private String password;

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        @NotEmpty(message = "Email cannot be empty")
        private String email;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

