package dao;

import db.DataBaseManager;
import Model.User;
import util.SecurityUtil;
import java.sql.*;

public class UserDAO {

    // Register new user
    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (username, email, password_hash, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = DataBaseManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            // Safeguard: block registering another admin
            if ("admin".equalsIgnoreCase(user.getRole())) {
                String checkAdminSql = "SELECT COUNT(*) FROM users WHERE role = 'admin'";
                try (PreparedStatement psCheck = conn.prepareStatement(checkAdminSql)) {
                    ResultSet rs = psCheck.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.out.println("Admin registration blocked: an admin already exists.");
                        return false;
                    }
                }
            }

            ps.setString(1, user.getUsername().trim());
            ps.setString(2, user.getEmail().trim());
            ps.setString(3, SecurityUtil.hashPassword(user.getPassword().trim())); // ✅ hash once here
            ps.setString(4, user.getRole());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error during registration: " + e.getMessage());
            return false;
        }
    }

    // Login user
    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password_hash = ?";
        try (Connection conn = DataBaseManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username.trim());
            ps.setString(2, SecurityUtil.hashPassword(password.trim())); // ✅ hash once here

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password_hash"), // hashed from DB
                        rs.getString("role"));
            }
        } catch (SQLException e) {
            System.err.println("Error during login: " + e.getMessage());
        }
        return null;
    }

    // Check if email already exists
    public boolean isEmailTaken(String email) {
        String sql = "SELECT id FROM users WHERE email = ?";
        try (Connection conn = DataBaseManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            ResultSet rs = ps.executeQuery();
            return rs.next(); // true if a row exists
        } catch (SQLException e) {
            System.err.println("Error checking email: " + e.getMessage());
            return false;
        }
    }

    // Check if username already exists
    public boolean isUsernameTaken(String username) {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = DataBaseManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            ResultSet rs = ps.executeQuery();
            return rs.next(); // true if a row exists
        } catch (SQLException e) {
            System.err.println("Error checking username: " + e.getMessage());
            return false;
        }
    }
}