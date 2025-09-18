package com.example.auth.entity.inheritance;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Enhanced User class demonstrating Single Inheritance
 * This class extends BasePerson and inherits all its properties and methods
 */
@Entity
@Table(name = "enhanced_users")
@DiscriminatorValue("USER")
public class EnhancedUser extends BasePerson {
    
    @NotBlank
    @Size(max = 20)
    @Column(unique = true)
    private String username;
    
    @NotBlank
    @Size(max = 120)
    private String password;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    
    @Size(max = 500)
    private String biography;
    
    // Default constructor
    public EnhancedUser() {
        super();
    }
    
    // Constructor using parent constructor
    public EnhancedUser(String firstName, String lastName, String email, String username, String password) {
        super(firstName, lastName, email); // Call parent constructor
        this.username = username;
        this.password = password;
    }
    
    // Methods that extend parent functionality
    @Override
    public String getDisplayInfo() {
        // Override parent method to include username
        return String.format("%s (@%s) - %s", getFullName(), username, email);
    }
    
    // New methods specific to EnhancedUser
    public boolean canLogin() {
        return isActive && password != null && !password.isEmpty();
    }
    
    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
        this.setUpdatedAt(LocalDateTime.now()); // Call inherited setter
    }
    
    public String getUserSummary() {
        return String.format("User: %s | Username: %s | Active: %s | Member since: %s", 
                            getFullName(), // Inherited method
                            username, 
                            isActive, 
                            getCreatedAt().toLocalDate()); // Inherited getter
    }
    
    // Demonstration of method overriding
    public boolean isEmailValid() {
        // Extended validation beyond parent class
        return super.isEmailValid() && !email.endsWith(".temp");
    }
    
    // Getters and Setters for new properties
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
    
    public String getBiography() { return biography; }
    public void setBiography(String biography) { this.biography = biography; }
}