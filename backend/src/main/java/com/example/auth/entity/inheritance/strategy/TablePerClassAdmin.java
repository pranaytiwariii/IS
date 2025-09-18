package com.example.auth.entity.inheritance.strategy;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Admin class using Table Per Class Inheritance
 * Has its own COMPLETE table with ALL parent fields + admin fields
 */
@Entity
@Table(name = "table_per_class_admins")
public class TablePerClassAdmin extends TablePerClassPerson {
    
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
    
    @Column(name = "failed_login_attempts")
    private Integer failedLoginAttempts = 0;
    
    @Column(name = "account_locked")
    private Boolean accountLocked = false;
    
    @Column(name = "session_timeout_minutes")
    private Integer sessionTimeoutMinutes = 30;
    
    @Column(name = "two_factor_enabled")
    private Boolean twoFactorEnabled = false;
    
    @ElementCollection
    @CollectionTable(name = "table_per_class_admin_permissions", 
                     joinColumns = @JoinColumn(name = "admin_id"))
    @Column(name = "permission")
    private Set<String> permissions = new HashSet<>();
    
    @ElementCollection
    @CollectionTable(name = "table_per_class_admin_managed_areas", 
                     joinColumns = @JoinColumn(name = "admin_id"))
    @Column(name = "managed_area")
    private Set<String> managedAreas = new HashSet<>();
    
    @ElementCollection
    @CollectionTable(name = "table_per_class_admin_access_logs", 
                     joinColumns = @JoinColumn(name = "admin_id"))
    @Column(name = "access_log")
    private Set<String> accessLogs = new HashSet<>();
    
    @ElementCollection
    @CollectionTable(name = "table_per_class_admin_ip_whitelist", 
                     joinColumns = @JoinColumn(name = "admin_id"))
    @Column(name = "ip_address")
    private Set<String> allowedIpAddresses = new HashSet<>();
    
    // Default constructor
    public TablePerClassAdmin() {
        super();
        this.lastPasswordChange = LocalDateTime.now();
    }
    
