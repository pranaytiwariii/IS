package com.example.auth.entity.inheritance.strategy;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Employee class using Joined Table Inheritance
 * Has its own table joined to parent table by foreign key
 */
@Entity
@Table(name = "joined_table_employees")
@PrimaryKeyJoinColumn(name = "id")
public class JoinedTableEmployee extends JoinedTablePerson {
    
    @NotNull
    @Size(max = 20)
    @Column(name = "employee_id", unique = true)
    private String employeeId;
    
    @NotNull
    @Size(max = 100)
    private String department;
    
    @Column(name = "salary", precision = 10, scale = 2)
    private Double salary;
    
    @NotNull
    @Column(name = "hire_date")
    private LocalDateTime hireDate;
    
    @Size(max = 50)
    private String position;
    
    @ElementCollection
    @CollectionTable(name = "joined_employee_skills", 
                     joinColumns = @JoinColumn(name = "employee_id"))
    @Column(name = "skill")
    private List<String> skills = new ArrayList<>();
    
    // Default constructor
    public JoinedTableEmployee() {
        super();
        this.hireDate = LocalDateTime.now();
    }
    
    // Constructor
    public JoinedTableEmployee(String firstName, String lastName, String email, 
                              String employeeId, String department, String position) {
        super(firstName, lastName, email);
        this.employeeId = employeeId;
        this.department = department;
        this.position = position;
        this.hireDate = LocalDateTime.now();
    }
    
    public void addSkill(String skill) {
        if (!skills.contains(skill)) {
            skills.add(skill);
        }
    }
    
    public void removeSkill(String skill) {
        skills.remove(skill);
    }
    
    public double getExperienceYears() {
        if (hireDate == null) return 0;
        return java.time.Duration.between(hireDate, LocalDateTime.now()).toDays() / 365.0;
    }
    
    public String getEmploymentStatus() {
        double years = getExperienceYears();
        if (years < 1) return "New Employee";
        if (years < 3) return "Junior";
        if (years < 7) return "Senior";
        return "Veteran";
    }
    
    public void promoteTo(String newPosition, Double newSalary) {
        this.position = newPosition;
        if (newSalary != null) {
            this.salary = newSalary;
        }
    }
    
    @Override
    public String getJoinedTableInfo() {
        return String.format("""
            %s
            
            === EMPLOYEE-SPECIFIC DATA (from joined_table_employees) ===
            Employee ID: %s (NOT NULL constraint enforced)
            Department: %s (NOT NULL constraint enforced)
            Position: %s
            Salary: %s
            Hire Date: %s (NOT NULL constraint enforced)
            Experience: %.1f years
            Employment Status: %s
            Skills: %s
            
            Joined Table Benefits:
            ✅ Normalized design - no student-related columns
            ✅ NOT NULL constraints on required employee fields
            ✅ Dedicated employee table for specialized queries
            ✅ Independent indexing and optimization
            
            Query Pattern:
            SELECT p.first_name, p.last_name, e.employee_id, e.department, e.salary
            FROM joined_table_persons p 
            JOIN joined_table_employees e ON p.id = e.id
            WHERE e.department = 'Engineering'
            """,
            super.getJoinedTableInfo(),
            employeeId,
            department,
            position != null ? position : "Not specified",
            salary != null ? String.format("$%.2f", salary) : "Not disclosed",
            hireDate.toLocalDate(),
            getExperienceYears(),
            getEmploymentStatus(),
            skills.isEmpty() ? "None" : String.join(", ", skills)
        );
    }
    
    // Getters and Setters
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public Double getSalary() { return salary; }
    public void setSalary(Double salary) { this.salary = salary; }
    
    public LocalDateTime getHireDate() { return hireDate; }
    public void setHireDate(LocalDateTime hireDate) { this.hireDate = hireDate; }
    
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    
    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }
}