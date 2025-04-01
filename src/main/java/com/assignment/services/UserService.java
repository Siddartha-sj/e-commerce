package com.assignment.services;

import com.assignment.DTO.AddressRequestDTO;
import com.assignment.DTO.LoginDTO;
import com.assignment.DTO.UserDTO;
import com.assignment.DTO.UserRegistrationDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

public interface UserService {


    ResponseEntity<Object> register(UserRegistrationDTO userRegistrationDTO, BindingResult bindingResult);

    ResponseEntity<Object> login(LoginDTO loginDTO, BindingResult bindingResult);

    ResponseEntity<Object> getUserProfile(String authorizationHeader);

    ResponseEntity<Object> findAllUsers();

    ResponseEntity<Object> findUserById(int id);

    ResponseEntity<Object> deleteUser(int id);

    ResponseEntity<Object> updateAddress(String authorizationHeader, AddressRequestDTO addressRequestDTO, BindingResult bindingResult);

    ResponseEntity<Object> updateUserProfile(String authorizationHeader, UserDTO userDTO);
}
