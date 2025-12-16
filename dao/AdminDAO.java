package dao;

import Model.User;
import java.sql.*;
import java.util.ArrayList;

public class AdminDAO {

    private Connection conn;

    public AdminDAO() {
        conn = DBConnection.getConnection();
    }

    public ArrayList<User> getAllUsers() {
        ArrayList<User> list = new ArrayList<>();

        try {
            String sql = "SELECT * FROM users";
            ResultSet rs = conn.prepareStatement(sql).executeQuery();

            while (rs.next()) {
                list.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password_hash"), // match your DB schema
                        rs.getString("role")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean deleteUser(int id) {
        try {
            String sql = "DELETE FROM users WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            return rows > 0; // true if at least one row deleted
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}