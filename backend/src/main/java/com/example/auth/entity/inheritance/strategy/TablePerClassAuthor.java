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
 * Author class using Table Per Class Inheritance
 * Has its own COMPLETE table with ALL parent fields + author fields
 */
@Entity
@Table(name = "table_per_class_authors")
public class TablePerClassAuthor extends TablePerClassPerson {
    
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
    
    @Column(name = "impact_factor")
    private Double impactFactor = 0.0;
    
    @Column(name = "total_conferences")
    private Integer totalConferences = 0;
    
    @ElementCollection
    @CollectionTable(name = "table_per_class_author_publications", 
                     joinColumns = @JoinColumn(name = "author_id"))
    @Column(name = "publication_title")
    private List<String> publications = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "table_per_class_author_research_areas", 
                     joinColumns = @JoinColumn(name = "author_id"))
    @Column(name = "research_area")
    private Set<String> researchAreas = new HashSet<>();
    
    @ElementCollection
    @CollectionTable(name = "table_per_class_author_collaborators", 
                     joinColumns = @JoinColumn(name = "author_id"))
    @Column(name = "collaborator_name")
    private Set<String> collaborators = new HashSet<>();
    
    @ElementCollection
    @CollectionTable(name = "table_per_class_author_journals", 
                     joinColumns = @JoinColumn(name = "author_id"))
    @Column(name = "journal_name")
    private Set<String> publishedJournals = new HashSet<>();
    
    // Default constructor
    public TablePerClassAuthor() {
        super();
    }
    
    // Constructor
    public TablePerClassAuthor(String firstName, String lastName, String email, 
                              String authorId, String affiliation, String researchField) {
        super(firstName, lastName, email);
        this.authorId = authorId;
        this.affiliation = affiliation;
        this.researchField = researchField;
    }
    
    public void addPublication(String publicationTitle, String journalName) {
        publications.add(publicationTitle);
        if (journalName != null) {
            publishedJournals.add(journalName);
        }
        if (firstPublicationDate == null) {
            firstPublicationDate = LocalDate.now();
        }
        calculateMetrics();
    }
    
    public void removePublication(String publicationTitle) {
        publications.remove(publicationTitle);
        calculateMetrics();
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
    
    public void attendConference(String conferenceName) {
        totalConferences++;
    }
    
    public void updateCitations(int newCitations) {
        this.totalCitations += newCitations;
        calculateMetrics();
    }
    
    public void completePeerReview() {
        this.isPeerReviewer = true;
        this.totalReviews++;
    }
    
    private void calculateMetrics() {
        // Calculate h-index
        int publicationCount = publications.size();
        if (publicationCount == 0) {
            this.hIndex = 0;
            this.impactFactor = 0.0;
        } else {
            // Simplified h-index calculation
            int avgCitationsPerPaper = totalCitations > 0 ? totalCitations / publicationCount : 0;
            this.hIndex = Math.min(publicationCount, avgCitationsPerPaper);
            
            // Simplified impact factor calculation
            this.impactFactor = (double) totalCitations / publicationCount;
        }
    }
    
    public String getAcademicRank() {
        if (hIndex >= 60) return "World-Renowned Scholar";
        if (hIndex >= 40) return "Distinguished Professor";
        if (hIndex >= 25) return "Full Professor";
        if (hIndex >= 15) return "Associate Professor";
        if (hIndex >= 5) return "Assistant Professor";
        return "Research Fellow";
    }
    
    public String getResearcherLevel() {
        if (firstPublicationDate == null) return "New Researcher";
        int yearsActive = LocalDate.now().getYear() - firstPublicationDate.getYear();
        if (yearsActive >= 25) return "Emeritus Scholar";
        if (yearsActive >= 20) return "Senior Scholar";
        if (yearsActive >= 15) return "Experienced Researcher";
        if (yearsActive >= 10) return "Established Researcher";
        if (yearsActive >= 5) return "Mid-career Researcher";
        return "Early Career Researcher";
    }
    
    public String getProductivityLevel() {
        double pubsPerYear = getPublicationsPerYear();
        if (pubsPerYear >= 5) return "Highly Productive";
        if (pubsPerYear >= 3) return "Very Productive";
        if (pubsPerYear >= 2) return "Productive";
        if (pubsPerYear >= 1) return "Moderately Productive";
        return "Low Productivity";
    }
    
    public double getPublicationsPerYear() {
        if (firstPublicationDate == null || publications.isEmpty()) return 0.0;
        int yearsActive = Math.max(1, LocalDate.now().getYear() - firstPublicationDate.getYear());
        return (double) publications.size() / yearsActive;
    }
    
    public boolean isInfluentialAuthor() {
        return hIndex >= 15 && totalCitations >= 1000 && publications.size() >= 30;
    }
    
    public boolean isActiveReviewer() {
        return isPeerReviewer && totalReviews >= 15;
    }
    
    public boolean isConferenceActive() {
        return totalConferences >= 10;
    }
    
    public double getAverageCollaborationsPerPaper() {
        if (publications.isEmpty()) return 0.0;
        return (double) collaborators.size() / publications.size();
    }
    
    public double getJournalDiversity() {
        if (publications.isEmpty()) return 0.0;
        return (double) publishedJournals.size() / publications.size();
    }
    
    @Override
    public String getTablePerClassInfo() {
        return String.format("""
            %s
            
            === AUTHOR TABLE STRUCTURE ===
            Table: table_per_class_authors
            Contains: ALL parent fields + author fields
            
            Parent Fields (duplicated from TablePerClassPerson):
            - id, first_name, last_name, email, phone_number, created_at
            
            Author Fields:
            - author_id, affiliation, research_field, h_index, total_citations, first_publication_date,
              is_peer_reviewer, total_reviews, impact_factor, total_conferences
            
            === AUTHOR-SPECIFIC DATA ===
            Author ID: %s (NOT NULL constraint possible)
            Affiliation: %s (NOT NULL constraint possible)
            Research Field: %s (NOT NULL constraint possible)
            H-Index: %d | Academic Rank: %s
            Total Citations: %d | Impact Factor: %.2f
            Publications Count: %d | Publications per Year: %.2f
            Productivity Level: %s
            First Publication: %s | Researcher Level: %s
            Peer Reviewer: %s | Total Reviews: %d | Active Reviewer: %s
            Conference Presentations: %d | Conference Active: %s
            Research Areas: %s
            Collaborators: %s (%d total)
            Published Journals: %s (%d total)
            Avg Collaborations per Paper: %.2f
            Journal Diversity Score: %.2f
            Influential Author: %s
            
            Table Per Class Benefits for Authors:
            ✅ Complete author data in single table (no JOINs)
            ✅ NOT NULL constraints on all author fields
            ✅ Fast queries for author-specific operations
            ✅ Independent table optimization for author workloads
            ✅ Easy to add author-specific indexes and constraints
            
            Table Per Class Drawbacks for Authors:
            ❌ Parent fields duplicated (id, first_name, last_name, email repeated)
            ❌ Schema changes require updating multiple person tables
            ❌ Polymorphic queries require UNION operations
            ❌ Cross-person-type reporting is complex
            
            Query Pattern Examples:
            
            1. Simple author query (FAST):
            SELECT * FROM table_per_class_authors 
            WHERE research_field = 'Computer Science' AND h_index >= 20;
            
            2. Polymorphic query (COMPLEX):
            SELECT id, first_name, last_name, email, 'AUTHOR' as type, h_index as metric
            FROM table_per_class_authors
            UNION ALL
            SELECT id, first_name, last_name, email, 'STUDENT' as type, gpa as metric
            FROM table_per_class_students
            UNION ALL
            SELECT id, first_name, last_name, email, 'ADMIN' as type, login_count as metric
            FROM table_per_class_admins;
            
            3. Author-specific analysis (OPTIMIZED):
            SELECT affiliation, AVG(h_index) as avg_h_index, COUNT(*) as author_count
            FROM table_per_class_authors
            WHERE total_citations >= 100
            GROUP BY affiliation
            ORDER BY avg_h_index DESC;
            """,
            super.getTablePerClassInfo(),
            authorId,
            affiliation,
            researchField,
            hIndex,
            getAcademicRank(),
            totalCitations,
            impactFactor,
            publications.size(),
            getPublicationsPerYear(),
            getProductivityLevel(),
            firstPublicationDate != null ? firstPublicationDate.toString() : "None",
            getResearcherLevel(),
            isPeerReviewer ? "Yes" : "No",
            totalReviews,
            isActiveReviewer() ? "Yes" : "No",
            totalConferences,
            isConferenceActive() ? "Yes" : "No",
            researchAreas.isEmpty() ? "None" : String.join(", ", researchAreas),
            collaborators.isEmpty() ? "None" : String.join(", ", collaborators),
            collaborators.size(),
            publishedJournals.isEmpty() ? "None" : String.join(", ", publishedJournals),
            publishedJournals.size(),
            getAverageCollaborationsPerPaper(),
            getJournalDiversity(),
            isInfluentialAuthor() ? "Yes" : "No"
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
    
    public Double getImpactFactor() { return impactFactor; }
    public void setImpactFactor(Double impactFactor) { this.impactFactor = impactFactor; }
    
    public Integer getTotalConferences() { return totalConferences; }
    public void setTotalConferences(Integer totalConferences) { this.totalConferences = totalConferences; }
    
    public List<String> getPublications() { return publications; }
    public void setPublications(List<String> publications) { this.publications = publications; }
    
    public Set<String> getResearchAreas() { return researchAreas; }
    public void setResearchAreas(Set<String> researchAreas) { this.researchAreas = researchAreas; }
    
    public Set<String> getCollaborators() { return collaborators; }
    public void setCollaborators(Set<String> collaborators) { this.collaborators = collaborators; }
    
    public Set<String> getPublishedJournals() { return publishedJournals; }
    public void setPublishedJournals(Set<String> publishedJournals) { this.publishedJournals = publishedJournals; }
}