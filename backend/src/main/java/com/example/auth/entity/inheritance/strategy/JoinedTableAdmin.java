package com.example.auth.entity.inheritance.strategy;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Admin class using Joined Table Inheritance
 * Has separate table joined to parent by foreign key
 */
@Entity
@Table(name = "joined_admins")
@PrimaryKeyJoinColumn(name = "person_id")
public class JoinedTableAdmin extends JoinedTablePerson {
    
    @NotNull
    @Size(max = 20)
    @Column(name = "admin_id", unique = true)
    private String adminId;
    
    @NotNull
    @Size(max = 100)
    @Column(name = "department")
    private String department;
    
    @NotNull
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
    
    @Column(name = "can_approve_papers")
    private Boolean canApprovePapers = false;
    
    @Column(name = "papers_reviewed")
    private Integer papersReviewed = 0;
    
    @Column(name = "last_password_change")
    private LocalDateTime lastPasswordChange;
    
    @ElementCollection
    @CollectionTable(name = "joined_admin_permissions", 
                     joinColumns = @JoinColumn(name = "admin_id"))
    @Column(name = "permission")
    private Set<String> permissions = new HashSet<>();
    
    @ElementCollection
    @CollectionTable(name = "joined_admin_managed_areas", 
                     joinColumns = @JoinColumn(name = "admin_id"))
    @Column(name = "managed_area")
    private Set<String> managedAreas = new HashSet<>();
    
    @ElementCollection
    @CollectionTable(name = "joined_admin_access_logs", 
                     joinColumns = @JoinColumn(name = "admin_id"))
    @Column(name = "access_log")
    private Set<String> accessLogs = new HashSet<>();
    
    // Default constructor
    public JoinedTableAdmin() {
        super();
        this.lastPasswordChange = LocalDateTime.now();
    }
    
    // Constructor
    public JoinedTableAdmin(String firstName, String lastName, String email, 
                           String adminId, String department, String adminLevel) {
        super(firstName, lastName, email);
        this.adminId = adminId;
        this.department = department;
        this.adminLevel = adminLevel;
        this.lastPasswordChange = LocalDateTime.now();
        setDefaultPermissions();
    }
    
    private void setDefaultPermissions() {
        permissions.clear();
        managedAreas.clear();
        
        switch (adminLevel != null ? adminLevel.toUpperCase() : "USER") {
            case "SYSTEM":
                isSuperAdmin = true;
                canManageUsers = true;
                canManageContent = true;
                canViewReports = true;
                canApprovePapers = true;
                permissions.addAll(Set.of("ALL_PERMISSIONS", "SYSTEM_CONFIG", "USER_MANAGEMENT", 
                                        "CONTENT_MANAGEMENT", "REPORTING", "PAPER_APPROVAL", "DATABASE_ACCESS"));
                managedAreas.addAll(Set.of("ALL_DEPARTMENTS", "SYSTEM_SETTINGS", "USER_ACCOUNTS"));
                break;
            case "DEPARTMENT":
                canManageUsers = true;
                canViewReports = true;
                canApprovePapers = true;
                permissions.addAll(Set.of("DEPARTMENT_MANAGEMENT", "USER_MANAGEMENT", "REPORTING", "PAPER_REVIEW"));
                managedAreas.add(department);
                break;
            case "CONTENT":
                canManageContent = true;
                canApprovePapers = true;
                permissions.addAll(Set.of("CONTENT_MANAGEMENT", "CONTENT_REVIEW", "CONTENT_PUBLISHING", "PAPER_APPROVAL"));
                managedAreas.addAll(Set.of("CONTENT_REVIEW", "PUBLICATIONS"));
                break;
            default: // USER level
                canViewReports = true;
                permissions.add("BASIC_ADMIN");
                managedAreas.add("LIMITED_ACCESS");
                break;
        }
    }
    
    public void recordLogin() {
        this.lastLogin = LocalDateTime.now();
        this.loginCount++;
        logAccess("LOGIN");
    }
    
    public void recordLogout() {
        logAccess("LOGOUT");
    }
    
    public void logAccess(String action) {
        String logEntry = LocalDateTime.now() + ": " + action;
        accessLogs.add(logEntry);
        // Keep only last 10 log entries
        if (accessLogs.size() > 10) {
            accessLogs.iterator().next();
            accessLogs.remove(accessLogs.iterator().next());
        }
    }
    
    public void addPermission(String permission) {
        permissions.add(permission);
        logAccess("PERMISSION_ADDED: " + permission);
    }
    
    public void removePermission(String permission) {
        permissions.remove(permission);
        logAccess("PERMISSION_REMOVED: " + permission);
    }
    
    public void addManagedArea(String area) {
        managedAreas.add(area);
    }
    
    public void removeManagedArea(String area) {
        managedAreas.remove(area);
    }
    
    public void approvePaper(String paperTitle) {
        papersReviewed++;
        logAccess("PAPER_APPROVED: " + paperTitle);
    }
    
    public void promoteToSuperAdmin() {
        this.isSuperAdmin = true;
        this.adminLevel = "SYSTEM";
        setDefaultPermissions();
        logAccess("PROMOTED_TO_SUPER_ADMIN");
    }
    
    public void changePassword() {
        this.lastPasswordChange = LocalDateTime.now();
        logAccess("PASSWORD_CHANGED");
    }
    
    public boolean hasPermission(String permission) {
        return isSuperAdmin || permissions.contains(permission) || permissions.contains("ALL_PERMISSIONS");
    }
    
    public boolean canManageArea(String area) {
        return isSuperAdmin || managedAreas.contains(area) || managedAreas.contains("ALL_DEPARTMENTS");
    }
    
