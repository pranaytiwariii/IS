package com.example.auth.entity.inheritance.strategy;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Admin class using Single Table Inheritance
 * Stored in same table as parent with discriminator
 */
@Entity
@DiscriminatorValue("ADMIN")
public class SingleTableAdmin extends SingleTablePerson {
    
    @Size(max = 20)
    @Column(name = "admin_id")
    private String adminId;
    
    @Size(max = 100)
    @Column(name = "department")
    private String department;
    
    @Size(max = 50)
    @Column(name = "admin_level")
    private String adminLevel; // SYSTEM, DEPARTMENT, CONTENT, USER
    
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    
    @Column(name = "login_count")
    private Integer loginCount = 0;
    
    @Column(name = "is_super_admin")
    private Boolean isSuperAdmin = false;
    
    @Column(name = "can_manage_users")
    private Boolean canManageUsers = false;
    
    @Column(name = "can_manage_content")
    private Boolean canManageContent = false;
    
    @Column(name = "can_view_reports")
    private Boolean canViewReports = false;
    
    @ElementCollection
    @CollectionTable(name = "single_table_admin_permissions", 
                     joinColumns = @JoinColumn(name = "person_id"))
    @Column(name = "permission")
    private Set<String> permissions = new HashSet<>();
    
    @ElementCollection
    @CollectionTable(name = "single_table_admin_managed_areas", 
                     joinColumns = @JoinColumn(name = "person_id"))
    @Column(name = "managed_area")
    private Set<String> managedAreas = new HashSet<>();
    
    // Default constructor
    public SingleTableAdmin() {
        super();
    }
    
    // Constructor
    public SingleTableAdmin(String firstName, String lastName, String email, 
                           String adminId, String department, String adminLevel) {
        super(firstName, lastName, email);
        this.adminId = adminId;
        this.department = department;
        this.adminLevel = adminLevel;
        setDefaultPermissions();
    }
    
    private void setDefaultPermissions() {
        switch (adminLevel != null ? adminLevel.toUpperCase() : "USER") {
            case "SYSTEM":
                isSuperAdmin = true;
                canManageUsers = true;
                canManageContent = true;
                canViewReports = true;
                permissions.addAll(Set.of("ALL_PERMISSIONS", "SYSTEM_CONFIG", "USER_MANAGEMENT", "CONTENT_MANAGEMENT", "REPORTING"));
                break;
            case "DEPARTMENT":
                canManageUsers = true;
                canViewReports = true;
                permissions.addAll(Set.of("DEPARTMENT_MANAGEMENT", "USER_MANAGEMENT", "REPORTING"));
                break;
            case "CONTENT":
                canManageContent = true;
                permissions.addAll(Set.of("CONTENT_MANAGEMENT", "CONTENT_REVIEW", "CONTENT_PUBLISHING"));
                break;
            default: // USER level
                canViewReports = true;
                permissions.add("BASIC_ADMIN");
                break;
        }
    }
    
    public void recordLogin() {
        this.lastLogin = LocalDateTime.now();
        this.loginCount++;
    }
    
    public void addPermission(String permission) {
        permissions.add(permission);
    }
    
    public void removePermission(String permission) {
        permissions.remove(permission);
    }
    
    public void addManagedArea(String area) {
        managedAreas.add(area);
    }
    
    public void removeManagedArea(String area) {
        managedAreas.remove(area);
    }
    
    public void promoteToSuperAdmin() {
        this.isSuperAdmin = true;
        this.adminLevel = "SYSTEM";
        this.canManageUsers = true;
        this.canManageContent = true;
        this.canViewReports = true;
        permissions.clear();
        permissions.addAll(Set.of("ALL_PERMISSIONS", "SYSTEM_CONFIG", "USER_MANAGEMENT", "CONTENT_MANAGEMENT", "REPORTING"));
    }
    
    public boolean hasPermission(String permission) {
        return isSuperAdmin || permissions.contains(permission) || permissions.contains("ALL_PERMISSIONS");
    }
    
    public String getAdminStatus() {
        if (isSuperAdmin) return "Super Administrator";
        if ("SYSTEM".equals(adminLevel)) return "System Administrator";
        if ("DEPARTMENT".equals(adminLevel)) return "Department Administrator";
        if ("CONTENT".equals(adminLevel)) return "Content Administrator";
        return "Basic Administrator";
    }
    