    // Constructor
    public TablePerClassAdmin(String firstName, String lastName, String email, 
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
                twoFactorEnabled = true;
                sessionTimeoutMinutes = 60;
                permissions.addAll(Set.of("ALL_PERMISSIONS", "SYSTEM_CONFIG", "USER_MANAGEMENT", 
                                        "CONTENT_MANAGEMENT", "REPORTING", "PAPER_APPROVAL", "DATABASE_ACCESS",
                                        "SECURITY_SETTINGS", "AUDIT_LOGS"));
                managedAreas.addAll(Set.of("ALL_DEPARTMENTS", "SYSTEM_SETTINGS", "USER_ACCOUNTS", "SECURITY"));
                break;
            case "DEPARTMENT":
                canManageUsers = true;
                canViewReports = true;
                canApprovePapers = true;
                twoFactorEnabled = true;
                sessionTimeoutMinutes = 45;
                permissions.addAll(Set.of("DEPARTMENT_MANAGEMENT", "USER_MANAGEMENT", "REPORTING", 
                                        "PAPER_REVIEW", "DEPARTMENT_SETTINGS"));
                managedAreas.addAll(Set.of(department, "DEPARTMENT_USERS", "DEPARTMENT_CONTENT"));
                break;
            case "CONTENT":
                canManageContent = true;
                canApprovePapers = true;
                sessionTimeoutMinutes = 40;
                permissions.addAll(Set.of("CONTENT_MANAGEMENT", "CONTENT_REVIEW", "CONTENT_PUBLISHING", 
                                        "PAPER_APPROVAL", "CONTENT_ANALYTICS"));
                managedAreas.addAll(Set.of("CONTENT_REVIEW", "PUBLICATIONS", "CONTENT_ANALYTICS"));
                break;
            default: // USER level
                canViewReports = true;
                sessionTimeoutMinutes = 30;
                permissions.add("BASIC_ADMIN");
                managedAreas.add("LIMITED_ACCESS");
                break;
        }
    }
    
    public boolean attemptLogin(String ipAddress) {
        if (accountLocked) {
            logAccess("LOGIN_ATTEMPT_BLOCKED - Account locked from " + ipAddress);
            return false;
        }
        
        if (!allowedIpAddresses.isEmpty() && !allowedIpAddresses.contains(ipAddress)) {
            failedLoginAttempts++;
            logAccess("LOGIN_FAILED - IP not whitelisted: " + ipAddress);
            checkAccountLock();
            return false;
        }
        
        this.lastLogin = LocalDateTime.now();
        this.loginCount++;
        this.failedLoginAttempts = 0; // Reset on successful login
        logAccess("LOGIN_SUCCESS from " + ipAddress);
        return true;
    }
    
    public void recordFailedLogin(String ipAddress) {
        failedLoginAttempts++;
        logAccess("LOGIN_FAILED from " + ipAddress + " (Attempt " + failedLoginAttempts + ")");
        checkAccountLock();
    }
    
    private void checkAccountLock() {
        if (failedLoginAttempts >= 5) {
            accountLocked = true;
            logAccess("ACCOUNT_LOCKED - Too many failed attempts");
        }
    }
    
    public void unlockAccount() {
        accountLocked = false;
        failedLoginAttempts = 0;
        logAccess("ACCOUNT_UNLOCKED");
    }
    
    public void recordLogout() {
        logAccess("LOGOUT");
    }
    
    public void logAccess(String action) {
        String logEntry = LocalDateTime.now() + ": " + action;
        accessLogs.add(logEntry);
        // Keep only last 20 log entries for table per class (more storage)
        if (accessLogs.size() > 20) {
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
        logAccess("MANAGED_AREA_ADDED: " + area);
    }
    
    public void removeManagedArea(String area) {
        managedAreas.remove(area);
        logAccess("MANAGED_AREA_REMOVED: " + area);
    }
    
    public void addAllowedIpAddress(String ipAddress) {
        allowedIpAddresses.add(ipAddress);
        logAccess("IP_WHITELISTED: " + ipAddress);
    }
    
    public void removeAllowedIpAddress(String ipAddress) {
        allowedIpAddresses.remove(ipAddress);
        logAccess("IP_REMOVED_FROM_WHITELIST: " + ipAddress);
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
    
    public void enableTwoFactor() {
        this.twoFactorEnabled = true;
        logAccess("TWO_FACTOR_ENABLED");
    }
    
    public void disableTwoFactor() {
        this.twoFactorEnabled = false;
        logAccess("TWO_FACTOR_DISABLED");
    }
    
    public boolean hasPermission(String permission) {
        return isSuperAdmin || permissions.contains(permission) || permissions.contains("ALL_PERMISSIONS");
    }
    
    public boolean canManageArea(String area) {
        return isSuperAdmin || managedAreas.contains(area) || managedAreas.contains("ALL_DEPARTMENTS");
    }
    
    public boolean isIpAllowed(String ipAddress) {
        return allowedIpAddresses.isEmpty() || allowedIpAddresses.contains(ipAddress);
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
        if (loginCount >= 500) return "Extremely Active";
        if (loginCount >= 300) return "Very Active";
        if (loginCount >= 150) return "Active";
        if (loginCount >= 50) return "Moderate";
        if (loginCount >= 10) return "Regular";
        return "New Admin";
    }
    
    public String getSecurityLevel() {
        int securityScore = 0;
        
        if (twoFactorEnabled) securityScore += 3;
        if (!allowedIpAddresses.isEmpty()) securityScore += 2;
        if (lastPasswordChange != null) {
            long daysSinceChange = java.time.Duration.between(lastPasswordChange, LocalDateTime.now()).toDays();
            if (daysSinceChange <= 30) securityScore += 2;
            else if (daysSinceChange <= 60) securityScore += 1;
        }
        if (failedLoginAttempts == 0) securityScore += 1;
        
        if (securityScore >= 7) return "Excellent Security";
        if (securityScore >= 5) return "Good Security";
        if (securityScore >= 3) return "Moderate Security";
        return "Security Needs Attention";
    }
    
    public boolean isExperiencedAdmin() {
        return loginCount >= 200 && permissions.size() >= 8 && papersReviewed >= 50;
    }
    
    public boolean isActiveReviewer() {
        return canApprovePapers && papersReviewed >= 25;
    }
    
    public boolean isHighSecurityAdmin() {
        return twoFactorEnabled && !allowedIpAddresses.isEmpty() && failedLoginAttempts == 0;
    }
    
    @Override
    public String getTablePerClassInfo() {
        return String.format("""
            %s
            
            === ADMIN TABLE STRUCTURE ===
            Table: table_per_class_admins
            Contains: ALL parent fields + admin fields
            
            Parent Fields (duplicated from TablePerClassPerson):
            - id, first_name, last_name, email, phone_number, created_at
            
            Admin Fields:
            - admin_id, department, admin_level, last_login, login_count, is_super_admin,
              can_manage_users, can_manage_content, can_view_reports, can_approve_papers,
              papers_reviewed, last_password_change, failed_login_attempts, account_locked,
              session_timeout_minutes, two_factor_enabled
            
            === ADMIN-SPECIFIC DATA ===
            Admin ID: %s (NOT NULL constraint possible)
            Department: %s (NOT NULL constraint possible)
            Admin Level: %s | Status: %s
            Super Admin: %s
            Last Login: %s | Activity Level: %s
            Login Count: %d | Failed Attempts: %d
            Account Locked: %s
            Papers Reviewed: %d | Active Reviewer: %s
            Last Password Change: %s
            Two-Factor Enabled: %s
            Session Timeout: %d minutes
            Security Level: %s
            High Security Admin: %s
            
            === PERMISSIONS & SECURITY ===
            Can Manage Users: %s
            Can Manage Content: %s
            Can View Reports: %s
            Can Approve Papers: %s
            Specific Permissions: %s
            Managed Areas: %s
            Allowed IP Addresses: %s
            Experienced Admin: %s
            
            Table Per Class Benefits for Admins:
            ✅ Complete admin data in single table (no JOINs for admin operations)
            ✅ NOT NULL constraints on all admin security fields
            ✅ Fast admin authentication and authorization queries
            ✅ Independent admin table optimization and indexing
            ✅ Enhanced security through isolated admin data
            ✅ Easy to implement admin-specific features and constraints
            
            Table Per Class Drawbacks for Admins:
            ❌ Parent fields duplicated across all person tables
            ❌ Schema changes affect multiple person tables
            ❌ Polymorphic user management queries require UNION
            ❌ Cross-person-type reporting and analytics are complex
            
            Query Pattern Examples:
            
            1. Fast admin authentication (OPTIMIZED):
            SELECT * FROM table_per_class_admins 
            WHERE admin_id = 'ADM001' AND account_locked = false;
            
            2. Security audit query (EFFICIENT):
            SELECT admin_id, login_count, failed_login_attempts, last_login, two_factor_enabled
            FROM table_per_class_admins
            WHERE last_password_change < NOW() - INTERVAL '90 days'
               OR failed_login_attempts > 3
               OR two_factor_enabled = false;
            
            3. Polymorphic user count (COMPLEX):
            SELECT 'TOTAL_USERS' as category, COUNT(*) as count FROM (
                SELECT id FROM table_per_class_students
                UNION ALL
                SELECT id FROM table_per_class_authors
                UNION ALL
                SELECT id FROM table_per_class_admins
            ) all_users;
            
            4. Department admin management (FAST):
            SELECT admin_id, first_name, last_name, admin_level, papers_reviewed
            FROM table_per_class_admins
            WHERE department = 'Computer Science' AND can_approve_papers = true
            ORDER BY papers_reviewed DESC;
            """,
            super.getTablePerClassInfo(),
            adminId,
            department,
            adminLevel,
            getAdminStatus(),
            isSuperAdmin ? "Yes" : "No",
            lastLogin != null ? lastLogin.toString() : "Never",
            getActivityLevel(),
            loginCount,
            failedLoginAttempts,
            accountLocked ? "Yes" : "No",
            papersReviewed,
            isActiveReviewer() ? "Yes" : "No",
            lastPasswordChange != null ? lastPasswordChange.toString() : "Never",
            twoFactorEnabled ? "Yes" : "No",
            sessionTimeoutMinutes,
            getSecurityLevel(),
            isHighSecurityAdmin() ? "Yes" : "No",
            canManageUsers ? "Yes" : "No",
            canManageContent ? "Yes" : "No",
            canViewReports ? "Yes" : "No",
            canApprovePapers ? "Yes" : "No",
            permissions.isEmpty() ? "None" : String.join(", ", permissions),
            managedAreas.isEmpty() ? "None" : String.join(", ", managedAreas),
            allowedIpAddresses.isEmpty() ? "Any IP" : String.join(", ", allowedIpAddresses),
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
    
    public Integer getFailedLoginAttempts() { return failedLoginAttempts; }
    public void setFailedLoginAttempts(Integer failedLoginAttempts) { this.failedLoginAttempts = failedLoginAttempts; }
    
    public Boolean getAccountLocked() { return accountLocked; }
    public void setAccountLocked(Boolean accountLocked) { this.accountLocked = accountLocked; }
    
    public Integer getSessionTimeoutMinutes() { return sessionTimeoutMinutes; }
    public void setSessionTimeoutMinutes(Integer sessionTimeoutMinutes) { this.sessionTimeoutMinutes = sessionTimeoutMinutes; }
    
    public Boolean getTwoFactorEnabled() { return twoFactorEnabled; }
    public void setTwoFactorEnabled(Boolean twoFactorEnabled) { this.twoFactorEnabled = twoFactorEnabled; }
    
    public Set<String> getPermissions() { return permissions; }
    public void setPermissions(Set<String> permissions) { this.permissions = permissions; }
    
    public Set<String> getManagedAreas() { return managedAreas; }
    public void setManagedAreas(Set<String> managedAreas) { this.managedAreas = managedAreas; }
    
    public Set<String> getAccessLogs() { return accessLogs; }
    public void setAccessLogs(Set<String> accessLogs) { this.accessLogs = accessLogs; }
    
    public Set<String> getAllowedIpAddresses() { return allowedIpAddresses; }
    public void setAllowedIpAddresses(Set<String> allowedIpAddresses) { this.allowedIpAddresses = allowedIpAddresses; }
}