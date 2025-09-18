package com.example.auth.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "JWT authentication response")
public class JwtResponse {
    @Schema(description = "Access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
    
    @Schema(description = "Token type", example = "Bearer")
    private String type = "Bearer";
    
    @Schema(description = "Username", example = "john_doe")
    private String username;
    
    @Schema(description = "Email address", example = "john@example.com")
    private String email;
    
    @Schema(description = "User role", example = "AUTHOR")
    private String role;

    public JwtResponse(String accessToken, String username, String email, String role) {
        this.token = accessToken;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    public String getAccessToken() {
        return token;
    }

    public void setAccessToken(String accessToken) {
        this.token = accessToken;
    }

    public String getTokenType() {
        return type;
    }

    public void setTokenType(String tokenType) {
        this.type = tokenType;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
