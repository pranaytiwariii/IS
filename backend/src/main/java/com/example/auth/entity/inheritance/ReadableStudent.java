package com.example.auth.entity.inheritance;

import com.example.auth.interfaces.Readable;
import jakarta.persistence.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Enhanced Student class implementing Readable interface
 * Demonstrates single interface inheritance
 */
@Entity
@Table(name = "readable_students")
@DiscriminatorValue("READABLE_STUDENT")
public class ReadableStudent extends Student implements Readable {
    
    @Column(name = "read_count")
    private int readCount = 0;
    
    @ElementCollection
    @CollectionTable(name = "student_reading_history", joinColumns = @JoinColumn(name = "student_id"))
    @MapKeyColumn(name = "document_id")
    @Column(name = "read_date")
    private Map<Long, String> readingHistory = new HashMap<>();
    
    // Default constructor
    public ReadableStudent() {
        super();
    }
    
    // Constructor
    public ReadableStudent(String firstName, String lastName, String email, 
                          String studentId, String major) {
        super(firstName, lastName, email, studentId, major);
    }
    
    // Implementation of Readable interface
    @Override
    public boolean readDocument(Long documentId) {
        if (documentId == null) return false;
        
        readingHistory.put(documentId, java.time.LocalDateTime.now().toString());
        readCount++;
        this.setUpdatedAt(java.time.LocalDateTime.now()); // Inherited method
        return true;
    }
    
    @Override
    public String[] getReadableDocumentTypes() {
        // Students can read basic academic documents
        return new String[]{"textbook", "assignment", "syllabus", "announcement", "research_paper"};
    }
    
    @Override
    public int getReadCount() {
        return readCount;
    }
    
    // Enhanced student profile including reading capabilities
    @Override
    public String getStudentProfile() {
        return String.format("""
            %s
            === READING CAPABILITIES ===
            Documents Read: %d
            Can Read Types: %s
            Recent Reading Activity: %s
            """,
            super.getStudentProfile(), // Call parent method
            readCount,
            String.join(", ", getReadableDocumentTypes()),
            readingHistory.isEmpty() ? "None" : "Last " + Math.min(3, readingHistory.size()) + " documents"
        );
    }
    
    // Method demonstrating interface inheritance
    public String getInterfaceCapabilities() {
        return String.format("""
            === INTERFACE INHERITANCE DEMONSTRATION ===
            Class: ReadableStudent
            Extends: Student (class inheritance)
            Implements: Readable (interface inheritance)
            
            Inherited from Student class:
            - Student ID: %s
            - Major: %s
            - GPA: %s
            
            Inherited from BasePerson class (through Student):
            - Full Name: %s
            - Email: %s
            
            Implemented from Readable interface:
            - Can read documents: %s
            - Readable types: %s
            - Documents read: %d
            """,
            getStudentId(), // From Student
            getMajor(), // From Student  
            getGpa() != null ? getGpa().toString() : "Not set", // From Student
            getFullName(), // From BasePerson (through Student)
            getEmail(), // From BasePerson (through Student)
            canRead("textbook"), // From Readable interface
            String.join(", ", getReadableDocumentTypes()), // From Readable interface
            getReadCount() // From Readable interface
        );
    }
    
    // Getters and setters
    public void setReadCount(int readCount) { this.readCount = readCount; }
    
    public Map<Long, String> getReadingHistory() { return readingHistory; }
    public void setReadingHistory(Map<Long, String> readingHistory) { this.readingHistory = readingHistory; }
}