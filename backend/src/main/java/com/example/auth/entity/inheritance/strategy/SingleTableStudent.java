package com.example.auth.entity.inheritance.strategy;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

/**
 * Student class using Single Table Inheritance
 * Stored in same table as parent with discriminator
 */
@Entity
@DiscriminatorValue("STUDENT")
public class SingleTableStudent extends SingleTablePerson {
    
    @Size(max = 20)
    @Column(name = "student_id")
    private String studentId;
    
    @Size(max = 100)
    private String major;
    
    @Column(name = "gpa")
    private Double gpa;
    
    @ElementCollection
    @CollectionTable(name = "single_table_student_courses", 
                     joinColumns = @JoinColumn(name = "student_id"))
    @Column(name = "course_code")
    private Set<String> courses = new HashSet<>();
    
    // Default constructor
    public SingleTableStudent() {
        super();
    }
    
    // Constructor
    public SingleTableStudent(String firstName, String lastName, String email, 
                             String studentId, String major) {
        super(firstName, lastName, email);
        this.studentId = studentId;
        this.major = major;
    }
    
    public void enrollInCourse(String courseCode) {
        courses.add(courseCode);
    }
    
    public String getAcademicStatus() {
        if (gpa == null) return "No GPA recorded";
        if (gpa >= 3.5) return "Dean's List";
        if (gpa >= 3.0) return "Good Standing";
        if (gpa >= 2.0) return "Satisfactory";
        return "Academic Probation";
    }
    
    @Override
    public String getSingleTableInfo() {
        return String.format("""
            %s
            
            === STUDENT-SPECIFIC DATA ===
            Student ID: %s
            Major: %s
            GPA: %s
            Academic Status: %s
            Enrolled Courses: %s
            
            Single Table Notes:
            - All student data stored in single_table_persons table
            - Non-student columns (employee_id, salary, etc.) are NULL
            - Discriminator person_type = 'STUDENT'
            """,
            super.getSingleTableInfo(),
            studentId,
            major,
            gpa != null ? String.format("%.2f", gpa) : "Not recorded",
            getAcademicStatus(),
            courses.isEmpty() ? "None" : String.join(", ", courses)
        );
    }
    
    // Getters and Setters
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    
    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }
    
    public Double getGpa() { return gpa; }
    public void setGpa(Double gpa) { this.gpa = gpa; }
    
    public Set<String> getCourses() { return courses; }
    public void setCourses(Set<String> courses) { this.courses = courses; }
}