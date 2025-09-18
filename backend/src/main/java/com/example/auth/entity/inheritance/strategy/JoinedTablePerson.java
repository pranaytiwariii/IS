package com.example.auth.entity.inheritance.strategy;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Joined Table Inheritance Strategy
 * Each class has its OWN table, joined by foreign key relationships
 * 
 * Database Schema:
 * 
 * CREATE TABLE joined_table_persons (
 *     id BIGINT PRIMARY KEY,
 *     first_name VARCHAR(50),
 *     last_name VARCHAR(50),
 *     email VARCHAR(100),
 *     phone_number VARCHAR(15),
 *     created_at TIMESTAMP
 * );
 * 
 * CREATE TABLE joined_table_students (
 *     id BIGINT PRIMARY KEY,
 *     student_id VARCHAR(20),
 *     major VARCHAR(100),
 *     gpa DECIMAL(3,2),
 *     FOREIGN KEY (id) REFERENCES joined_table_persons(id)
 * );
 * 
 * CREATE TABLE joined_table_employees (
 *     id BIGINT PRIMARY KEY,
 *     employee_id VARCHAR(20),
 *     department VARCHAR(100),
 *     salary DECIMAL(10,2),
 *     hire_date TIMESTAMP,
 *     FOREIGN KEY (id) REFERENCES joined_table_persons(id)
 * );
 */
@Entity
@Table(name = "joined_table_persons")
@Inheritance(strategy = InheritanceType.JOINED)
public class JoinedTablePerson {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 50)
    @Column(name = "first_name")
    private String firstName;
    
    @NotBlank
    @Size(max = 50)
    @Column(name = "last_name")
    private String lastName;
    
    @NotBlank
    @Size(max = 100)
    @Email
    @Column(unique = true)
    private String email;
    
    @Size(max = 15)
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Default constructor
    public JoinedTablePerson() {
        this.createdAt = LocalDateTime.now();
    }
    
    // Constructor
    public JoinedTablePerson(String firstName, String lastName, String email) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    
    // Common methods
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public String getTableName() {
        Table table = this.getClass().getAnnotation(Table.class);
        return table != null ? table.name() : this.getClass().getSimpleName().toLowerCase();
    }
    
    public String getJoinedTableInfo() {
        return String.format("""
            === JOINED TABLE INHERITANCE ===
            Strategy: Each class has its OWN table
            Base Table: joined_table_persons
            Child Table: %s
            Join: Foreign Key relationship on ID
            
            Person Details:
            ID: %d | Name: %s | Email: %s
            Phone: %s | Created: %s
            
            Database Characteristics:
            ✅ Normalized schema (no NULL columns)
            ✅ NOT NULL constraints possible on subclass fields
            ✅ Clean separation of concerns
            ❌ Requires JOINs for queries (performance impact)
            ❌ More complex schema
            """,
            getTableName(),
            id != null ? id : 0,
            getFullName(),
            email,
            phoneNumber != null ? phoneNumber : "Not provided",
            createdAt != null ? createdAt.toLocalDate() : "Not set"
        );
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
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}