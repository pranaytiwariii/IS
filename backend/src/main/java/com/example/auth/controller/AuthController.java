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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

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
        if (authService.isUsernameExists(signupRequest.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (authService.isEmailExists(signupRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        boolean success = authService.signup(signupRequest);
        
        if (success) {
            return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
        } else {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Failed to register user!"));
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
        try {
            // Additional validation
            if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Error: Username is required!"));
            }
            
            if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Error: Password is required!"));
            }

            User user = authService.authenticateAndGetUser(loginRequest);
            
            if (user != null) {
                String roleName = user.getRole() != null ? user.getRole().getName().name() : "USER";
                JwtResponse response = new JwtResponse("dummy-token", user.getUsername(), user.getEmail(), roleName);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Error: Invalid username or password!"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
}
