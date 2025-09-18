package com.example.auth.entity.inheritance.strategy;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Table Per Class Inheritance Strategy
 * Each CONCRETE class has its own COMPLETE table (including all parent fields)
 * Abstract parent class does NOT have a table
 * 
 * Database Schema:
 * 
 * -- NO table for TablePerClassPerson (abstract class)
 * 
 * CREATE TABLE table_per_class_students (
 *     id BIGINT PRIMARY KEY,
 *     -- All fields from parent AND child
 *     first_name VARCHAR(50),
 *     last_name VARCHAR(50),
 *     email VARCHAR(100),
 *     phone_number VARCHAR(15),
 *     created_at TIMESTAMP,
 *     -- Student-specific fields
 *     student_id VARCHAR(20),
 *     major VARCHAR(100),
 *     gpa DECIMAL(3,2)
 * );
 * 
 * CREATE TABLE table_per_class_employees (
 *     id BIGINT PRIMARY KEY,
 *     -- All fields from parent AND child
 *     first_name VARCHAR(50),
 *     last_name VARCHAR(50),
 *     email VARCHAR(100),
 *     phone_number VARCHAR(15),
 *     created_at TIMESTAMP,
 *     -- Employee-specific fields
 *     employee_id VARCHAR(20),
 *     department VARCHAR(100),
 *     salary DECIMAL(10,2),
 *     hire_date TIMESTAMP
 * );
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class TablePerClassPerson {
    
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
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
    public TablePerClassPerson() {
        this.createdAt = LocalDateTime.now();
    }
    
    // Constructor
    public TablePerClassPerson(String firstName, String lastName, String email) {
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
    
    public String getTablePerClassInfo() {
        return String.format("""
            === TABLE PER CLASS INHERITANCE ===
            Strategy: Each CONCRETE class has COMPLETE table
            Abstract Parent: NO table (TablePerClassPerson)
            Concrete Table: %s
            ID Generation: TABLE strategy (shared sequence)
            
            Person Details:
            ID: %d | Name: %s | Email: %s
            Phone: %s | Created: %s
            
            Database Characteristics:
            ✅ No JOINs needed (all data in one table)
            ✅ NOT NULL constraints possible
            ✅ Independent table optimization
            ❌ Data duplication across tables
            ❌ Complex polymorphic queries (UNION required)
            ❌ Difficult to query across all person types
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