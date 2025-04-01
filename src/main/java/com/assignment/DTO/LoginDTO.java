package com.assignment.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public class LoginDTO {

    @NotBlank(message = "Username is required")
    @NotEmpty(message = "Username cannot be empty")
    private String username;

    @NotBlank(message = "Password is required")
    @NotEmpty(message = "Password cannot be empty")
    private String password;

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
}
