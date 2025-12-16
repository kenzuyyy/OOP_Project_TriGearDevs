package dao;

import Model.Seed;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FarmerDAO {

    // ---------------- GET SEEDS BY FARMER ----------------
    public List<Seed> getSeedsByFarmer(int farmerId) {
        List<Seed> list = new ArrayList<>();
        String sql = "SELECT * FROM seeds WHERE farmer_id=?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, farmerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Seed s = new Seed(
                        rs.getInt("id"),
                        rs.getString("seed_name"),
                        rs.getInt("quantity"),
                        rs.getInt("farmer_id"),
                        rs.getString("status"),
                        rs.getString("desired_trade"));
                list.add(s);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ---------------- ADD SEED ----------------
    public boolean addSeed(int farmerId, String name, int quantity, String status, String desiredTrade) {
        // ✅ Quantity safeguard
        if (quantity <= 0) {
            System.out.println("Quantity must be greater than zero.");
            return false;
        }

        // ✅ Seed/product validation
        if (!isValidSeed(name)) {
            System.out.println("Invalid seed/product name: " + name);
            return false;
        }

        String sql = "INSERT INTO seeds (seed_name, quantity, farmer_id, status, desired_trade) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setInt(2, quantity);
            ps.setInt(3, farmerId);
            ps.setString(4, status);
            ps.setString(5, desiredTrade);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ---------------- UPDATE SEED ----------------
    public boolean updateSeed(int seedId, String name, int quantity, String status, String desiredTrade) {
        // ✅ Quantity safeguard
        if (quantity <= 0) {
            System.out.println("Quantity must be greater than zero.");
            return false;
        }

        // ✅ Seed/product validation
        if (!isValidSeed(name)) {
            System.out.println("Invalid seed/product name: " + name);
            return false;
        }

        String sql = "UPDATE seeds SET seed_name=?, quantity=?, status=?, desired_trade=? WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setInt(2, quantity);
            ps.setString(3, status);
            ps.setString(4, desiredTrade);
            ps.setInt(5, seedId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ---------------- DELETE SEED ----------------
    public boolean deleteSeed(int seedId) {
        String sql = "DELETE FROM seeds WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, seedId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ---------------- VALIDATE SEED ----------------
    public boolean isValidSeed(String seedName) {
        // This assumes you have a master list of valid seeds/products in a table called
        // "seed_master"
        String sql = "SELECT 1 FROM seed_master WHERE seed_name = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, seedName);
            ResultSet rs = ps.executeQuery();
            return rs.next(); // true if seed exists in master list

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}