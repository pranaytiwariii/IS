package com.example.auth.entity.inheritance;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Professor class demonstrating Hierarchical Inheritance
 * Another class inheriting from BasePerson alongside Student
 */
@Entity
@Table(name = "professors")
@DiscriminatorValue("PROFESSOR")
public class Professor extends BasePerson {
    
    @Column(name = "employee_id", unique = true)
    private String employeeId;
    
    @Size(max = 100)
    private String department;
    
    @Size(max = 50)
    private String rank; // "Assistant Professor", "Associate Professor", "Full Professor"
    
    @Column(name = "hire_date")
    private LocalDateTime hireDate;
    
    @Column(name = "salary")
    private Double salary;
    
    @Column(name = "is_tenured")
    private Boolean isTenured = false;
    
    @Size(max = 500)
    private String researchArea;
    
    @ElementCollection
    @CollectionTable(name = "professor_courses", joinColumns = @JoinColumn(name = "professor_id"))
    @Column(name = "course_code")
    private Set<String> teachingCourses = new HashSet<>();
    
    @ElementCollection
    @CollectionTable(name = "professor_publications", joinColumns = @JoinColumn(name = "professor_id"))
    @Column(name = "publication_title")
    private List<String> publications = new ArrayList<>();
    
    // Default constructor
    public Professor() {
        super();
        this.hireDate = LocalDateTime.now();
    }
    
    // Constructor using parent constructor
    public Professor(String firstName, String lastName, String email, 
                    String employeeId, String department, String rank) {
        super(firstName, lastName, email); // Call BasePerson constructor
        this.employeeId = employeeId;
        this.department = department;
        this.rank = rank;
        this.hireDate = LocalDateTime.now();
    }
    
    // Override parent method with professor-specific info
    @Override
    public String getDisplayInfo() {
        return String.format("Prof. %s (%s) - %s, %s Department", 
                            getFullName(), // Inherited method
                            rank, 
                            department,
                            isTenured ? "Tenured" : "Non-Tenured");
    }
    
    // Professor-specific methods
    public void assignCourse(String courseCode) {
        teachingCourses.add(courseCode);
        this.setUpdatedAt(LocalDateTime.now()); // Use inherited method
    }
    
    public void removeCourse(String courseCode) {
        teachingCourses.remove(courseCode);
        this.setUpdatedAt(LocalDateTime.now());
    }
    
    public void publishPaper(String title) {
        publications.add(title);
        this.setUpdatedAt(LocalDateTime.now());
    }
    
    public boolean isEligibleForTenure() {
        if (hireDate == null) return false;
        
        // Simple rule: eligible after 6 years and 5+ publications
        long yearsEmployed = java.time.Duration.between(hireDate, LocalDateTime.now()).toDays() / 365;
        return yearsEmployed >= 6 && publications.size() >= 5;
    }
    
    public void grantTenure() {
        if (isEligibleForTenure()) {
            this.isTenured = true;
            this.setUpdatedAt(LocalDateTime.now());
        }
    }
    
    public String getTeachingLoad() {
        int courseCount = teachingCourses.size();
        if (courseCount == 0) return "No courses assigned";
        if (courseCount <= 2) return "Light load";
        if (courseCount <= 4) return "Normal load";
        return "Heavy load";
    }
    
    public double getExperienceYears() {
        if (hireDate == null) return 0;
        return java.time.Duration.between(hireDate, LocalDateTime.now()).toDays() / 365.0;
    }
    
    public String getProfessorProfile() {
        return String.format("""
            === PROFESSOR PROFILE ===
            %s
            Employee ID: %s | Department: %s | Rank: %s
            Hire Date: %s | Experience: %.1f years
            Tenure Status: %s | Salary: %s
            Research Area: %s
            Teaching Load: %s (%d courses)
            Courses: %s
            Publications: %d total
            Recent Publications: %s
            """,
            getDisplayInfo(), // Overridden method
            employeeId,
            department,
            rank,
            hireDate.toLocalDate(),
            getExperienceYears(),
            isTenured ? "Tenured" : (isEligibleForTenure() ? "Eligible for Tenure" : "Not Eligible"),
            salary != null ? String.format("$%.2f", salary) : "Not disclosed",
            researchArea != null ? researchArea : "Not specified",
            getTeachingLoad(),
            teachingCourses.size(),
            teachingCourses.isEmpty() ? "None" : String.join(", ", teachingCourses),
            publications.size(),
            publications.isEmpty() ? "None" : 
                publications.subList(Math.max(0, publications.size() - 3), publications.size())
                           .toString()
        );
    }
    
    // Getters and Setters
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public String getRank() { return rank; }
    public void setRank(String rank) { this.rank = rank; }
    
    public LocalDateTime getHireDate() { return hireDate; }
    public void setHireDate(LocalDateTime hireDate) { this.hireDate = hireDate; }
    
    public Double getSalary() { return salary; }
    public void setSalary(Double salary) { this.salary = salary; }
    
    public Boolean getIsTenured() { return isTenured; }
    public void setIsTenured(Boolean isTenured) { this.isTenured = isTenured; }
    
    public String getResearchArea() { return researchArea; }
    public void setResearchArea(String researchArea) { this.researchArea = researchArea; }
    
    public Set<String> getTeachingCourses() { return teachingCourses; }
    public void setTeachingCourses(Set<String> teachingCourses) { this.teachingCourses = teachingCourses; }
    
    public List<String> getPublications() { return publications; }
    public void setPublications(List<String> publications) { this.publications = publications; }
}