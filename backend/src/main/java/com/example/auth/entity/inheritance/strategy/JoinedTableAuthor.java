package com.example.auth.entity.inheritance.strategy;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Author class using Joined Table Inheritance
 * Has separate table joined to parent by foreign key
 */
@Entity
@Table(name = "joined_authors")
@PrimaryKeyJoinColumn(name = "person_id")
public class JoinedTableAuthor extends JoinedTablePerson {
    
    @NotNull
    @Size(max = 20)
    @Column(name = "author_id", unique = true)
    private String authorId;
    
    @NotNull
    @Size(max = 100)
    @Column(name = "affiliation")
    private String affiliation;
    
    @NotNull
    @Size(max = 50)
    @Column(name = "research_field")
    private String researchField;
    
    @Column(name = "h_index")
    private Integer hIndex = 0;
    
    @Column(name = "total_citations")
    private Integer totalCitations = 0;
    
    @Column(name = "first_publication_date")
    private LocalDate firstPublicationDate;
    
    @Column(name = "is_peer_reviewer")
    private Boolean isPeerReviewer = false;
    
    @Column(name = "total_reviews")
    private Integer totalReviews = 0;
    
    @ElementCollection
    @CollectionTable(name = "joined_author_publications", 
                     joinColumns = @JoinColumn(name = "author_id"))
    @Column(name = "publication_title")
    private List<String> publications = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "joined_author_research_areas", 
                     joinColumns = @JoinColumn(name = "author_id"))
    @Column(name = "research_area")
    private Set<String> researchAreas = new HashSet<>();
    
    @ElementCollection
    @CollectionTable(name = "joined_author_collaborators", 
                     joinColumns = @JoinColumn(name = "author_id"))
    @Column(name = "collaborator_name")
    private Set<String> collaborators = new HashSet<>();
    
    // Default constructor
    public JoinedTableAuthor() {
        super();
    }
    
    // Constructor
    public JoinedTableAuthor(String firstName, String lastName, String email, 
                            String authorId, String affiliation, String researchField) {
        super(firstName, lastName, email);
        this.authorId = authorId;
        this.affiliation = affiliation;
        this.researchField = researchField;
    }
    
    public void addPublication(String publicationTitle) {
        publications.add(publicationTitle);
        if (firstPublicationDate == null) {
            firstPublicationDate = LocalDate.now();
        }
        calculateHIndex();
    }
    
    public void removePublication(String publicationTitle) {
        publications.remove(publicationTitle);
        calculateHIndex();
    }
    
    public void addResearchArea(String area) {
        researchAreas.add(area);
    }
    
    public void removeResearchArea(String area) {
        researchAreas.remove(area);
    }
    
    public void addCollaborator(String collaboratorName) {
        collaborators.add(collaboratorName);
    }
    
    public void removeCollaborator(String collaboratorName) {
        collaborators.remove(collaboratorName);
    }
    
    public void updateCitations(int newCitations) {
        this.totalCitations += newCitations;
        calculateHIndex();
    }
    
    public void completePeerReview() {
        this.isPeerReviewer = true;
        this.totalReviews++;
    }
    
    private void calculateHIndex() {
        // Simplified h-index calculation
        int publicationCount = publications.size();
        if (publicationCount == 0) {
            this.hIndex = 0;
        } else {
            // Simple approximation: h-index based on publications and citations
            int avgCitationsPerPaper = totalCitations > 0 ? totalCitations / publicationCount : 0;
            this.hIndex = Math.min(publicationCount, avgCitationsPerPaper);
        }
    }
    
    public String getAcademicRank() {
        if (hIndex >= 50) return "Distinguished Scholar";
        if (hIndex >= 30) return "Full Professor";
        if (hIndex >= 15) return "Associate Professor";
        if (hIndex >= 5) return "Assistant Professor";
        return "Research Fellow";
    }
    
    public String getResearcherLevel() {
        if (firstPublicationDate == null) return "New Researcher";
        int yearsActive = LocalDate.now().getYear() - firstPublicationDate.getYear();
        if (yearsActive >= 20) return "Senior Scholar";
        if (yearsActive >= 15) return "Experienced Researcher";
        if (yearsActive >= 10) return "Established Researcher";
        if (yearsActive >= 5) return "Mid-career Researcher";
        return "Early Career Researcher";
    }
    
    public boolean isInfluentialAuthor() {
        return hIndex >= 10 && totalCitations >= 500 && publications.size() >= 20;
    }
    
    public boolean isActiveReviewer() {
        return isPeerReviewer && totalReviews >= 10;
    }
    
    public double getAverageCollaborationsPerPaper() {
        if (publications.isEmpty()) return 0.0;
        return (double) collaborators.size() / publications.size();
    }
    
    @Override
    public String getJoinedTableInfo() {
        return String.format("""
            %s
            
            === JOINED TABLE AUTHOR STRUCTURE ===
            Parent Table: joined_persons (id, first_name, last_name, email, phone_number, created_at)
            Child Table: joined_authors (person_id FK, author_id, affiliation, research_field, h_index, etc.)
            Relationship: 1:1 JOIN on joined_persons.id = joined_authors.person_id
            
            === AUTHOR-SPECIFIC DATA ===
            Author ID: %s (NOT NULL constraint enforced)
            Affiliation: %s (NOT NULL constraint enforced)
            Research Field: %s (NOT NULL constraint enforced)
            H-Index: %d | Academic Rank: %s
            Total Citations: %d
            Publications Count: %d
            First Publication: %s | Researcher Level: %s
            Peer Reviewer: %s | Total Reviews: %d
            Research Areas: %s
            Collaborators: %s (%d total)
            Avg Collaborations per Paper: %.2f
            Influential Author: %s
            Active Reviewer: %s
            
            Joined Table Benefits for Authors:
            ✅ Normalized structure - no nullable author fields in parent table
            ✅ NOT NULL constraints possible on all author-specific fields
            ✅ Clean separation of author data from other person types
            ✅ Easy to add/modify author-specific fields without affecting other tables
            
            Joined Table Challenges for Authors:
            ❌ Requires JOIN for complete author information
            ❌ More complex queries for author details
            ❌ Performance overhead for frequent author data access
            
            Query Examples:
            
            1. Get all author details:
            SELECT p.*, a.author_id, a.affiliation, a.research_field, a.h_index
            FROM joined_persons p
            JOIN joined_authors a ON p.id = a.person_id
            WHERE a.research_field = 'Computer Science';
            
            2. Authors with high h-index:
            SELECT p.first_name, p.last_name, a.h_index, a.total_citations
            FROM joined_persons p
            JOIN joined_authors a ON p.id = a.person_id
            WHERE a.h_index >= 20;
            """,
            super.getJoinedTableInfo(),
            authorId,
            affiliation,
            researchField,
            hIndex,
            getAcademicRank(),
            totalCitations,
            publications.size(),
            firstPublicationDate != null ? firstPublicationDate.toString() : "None",
            getResearcherLevel(),
            isPeerReviewer ? "Yes" : "No",
            totalReviews,
            researchAreas.isEmpty() ? "None" : String.join(", ", researchAreas),
            collaborators.isEmpty() ? "None" : String.join(", ", collaborators),
            collaborators.size(),
            getAverageCollaborationsPerPaper(),
            isInfluentialAuthor() ? "Yes" : "No",
            isActiveReviewer() ? "Yes" : "No"
        );
    }
    
    // Getters and Setters
    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }
    
    public String getAffiliation() { return affiliation; }
    public void setAffiliation(String affiliation) { this.affiliation = affiliation; }
    
    public String getResearchField() { return researchField; }
    public void setResearchField(String researchField) { this.researchField = researchField; }
    
    public Integer getHIndex() { return hIndex; }
    public void setHIndex(Integer hIndex) { this.hIndex = hIndex; }
    
    public Integer getTotalCitations() { return totalCitations; }
    public void setTotalCitations(Integer totalCitations) { this.totalCitations = totalCitations; }
    
    public LocalDate getFirstPublicationDate() { return firstPublicationDate; }
    public void setFirstPublicationDate(LocalDate firstPublicationDate) { this.firstPublicationDate = firstPublicationDate; }
    
    public Boolean getIsPeerReviewer() { return isPeerReviewer; }
    public void setIsPeerReviewer(Boolean isPeerReviewer) { this.isPeerReviewer = isPeerReviewer; }
    
    public Integer getTotalReviews() { return totalReviews; }
    public void setTotalReviews(Integer totalReviews) { this.totalReviews = totalReviews; }
    
    public List<String> getPublications() { return publications; }
    public void setPublications(List<String> publications) { this.publications = publications; }
    
    public Set<String> getResearchAreas() { return researchAreas; }
    public void setResearchAreas(Set<String> researchAreas) { this.researchAreas = researchAreas; }
    
    public Set<String> getCollaborators() { return collaborators; }
    public void setCollaborators(Set<String> collaborators) { this.collaborators = collaborators; }
}