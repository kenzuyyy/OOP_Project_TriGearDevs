package dao;

import db.DataBaseManager;
import Model.User;
import util.SecurityUtil;
import java.sql.*;

public class UserDAO {
    private Connection conn;

    public UserDAO() {
        conn = DBConnection.getConnection(); // or DataBaseManager.getConnection()
    }

    // Register new user
    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (username, email, password_hash, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = DataBaseManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            // Validate username
            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                System.out.println("ERROR: Username is NULL");
                return false;
            }

            // Validate email
            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                System.out.println("ERROR: Email is NULL");
                return false;
            }

            // Validate password
            if (user.getPasswordHash() == null || user.getPasswordHash().trim().isEmpty()) {
                System.out.println("ERROR: Password hash is NULL");
                return false;
            }

            // Validate role
            String role = user.getRole();
            if (role == null || !(role.equals("farmer") || role.equals("buyer") || role.equals("admin"))) {
                role = "farmer"; // fallback
            }

            // ✅ Safeguard: block registering another admin
            if ("admin".equalsIgnoreCase(role)) {
                String checkAdminSql = "SELECT COUNT(*) FROM users WHERE role = 'admin'";
                try (PreparedStatement psCheck = conn.prepareStatement(checkAdminSql)) {
                    ResultSet rs = psCheck.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.out.println("Admin registration blocked: an admin already exists.");
                        return false;
                    }
                }
            }

            // Bind parameters
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, role);

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("\n===== SQL ERROR DURING REGISTRATION =====");
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("ErrorCode: " + e.getErrorCode());
            System.out.println("Message: " + e.getMessage());
            System.out.println("==========================================\n");
            return false;
        }
    }

    // Check if email already exists
    public boolean isEmailTaken(String email) {
        String sql = "SELECT id FROM users WHERE email = ?";
        try (Connection conn = DataBaseManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Optional: Check if username already exists
    public boolean isUsernameTaken(String username) {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = DataBaseManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Login user
    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password_hash = ?";
        try (Connection conn = DataBaseManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, SecurityUtil.hashPassword(password));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password_hash"),
                        rs.getString("role"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Update user info
    public boolean updateUser(int id, String username, String password, String role) {
        String sql = "UPDATE users SET username=?, password_hash=?, role=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, SecurityUtil.hashPassword(password)); // ✅ hash password
            ps.setString(3, role);
            ps.setInt(4, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public User validateLogin(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password_hash = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username.trim());
            ps.setString(2, SecurityUtil.hashPassword(password.trim()));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("role"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}