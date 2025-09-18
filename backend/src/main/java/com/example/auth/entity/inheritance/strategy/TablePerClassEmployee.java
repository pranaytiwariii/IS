package com.example.auth.entity.inheritance.strategy;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Employee class using Table Per Class Inheritance
 * Has its own COMPLETE table with ALL parent fields + employee fields
 */
@Entity
@Table(name = "table_per_class_employees")
public class TablePerClassEmployee extends TablePerClassPerson {
    
    @NotNull
    @Size(max = 20)
    @Column(name = "employee_id", unique = true)
    private String employeeId;
    
    @NotNull
    @Size(max = 100)
    private String department;
    
    @NotNull
    @Size(max = 50)
    private String position;
    
    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(precision = 10, scale = 2)
    private BigDecimal salary;
    
    @Column(name = "hire_date")
    private LocalDate hireDate;
    
    @Column(name = "is_manager")
    private Boolean isManager = false;
    
    @Column(name = "manager_id")
    private String managerId;
    
    @ElementCollection
    @CollectionTable(name = "table_per_class_employee_skills", 
                     joinColumns = @JoinColumn(name = "employee_id"))
    @Column(name = "skill")
    private Set<String> skills = new HashSet<>();
    
    @ElementCollection
    @CollectionTable(name = "table_per_class_employee_certifications", 
                     joinColumns = @JoinColumn(name = "employee_id"))
    @Column(name = "certification")
    private Set<String> certifications = new HashSet<>();
    
    // Default constructor
    public TablePerClassEmployee() {
        super();
        this.hireDate = LocalDate.now();
    }
    
    // Constructor
    public TablePerClassEmployee(String firstName, String lastName, String email,
                                String employeeId, String department, String position,
                                BigDecimal salary) {
        super(firstName, lastName, email);
        this.employeeId = employeeId;
        this.department = department;
        this.position = position;
        this.salary = salary;
        this.hireDate = LocalDate.now();
    }
    
    public void addSkill(String skill) {
        skills.add(skill);
    }
    
    public void removeSkill(String skill) {
        skills.remove(skill);
    }
    
    public void addCertification(String certification) {
        certifications.add(certification);
    }
    
    public void removeCertification(String certification) {
        certifications.remove(certification);
    }
    
    public void promoteToManager(String newPosition) {
        this.position = newPosition;
        this.isManager = true;
    }
    
    public void raiseSalary(BigDecimal raise) {
        this.salary = this.salary.add(raise);
    }
    
    public void raiseSalaryByPercentage(double percentage) {
        BigDecimal multiplier = BigDecimal.valueOf(1 + percentage / 100);
        this.salary = this.salary.multiply(multiplier);
    }
    
    public int getYearsOfService() {
        if (hireDate == null) return 0;
        return LocalDate.now().getYear() - hireDate.getYear();
    }
    
    public String getExperienceLevel() {
        int years = getYearsOfService();
        if (years < 2) return "Junior";
        if (years < 5) return "Mid-level";
        if (years < 10) return "Senior";
        return "Expert";
    }
    
    public String getPayGrade() {
        if (salary.compareTo(new BigDecimal("100000")) >= 0) return "Executive";
        if (salary.compareTo(new BigDecimal("75000")) >= 0) return "Senior";
        if (salary.compareTo(new BigDecimal("50000")) >= 0) return "Mid-level";
        return "Entry-level";
    }
    
    @Override
    public String getTablePerClassInfo() {
        return String.format("""
            %s
            
            === EMPLOYEE TABLE STRUCTURE ===
            Table: table_per_class_employees
            Contains: ALL parent fields + employee fields
            
            Parent Fields (duplicated from TablePerClassPerson):
            - id, first_name, last_name, email, phone_number, created_at
            
            Employee Fields:
            - employee_id, department, position, salary, hire_date, is_manager, manager_id
            
            === EMPLOYEE-SPECIFIC DATA ===
            Employee ID: %s (NOT NULL constraint possible)
            Department: %s (NOT NULL constraint possible)
            Position: %s | Manager: %s
            Salary: $%s | Pay Grade: %s
            Hire Date: %s | Years of Service: %d
            Experience Level: %s
            Manager ID: %s
            Skills: %s
            Certifications: %s
            
            Table Per Class Benefits:
            ✅ Complete data in single table (no JOINs)
            ✅ NOT NULL constraints on all fields
            ✅ Fast queries for specific type
            ✅ Independent table optimization and indexing
            ✅ Best performance for type-specific queries
            
            Table Per Class Drawbacks:
            ❌ Data duplication (parent fields in every table)
            ❌ Schema changes require updating multiple tables
            ❌ Polymorphic queries require UNION operations
            ❌ Most complex for cross-type reporting
            
            Specific Query Examples:
            
            1. Type-specific (FAST):
            SELECT * FROM table_per_class_employees 
            WHERE department = 'Engineering' AND salary > 80000;
            
            2. Polymorphic (COMPLEX):
            SELECT id, first_name, last_name, email, 'EMPLOYEE' as type, salary as extra_info
            FROM table_per_class_employees
            UNION ALL
            SELECT id, first_name, last_name, email, 'STUDENT' as type, gpa as extra_info
            FROM table_per_class_students;
            
            3. Count all persons (REQUIRES UNION):
            SELECT COUNT(*) FROM (
                SELECT id FROM table_per_class_employees
                UNION ALL
                SELECT id FROM table_per_class_students
            ) all_persons;
            """,
            super.getTablePerClassInfo(),
            employeeId,
            department,
            position,
            isManager ? "Yes" : "No",
            salary,
            getPayGrade(),
            hireDate != null ? hireDate.toString() : "Not recorded",
            getYearsOfService(),
            getExperienceLevel(),
            managerId != null ? managerId : "None",
            skills.isEmpty() ? "None" : String.join(", ", skills),
            certifications.isEmpty() ? "None" : String.join(", ", certifications)
        );
    }
    
    // Getters and Setters
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    
    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }
    
    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }
    
    public Boolean getIsManager() { return isManager; }
    public void setIsManager(Boolean isManager) { this.isManager = isManager; }
    
    public String getManagerId() { return managerId; }
    public void setManagerId(String managerId) { this.managerId = managerId; }
    
    public Set<String> getSkills() { return skills; }
    public void setSkills(Set<String> skills) { this.skills = skills; }
    
    public Set<String> getCertifications() { return certifications; }
    public void setCertifications(Set<String> certifications) { this.certifications = certifications; }
}