    public String getActivityLevel() {
        if (loginCount >= 100) return "Very Active";
        if (loginCount >= 50) return "Active";
        if (loginCount >= 10) return "Moderate";
        return "New Admin";
    }
    
    public boolean isExperiencedAdmin() {
        return loginCount >= 50 && permissions.size() >= 3;
    }
    
    @Override
    public String getSingleTableInfo() {
        return String.format("""
            %s
            
            === SINGLE TABLE ADMIN DETAILS ===
            Discriminator: ADMIN (person_type = 'ADMIN')
            Admin-specific fields in same table (nullable):
            - admin_id, department, admin_level, last_login, login_count, is_super_admin,
              can_manage_users, can_manage_content, can_view_reports
            
            === ADMIN DATA ===
            Admin ID: %s
            Department: %s
            Admin Level: %s | Status: %s
            Super Admin: %s
            Last Login: %s
            Login Count: %d | Activity Level: %s
            
            === PERMISSIONS ===
            Can Manage Users: %s
            Can Manage Content: %s
            Can View Reports: %s
            Specific Permissions: %s
            Managed Areas: %s
            Experienced Admin: %s
            
            Single Table Benefits for Admins:
            ✅ Fast admin authentication queries
            ✅ Simple permission checks across all person types
            ✅ Easy to implement role-based access control
            
            Single Table Challenges for Admins:
            ❌ Admin-specific fields nullable for other person types
            ❌ Security-sensitive data mixed with other person data
            ❌ Complex permission schema in single table
            
            Example Queries:
            -- Find all super admins
            SELECT * FROM single_table_persons 
            WHERE person_type = 'ADMIN' AND is_super_admin = true;
            
            -- Find admins in specific department
            SELECT * FROM single_table_persons 
            WHERE person_type = 'ADMIN' AND department = 'IT';
            """,
            super.getSingleTableInfo(),
            adminId != null ? adminId : "Not assigned",
            department != null ? department : "Not specified",
            adminLevel != null ? adminLevel : "Not specified",
            getAdminStatus(),
            isSuperAdmin ? "Yes" : "No",
            lastLogin != null ? lastLogin.toString() : "Never",
            loginCount,
            getActivityLevel(),
            canManageUsers ? "Yes" : "No",
            canManageContent ? "Yes" : "No",
            canViewReports ? "Yes" : "No",
            permissions.isEmpty() ? "None" : String.join(", ", permissions),
            managedAreas.isEmpty() ? "None" : String.join(", ", managedAreas),
            isExperiencedAdmin() ? "Yes" : "No"
        );
    }
    
    // Getters and Setters
    public String getAdminId() { return adminId; }
    public void setAdminId(String adminId) { this.adminId = adminId; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public String getAdminLevel() { return adminLevel; }
    public void setAdminLevel(String adminLevel) { 
        this.adminLevel = adminLevel;
        setDefaultPermissions(); // Reset permissions based on new level
    }
    
    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
    
    public Integer getLoginCount() { return loginCount; }
    public void setLoginCount(Integer loginCount) { this.loginCount = loginCount; }
    
    public Boolean getIsSuperAdmin() { return isSuperAdmin; }
    public void setIsSuperAdmin(Boolean isSuperAdmin) { this.isSuperAdmin = isSuperAdmin; }
    
    public Boolean getCanManageUsers() { return canManageUsers; }
    public void setCanManageUsers(Boolean canManageUsers) { this.canManageUsers = canManageUsers; }
    
    public Boolean getCanManageContent() { return canManageContent; }
    public void setCanManageContent(Boolean canManageContent) { this.canManageContent = canManageContent; }
    
    public Boolean getCanViewReports() { return canViewReports; }
    public void setCanViewReports(Boolean canViewReports) { this.canViewReports = canViewReports; }
    
    public Set<String> getPermissions() { return permissions; }
    public void setPermissions(Set<String> permissions) { this.permissions = permissions; }
    
    public Set<String> getManagedAreas() { return managedAreas; }
    public void setManagedAreas(Set<String> managedAreas) { this.managedAreas = managedAreas; }
}