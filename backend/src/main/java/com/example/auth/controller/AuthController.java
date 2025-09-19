package com.example.auth.controller;

import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.MessageResponse;
import com.example.auth.dto.SignupRequest;
import com.example.auth.payload.response.JwtResponse;
import com.example.auth.entity.User;
import com.example.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    @Operation(
        summary = "Register a new user",
        description = "Create a new user account with username, email, password, and role"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User registered successfully",
            content = @Content(schema = @Schema(implementation = MessageResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - Username or email already exists",
            content = @Content(schema = @Schema(implementation = MessageResponse.class))
        )
    })
    public ResponseEntity<?> registerUser(
            @Parameter(description = "User registration details", required = true)
            @Valid @RequestBody SignupRequest signupRequest) {
        
        logger.info("Registration attempt for username: {}, email: {}, role: {}", 
                   signupRequest.getUsername(), signupRequest.getEmail(), signupRequest.getRole());
        
        if (authService.isUsernameExists(signupRequest.getUsername())) {
            logger.warn("Registration failed: Username '{}' already exists", signupRequest.getUsername());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (authService.isEmailExists(signupRequest.getEmail())) {
            logger.warn("Registration failed: Email '{}' already exists", signupRequest.getEmail());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        try {
            boolean success = authService.signup(signupRequest);
            
            if (success) {
                logger.info("User registration successful for username: {}", signupRequest.getUsername());
                return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
            } else {
                logger.error("User registration failed for username: {}", signupRequest.getUsername());
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Error: Failed to register user!"));
            }
        } catch (Exception e) {
            logger.error("Unexpected error during registration for username: {}, error: {}", 
                        signupRequest.getUsername(), e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Registration failed due to server error!"));
        }
    }

    @PostMapping("/login")
    @Operation(
        summary = "Authenticate user",
        description = "Login with username and password to get authentication token"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User authenticated successfully",
            content = @Content(schema = @Schema(implementation = JwtResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid credentials",
            content = @Content(schema = @Schema(implementation = MessageResponse.class))
        )
    })
    public ResponseEntity<?> authenticateUser(
            @Parameter(description = "User login credentials", required = true)
            @Valid @RequestBody LoginRequest loginRequest) {
        
        logger.info("Login attempt for username: {}", loginRequest.getUsername());
        
        try {
            // Additional validation
            if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty()) {
                logger.warn("Login failed: Username is empty");
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Error: Username is required!"));
            }
            
            if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
                logger.warn("Login failed: Password is empty for username: {}", loginRequest.getUsername());
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Error: Password is required!"));
            }

            User user = authService.authenticateAndGetUser(loginRequest);
            
            if (user != null) {
                String roleName = user.getRole() != null ? user.getRole().getName().name() : "USER";
                logger.info("Login successful for username: {}, role: {}", user.getUsername(), roleName);
                JwtResponse response = new JwtResponse("dummy-token", user.getUsername(), user.getEmail(), roleName);
                return ResponseEntity.ok(response);
            } else {
                logger.warn("Login failed: Invalid credentials for username: {}", loginRequest.getUsername());
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Error: Invalid username or password!"));
            }
        } catch (Exception e) {
            logger.error("Unexpected error during login for username: {}, error: {}", 
                        loginRequest.getUsername(), e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
}
