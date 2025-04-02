package com.assignment.controller;

import com.assignment.DTO.AddressRequestDTO;
import com.assignment.DTO.LoginDTO;
import com.assignment.DTO.UserDTO;
import com.assignment.DTO.UserRegistrationDTO;
import com.assignment.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("http://localhost:3000")
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping("/register")
    public ResponseEntity<Object> register(@Valid @RequestBody UserRegistrationDTO userRegistrationDTO, BindingResult bindingResult) {
        return userService.register(userRegistrationDTO, bindingResult);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginDTO loginDTO, BindingResult bindingResult) {
        return userService.login(loginDTO, bindingResult);
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Object> findAllUsers() {
        return userService.findAllUsers();
    }

    @GetMapping("/user/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Object> findUserById(@PathVariable int id) {
        return userService.findUserById(id);
    }

    @DeleteMapping("/user/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Object> deleteUser(@PathVariable int id) {
        return userService.deleteUser(id);
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Object>  getUserProfile(@RequestHeader("Authorization") String authorizationHeader) {

        return userService.getUserProfile(authorizationHeader);
    }
    @PutMapping("/profile")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Object> updateUserProfile(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody UserDTO userDTO) {

        return userService.updateUserProfile(authorizationHeader, userDTO);
    }


    @PostMapping("/address")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Object> updateAddress(@RequestHeader("Authorization") String authorizationHeader,
                                                @Valid @RequestBody AddressRequestDTO addressRequest,
                                                BindingResult bindingResult) {
        return userService.updateAddress(authorizationHeader, addressRequest, bindingResult);
    }
}