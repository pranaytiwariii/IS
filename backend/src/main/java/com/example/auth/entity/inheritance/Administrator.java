package com.example.auth.entity.inheritance;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Administrator class demonstrating Hierarchical Inheritance
 * Third class inheriting from BasePerson alongside Student and Professor
 */
@Entity
@Table(name = "administrators")
@DiscriminatorValue("ADMINISTRATOR")
public class Administrator extends BasePerson {
    
    @Column(name = "employee_id", unique = true)
    private String employeeId;
    
    @Size(max = 100)
    private String department;
    
    @Size(max = 50)
    private String position; // "Dean", "Registrar", "Director", etc.
    
    @Column(name = "hire_date")
    private LocalDateTime hireDate;
    
    @Column(name = "clearance_level")
    private Integer clearanceLevel = 1; // 1-5, higher means more access
    
    @ElementCollection
    @CollectionTable(name = "admin_responsibilities", joinColumns = @JoinColumn(name = "admin_id"))
    @Column(name = "responsibility")
    private Set<String> responsibilities = new HashSet<>();
    
    @ElementCollection
    @CollectionTable(name = "admin_access_systems", joinColumns = @JoinColumn(name = "admin_id"))
    @Column(name = "system_name")
    private Set<String> accessibleSystems = new HashSet<>();
    
    @ElementCollection
    @CollectionTable(name = "admin_managed_departments", joinColumns = @JoinColumn(name = "admin_id"))
    @Column(name = "department_name")
    private List<String> managedDepartments = new ArrayList<>();
    
    // Default constructor
    public Administrator() {
        super();
        this.hireDate = LocalDateTime.now();
    }
    
    // Constructor using parent constructor
    public Administrator(String firstName, String lastName, String email, 
                        String employeeId, String department, String position) {
        super(firstName, lastName, email); // Call BasePerson constructor
        this.employeeId = employeeId;
        this.department = department;
        this.position = position;
        this.hireDate = LocalDateTime.now();
        
        // Set initial clearance based on position
        setClearanceByPosition(position);
    }
    
    private void setClearanceByPosition(String position) {
        if (position == null) {
            this.clearanceLevel = 1;
            return;
        }
        
        switch (position.toLowerCase()) {
            case "dean", "president", "provost" -> this.clearanceLevel = 5;
            case "director", "associate dean" -> this.clearanceLevel = 4;
            case "registrar", "coordinator" -> this.clearanceLevel = 3;
            case "assistant", "secretary" -> this.clearanceLevel = 2;
            default -> this.clearanceLevel = 1;
        }
    }
    
    // Override parent method with administrator-specific info
    @Override
    public String getDisplayInfo() {
        return String.format("%s %s (%s) - %s Department | Clearance Level %d", 
                            position != null ? position : "Administrator",
                            getFullName(), // Inherited method
                            employeeId,
                            department,
                            clearanceLevel);
    }
    
    // Administrator-specific methods
    public void addResponsibility(String responsibility) {
        responsibilities.add(responsibility);
        this.setUpdatedAt(LocalDateTime.now()); // Use inherited method
    }
    
    public void removeResponsibility(String responsibility) {
        responsibilities.remove(responsibility);
        this.setUpdatedAt(LocalDateTime.now());
    }
    
    public void grantSystemAccess(String systemName) {
        accessibleSystems.add(systemName);
        this.setUpdatedAt(LocalDateTime.now());
    }
    
    public void revokeSystemAccess(String systemName) {
        accessibleSystems.remove(systemName);
        this.setUpdatedAt(LocalDateTime.now());
    }
    
    public void assignDepartment(String departmentName) {
        if (!managedDepartments.contains(departmentName)) {
            managedDepartments.add(departmentName);
            this.setUpdatedAt(LocalDateTime.now());
        }
    }
    
    public boolean hasAccessTo(String systemName) {
        return accessibleSystems.contains(systemName);
    }
    
