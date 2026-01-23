package com.example.demo.user;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class AppUser {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 80)
    private String username;

    @Column(nullable = false, length = 200)
    private String passwordHash;

    @Column(nullable = false, length = 30)
    private String role = "ADMIN"; // ADMIN ou USER

    @Column(nullable = false)
    private boolean enabled = true;

    public AppUser() {}

    public AppUser(String username, String passwordHash, String role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.enabled = true;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getRole() { return role; }
    public boolean isEnabled() { return enabled; }

    public void setUsername(String username) { this.username = username; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setRole(String role) { this.role = role; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
