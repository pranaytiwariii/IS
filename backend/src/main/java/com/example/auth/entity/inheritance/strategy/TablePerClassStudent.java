package com.example.auth.entity.inheritance.strategy;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

/**
 * Student class using Table Per Class Inheritance
 * Has its own COMPLETE table with ALL parent fields + student fields
 */
@Entity
@Table(name = "table_per_class_students")
public class TablePerClassStudent extends TablePerClassPerson {
    
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
    
    @Column(name = "credits_completed")
    private Integer creditsCompleted = 0;
    
    @ElementCollection
    @CollectionTable(name = "table_per_class_student_courses", 
                     joinColumns = @JoinColumn(name = "student_id"))
    @Column(name = "course_code")
    private Set<String> courses = new HashSet<>();
    
    // Default constructor
    public TablePerClassStudent() {
        super();
    }
    
    // Constructor
    public TablePerClassStudent(String firstName, String lastName, String email, 
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
    
    public void addCredits(int credits) {
        this.creditsCompleted += credits;
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
    
    public String getClassStanding() {
        if (creditsCompleted < 30) return "Freshman";
        if (creditsCompleted < 60) return "Sophomore";
        if (creditsCompleted < 90) return "Junior";
        return "Senior";
    }
    
    @Override
    public String getTablePerClassInfo() {
        return String.format("""
            %s
            
            === STUDENT TABLE STRUCTURE ===
            Table: table_per_class_students
            Contains: ALL parent fields + student fields
            
            Parent Fields (duplicated from TablePerClassPerson):
            - id, first_name, last_name, email, phone_number, created_at
            
            Student Fields:
            - student_id, major, gpa, year_of_study, credits_completed
            
            === STUDENT-SPECIFIC DATA ===
            Student ID: %s (NOT NULL constraint possible)
            Major: %s (NOT NULL constraint possible)
            GPA: %s
            Year: %d | Class Standing: %s
            Credits Completed: %d
            Academic Status: %s
            Enrolled Courses: %s
            Full-time Status: %s
            
            Table Per Class Benefits:
            ✅ Complete data in single table (no JOINs)
            ✅ NOT NULL constraints on all fields
            ✅ Fast queries for specific type
            ✅ Independent table optimization and indexing
            
            Table Per Class Drawbacks:
            ❌ Data duplication (parent fields in every table)
            ❌ Schema changes require updating multiple tables
            ❌ Polymorphic queries require UNION operations
            
            Query Pattern (simple):
            SELECT * FROM table_per_class_students WHERE major = 'Computer Science'
            
            Query Pattern (polymorphic - complex):
            SELECT id, first_name, last_name, email, 'STUDENT' as type 
            FROM table_per_class_students
            UNION ALL
            SELECT id, first_name, last_name, email, 'EMPLOYEE' as type 
            FROM table_per_class_employees
            """,
            super.getTablePerClassInfo(),
            studentId,
            major,
            gpa != null ? String.format("%.2f", gpa) : "Not recorded",
            yearOfStudy,
            getClassStanding(),
            creditsCompleted,
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
    
    public Integer getCreditsCompleted() { return creditsCompleted; }
    public void setCreditsCompleted(Integer creditsCompleted) { this.creditsCompleted = creditsCompleted; }
    
    public Set<String> getCourses() { return courses; }
    public void setCourses(Set<String> courses) { this.courses = courses; }
}