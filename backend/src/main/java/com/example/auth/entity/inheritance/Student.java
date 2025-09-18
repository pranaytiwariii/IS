package com.example.auth.entity.inheritance;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Student class demonstrating Hierarchical Inheritance
 * Multiple classes (Student, Professor, Administrator) inherit from the same parent (BasePerson)
 */
@Entity
@Table(name = "students")
@DiscriminatorValue("STUDENT")
public class Student extends BasePerson {
    
    @Column(name = "student_id", unique = true)
    private String studentId;
    
    @Size(max = 100)
    private String major;
    
    @Column(name = "year_of_study")
    private Integer yearOfStudy;
    
    @Column(name = "gpa")
    private Double gpa;
    
    @Column(name = "enrollment_date")
    private LocalDateTime enrollmentDate;
    
    @Size(max = 100)
    private String advisor;
    
    @ElementCollection
    @CollectionTable(name = "student_courses", joinColumns = @JoinColumn(name = "student_id"))
    @Column(name = "course_code")
    private Set<String> enrolledCourses = new HashSet<>();
    
    // Default constructor
    public Student() {
        super();
        this.enrollmentDate = LocalDateTime.now();
    }
    
    // Constructor using parent constructor
    public Student(String firstName, String lastName, String email, String studentId, String major) {
        super(firstName, lastName, email); // Call BasePerson constructor
        this.studentId = studentId;
        this.major = major;
        this.enrollmentDate = LocalDateTime.now();
        this.yearOfStudy = 1;
    }
    
    // Override parent method with student-specific info
    @Override
    public String getDisplayInfo() {
        return String.format("Student: %s (ID: %s) - %s, Year %d", 
                            getFullName(), // Inherited method
                            studentId, 
                            major, 
                            yearOfStudy);
    }
    
    // Student-specific methods
    public void enrollInCourse(String courseCode) {
        enrolledCourses.add(courseCode);
        this.setUpdatedAt(LocalDateTime.now()); // Use inherited method
    }
    
    public void dropCourse(String courseCode) {
        enrolledCourses.remove(courseCode);
        this.setUpdatedAt(LocalDateTime.now());
    }
    
    public boolean isFullTimeStudent() {
        return enrolledCourses.size() >= 4;
    }
    
    public String getAcademicStanding() {
        if (gpa == null) return "No GPA recorded";
        if (gpa >= 3.5) return "Dean's List";
        if (gpa >= 3.0) return "Good Standing";
        if (gpa >= 2.0) return "Satisfactory";
        return "Academic Probation";
    }
    
    public void updateGPA(double newGpa) {
        this.gpa = newGpa;
        this.setUpdatedAt(LocalDateTime.now());
    }
    
    public String getStudentProfile() {
        return String.format("""
            === STUDENT PROFILE ===
            %s
            Student ID: %s | Major: %s | Year: %d
            GPA: %s | Academic Standing: %s
            Advisor: %s
            Enrolled Courses: %s
            Enrollment Date: %s
            """,
            getDisplayInfo(), // Overridden method
            studentId,
            major,
            yearOfStudy,
            gpa != null ? String.format("%.2f", gpa) : "Not recorded",
            getAcademicStanding(),
            advisor != null ? advisor : "Not assigned",
            enrolledCourses.isEmpty() ? "None" : String.join(", ", enrolledCourses),
            enrollmentDate.toLocalDate()
        );
    }
    
    // Getters and Setters
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    
    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }
    
    public Integer getYearOfStudy() { return yearOfStudy; }
    public void setYearOfStudy(Integer yearOfStudy) { this.yearOfStudy = yearOfStudy; }
    
    public Double getGpa() { return gpa; }
    public void setGpa(Double gpa) { this.gpa = gpa; }
    
    public LocalDateTime getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDateTime enrollmentDate) { this.enrollmentDate = enrollmentDate; }
    
    public String getAdvisor() { return advisor; }
    public void setAdvisor(String advisor) { this.advisor = advisor; }
    
    public Set<String> getEnrolledCourses() { return enrolledCourses; }
    public void setEnrolledCourses(Set<String> enrolledCourses) { this.enrolledCourses = enrolledCourses; }
}