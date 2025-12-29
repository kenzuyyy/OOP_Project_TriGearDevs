package dao;

import Model.Seed;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FarmerDAO {

    // ---------------- GET SEEDS BY FARMER ----------------
    public List<Seed> getSeedsByFarmer(int farmerId) {
        List<Seed> list = new ArrayList<>();
        // ✅ Join with seed_master to get seed_name
        String sql = "SELECT s.id, sm.seed_name, s.quantity, s.farmer_id, s.status, s.desired_trade " +
                "FROM seeds s " +
                "JOIN seed_master sm ON s.seed_id = sm.id " +
                "WHERE s.farmer_id=?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, farmerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Seed s = new Seed(
                        rs.getInt("id"), // PK from seeds
                        rs.getString("seed_name"), // ✅ from seed_master
                        rs.getInt("quantity"),
                        rs.getInt("farmer_id"),
                        rs.getString("status"),
                        rs.getString("desired_trade"));
                list.add(s);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ---------------- ADD SEED ----------------
    public boolean addSeed(int farmerId, int seedId, int quantity, String status, String desiredTrade) {
        if (quantity <= 0) {
            System.out.println("Quantity must be greater than zero.");
            return false;
        }

        if (!isValidSeedId(seedId)) {
            System.out.println("Invalid seed/product id: " + seedId);
            return false;
        }

        if (desiredTrade != null && !desiredTrade.isEmpty() && !isValidTradeProduct(desiredTrade)) {
            System.out.println("Invalid desired trade product: " + desiredTrade);
            return false;
        }

        String sql = "INSERT INTO seeds (seed_id, quantity, farmer_id, status, desired_trade) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, seedId);
            ps.setInt(2, quantity);
            ps.setInt(3, farmerId);
            ps.setString(4, status);
            ps.setString(5, desiredTrade);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ---------------- UPDATE SEED ----------------
    public boolean updateSeed(int seedId, int seedMasterId, int quantity, String status, String desiredTrade) {
        if (quantity <= 0) {
            System.out.println("Quantity must be greater than zero.");
            return false;
        }

        if (!isValidSeedId(seedMasterId)) {
            System.out.println("Invalid seed/product id: " + seedMasterId);
            return false;
        }

        if (desiredTrade != null && !desiredTrade.isEmpty() && !isValidTradeProduct(desiredTrade)) {
            System.out.println("Invalid desired trade product: " + desiredTrade);
            return false;
        }

        String sql = "UPDATE seeds SET seed_id=?, quantity=?, status=?, desired_trade=? WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, seedMasterId);
            ps.setInt(2, quantity);
            ps.setString(3, status);
            ps.setString(4, desiredTrade);
            ps.setInt(5, seedId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
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

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ---------------- VALIDATE SEED BY ID ----------------
    public boolean isValidSeedId(int seedId) {
        String sql = "SELECT 1 FROM seed_master WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, seedId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ---------------- LOOKUP SEED ID BY NAME ----------------
    public int getSeedIdByName(String seedName) {
        String sql = "SELECT id FROM seed_master WHERE seed_name = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, seedName.trim());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // not found
    }

    // ---------------- VALIDATE TRADE PRODUCT ----------------
    public boolean isValidTradeProduct(String productName) {
        String sql = "SELECT 1 FROM seed_master WHERE seed_name = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, productName.trim());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}