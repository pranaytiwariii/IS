package com.example.auth.service;

import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.SignupRequest;
import com.example.auth.entity.Role;
import com.example.auth.entity.RoleName;
import com.example.auth.entity.User;
import com.example.auth.repository.RoleRepository;
import com.example.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public boolean signup(SignupRequest signupRequest) {
        logger.debug("Starting signup process for username: {}, email: {}", 
                    signupRequest.getUsername(), signupRequest.getEmail());
        
        // Check if username already exists
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            logger.warn("Signup failed: Username '{}' already exists", signupRequest.getUsername());
            return false;
        }

        // Check if email already exists
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            logger.warn("Signup failed: Email '{}' already exists", signupRequest.getEmail());
            return false;
        }

        // Get role
        RoleName roleName;
        try {
            roleName = RoleName.valueOf(signupRequest.getRole().toUpperCase());
            logger.debug("Role validated: {}", roleName);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid role provided: {}", signupRequest.getRole());
            return false; // Invalid role
        }

        Optional<Role> roleOptional = roleRepository.findByName(roleName);
        if (roleOptional.isEmpty()) {
            // Create role if it doesn't exist
            logger.info("Creating new role: {}", roleName);
            Role role = new Role(roleName);
            roleOptional = Optional.of(roleRepository.save(role));
        } else {
            logger.debug("Using existing role: {}", roleName);
        }

        try {
            // Create new user
            User user = new User(
                signupRequest.getUsername(),
                signupRequest.getEmail(),
                passwordEncoder.encode(signupRequest.getPassword()),
                roleOptional.get()
            );

            User savedUser = userRepository.save(user);
            logger.info("User created successfully with ID: {}, username: {}", 
                       savedUser.getId(), savedUser.getUsername());
            return true;
        } catch (Exception e) {
            logger.error("Error creating user: {}, error: {}", signupRequest.getUsername(), e.getMessage(), e);
            return false;
        }
    }

    public boolean login(LoginRequest loginRequest) {
        logger.debug("Attempting login for username: {}", loginRequest.getUsername());
        
        Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            boolean passwordMatches = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
            
            if (passwordMatches) {
                logger.info("Login successful for username: {}", loginRequest.getUsername());
            } else {
                logger.warn("Login failed: Invalid password for username: {}", loginRequest.getUsername());
            }
            
            return passwordMatches;
        } else {
            logger.warn("Login failed: User not found with username: {}", loginRequest.getUsername());
        }
        
        return false;
    }

    public User authenticateAndGetUser(LoginRequest loginRequest) {
        logger.debug("Authenticating and retrieving user for username: {}", loginRequest.getUsername());
        
        Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                logger.info("Authentication successful for username: {}, role: {}", 
                           user.getUsername(), user.getRole() != null ? user.getRole().getName() : "No role");
                return user;
            } else {
                logger.warn("Authentication failed: Invalid password for username: {}", loginRequest.getUsername());
            }
        } else {
            logger.warn("Authentication failed: User not found with username: {}", loginRequest.getUsername());
        }
        
        return null;
    }

    public boolean isUsernameExists(String username) {
        logger.debug("Checking if username exists: {}", username);
        boolean exists = userRepository.existsByUsername(username);
        logger.debug("Username '{}' exists: {}", username, exists);
        return exists;
    }

    public boolean isEmailExists(String email) {
        logger.debug("Checking if email exists: {}", email);
        boolean exists = userRepository.existsByEmail(email);
        logger.debug("Email '{}' exists: {}", email, exists);
        return exists;
    }

    public Optional<User> findByUsername(String username) {
        logger.debug("Finding user by username: {}", username);
        Optional<User> user = userRepository.findByUsername(username);
        logger.debug("User found for username '{}': {}", username, user.isPresent());
        return user;
    }

    public String getUserRole(String username) {
        logger.debug("Getting role for username: {}", username);
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent() && userOptional.get().getRole() != null) {
            String role = userOptional.get().getRole().getName().toString();
            logger.debug("Role found for username '{}': {}", username, role);
            return role;
        }
        logger.debug("No role found for username: {}", username);
        return null;
    }
}
