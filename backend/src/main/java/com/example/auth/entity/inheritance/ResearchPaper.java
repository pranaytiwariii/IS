package com.example.auth.entity.inheritance;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Research Paper concrete class extending AbstractDocument
 * Demonstrates concrete implementation of abstract class
 */
@Entity
@Table(name = "research_papers")
@DiscriminatorValue("RESEARCH_PAPER")
public class ResearchPaper extends AbstractDocument {
    
    @Size(max = 1000)
    @Column(name = "abstract_text")
    private String abstractText;
    
    @Size(max = 100)
    private String journal;
    
    @Size(max = 50)
    private String doi; // Digital Object Identifier
    
    @ElementCollection
    @CollectionTable(name = "paper_references", joinColumns = @JoinColumn(name = "paper_id"))
    @Column(name = "reference")
    private List<String> references = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "paper_coauthors", joinColumns = @JoinColumn(name = "paper_id"))
    @Column(name = "coauthor_name")
    private List<String> coAuthors = new ArrayList<>();
    
    @Column(name = "peer_reviewed")
    private Boolean peerReviewed = false;
    
    @Column(name = "citation_count")
    private Integer citationCount = 0;
    
    @Size(max = 50)
    private String researchField;
    
    // Default constructor
    public ResearchPaper() {
        super();
    }
    
    // Constructor
    public ResearchPaper(String title, String content, String authorName, Long authorId, 
                        String abstractText, String researchField) {
        super(title, content, authorName, authorId); // Call parent constructor
        this.abstractText = abstractText;
        this.researchField = researchField;
    }
    
    // Implementation of abstract methods from AbstractDocument
    
    @Override
    public String getDocumentType() {
        return "Research Paper";
    }
    
    @Override
    public boolean validateContent() {
        // Research papers have specific validation requirements
        if (!isContentValid()) return false; // Use inherited method
        
        // Additional validation for research papers
        return abstractText != null && !abstractText.trim().isEmpty() &&
               getContentWordCount() >= 1000 && // Minimum 1000 words
               references.size() >= 5 && // At least 5 references
               researchField != null && !researchField.trim().isEmpty();
    }
    
    @Override
    public String formatForDisplay() {
        return String.format("""
            === RESEARCH PAPER FORMATTING ===
            Abstract: %s
            Research Field: %s
            Journal: %s
            DOI: %s
            Peer Reviewed: %s
            Citations: %d
            Co-authors: %s
            References: %d total
            Word Count: %d
            """,
            abstractText != null ? (abstractText.length() > 100 ? 
                abstractText.substring(0, 100) + "..." : abstractText) : "Not provided",
            researchField != null ? researchField : "Not specified",
            journal != null ? journal : "Not published",
            doi != null ? doi : "Not assigned",
            peerReviewed ? "Yes" : "No",
            citationCount,
            coAuthors.isEmpty() ? "None" : String.join(", ", coAuthors),
            references.size(),
            getContentWordCount() // Inherited method
        );
    }
    
    @Override
    public String getCategory() {
        return "Academic Research";
    }
    
    // Research paper specific methods
    
    public void addReference(String reference) {
        if (reference != null && !reference.trim().isEmpty()) {
            references.add(reference);
            this.setUpdatedAt(LocalDateTime.now()); // Use inherited setter
        }
    }
    
    public void addCoAuthor(String coAuthor) {
        if (coAuthor != null && !coAuthor.trim().isEmpty()) {
            coAuthors.add(coAuthor);
            this.setUpdatedAt(LocalDateTime.now());
        }
    }
    
    public void submitForPeerReview() {
        if (validateContent()) {
            // Simulate submission process
            this.setUpdatedAt(LocalDateTime.now());
        }
    }
    
    public void markAsPeerReviewed() {
        this.peerReviewed = true;
        this.setUpdatedAt(LocalDateTime.now());
    }
    
    public void addCitation() {
        this.citationCount++;
        // Don't update updatedAt for citation tracking
    }
    
    public void publishInJournal(String journalName, String doiNumber) {
        this.journal = journalName;
        this.doi = doiNumber;
        this.publish(); // Use inherited method
    }
    
    public String getResearchPaperProfile() {
        return String.format("""
            === RESEARCH PAPER PROFILE ===
            %s
            
            === RESEARCH-SPECIFIC DETAILS ===
            Abstract Length: %d characters
            Research Field: %s
            Publication Status: %s
            Peer Review Status: %s
            Academic Impact: %d citations
            Collaboration: %d co-authors
            Reference Quality: %d references
            Academic Validation: %s
            """,
            getCompleteInfo(), // Use inherited template method
            abstractText != null ? abstractText.length() : 0,
            researchField,
            journal != null ? "Published in " + journal : "Not published",
            peerReviewed ? "Peer Reviewed" : "Not peer reviewed",
            citationCount,
            coAuthors.size(),
            references.size(),
            validateContent() ? "Valid" : "Needs revision"
        );
    }
    
    // Method demonstrating abstract class inheritance
    public String getAbstractInheritanceDemo() {
        return String.format("""
            === ABSTRACT CLASS INHERITANCE DEMONSTRATION ===
            Concrete Class: ResearchPaper
            Extends: AbstractDocument (abstract class)
            
            === IMPLEMENTED ABSTRACT METHODS ===
            getDocumentType(): "%s"
            validateContent(): %s (research-specific validation)
            formatForDisplay(): Custom research paper formatting
            getCategory(): "%s"
            
            === INHERITED CONCRETE METHODS ===
            getCompleteInfo(): %s (template method pattern)
            publish(): %s
            addKeyword(): Available
            getStatus(): "%s"
            getContentWordCount(): %d words
            getSummary(): Available
            
            === INHERITED PROPERTIES ===
            ID: %d
            Title: %s
            Author: %s (ID: %d)
            Created: %s
            Published: %s
            Keywords: %s
            Views: %d
            
            === RESEARCH PAPER SPECIFIC PROPERTIES ===
            Abstract: %s
            Research Field: %s
            Journal: %s
            DOI: %s
            Peer Reviewed: %s
            Citations: %d
            Co-authors: %d
            References: %d
            """,
            getDocumentType(),
            validateContent(),
            getCategory(),
            "Uses template method pattern",
            getIsPublished() ? "Published" : "Can publish",
            getStatus(),
            getContentWordCount(),
            getId() != null ? getId() : 0,
            getTitle() != null ? getTitle() : "Not set",
            getAuthorName() != null ? getAuthorName() : "Not set",
            getAuthorId() != null ? getAuthorId() : 0,
            getCreatedAt() != null ? getCreatedAt().toLocalDate().toString() : "Not set",
            getIsPublished() ? getPublishedAt().toLocalDate().toString() : "Not published",
            getKeywords().isEmpty() ? "None" : String.join(", ", getKeywords()),
            getViewCount(),
            abstractText != null ? "Available (" + abstractText.length() + " chars)" : "Not provided",
            researchField,
            journal != null ? journal : "Not published",
            doi != null ? doi : "Not assigned",
            peerReviewed,
            citationCount,
            coAuthors.size(),
            references.size()
        );
    }
    
    // Getters and Setters
    public String getAbstractText() { return abstractText; }
    public void setAbstractText(String abstractText) { 
        this.abstractText = abstractText;
        this.setUpdatedAt(LocalDateTime.now());
    }
    
    public String getJournal() { return journal; }
    public void setJournal(String journal) { this.journal = journal; }
    
    public String getDoi() { return doi; }
    public void setDoi(String doi) { this.doi = doi; }
    
    public List<String> getReferences() { return references; }
    public void setReferences(List<String> references) { this.references = references; }
    
    public List<String> getCoAuthors() { return coAuthors; }
    public void setCoAuthors(List<String> coAuthors) { this.coAuthors = coAuthors; }
    
    public Boolean getPeerReviewed() { return peerReviewed; }
    public void setPeerReviewed(Boolean peerReviewed) { this.peerReviewed = peerReviewed; }
    
    public Integer getCitationCount() { return citationCount; }
    public void setCitationCount(Integer citationCount) { this.citationCount = citationCount; }
    
    public String getResearchField() { return researchField; }
    public void setResearchField(String researchField) { this.researchField = researchField; }
}