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
 * Author class using Single Table Inheritance
 * Stored in same table as parent with discriminator
 */
@Entity
@DiscriminatorValue("AUTHOR")
public class SingleTableAuthor extends SingleTablePerson {
    
    @Size(max = 20)
    @Column(name = "author_id")
    private String authorId;
    
    @Size(max = 100)
    @Column(name = "affiliation")
    private String affiliation;
    
    @Size(max = 50)
    @Column(name = "research_field")
    private String researchField;
    
    @Column(name = "h_index")
    private Integer hIndex = 0;
    
    @Column(name = "total_citations")
    private Integer totalCitations = 0;
    
    @Column(name = "first_publication_date")
    private LocalDate firstPublicationDate;
    
    @ElementCollection
    @CollectionTable(name = "single_table_author_publications", 
                     joinColumns = @JoinColumn(name = "person_id"))
    @Column(name = "publication_title")
    private List<String> publications = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "single_table_author_research_areas", 
                     joinColumns = @JoinColumn(name = "person_id"))
    @Column(name = "research_area")
    private Set<String> researchAreas = new HashSet<>();
    
    // Default constructor
    public SingleTableAuthor() {
        super();
    }
    
    // Constructor
    public SingleTableAuthor(String firstName, String lastName, String email, 
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
    }
    
    public void removePublication(String publicationTitle) {
        publications.remove(publicationTitle);
    }
    
    public void addResearchArea(String area) {
        researchAreas.add(area);
    }
    
    public void removeResearchArea(String area) {
        researchAreas.remove(area);
    }
    
    public void updateCitations(int newCitations) {
        this.totalCitations += newCitations;
        // Simple h-index calculation (simplified)
        calculateHIndex();
    }
    
    private void calculateHIndex() {
        // Simplified h-index calculation
        int publicationCount = publications.size();
        if (publicationCount == 0) {
            this.hIndex = 0;
        } else {
            // Simple approximation: assume equal citations per paper
            int avgCitationsPerPaper = totalCitations / publicationCount;
            this.hIndex = Math.min(publicationCount, avgCitationsPerPaper);
        }
    }
    
    public String getAcademicRank() {
        if (hIndex >= 40) return "Distinguished Professor";
        if (hIndex >= 20) return "Full Professor";
        if (hIndex >= 10) return "Associate Professor";
        if (hIndex >= 5) return "Assistant Professor";
        return "Research Fellow";
    }
    
    public String getExperienceLevel() {
        if (firstPublicationDate == null) return "New Researcher";
        int yearsActive = LocalDate.now().getYear() - firstPublicationDate.getYear();
        if (yearsActive >= 15) return "Senior Researcher";
        if (yearsActive >= 10) return "Experienced Researcher";
        if (yearsActive >= 5) return "Mid-level Researcher";
        return "Junior Researcher";
    }
    
    public boolean isProductiveAuthor() {
        return publications.size() >= 10 && totalCitations >= 100;
    }
    
    @Override
    public String getSingleTableInfo() {
        return String.format("""
            %s
            
            === SINGLE TABLE AUTHOR DETAILS ===
            Discriminator: AUTHOR (person_type = 'AUTHOR')
            Author-specific fields in same table (nullable):
            - author_id, affiliation, research_field, h_index, total_citations, first_publication_date
            
            === AUTHOR DATA ===
            Author ID: %s
            Affiliation: %s
            Research Field: %s
            H-Index: %d | Academic Rank: %s
            Total Citations: %d
            Publications Count: %d
            Research Areas: %s
            Experience Level: %s
            Productive Author: %s
            First Publication: %s
            
            Single Table Benefits for Authors:
            ✅ Fast queries - no JOINs needed
            ✅ Simple polymorphic queries across all person types
            ✅ Easy to add/modify author fields
            
            Single Table Challenges for Authors:
            ❌ Author fields must be nullable (other person types don't use them)
            ❌ Table grows wide with each person type
            ❌ Potential storage waste for non-author persons
            
            Example Query:
            SELECT * FROM single_table_persons 
            WHERE person_type = 'AUTHOR' AND research_field = 'Computer Science'
            """,
            super.getSingleTableInfo(),
            authorId != null ? authorId : "Not assigned",
            affiliation != null ? affiliation : "Not specified",
            researchField != null ? researchField : "Not specified",
            hIndex,
            getAcademicRank(),
            totalCitations,
            publications.size(),
            researchAreas.isEmpty() ? "None" : String.join(", ", researchAreas),
            getExperienceLevel(),
            isProductiveAuthor() ? "Yes" : "No",
            firstPublicationDate != null ? firstPublicationDate.toString() : "None"
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
    
    public List<String> getPublications() { return publications; }
    public void setPublications(List<String> publications) { this.publications = publications; }
    
    public Set<String> getResearchAreas() { return researchAreas; }
    public void setResearchAreas(Set<String> researchAreas) { this.researchAreas = researchAreas; }
}