    public String getAdminStatus() {
        if (isSuperAdmin) return "Super Administrator";
        return switch (adminLevel != null ? adminLevel.toUpperCase() : "USER") {
            case "SYSTEM" -> "System Administrator";
            case "DEPARTMENT" -> "Department Administrator";
            case "CONTENT" -> "Content Administrator";
            default -> "Basic Administrator";
        };
    }
    
    public String getActivityLevel() {
        if (loginCount >= 200) return "Very Active";
        if (loginCount >= 100) return "Active";
        if (loginCount >= 50) return "Moderate";
        if (loginCount >= 10) return "Regular";
        return "New Admin";
    }
    
    public String getSecurityLevel() {
        if (lastPasswordChange == null) return "Critical - No password change";
        long daysSinceChange = java.time.Duration.between(lastPasswordChange, LocalDateTime.now()).toDays();
        if (daysSinceChange > 90) return "High Risk - Password expired";
        if (daysSinceChange > 60) return "Medium Risk - Password aging";
        return "Good - Recent password change";
    }
    
    public boolean isExperiencedAdmin() {
        return loginCount >= 100 && permissions.size() >= 5 && papersReviewed >= 20;
    }
    
    public boolean isActiveReviewer() {
        return canApprovePapers && papersReviewed >= 10;
    }
    
    @Override
    public String getJoinedTableInfo() {
        return String.format("""
            %s
            
            === JOINED TABLE ADMIN STRUCTURE ===
            Parent Table: joined_persons (id, first_name, last_name, email, phone_number, created_at)
            Child Table: joined_admins (person_id FK, admin_id, department, admin_level, permissions, etc.)
            Relationship: 1:1 JOIN on joined_persons.id = joined_admins.person_id
            
            === ADMIN-SPECIFIC DATA ===
            Admin ID: %s (NOT NULL constraint enforced)
            Department: %s (NOT NULL constraint enforced)
            Admin Level: %s | Status: %s
            Super Admin: %s
            Last Login: %s | Activity Level: %s
            Login Count: %d
            Papers Reviewed: %d | Active Reviewer: %s
            Last Password Change: %s
            Security Level: %s
            
            === PERMISSIONS & AREAS ===
            Can Manage Users: %s
            Can Manage Content: %s
            Can View Reports: %s
            Can Approve Papers: %s
            Specific Permissions: %s
            Managed Areas: %s
            Experienced Admin: %s
            
            Joined Table Benefits for Admins:
            ✅ Normalized admin data - no nullable admin fields in parent table
            ✅ NOT NULL constraints on critical admin fields (admin_id, department, level)
            ✅ Clean separation of admin security data from other person types
            ✅ Easy to add complex admin-specific fields and relationships
            ✅ Better security through data isolation
            
            Joined Table Challenges for Admins:
            ❌ Requires JOIN for complete admin authentication
            ❌ More complex queries for admin session management
            ❌ Performance overhead for frequent admin operations
            
            Query Examples:
            
            1. Get admin with full details:
            SELECT p.*, a.admin_id, a.department, a.admin_level, a.is_super_admin
            FROM joined_persons p
            JOIN joined_admins a ON p.id = a.person_id
            WHERE a.admin_id = 'ADM001';
            
            2. Find super admins in department:
            SELECT p.first_name, p.last_name, a.admin_level, a.login_count
            FROM joined_persons p
            JOIN joined_admins a ON p.id = a.person_id
            WHERE a.is_super_admin = true AND a.department = 'IT';
            
            3. Active admins with many reviews:
            SELECT p.email, a.admin_id, a.papers_reviewed, a.last_login
            FROM joined_persons p
            JOIN joined_admins a ON p.id = a.person_id
            WHERE a.papers_reviewed >= 10 AND a.last_login > NOW() - INTERVAL '30 days';
            """,
            super.getJoinedTableInfo(),
            adminId,
            department,
            adminLevel,
            getAdminStatus(),
            isSuperAdmin ? "Yes" : "No",
            lastLogin != null ? lastLogin.toString() : "Never",
            getActivityLevel(),
            loginCount,
            papersReviewed,
            isActiveReviewer() ? "Yes" : "No",
            lastPasswordChange != null ? lastPasswordChange.toString() : "Never",
            getSecurityLevel(),
            canManageUsers ? "Yes" : "No",
            canManageContent ? "Yes" : "No",
            canViewReports ? "Yes" : "No",
            canApprovePapers ? "Yes" : "No",
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
        setDefaultPermissions();
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
    
    public Boolean getCanApprovePapers() { return canApprovePapers; }
    public void setCanApprovePapers(Boolean canApprovePapers) { this.canApprovePapers = canApprovePapers; }
    
    public Integer getPapersReviewed() { return papersReviewed; }
    public void setPapersReviewed(Integer papersReviewed) { this.papersReviewed = papersReviewed; }
    
    public LocalDateTime getLastPasswordChange() { return lastPasswordChange; }
    public void setLastPasswordChange(LocalDateTime lastPasswordChange) { this.lastPasswordChange = lastPasswordChange; }
    
    public Set<String> getPermissions() { return permissions; }
    public void setPermissions(Set<String> permissions) { this.permissions = permissions; }
    
    public Set<String> getManagedAreas() { return managedAreas; }
    public void setManagedAreas(Set<String> managedAreas) { this.managedAreas = managedAreas; }
    
    public Set<String> getAccessLogs() { return accessLogs; }
    public void setAccessLogs(Set<String> accessLogs) { this.accessLogs = accessLogs; }
}