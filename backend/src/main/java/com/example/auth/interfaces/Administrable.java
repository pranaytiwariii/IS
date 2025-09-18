package com.example.auth.interfaces;

/**
 * Interface for administrative capabilities
 * Administrators can manage users and system settings
 */
public interface Administrable {
    
    /**
     * Create a new user account
     * @param username the username
     * @param email the email
     * @param role the user role
     * @return true if user creation was successful
     */
    boolean createUser(String username, String email, String role);
    
    /**
     * Delete a user account
     * @param userId the ID of the user to delete
     * @return true if deletion was successful
     */
    boolean deleteUser(Long userId);
    
    /**
     * Modify user permissions
     * @param userId the ID of the user
     * @param newRole the new role to assign
     * @return true if role change was successful
     */
    boolean changeUserRole(Long userId, String newRole);
    
    /**
     * Get system statistics
     * @return array of system stats
     */
    String[] getSystemStatistics();
    
    /**
     * Backup system data
     * @return true if backup was successful
     */
    boolean backupSystemData();
    
    /**
     * Get administrative permissions level
     * @return the clearance level (1-5)
     */
    int getAdministrativeLevel();
    
    /**
     * Check if user can perform administrative action
     * @param action the action to check
     * @return true if user can perform this action
     */
    default boolean canPerformAdminAction(String action) {
        int level = getAdministrativeLevel();
        return switch (action.toLowerCase()) {
            case "view_users", "view_reports" -> level >= 1;
            case "create_user", "edit_user" -> level >= 2;
            case "delete_user", "manage_roles" -> level >= 3;
            case "system_settings", "backup_data" -> level >= 4;
            case "full_system_access", "emergency_override" -> level >= 5;
            default -> false;
        };
    }
}