    public boolean canManageDepartment(String departmentName) {
        return managedDepartments.contains(departmentName) || clearanceLevel >= 4;
    }
    
    public void promoteTo(String newPosition) {
        this.position = newPosition;
        setClearanceByPosition(newPosition);
        this.setUpdatedAt(LocalDateTime.now());
    }
    
    public String getAuthorityLevel() {
        return switch (clearanceLevel) {
            case 5 -> "Executive Level";
            case 4 -> "Senior Management";
            case 3 -> "Middle Management";
            case 2 -> "Supervisory";
            case 1 -> "Basic Administrative";
            default -> "Undefined";
        };
    }
    
    public double getServiceYears() {
        if (hireDate == null) return 0;
        return java.time.Duration.between(hireDate, LocalDateTime.now()).toDays() / 365.0;
    }
    
    public String getAdministratorProfile() {
        return String.format("""
            === ADMINISTRATOR PROFILE ===
            %s
            Employee ID: %s | Department: %s | Position: %s
            Hire Date: %s | Service Years: %.1f
            Authority Level: %s (Clearance %d)
            Responsibilities (%d): %s
            System Access (%d): %s
            Managed Departments (%d): %s
            Contact: %s | Phone: %s
            """,
            getDisplayInfo(), // Overridden method
            employeeId,
            department,
            position,
            hireDate.toLocalDate(),
            getServiceYears(),
            getAuthorityLevel(),
            clearanceLevel,
            responsibilities.size(),
            responsibilities.isEmpty() ? "None assigned" : String.join(", ", responsibilities),
            accessibleSystems.size(),
            accessibleSystems.isEmpty() ? "None granted" : String.join(", ", accessibleSystems),
            managedDepartments.size(),
            managedDepartments.isEmpty() ? "None assigned" : String.join(", ", managedDepartments),
            getEmail(), // Inherited method
            getPhoneNumber() != null ? getPhoneNumber() : "Not provided" // Inherited method
        );
    }
    
    // Method to demonstrate hierarchical inheritance alongside other classes
    public String compareWithOtherRoles() {
        return String.format("""
            === HIERARCHICAL INHERITANCE COMPARISON ===
            This Administrator inherits from BasePerson just like:
            - Student (focuses on academic progress)
            - Professor (focuses on teaching and research)
            - Administrator (focuses on management and administration)
            
            Shared inherited properties from BasePerson:
            - ID: %d
            - Full Name: %s
            - Email: %s
            - Phone: %s
            - Created: %s
            
            Administrator-specific properties:
            - Employee ID: %s
            - Position: %s
            - Clearance Level: %d
            - Authority: %s
            """,
            getId(), // From BasePerson
            getFullName(), // From BasePerson
            getEmail(), // From BasePerson
            getPhoneNumber() != null ? getPhoneNumber() : "Not set", // From BasePerson
            getCreatedAt().toLocalDate(), // From BasePerson
            employeeId,
            position,
            clearanceLevel,
            getAuthorityLevel()
        );
    }
    
    // Getters and Setters
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    
    public LocalDateTime getHireDate() { return hireDate; }
    public void setHireDate(LocalDateTime hireDate) { this.hireDate = hireDate; }
    
    public Integer getClearanceLevel() { return clearanceLevel; }
    public void setClearanceLevel(Integer clearanceLevel) { this.clearanceLevel = clearanceLevel; }
    
    public Set<String> getResponsibilities() { return responsibilities; }
    public void setResponsibilities(Set<String> responsibilities) { this.responsibilities = responsibilities; }
    
    public Set<String> getAccessibleSystems() { return accessibleSystems; }
    public void setAccessibleSystems(Set<String> accessibleSystems) { this.accessibleSystems = accessibleSystems; }
    
    public List<String> getManagedDepartments() { return managedDepartments; }
    public void setManagedDepartments(List<String> managedDepartments) { this.managedDepartments = managedDepartments; }
}