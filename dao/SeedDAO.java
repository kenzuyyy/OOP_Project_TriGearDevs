package dao;

import db.DataBaseManager;
import Model.Seed;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeedDAO {

    public boolean addSeed(String name, int quantity, int farmerId, String status) {
        String sql = "INSERT INTO seeds (seed_name, quantity, farmer_id, status, desired_trade) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DataBaseManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt(2, quantity);
            ps.setInt(3, farmerId);
            ps.setString(4, status);
            ps.setString(5, ""); // default empty
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Seed insert error: " + e.getMessage());
            return false;
        }
    }

    public List<Seed> getAllSeeds() {
        List<Seed> list = new ArrayList<>();
        String sql = "SELECT * FROM seeds";
        try (Connection conn = DataBaseManager.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Seed(
                        rs.getInt("id"),
                        rs.getString("seed_name"),
                        rs.getInt("quantity"),
                        rs.getInt("farmer_id"),
                        rs.getString("status"),
                        rs.getString("desired_trade")));
            }
        } catch (SQLException e) {
            System.out.println("Load seeds error: " + e.getMessage());
        }
        return list;
    }

    public boolean updateSeedWithTrade(int id, int newQty, String newStatus, String desiredTrade) {
        String sql = "UPDATE seeds SET quantity=?, status=?, desired_trade=? WHERE id=?";
        try (Connection conn = DataBaseManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newQty);
            ps.setString(2, newStatus);
            ps.setString(3, desiredTrade);
            ps.setInt(4, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Update with trade error: " + e.getMessage());
            return false;
        }
    }

    public ArrayList<Seed> loadSeedsByFarmer(int farmerId) {
        ArrayList<Seed> seeds = new ArrayList<>();
        String sql = "SELECT * FROM seeds WHERE farmer_id = ?";
        try (Connection conn = DataBaseManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, farmerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                seeds.add(new Seed(
                        rs.getInt("id"),
                        rs.getString("seed_name"),
                        rs.getInt("quantity"),
                        rs.getInt("farmer_id"),
                        rs.getString("status"),
                        rs.getString("desired_trade")));
            }
        } catch (SQLException e) {
            System.out.println("Load seeds by farmer error: " + e.getMessage());
        }
        return seeds;
    }

    // Added method
    public boolean deleteSeed(int id) {
        String sql = "DELETE FROM seeds WHERE id=?";
        try (Connection conn = DataBaseManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Delete error: " + e.getMessage());
            return false;
        }
    }
}