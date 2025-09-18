package com.example.auth.entity.inheritance.strategy;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Employee class using Single Table Inheritance
 * Stored in same table as parent with discriminator
 */
@Entity
@DiscriminatorValue("EMPLOYEE")
public class SingleTableEmployee extends SingleTablePerson {
    
    @Size(max = 20)
    @Column(name = "employee_id")
    private String employeeId;
    
    @Size(max = 100)
    private String department;
    
    @Column(name = "salary", precision = 10, scale = 2)
    private Double salary;
    
    @Column(name = "hire_date")
    private LocalDateTime hireDate;
    
    @ElementCollection
    @CollectionTable(name = "single_table_employee_skills", 
                     joinColumns = @JoinColumn(name = "employee_id"))
    @Column(name = "skill")
    private List<String> skills = new ArrayList<>();
    
    // Default constructor
    public SingleTableEmployee() {
        super();
        this.hireDate = LocalDateTime.now();
    }
    
    // Constructor
    public SingleTableEmployee(String firstName, String lastName, String email, 
                              String employeeId, String department, Double salary) {
        super(firstName, lastName, email);
        this.employeeId = employeeId;
        this.department = department;
        this.salary = salary;
        this.hireDate = LocalDateTime.now();
    }
    
    public void addSkill(String skill) {
        if (!skills.contains(skill)) {
            skills.add(skill);
        }
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
    
    @Override
    public String getSingleTableInfo() {
        return String.format("""
            %s
            
            === EMPLOYEE-SPECIFIC DATA ===
            Employee ID: %s
            Department: %s
            Salary: %s
            Hire Date: %s
            Experience: %.1f years
            Employment Status: %s
            Skills: %s
            
            Single Table Notes:
            - All employee data stored in single_table_persons table
            - Non-employee columns (student_id, gpa, etc.) are NULL
            - Discriminator person_type = 'EMPLOYEE'
            """,
            super.getSingleTableInfo(),
            employeeId,
            department,
            salary != null ? String.format("$%.2f", salary) : "Not disclosed",
            hireDate != null ? hireDate.toLocalDate() : "Not set",
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
    
    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }
}