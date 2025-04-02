package com.assignment.implmentation;

import com.assignment.DTO.*;
import com.assignment.config.JWTService;
import com.assignment.entites.*;
import com.assignment.exception.InvalidDataException;
import com.assignment.exception.ResourceNotFoundException;
import com.assignment.repository.*;
import com.assignment.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the UserService interface.
 * <p>
 * This service provides functionalities related to user management, including
 * registration, login, address update, and user retrieval or deletion.
 * </p>
 */
@Service
public class UserServiceImp implements UserService {


    @Autowired
    UserRepository userRepository;


    @Autowired
    CartRepository cartRepository;

    @Autowired
    WalletRepository walletRepository;


    @Autowired
    RoleRepository roleRepository;


    @Autowired
    JWTService jwtService;

    @Autowired
    AuthenticationManager authenticationManager;

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);


    /**
     * Registers a new user based on the provided registration details.
     * <p>
     * It checks for duplicate usernames and emails, assigns a role (USER or ADMIN),
     * and creates a wallet and cart for regular users. Returns a success message
     * or validation errors.
     * </p>
     *
     * @param userRegistrationDTO The DTO containing registration details.
     * @param bindingResult       The result of input validation.
     * @return ResponseEntity containing success or error message.
     */
    @Override
    public ResponseEntity<Object> register(UserRegistrationDTO userRegistrationDTO, BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> {
                errorMap.put(error.getField(), error.getDefaultMessage());
            });
            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
        }

        // Check for duplicate username and email
        if (userRepository.findByUsername(userRegistrationDTO.getUsername()) != null) {
            throw new InvalidDataException("Username is already taken");
        }
        if (userRepository.findByEmail(userRegistrationDTO.getEmail()) != null) {
            throw new InvalidDataException("Email is already taken");
        }

        // Find role based on user type (ADMIN or USER)
        Role role = roleRepository.findById(userRegistrationDTO.getRole().equalsIgnoreCase("ADMIN") ? 2 : 1)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        User user = new User();
        user.setUsername(userRegistrationDTO.getUsername());
        user.setPassword(encoder.encode(userRegistrationDTO.getPassword()));
        user.setEmail(userRegistrationDTO.getEmail());
        user.setRole(role);

        userRepository.save(user);


        // Create wallet and cart if the user is a regular user
        if (userRegistrationDTO.getRole().equalsIgnoreCase("USER")) {

            // Create and save wallet and cart for user
            Wallet wallet = new Wallet();
            wallet.setUser(user);
            walletRepository.save(wallet);

            Cart cart = new Cart();
            cart.setUser(user);
            cartRepository.save(cart);
            return ResponseEntity.ok(Map.of("message", "User successfully created with wallet and cart"));


        } else if (userRegistrationDTO.getRole().equalsIgnoreCase("ADMIN")) {
            // If it's an ADMIN, save only the user and return appropriate message
            return ResponseEntity.ok(Map.of("message", "Admin user successfully created"));
        } else {
            // If it's an ADMIN, save only the user and return appropriate message
            throw new InvalidDataException("Role has to be USER or ADMIN!");
        }


    }

    /**
     * Authenticates a user and generates a JWT token if the credentials are valid.
     * <p>
     * Verifies username and password using bcrypt and Spring Security’s authentication manager.
     * Returns a JWT token and success message or error message if authentication fails.
     * </p>
     *
     * @param loginDTO      The DTO containing login details.
     * @param bindingResult The result of input validation.
     * @return ResponseEntity containing a token and message or error information.
     */
    @Override
    public ResponseEntity<Object> login(LoginDTO loginDTO, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> {
                errorMap.put(error.getField(), error.getDefaultMessage());
            });
            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
        }


        // Find user by username
        User existingUser = userRepository.findByUsername(loginDTO.getUsername());

        // Check if the user exists
        if (existingUser == null) {
            throw new InvalidDataException("User not found!");
        }

        // Verify the password using bcrypt or other encoding mechanism
        if (!encoder.matches(loginDTO.getPassword(), existingUser.getPassword())) {
            throw new InvalidDataException("Bad credentials!");
        }

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword()));

        if (authentication.isAuthenticated()) {
            String token = jwtService.generateToken(loginDTO.getUsername(), existingUser.getRole().getName());

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "message", "Login Successful!"
            ));

        }

        throw new InvalidDataException("Login Failed!"); // Return false if the user is not found
    }


    /**
     * Updates the address and phone number for the authenticated user.
     * <p>
     * Extracts the username from the JWT token provided in the authorization header
     * and updates the user’s address and phone number.
     * </p>
     *
     * @param authorizationHeader The authorization header containing the JWT token.
     * @param addressRequestDTO   The DTO containing address and phone number.
     * @param bindingResult       The result of input validation.
     * @return ResponseEntity with success message or validation errors.
     */
    @Override
    public ResponseEntity<Object> updateAddress(String authorizationHeader, AddressRequestDTO addressRequestDTO, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> {
                errorMap.put(error.getField(), error.getDefaultMessage());
            });
            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
        }


        String token = authorizationHeader.replace("Bearer ", "");
        String username = jwtService.extractUserName(token);
        User user = Optional.ofNullable(userRepository.findByUsername(username))
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));


        // Update address and phone number in the user entity
        user.setAddress(addressRequestDTO.getAddress());
        user.setPhoneNumber(addressRequestDTO.getPhoneNumber());

        // Save the updated user entity
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Address and phone number updated successfully!"));
    }

    @Override
    public ResponseEntity<Object> updateUserProfile(String authorizationHeader, UserDTO userDTO) {
        String token = authorizationHeader.replace("Bearer ", "");
        String username = jwtService.extractUserName(token);

        User user = Optional.ofNullable(userRepository.findByUsername(username))
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

        // ✅ Update fields if provided
        if (userDTO.getAddress() != null) {
            user.setAddress(userDTO.getAddress());
        }
        if (userDTO.getPhoneNumber() != null) {
            user.setPhoneNumber(userDTO.getPhoneNumber());
        }
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail());
        }

        // ✅ Save updated user
        userRepository.save(user);

        // ✅ Return updated profile
        return ResponseEntity.ok(Map.of("message", "Profile updated successfully!"));
    }

    @Override
    public ResponseEntity<Object> getUserProfile(String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        String username = jwtService.extractUserName(token);
        User user = Optional.ofNullable(userRepository.findByUsername(username))
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

        // Map to UserDTO
        double walletBalance = user.getWallet() != null ? user.getWallet().getBalance() : 0.0;



        return ResponseEntity.ok(Map.of("Data", new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().getName(),
                user.getWallet() != null,
                user.getAddress(),
                user.getPhoneNumber(),
                walletBalance)));

    }

    /**
     * Retrieves a list of all users.
     * <p>
     * Converts User entities to UserDTO objects for response and returns the list.
     * </p>
     *
     * @return ResponseEntity containing a list of user details.
     */
    @Override
    public ResponseEntity<Object> findAllUsers() {
        // Fetch users from the repository
        List<User> users = userRepository.findAll();

        // Map User entities to UserDTO objects
        List<UserDTO> userDTOs = users.stream().map(user -> {
            String roleName = (user.getRole() != null) ? user.getRole().getName() : "No Role";
            boolean hasWallet = (user.getWallet() != null);

            double walletBalance = user.getWallet() != null ? user.getWallet().getBalance() : 0.0;
            return new UserDTO(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole().getName(),
                    user.getWallet() != null,
                    user.getAddress(),
                    user.getPhoneNumber(),
                    walletBalance);
        }).collect(Collectors.toList());

        // Prepare the response

        return ResponseEntity.ok(Map.of("Data", userDTOs));
    }

    /**
     * Retrieves a user by their ID.
     * <p>
     * Throws a ResourceNotFoundException if no user is found with the given ID.
     * </p>
     *
     * @param id The ID of the user to retrieve.
     * @return ResponseEntity containing the user details.
     */
    @Override
    public ResponseEntity<Object> findUserById(int id) {
        // Fetch the user using the repository
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        return ResponseEntity.ok(Map.of("User", user));
    }

    /**
     * Soft deletes a user by setting the deleted flag to true.
     * <p>
     * Throws a ResourceNotFoundException if no user is found with the given ID.
     * </p>
     *
     * @param id The ID of the user to delete.
     * @return ResponseEntity containing a success message.
     */
    @Override
    public ResponseEntity<Object> deleteUser(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        // Soft delete the user by setting the deleted flag to true
        user.setDeleted(true);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "User deleted successfully!"));

    }

}



