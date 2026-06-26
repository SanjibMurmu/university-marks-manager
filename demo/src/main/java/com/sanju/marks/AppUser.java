package com.sanju.marks;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents a unified user entity within the system.
 * This class handles authentication credentials, personal details, and role-based 
 * access control (RBAC) for Students, Teachers, and Administrators.
 * It also stores temporary tokens for secure password recovery.
 *
 * @author Sanjib Murmu
 * @version 1.0
 */

@Entity
@Table(name = "app_user")
public class AppUser {

    @Id
    
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username; 

    @Column(nullable = false)
    private String password;
    private String role;
    private String fullName;

    @Column(unique = true)
    private String email;

    private String resetToken;
    private LocalDateTime tokenExpiry;

    /**
     * Default no-argument constructor required by JPA/Hibernate.
     * Framework use only. Do not use this to instantiate new users manually.
     */
    public AppUser() {}

    /**
     * Constructs a fully initialized AppUser entity.
     *
     * @param username The unique login identifier (Roll No or text).
     * @param password The encrypted BCrypt password hash.
     * @param role The system role (e.g., "student", "teacher", "admin").
     * @param fullName The user's legal name.
     * @param email The registered email address for account recovery.
     */
    public AppUser(String username, String password, String role, String fullName,String email) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
        this.email = email;
    }

    public Long getId() { return id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getResetToken() { return resetToken; }
    public void setResetToken(String resetToken) { this.resetToken = resetToken; }

    public LocalDateTime getTokenExpiry() { return tokenExpiry; }
    public void setTokenExpiry(LocalDateTime tokenExpiry) { this.tokenExpiry = tokenExpiry; }
}
