package com.example.auth.entity.inheritance.strategy;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

/**
 * Student class using Joined Table Inheritance
 * Has its own table joined to parent table by foreign key
 */
@Entity
@Table(name = "joined_table_students")
@PrimaryKeyJoinColumn(name = "id")
public class JoinedTableStudent extends JoinedTablePerson {
    
    @NotNull
    @Size(max = 20)
    @Column(name = "student_id", unique = true)
    private String studentId;
    
    @NotNull
    @Size(max = 100)
    private String major;
    
    @Column(name = "gpa", precision = 3, scale = 2)
    private Double gpa;
    
    @Column(name = "year_of_study")
    private Integer yearOfStudy = 1;
    
    @ElementCollection
    @CollectionTable(name = "joined_student_courses", 
                     joinColumns = @JoinColumn(name = "student_id"))
    @Column(name = "course_code")
    private Set<String> courses = new HashSet<>();
    
    // Default constructor
    public JoinedTableStudent() {
        super();
    }
    
    // Constructor
    public JoinedTableStudent(String firstName, String lastName, String email, 
                             String studentId, String major) {
        super(firstName, lastName, email);
        this.studentId = studentId;
        this.major = major;
    }
    
    public void enrollInCourse(String courseCode) {
        courses.add(courseCode);
    }
    
    public void dropCourse(String courseCode) {
        courses.remove(courseCode);
    }
    
    public String getAcademicStatus() {
        if (gpa == null) return "No GPA recorded";
        if (gpa >= 3.5) return "Dean's List";
        if (gpa >= 3.0) return "Good Standing";
        if (gpa >= 2.0) return "Satisfactory";
        return "Academic Probation";
    }
    
    public boolean isFullTimeStudent() {
        return courses.size() >= 4;
    }
    
    @Override
    public String getJoinedTableInfo() {
        return String.format("""
            %s
            
            === STUDENT-SPECIFIC DATA (from joined_table_students) ===
            Student ID: %s (NOT NULL constraint possible)
            Major: %s (NOT NULL constraint possible)
            GPA: %s
            Year: %d
            Academic Status: %s
            Enrolled Courses: %s
            Full-time Status: %s
            
            Joined Table Benefits:
            ✅ Clean normalization - no unused columns
            ✅ NOT NULL constraints on student-specific fields
            ✅ Type-safe queries on student table
            ✅ Separate indexing strategies per table
            
            Query Pattern:
            SELECT p.first_name, p.last_name, s.student_id, s.major 
            FROM joined_table_persons p 
            JOIN joined_table_students s ON p.id = s.id
            """,
            super.getJoinedTableInfo(),
            studentId,
            major,
            gpa != null ? String.format("%.2f", gpa) : "Not recorded",
            yearOfStudy,
            getAcademicStatus(),
            courses.isEmpty() ? "None" : String.join(", ", courses),
            isFullTimeStudent() ? "Full-time" : "Part-time"
        );
    }
    
    // Getters and Setters
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    
    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }
    
    public Double getGpa() { return gpa; }
    public void setGpa(Double gpa) { this.gpa = gpa; }
    
    public Integer getYearOfStudy() { return yearOfStudy; }
    public void setYearOfStudy(Integer yearOfStudy) { this.yearOfStudy = yearOfStudy; }
    
    public Set<String> getCourses() { return courses; }
    public void setCourses(Set<String> courses) { this.courses = courses; }
}