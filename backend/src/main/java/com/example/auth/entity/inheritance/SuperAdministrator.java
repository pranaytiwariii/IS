package com.example.auth.entity.inheritance;

import com.example.auth.interfaces.Administrable;
import com.example.auth.interfaces.Publishable;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Super Administrator class implementing multiple interfaces
 * Demonstrates multiple interface inheritance: Publishable (which extends Writable -> Readable) + Administrable
 */
@Entity
@Table(name = "super_administrators")
@DiscriminatorValue("SUPER_ADMIN")
public class SuperAdministrator extends Administrator implements Publishable, Administrable {
    
    // Fields for Readable interface implementation
    @Column(name = "read_count")
    private int readCount = 0;
    
    @ElementCollection
    @CollectionTable(name = "admin_reading_history", joinColumns = @JoinColumn(name = "admin_id"))
    @MapKeyColumn(name = "document_id")
    @Column(name = "read_date")
    private Map<Long, String> readingHistory = new HashMap<>();
    
    // Fields for Writable interface implementation
    @Column(name = "created_document_count")
    private int createdDocumentCount = 0;
    
    @ElementCollection
    @CollectionTable(name = "admin_owned_documents", joinColumns = @JoinColumn(name = "admin_id"))
    @Column(name = "document_id")
    private List<Long> ownedDocuments = new ArrayList<>();
    
    // Fields for Publishable interface implementation
    @Column(name = "published_document_count")
    private int publishedDocumentCount = 0;
    
    @Column(name = "reviewed_document_count")
    private int reviewedDocumentCount = 0;
    
