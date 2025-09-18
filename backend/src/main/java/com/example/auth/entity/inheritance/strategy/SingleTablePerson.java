package com.example.auth.entity.inheritance.strategy;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Single Table Inheritance Strategy
 * ALL subclasses are stored in ONE table with a discriminator column
 * 
 * Database Schema:
 * CREATE TABLE single_table_persons (
 *     id BIGINT PRIMARY KEY,
 *     person_type VARCHAR(31) NOT NULL,  -- Discriminator column (STUDENT/AUTHOR/ADMIN)
 *     first_name VARCHAR(50),
 *     last_name VARCHAR(50), 
 *     email VARCHAR(100),
 *     phone_number VARCHAR(15),
 *     created_at TIMESTAMP,
 *     -- Student columns (NULL for non-students)
 *     student_id VARCHAR(20),
 *     major VARCHAR(100),
 *     gpa DECIMAL(3,2),
 *     year_of_study INTEGER,
 *     credits_completed INTEGER,
 *     -- Author columns (NULL for non-authors)
 *     author_id VARCHAR(20),
 *     affiliation VARCHAR(100),
 *     research_field VARCHAR(50),
 *     h_index INTEGER,
 *     total_citations INTEGER,
 *     first_publication_date DATE,
 *     -- Admin columns (NULL for non-admins)
 *     admin_id VARCHAR(20),
 *     department VARCHAR(100),
 *     admin_level VARCHAR(50),
 *     last_login TIMESTAMP,
 *     login_count INTEGER,
 *     is_super_admin BOOLEAN,
 *     can_manage_users BOOLEAN,
 *     can_manage_content BOOLEAN,
 *     can_view_reports BOOLEAN,
 *     -- Professor columns (NULL for non-professors)
 *     rank VARCHAR(50),
 *     is_tenured BOOLEAN,
 *     research_area VARCHAR(500)
 * );
 */
@Entity
@Table(name = "single_table_persons")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "person_type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("PERSON")
public class SingleTablePerson {
    
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
    public SingleTablePerson() {
        this.createdAt = LocalDateTime.now();
    }
    
    // Constructor
    public SingleTablePerson(String firstName, String lastName, String email) {
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
    
    public String getPersonType() {
        return this.getClass().getAnnotation(DiscriminatorValue.class).value();
    }
    
    public String getSingleTableInfo() {
        return String.format("""
            === SINGLE TABLE INHERITANCE ===
            Strategy: All subclasses in ONE table
            Table: single_table_persons
            Discriminator: person_type = '%s'
            
            Person Details:
            ID: %d | Name: %s | Email: %s
            Phone: %s | Created: %s
            
            Database Characteristics:
            ✅ Fast queries (no JOINs needed)
            ✅ Simple schema (one table)
            ❌ Many NULL columns for unused fields
            ❌ No NOT NULL constraints on subclass fields
            """,
            getPersonType(),
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