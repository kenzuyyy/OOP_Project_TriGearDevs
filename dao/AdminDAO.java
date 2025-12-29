package dao;

import Model.User;
import Model.Seed;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminDAO {

    // ---------------- GET ALL USERS ----------------
    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("role")));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching users: " + e.getMessage());
        }

        return list;
    }

    // ---------------- DELETE USER ----------------
    public boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }

    // ---------------- GET ALL SEEDS ----------------
    public List<Seed> getAllSeeds() {
        List<Seed> list = new ArrayList<>();
        String sql = "SELECT s.id, sm.seed_name, s.quantity, s.status, s.desired_trade, u.username " +
                "FROM seeds s " +
                "JOIN seed_master sm ON s.seed_id = sm.id " +
                "JOIN users u ON s.farmer_id = u.id";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Seed seed = new Seed(
                        rs.getInt("id"),
                        rs.getString("seed_name"),
                        rs.getInt("quantity"),
                        rs.getString("status"),
                        rs.getString("desired_trade"),
                        rs.getString("username") // farmer’s username
                );
                list.add(seed);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching seeds: " + e.getMessage());
        }

        return list;
    }

    // ---------------- DELETE SEED ----------------
    public boolean deleteSeed(int seedId) {
        String sql = "DELETE FROM seeds WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, seedId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting seed: " + e.getMessage());
            return false;
        }
    }

    // ---------------- ADMIN LOGIN CONTROL ----------------
    private static boolean adminLoggedIn = false;

    public boolean login(User user) {
        if ("admin".equalsIgnoreCase(user.getRole())) {
            if (adminLoggedIn) {
                System.out.println("Admin login blocked: another admin session already active.");
                return false; // another admin session already active
            }
            adminLoggedIn = true;
        }
        return true;
    }

    public void logout(User user) {
        if ("admin".equalsIgnoreCase(user.getRole())) {
            adminLoggedIn = false;
        }
    }
}