    @ElementCollection
    @CollectionTable(name = "admin_pending_reviews", joinColumns = @JoinColumn(name = "admin_id"))
    @Column(name = "document_id")
    private List<Long> pendingReviewDocuments = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "admin_published_docs", joinColumns = @JoinColumn(name = "admin_id"))
    @Column(name = "document_id")
    private List<Long> publishedDocuments = new ArrayList<>();
    
    // Default constructor
    public SuperAdministrator() {
        super();
    }
    
    // Constructor
    public SuperAdministrator(String firstName, String lastName, String email, 
                             String employeeId, String department, String position) {
        super(firstName, lastName, email, employeeId, department, position);
    }
    
    // ===== READABLE INTERFACE IMPLEMENTATION =====
    @Override
    public boolean readDocument(Long documentId) {
        if (documentId == null) return false;
        
        readingHistory.put(documentId, LocalDateTime.now().toString());
        readCount++;
        this.setUpdatedAt(LocalDateTime.now());
        return true;
    }
    
    @Override
    public String[] getReadableDocumentTypes() {
        // Super administrators can read everything
        return new String[]{"research_paper", "policy_document", "financial_report", 
                           "meeting_minutes", "user_data", "system_logs", "confidential"};
    }
    
    @Override
    public int getReadCount() {
        return readCount;
    }
    
    // ===== WRITABLE INTERFACE IMPLEMENTATION =====
    @Override
    public Long createDocument(String title, String content) {
        if (title == null || content == null) return null;
        
        Long documentId = System.currentTimeMillis();
        ownedDocuments.add(documentId);
        createdDocumentCount++;
        this.setUpdatedAt(LocalDateTime.now());
        return documentId;
    }
    
    @Override
    public boolean editDocument(Long documentId, String newContent) {
        if (documentId == null || newContent == null) return false;
        // Super admins can edit any document
        this.setUpdatedAt(LocalDateTime.now());
        return true;
    }
    
    @Override
    public boolean deleteDocument(Long documentId) {
        if (documentId == null) return false;
        // Super admins can delete any document
        ownedDocuments.remove(documentId);
        this.setUpdatedAt(LocalDateTime.now());
        return true;
    }
    
    @Override
    public String[] getWritableDocumentTypes() {
        // Super administrators can write all types
        return new String[]{"policy_document", "announcement", "directive", 
                           "financial_report", "system_notice", "regulation"};
    }
    
    @Override
    public int getCreatedDocumentCount() {
        return createdDocumentCount;
    }
    
    @Override
    public boolean ownsDocument(Long documentId) {
        // Super admins can act as owners of any document
        return true;
    }
    
    // ===== PUBLISHABLE INTERFACE IMPLEMENTATION =====
    @Override
    public boolean publishDocument(Long documentId) {
        if (documentId == null) return false;
        
        publishedDocuments.add(documentId);
        publishedDocumentCount++;
        
        // Remove from pending if it was there
        pendingReviewDocuments.remove(documentId);
        
        this.setUpdatedAt(LocalDateTime.now());
        return true;
    }
    
    @Override
    public boolean unpublishDocument(Long documentId) {
        if (documentId == null) return false;
        
        publishedDocuments.remove(documentId);
        publishedDocumentCount = Math.max(0, publishedDocumentCount - 1);
        this.setUpdatedAt(LocalDateTime.now());
        return true;
    }
    
    @Override
    public boolean reviewDocument(Long documentId, boolean approved, String comments) {
        if (documentId == null) return false;
        
        reviewedDocumentCount++;
        pendingReviewDocuments.remove(documentId);
        
        if (approved) {
            publishDocument(documentId);
        }
        
        this.setUpdatedAt(LocalDateTime.now());
        return true;
    }
    
    @Override
    public Long[] getPendingReviewDocuments() {
        return pendingReviewDocuments.toArray(new Long[0]);
    }
    
    @Override
    public String[] getPublishableDocumentTypes() {
        // Super administrators can publish everything
        return new String[]{"research_paper", "policy_document", "announcement", 
                           "regulation", "directive", "report", "notice"};
    }
    
    @Override
    public int getPublishedDocumentCount() {
        return publishedDocumentCount;
    }
    
    @Override
    public int getReviewedDocumentCount() {
        return reviewedDocumentCount;
    }
    
    // ===== ADMINISTRABLE INTERFACE IMPLEMENTATION =====
    @Override
    public boolean createUser(String username, String email, String role) {
        if (username == null || email == null || role == null) return false;
        // Simulate user creation
        this.setUpdatedAt(LocalDateTime.now());
        return true;
    }
    
    @Override
    public boolean deleteUser(Long userId) {
        if (userId == null) return false;
        // Simulate user deletion
        this.setUpdatedAt(LocalDateTime.now());
        return true;
    }
    
    @Override
    public boolean changeUserRole(Long userId, String newRole) {
        if (userId == null || newRole == null) return false;
        // Simulate role change
        this.setUpdatedAt(LocalDateTime.now());
        return true;
    }
    
    @Override
    public String[] getSystemStatistics() {
        return new String[]{
            "Total Users: 1250",
            "Active Sessions: 89",
            "Documents: 3420",
            "System Uptime: 99.9%",
            "Storage Used: 75%"
        };
    }
    
    @Override
    public boolean backupSystemData() {
        // Simulate backup
        this.setUpdatedAt(LocalDateTime.now());
        return true;
    }
    
    @Override
    public int getAdministrativeLevel() {
        return 5; // Maximum clearance level
    }
    
    // Enhanced administrator profile with all capabilities
    @Override
    public String getAdministratorProfile() {
        return String.format("""
            %s
            === MULTIPLE INTERFACE CAPABILITIES ===
            Reading: %d documents read, can read %s
            Writing: %d documents created, can write %s  
            Publishing: %d published, %d reviewed, can publish %s
            Administration: Level %d clearance, can perform all admin actions
            """,
            super.getAdministratorProfile(),
            readCount,
            String.join(", ", getReadableDocumentTypes()),
            createdDocumentCount,
            String.join(", ", getWritableDocumentTypes()),
            publishedDocumentCount,
            reviewedDocumentCount,
            String.join(", ", getPublishableDocumentTypes()),
            getAdministrativeLevel()
        );
    }
    
    // Comprehensive method demonstrating multiple interface inheritance
    public String getMultipleInterfaceDemo() {
        return String.format("""
            === MULTIPLE INTERFACE INHERITANCE DEMONSTRATION ===
            Class: SuperAdministrator
            Extends: Administrator (class inheritance)
            Implements: Publishable, Administrable (multiple interface inheritance)
            
            Interface inheritance chain:
            1. Publishable extends Writable
            2. Writable extends Readable  
            3. Administrable (independent interface)
            
            === CAPABILITIES FROM READABLE ===
            - Read documents: %s types
            - Documents read: %d
            - Can read research papers: %s
            
            === CAPABILITIES FROM WRITABLE ===
            - Write documents: %s types
            - Documents created: %d
            - Can write policies: %s
            
            === CAPABILITIES FROM PUBLISHABLE ===
            - Publish documents: %s types
            - Documents published: %d
            - Documents reviewed: %d
            - Pending reviews: %d
            - Can publish announcements: %s
            
            === CAPABILITIES FROM ADMINISTRABLE ===
            - Administrative level: %d
            - Can create users: %s
            - Can delete users: %s
            - Can backup system: %s
            - Can perform emergency override: %s
            
            === CLASS INHERITANCE FROM ADMINISTRATOR ===
            - Employee ID: %s
            - Position: %s
            - Clearance Level: %d
            - Managed Departments: %d
            
            === CLASS INHERITANCE FROM BASEPERSON ===
            - Full Name: %s
            - Email: %s
            - Created: %s
            """,
            getReadableDocumentTypes().length,
            getReadCount(),
            canRead("research_paper"),
            getWritableDocumentTypes().length,
            getCreatedDocumentCount(),
            canWrite("policy_document"),
            getPublishableDocumentTypes().length,
            getPublishedDocumentCount(),
            getReviewedDocumentCount(),
            getPendingReviewDocuments().length,
            canPublish("announcement"),
            getAdministrativeLevel(),
            canPerformAdminAction("create_user"),
            canPerformAdminAction("delete_user"),
            canPerformAdminAction("backup_data"),
            canPerformAdminAction("emergency_override"),
            getEmployeeId(),
            getPosition(),
            getClearanceLevel(),
            getManagedDepartments().size(),
            getFullName(),
            getEmail(),
            getCreatedAt().toLocalDate()
        );
    }
    
    // Getters and setters for interface implementation fields
    public void setReadCount(int readCount) { this.readCount = readCount; }
    public void setCreatedDocumentCount(int createdDocumentCount) { this.createdDocumentCount = createdDocumentCount; }
    public void setPublishedDocumentCount(int publishedDocumentCount) { this.publishedDocumentCount = publishedDocumentCount; }
    public void setReviewedDocumentCount(int reviewedDocumentCount) { this.reviewedDocumentCount = reviewedDocumentCount; }
    
    public Map<Long, String> getReadingHistory() { return readingHistory; }
    public void setReadingHistory(Map<Long, String> readingHistory) { this.readingHistory = readingHistory; }
    
    public List<Long> getOwnedDocuments() { return ownedDocuments; }
    public void setOwnedDocuments(List<Long> ownedDocuments) { this.ownedDocuments = ownedDocuments; }
    
    public List<Long> getPendingReviewDocumentsList() { return pendingReviewDocuments; }
    public void setPendingReviewDocuments(List<Long> pendingReviewDocuments) { this.pendingReviewDocuments = pendingReviewDocuments; }
    
    public List<Long> getPublishedDocumentsList() { return publishedDocuments; }
    public void setPublishedDocuments(List<Long> publishedDocuments) { this.publishedDocuments = publishedDocuments; }
}