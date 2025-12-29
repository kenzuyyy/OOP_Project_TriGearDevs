package dao;

import Model.Seed;
import java.sql.*;
import java.util.ArrayList;

public class AdminSeedDAO {

    public ArrayList<Seed> getAllSeeds() {
        ArrayList<Seed> list = new ArrayList<>();
        String sql = "SELECT * FROM seeds";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Seed(
                        rs.getInt("id"),
                        rs.getString("seed_name"),
                        rs.getInt("quantity"),
                        rs.getInt("farmer_id"),
                        rs.getString("status"),
                        rs.getString("desired_trade") // include if column exists
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean deleteSeed(int id) {
        String sql = "DELETE FROM seeds WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isValidSeed(String seedName) {
        String sql = "SELECT 1 FROM seed_master WHERE seed_name = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, seedName);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}