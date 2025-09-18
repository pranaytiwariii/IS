package com.example.auth.entity.inheritance;

import com.example.auth.interfaces.Writable;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Academic Author class implementing Writable interface (which extends Readable)
 * Demonstrates interface inheritance chain: Readable -> Writable
 */
@Entity
@Table(name = "writable_professors")
@DiscriminatorValue("WRITABLE_PROFESSOR")
public class WritableProfessor extends Professor implements Writable {
    
    @Column(name = "read_count")
    private int readCount = 0;
    
    @Column(name = "created_document_count")
    private int createdDocumentCount = 0;
    
    @ElementCollection
    @CollectionTable(name = "professor_reading_history", joinColumns = @JoinColumn(name = "professor_id"))
    @MapKeyColumn(name = "document_id")
    @Column(name = "read_date")
    private Map<Long, String> readingHistory = new HashMap<>();
    
    @ElementCollection
    @CollectionTable(name = "professor_owned_documents", joinColumns = @JoinColumn(name = "professor_id"))
    @Column(name = "document_id")
    private List<Long> ownedDocuments = new ArrayList<>();
    
    // Default constructor
    public WritableProfessor() {
        super();
    }
    
    // Constructor
    public WritableProfessor(String firstName, String lastName, String email, 
                            String employeeId, String department, String rank) {
        super(firstName, lastName, email, employeeId, department, rank);
    }
    
    // Implementation of Readable interface (inherited through Writable)
    @Override
    public boolean readDocument(Long documentId) {
        if (documentId == null) return false;
        
        readingHistory.put(documentId, LocalDateTime.now().toString());
        readCount++;
        this.setUpdatedAt(LocalDateTime.now()); // Inherited method
        return true;
    }
    
    @Override
    public String[] getReadableDocumentTypes() {
        // Professors can read academic and administrative documents
        return new String[]{"research_paper", "textbook", "thesis", "grant_proposal", 
                           "policy_document", "meeting_minutes", "syllabus"};
    }
    
    @Override
    public int getReadCount() {
        return readCount;
    }
    
    // Implementation of Writable interface methods
    @Override
    public Long createDocument(String title, String content) {
        if (title == null || content == null) return null;
        
        // Simulate document creation (in real app, this would interact with database)
        Long documentId = System.currentTimeMillis(); // Simple ID generation
        ownedDocuments.add(documentId);
        createdDocumentCount++;
        
        // Add to publications if it's a research paper
        if (title.toLowerCase().contains("research") || title.toLowerCase().contains("study")) {
            this.publishPaper(title); // Use inherited method from Professor
        }
        
        this.setUpdatedAt(LocalDateTime.now());
        return documentId;
    }
    
    @Override
    public boolean editDocument(Long documentId, String newContent) {
        if (documentId == null || newContent == null) return false;
        if (!ownsDocument(documentId)) return false;
        
        this.setUpdatedAt(LocalDateTime.now());
        return true;
    }
    
    @Override
    public boolean deleteDocument(Long documentId) {
        if (documentId == null) return false;
        if (!ownsDocument(documentId)) return false;
        
        ownedDocuments.remove(documentId);
        createdDocumentCount = Math.max(0, createdDocumentCount - 1);
        this.setUpdatedAt(LocalDateTime.now());
        return true;
    }
    
    @Override
    public String[] getWritableDocumentTypes() {
        // Professors can write academic documents
        return new String[]{"research_paper", "syllabus", "assignment", "exam", 
                           "lecture_notes", "grant_proposal", "review"};
    }
    
    @Override
    public int getCreatedDocumentCount() {
        return createdDocumentCount;
    }
    
    @Override
    public boolean ownsDocument(Long documentId) {
        return ownedDocuments.contains(documentId);
    }
    
    // Enhanced professor profile including writing capabilities
    @Override
    public String getProfessorProfile() {
        return String.format("""
            %s
            === READING & WRITING CAPABILITIES ===
            Documents Read: %d
            Documents Created: %d
            Can Read Types: %s
            Can Write Types: %s
            Owned Documents: %d
            """,
            super.getProfessorProfile(), // Call parent method
            readCount,
            createdDocumentCount,
            String.join(", ", getReadableDocumentTypes()),
            String.join(", ", getWritableDocumentTypes()),
            ownedDocuments.size()
        );
    }
    
    // Method demonstrating multiple interface inheritance
    public String getInterfaceInheritanceDemo() {
        return String.format("""
            === INTERFACE INHERITANCE CHAIN DEMONSTRATION ===
            Class: WritableProfessor
            Extends: Professor (class inheritance)
            Implements: Writable (interface inheritance)
            
            Interface inheritance chain:
            Writable extends Readable
            
            From Readable interface:
            - readDocument(): %s
            - getReadableDocumentTypes(): %s
            - canRead("research_paper"): %s
            - getReadCount(): %d
            
            From Writable interface (extends Readable):
            - createDocument(): %s
            - editDocument(): %s  
            - deleteDocument(): %s
            - getWritableDocumentTypes(): %s
            - canWrite("syllabus"): %s
            - getCreatedDocumentCount(): %d
            
            Class inheritance from Professor:
            - Employee ID: %s
            - Department: %s
            - Teaching Courses: %d
            
            Class inheritance from BasePerson (through Professor):
            - Full Name: %s
            - Email: %s
            """,
            "Available", // readDocument availability
            String.join(", ", getReadableDocumentTypes()),
            canRead("research_paper"),
            getReadCount(),
            "Available", // createDocument availability
            "Available", // editDocument availability
            "Available", // deleteDocument availability
            String.join(", ", getWritableDocumentTypes()),
            canWrite("syllabus"),
            getCreatedDocumentCount(),
            getEmployeeId(), // From Professor
            getDepartment(), // From Professor
            getTeachingCourses().size(), // From Professor
            getFullName(), // From BasePerson (through Professor)
            getEmail() // From BasePerson (through Professor)
        );
    }
    
    // Getters and setters
    public void setReadCount(int readCount) { this.readCount = readCount; }
    public void setCreatedDocumentCount(int createdDocumentCount) { this.createdDocumentCount = createdDocumentCount; }
    
    public Map<Long, String> getReadingHistory() { return readingHistory; }
    public void setReadingHistory(Map<Long, String> readingHistory) { this.readingHistory = readingHistory; }
    
    public List<Long> getOwnedDocuments() { return ownedDocuments; }
    public void setOwnedDocuments(List<Long> ownedDocuments) { this.ownedDocuments = ownedDocuments; }
}