package com.example.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "User registration request payload")
public class SignupRequest {
    @NotBlank
    @Size(min = 3, max = 20)
    @Schema(description = "Username (3-20 characters)", example = "john_doe", required = true)
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    @Schema(description = "Email address", example = "john@example.com", required = true)
    private String email;

    @NotBlank
    @Size(min = 6, max = 40)
    @Schema(description = "Password (6-40 characters)", example = "password123", required = true)
    private String password;

    @NotBlank
    @Schema(description = "User role", example = "AUTHOR", allowableValues = {"STUDENT", "AUTHOR", "COMMITTEE"}, required = true)
    private String role; // STUDENT, AUTHOR, or COMMITTEE

    public SignupRequest() {
    }

    public SignupRequest(String username, String email, String password, String role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
