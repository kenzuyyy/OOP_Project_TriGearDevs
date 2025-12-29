package Model;

import java.time.LocalDate;

public class User {
    private int id;
    private String username;
    private String email;
    private String password; // plain text at registration, hashed in DAO
    private String role;
    private LocalDate birthdate;

    // Existing user (id, username, email, role only)
    public User(int id, String username, String email, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    // Default constructor
    public User() {
    }

    // Registration without birthdate
    public User(String username, String email, String password, String role) {
        this.username = username;
        this.email = email;
        this.password = password; // plain text here
        this.role = role;
    }

    // Registration with birthdate
    public User(String username, String email, String password, String role, LocalDate birthdate) {
        this.username = username;
        this.email = email;
        this.password = password; // plain text here
        this.role = role;
        this.birthdate = birthdate;
    }

    // Existing user (login from DB)
    public User(int id, String username, String email, String password, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password; // hashed value from DB
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

    public String getPassword() {
        return password;
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

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }
}