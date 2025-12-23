package dao;

import Model.Seed;
import java.sql.*;
import java.util.ArrayList;

public class AdminSeedDAO {

    private Connection conn;

    public AdminSeedDAO() {
        conn = DBConnection.getConnection();
    }

    public ArrayList<Seed> getAllSeeds() {
        ArrayList<Seed> list = new ArrayList<>();

        try {
            String sql = "SELECT * FROM seeds";
            ResultSet rs = conn.prepareStatement(sql).executeQuery();

            while (rs.next()) {
                list.add(new Seed(
                        rs.getInt("id"),
                        rs.getString("seed_name"), // fixed column name
                        rs.getInt("quantity"),
                        rs.getInt("farmer_id"),
                        rs.getString("status")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean deleteSeed(int id) {
        try {
            String sql = "DELETE FROM seeds WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            return rows > 0; // return true if at least one row deleted
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}