package com.example.auth.entity.inheritance.strategy;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Professor class using Joined Table Inheritance
 * Has its own table joined to employee table (which joins to person table)
 * Demonstrates multi-level joined inheritance
 */
@Entity
@Table(name = "joined_table_professors")
@PrimaryKeyJoinColumn(name = "id")
public class JoinedTableProfessor extends JoinedTableEmployee {
    
    @NotNull
    @Size(max = 50)
    private String rank; // "Assistant Professor", "Associate Professor", "Full Professor"
    
    @Column(name = "is_tenured")
    private Boolean isTenured = false;
    
    @Size(max = 500)
    @Column(name = "research_area")
    private String researchArea;
    
    @Column(name = "office_number")
    private String officeNumber;
    
    @ElementCollection
    @CollectionTable(name = "joined_professor_publications", 
                     joinColumns = @JoinColumn(name = "professor_id"))
    @Column(name = "publication")
    private List<String> publications = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "joined_professor_courses", 
                     joinColumns = @JoinColumn(name = "professor_id"))
    @Column(name = "course_code")
    private Set<String> teachingCourses = new HashSet<>();
    
    // Default constructor
    public JoinedTableProfessor() {
        super();
    }
    
    // Constructor
    public JoinedTableProfessor(String firstName, String lastName, String email, 
                               String employeeId, String department, String rank, String researchArea) {
        super(firstName, lastName, email, employeeId, department, "Professor");
        this.rank = rank;
        this.researchArea = researchArea;
    }
    
    public void publishPaper(String title) {
        publications.add(title);
    }
    
    public void assignCourse(String courseCode) {
        teachingCourses.add(courseCode);
    }
    
    public void removeCourse(String courseCode) {
        teachingCourses.remove(courseCode);
    }
    
    public boolean isEligibleForTenure() {
        return getExperienceYears() >= 6 && publications.size() >= 5;
    }
    
    public void grantTenure() {
        if (isEligibleForTenure()) {
            this.isTenured = true;
        }
    }
    
    public String getTeachingLoad() {
        int courseCount = teachingCourses.size();
        if (courseCount == 0) return "No courses assigned";
        if (courseCount <= 2) return "Light load";
        if (courseCount <= 4) return "Normal load";
        return "Heavy load";
    }
    
    @Override
    public String getJoinedTableInfo() {
        return String.format("""
            %s
            
            === PROFESSOR-SPECIFIC DATA (from joined_table_professors) ===
            Rank: %s (NOT NULL constraint enforced)
            Tenure Status: %s
            Research Area: %s
            Office: %s
            Publications: %d
            Teaching Load: %s (%d courses)
            Recent Papers: %s
            Tenure Eligibility: %s
            
            Multi-Level Joined Tables:
            📋 joined_table_persons (base person data)
                ↓ JOIN ON id
            👔 joined_table_employees (employee data)  
                ↓ JOIN ON id
            🎓 joined_table_professors (professor data)
            
            Complex Query Pattern:
            SELECT p.first_name, p.last_name, e.employee_id, e.department, 
                   pr.rank, pr.research_area, pr.is_tenured
            FROM joined_table_persons p 
            JOIN joined_table_employees e ON p.id = e.id
            JOIN joined_table_professors pr ON e.id = pr.id
            WHERE pr.is_tenured = true
            """,
            super.getJoinedTableInfo(),
            rank,
            isTenured ? "Tenured" : "Not Tenured",
            researchArea != null ? researchArea : "Not specified",
            officeNumber != null ? officeNumber : "Not assigned",
            publications.size(),
            getTeachingLoad(),
            teachingCourses.size(),
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
    
    public String getOfficeNumber() { return officeNumber; }
    public void setOfficeNumber(String officeNumber) { this.officeNumber = officeNumber; }
    
    public List<String> getPublications() { return publications; }
    public void setPublications(List<String> publications) { this.publications = publications; }
    
    public Set<String> getTeachingCourses() { return teachingCourses; }
    public void setTeachingCourses(Set<String> teachingCourses) { this.teachingCourses = teachingCourses; }
}