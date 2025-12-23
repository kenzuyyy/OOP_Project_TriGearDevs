package Model;

import java.time.LocalDate;

public class User {
    private int id;
    private String username;
    private String email;
    private String passwordHash;
    private String role;
    private LocalDate birthdate;

    // Default constructor
    public User() {
    }

    // Registration without birthdate
    public User(String username, String email, String passwordHash, String role) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    // Registration with birthdate
    public User(String username, String email, String passwordHash, String role, LocalDate birthdate) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.birthdate = birthdate;
    }

    // Existing user (login from DB without birthdate)
    public User(int id, String username, String email, String passwordHash, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    // Existing user (login from DB with birthdate)
    public User(int id, String username, String email, String passwordHash, String role, LocalDate birthdate) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.birthdate = birthdate;
    }

    // Existing user (id, username, email, role only)
    public User(int id, String username, String email, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    // --- Getters ---
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getRole() {
        return role;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    // --- Setters ---
    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }
}