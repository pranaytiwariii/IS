package com.example.auth.entity.inheritance.strategy;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * Professor class using Single Table Inheritance
 * Extends Employee and stored in same table with discriminator
 */
@Entity
@DiscriminatorValue("PROFESSOR")
public class SingleTableProfessor extends SingleTableEmployee {
    
    @Size(max = 50)
    private String rank; // "Assistant Professor", "Associate Professor", "Full Professor"
    
    @Column(name = "is_tenured")
    private Boolean isTenured = false;
    
    @Size(max = 500)
    @Column(name = "research_area")
    private String researchArea;
    
    @ElementCollection
    @CollectionTable(name = "single_table_professor_publications", 
                     joinColumns = @JoinColumn(name = "professor_id"))
    @Column(name = "publication")
    private List<String> publications = new ArrayList<>();
    
    // Default constructor
    public SingleTableProfessor() {
        super();
    }
    
    // Constructor
    public SingleTableProfessor(String firstName, String lastName, String email, 
                               String employeeId, String department, Double salary,
                               String rank, String researchArea) {
        super(firstName, lastName, email, employeeId, department, salary);
        this.rank = rank;
        this.researchArea = researchArea;
    }
    
    public void publishPaper(String title) {
        publications.add(title);
    }
    
    public boolean isEligibleForTenure() {
        return getExperienceYears() >= 6 && publications.size() >= 5;
    }
    
    public void grantTenure() {
        if (isEligibleForTenure()) {
            this.isTenured = true;
        }
    }
    
    @Override
    public String getSingleTableInfo() {
        return String.format("""
            %s
            
            === PROFESSOR-SPECIFIC DATA ===
            Rank: %s
            Tenure Status: %s
            Research Area: %s
            Publications: %d
            Recent Papers: %s
            Tenure Eligibility: %s
            
            Single Table Notes:
            - Professor inherits from Employee (multi-level in single table)
            - All professor data stored in single_table_persons table
            - Discriminator person_type = 'PROFESSOR'
            - Unused columns (student_id, gpa) are NULL
            """,
            super.getSingleTableInfo(),
            rank,
            isTenured ? "Tenured" : "Not Tenured",
            researchArea != null ? researchArea : "Not specified",
            publications.size(),
            publications.isEmpty() ? "None" : 
                publications.subList(Math.max(0, publications.size() - 2), publications.size()).toString(),
            isEligibleForTenure() ? "Eligible" : "Not Eligible"
        );
    }
    
    // Getters and Setters
    public String getRank() { return rank; }
    public void setRank(String rank) { this.rank = rank; }
    
    public Boolean getIsTenured() { return isTenured; }
    public void setIsTenured(Boolean isTenured) { this.isTenured = isTenured; }
    
    public String getResearchArea() { return researchArea; }
    public void setResearchArea(String researchArea) { this.researchArea = researchArea; }
    
    public List<String> getPublications() { return publications; }
    public void setPublications(List<String> publications) { this.publications = publications; }
}