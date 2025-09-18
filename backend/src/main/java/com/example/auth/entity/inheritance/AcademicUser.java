package com.example.auth.entity.inheritance;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Academic User - Second level in multi-level inheritance hierarchy
 * BasePerson -> EnhancedUser -> AcademicUser
 */
@Entity
@Table(name = "academic_users")
@DiscriminatorValue("ACADEMIC")
public class AcademicUser extends EnhancedUser {
    
    @Size(max = 100)
    private String institution;
    
    @Size(max = 50)
    private String department;
    
    @Size(max = 20)
    private String academicLevel; // "Bachelor", "Master", "PhD", "Professor"
    
    @Column(name = "student_id")
    private String studentId;
    
    @Column(name = "graduation_year")
    private Integer graduationYear;
    
    @ElementCollection
    @CollectionTable(name = "academic_interests", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "interest")
    private Set<String> researchInterests = new HashSet<>();
    
    // Default constructor
    public AcademicUser() {
        super();
    }
    
    // Constructor building on parent constructors
    public AcademicUser(String firstName, String lastName, String email, String username, 
                       String password, String institution, String academicLevel) {
        super(firstName, lastName, email, username, password); // Call parent constructor
        this.institution = institution;
        this.academicLevel = academicLevel;
    }
    
    // Override display info to include academic details
    @Override
    public String getDisplayInfo() {
        String baseInfo = super.getDisplayInfo(); // Call parent's overridden method
        return String.format("%s | %s at %s", baseInfo, academicLevel, institution);
    }
    
    // New academic-specific methods
    public boolean isStudent() {
        return academicLevel != null && 
               (academicLevel.equalsIgnoreCase("Bachelor") || 
                academicLevel.equalsIgnoreCase("Master") || 
                academicLevel.equalsIgnoreCase("PhD"));
    }
    
    public boolean isFaculty() {
        return academicLevel != null && 
               (academicLevel.equalsIgnoreCase("Professor") || 
                academicLevel.equalsIgnoreCase("Associate Professor") ||
                academicLevel.equalsIgnoreCase("Assistant Professor"));
    }
    
    public String getAcademicStatus() {
        return String.format("%s %s at %s (%s)", 
                            academicLevel, 
                            isStudent() ? "Student" : "Faculty",
                            institution,
                            department != null ? department : "General");
    }
    
    public void addResearchInterest(String interest) {
        this.researchInterests.add(interest);
        this.setUpdatedAt(LocalDateTime.now()); // Use inherited method
    }
    
    // Extended validation using inherited methods
    @Override
    public boolean canLogin() {
        // Academic users need institution info to login
        return super.canLogin() && institution != null && !institution.isEmpty();
    }
    
    // Academic-specific summary extending parent functionality
    public String getAcademicProfile() {
        return String.format("%s | %s | Interests: %s", 
                            getUserSummary(), // Inherited method
                            getAcademicStatus(),
                            String.join(", ", researchInterests));
    }
    
    // Getters and Setters
    public String getInstitution() { return institution; }
    public void setInstitution(String institution) { this.institution = institution; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public String getAcademicLevel() { return academicLevel; }
    public void setAcademicLevel(String academicLevel) { this.academicLevel = academicLevel; }
    
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    
    public Integer getGraduationYear() { return graduationYear; }
    public void setGraduationYear(Integer graduationYear) { this.graduationYear = graduationYear; }
    
    public Set<String> getResearchInterests() { return researchInterests; }
    public void setResearchInterests(Set<String> researchInterests) { this.researchInterests = researchInterests; }
}