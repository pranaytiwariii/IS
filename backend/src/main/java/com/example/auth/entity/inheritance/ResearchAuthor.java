package com.example.auth.entity.inheritance;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Research Author - Third level in multi-level inheritance hierarchy
 * BasePerson -> EnhancedUser -> AcademicUser -> ResearchAuthor
 * This demonstrates the deepest level of inheritance with specialized functionality
 */
@Entity
@Table(name = "research_authors")
@DiscriminatorValue("RESEARCH_AUTHOR")
public class ResearchAuthor extends AcademicUser {
    
    @Column(name = "author_id", unique = true)
    private String authorId; // Unique research identifier
    
    @Size(max = 20)
    private String specialization; // "AI", "Biology", "Physics", etc.
    
    @Column(name = "publications_count")
    private Integer publicationsCount = 0;
    
    @Column(name = "h_index")
    private Integer hIndex = 0;
    
    @Column(name = "is_peer_reviewer")
    private Boolean isPeerReviewer = false;
    
    @ElementCollection
    @CollectionTable(name = "author_publications", joinColumns = @JoinColumn(name = "author_id"))
    @Column(name = "publication_title")
    private List<String> publications = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "author_awards", joinColumns = @JoinColumn(name = "author_id"))
    @Column(name = "award_name")
    private List<String> awards = new ArrayList<>();
    
    // Default constructor
    public ResearchAuthor() {
        super();
    }
    
    // Constructor using all parent constructors in chain
    public ResearchAuthor(String firstName, String lastName, String email, String username, 
                         String password, String institution, String academicLevel, 
                         String specialization, String authorId) {
        // This calls: AcademicUser -> EnhancedUser -> BasePerson constructors
        super(firstName, lastName, email, username, password, institution, academicLevel);
        this.specialization = specialization;
        this.authorId = authorId;
    }
    
    // Override display info through all inheritance levels
    @Override
    public String getDisplayInfo() {
        String academicInfo = super.getDisplayInfo(); // Calls AcademicUser's version
        return String.format("%s | Research Author (%s) - %d publications", 
                            academicInfo, specialization, publicationsCount);
    }
    
    // Research-specific methods building on inherited functionality
    public void publishPaper(String title) {
        publications.add(title);
        publicationsCount++;
        updateResearchMetrics();
        this.setUpdatedAt(LocalDateTime.now()); // Inherited from BasePerson
    }
    
    public void receiveAward(String awardName) {
        awards.add(awardName);
        this.addResearchInterest("Award Winner"); // Using AcademicUser method
        this.setUpdatedAt(LocalDateTime.now()); // Inherited from BasePerson
    }
    
    private void updateResearchMetrics() {
        // Simple h-index calculation (in real world, this would be more complex)
        this.hIndex = Math.min(publicationsCount, publicationsCount / 2 + 1);
        
        // Automatically become peer reviewer if experienced enough
        if (publicationsCount >= 5 && !isPeerReviewer) {
            this.isPeerReviewer = true;
        }
    }
    
    // Enhanced validation using all inherited methods
    @Override
    public boolean canLogin() {
        // Research authors need additional validation
        return super.canLogin() && // AcademicUser validation
               authorId != null && 
               !authorId.isEmpty() &&
               specialization != null;
    }
    
    // Complex profile using methods from all inheritance levels
    public String getCompleteResearchProfile() {
        return String.format("""
            === COMPLETE RESEARCH PROFILE ===
            Personal: %s (ID: %d)
            Contact: %s | Phone: %s
            Academic: %s
            Research: Author ID: %s | Specialization: %s
            Metrics: %d Publications | H-Index: %d | Peer Reviewer: %s
            Publications: %s
            Awards: %s
            Research Interests: %s
            Status: %s | Last Login: %s
            """, 
            getFullName(), // From BasePerson
            getId(), // From BasePerson
            getEmail(), // From BasePerson
            getPhoneNumber() != null ? getPhoneNumber() : "Not provided", // From BasePerson
            getAcademicStatus(), // From AcademicUser
            authorId,
            specialization,
            publicationsCount,
            hIndex,
            isPeerReviewer,
            publications.isEmpty() ? "None" : String.join(", ", publications),
            awards.isEmpty() ? "None" : String.join(", ", awards),
            getResearchInterests().isEmpty() ? "None" : String.join(", ", getResearchInterests()), // From AcademicUser
            getIsActive() ? "Active" : "Inactive", // From EnhancedUser
            getLastLogin() != null ? getLastLogin().toString() : "Never" // From EnhancedUser
        );
    }
    
    // Method showing inheritance chain usage
    public String getInheritanceChainDemo() {
        return String.format("""
            === INHERITANCE CHAIN DEMONSTRATION ===
            Level 1 (BasePerson): %s - %s
            Level 2 (EnhancedUser): Username: %s, Active: %s
            Level 3 (AcademicUser): %s
            Level 4 (ResearchAuthor): Author ID: %s, Specialization: %s
            """,
            getFullName(), // Level 1 method
            isEmailValid(), // Level 1 method (may be overridden)
            getUsername(), // Level 2 property
            getIsActive(), // Level 2 property
            getAcademicStatus(), // Level 3 method
            authorId, // Level 4 property
            specialization // Level 4 property
        );
    }
    
    // Getters and Setters for new properties
    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }
    
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    
    public Integer getPublicationsCount() { return publicationsCount; }
    public void setPublicationsCount(Integer publicationsCount) { this.publicationsCount = publicationsCount; }
    
    public Integer getHIndex() { return hIndex; }
    public void setHIndex(Integer hIndex) { this.hIndex = hIndex; }
    
    public Boolean getIsPeerReviewer() { return isPeerReviewer; }
    public void setIsPeerReviewer(Boolean isPeerReviewer) { this.isPeerReviewer = isPeerReviewer; }
    
    public List<String> getPublications() { return publications; }
    public void setPublications(List<String> publications) { this.publications = publications; }
    
    public List<String> getAwards() { return awards; }
    public void setAwards(List<String> awards) { this.awards = awards; }
}