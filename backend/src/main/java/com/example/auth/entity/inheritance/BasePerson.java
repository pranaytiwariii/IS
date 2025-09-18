package com.example.auth.entity.inheritance;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Base class demonstrating Single Inheritance
 * This is the parent class that provides common properties for all person types
 */
@Entity
@Table(name = "base_persons")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "person_type", discriminatorType = DiscriminatorType.STRING)
public class BasePerson {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    
    @NotBlank
    @Size(max = 50)
    protected String firstName;
    
    @NotBlank
    @Size(max = 50)
    protected String lastName;
    
    @NotBlank
    @Size(max = 100)
    @Email
    @Column(unique = true)
    protected String email;
    
    @Column(name = "date_of_birth")
    protected LocalDateTime dateOfBirth;
    
    @Size(max = 15)
    protected String phoneNumber;
    
    @Column(name = "created_at")
    protected LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    protected LocalDateTime updatedAt;
    
    // Default constructor
    public BasePerson() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Constructor with basic info
    public BasePerson(String firstName, String lastName, String email) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
    
    // JPA lifecycle methods
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Common behavior methods that all person types inherit
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public boolean isEmailValid() {
        return email != null && email.contains("@");
    }
    
    public String getDisplayInfo() {
        return String.format("%s (%s)", getFullName(), email);
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public LocalDateTime getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDateTime dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}