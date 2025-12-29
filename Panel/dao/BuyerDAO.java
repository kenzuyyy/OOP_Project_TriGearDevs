package dao;

import Model.Seed;
import Model.TradeRequest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BuyerDAO {

    // --- Get all available seeds ---
    public List<Seed> getAllAvailableSeeds() {
        List<Seed> list = new ArrayList<>();
        String sql = "SELECT s.id, sm.seed_name, s.quantity, s.farmer_id, s.status, s.desired_trade " +
                "FROM seeds s " +
                "JOIN seed_master sm ON s.seed_id = sm.id " +
                "WHERE s.status IN ('Available', 'For Trade')";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Seed s = new Seed(
                        rs.getInt("id"),
                        rs.getString("seed_name"), // ✅ from seed_master
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

    // --- Get all trade requests by buyer ---
    public List<TradeRequest> getTradeRequestsByBuyer(int buyerId) {
        List<TradeRequest> list = new ArrayList<>();
        String sql = "SELECT tr.id, sm.seed_name, s.desired_trade, tr.offer, tr.request_date " +
                "FROM trade_requests tr " +
                "JOIN seeds s ON tr.seed_id = s.id " +
                "JOIN seed_master sm ON s.seed_id = sm.id " +
                "WHERE tr.buyer_id = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, buyerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                TradeRequest tr = new TradeRequest(
                        rs.getInt("id"),
                        rs.getString("seed_name"), // ✅ from seed_master
                        rs.getString("desired_trade"),
                        rs.getString("offer"),
                        rs.getTimestamp("request_date"));
                list.add(tr);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // --- Purchase seed ---
    public boolean purchaseSeed(int seedId, int qty, int buyerId) {
        String update = "UPDATE seeds SET quantity = quantity - ? WHERE id=? AND quantity >= ?";
        String purchase = "INSERT INTO purchases (seed_id, buyer_id, quantity, purchase_date, desired_trade) VALUES (?, ?, ?, NOW(), ?)";
        String mark = "UPDATE seeds SET status='Out of Stock' WHERE id=? AND quantity=0";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            // Get desired trade info
            String desiredTrade = "";
            try (PreparedStatement psSeed = conn.prepareStatement("SELECT desired_trade FROM seeds WHERE id=?")) {
                psSeed.setInt(1, seedId);
                ResultSet rs = psSeed.executeQuery();
                if (rs.next()) {
                    desiredTrade = rs.getString("desired_trade");
                }
            }

            // Update seed quantity
            try (PreparedStatement psUpdate = conn.prepareStatement(update)) {
                psUpdate.setInt(1, qty);
                psUpdate.setInt(2, seedId);
                psUpdate.setInt(3, qty);
                int rows = psUpdate.executeUpdate();
                if (rows == 0) {
                    conn.rollback();
                    return false;
                }
            }

            // Insert purchase record with desired trade
            try (PreparedStatement psPurchase = conn.prepareStatement(purchase)) {
                psPurchase.setInt(1, seedId);
                psPurchase.setInt(2, buyerId);
                psPurchase.setInt(3, qty);
                psPurchase.setString(4, desiredTrade);
                psPurchase.executeUpdate();
            }

            // Mark out of stock if needed
            try (PreparedStatement psMark = conn.prepareStatement(mark)) {
                psMark.setInt(1, seedId);
                psMark.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- Request trade ---
    public boolean requestTrade(int seedId, int buyerId, String offer) {
        String sql = "INSERT INTO trade_requests (seed_id, buyer_id, offer, request_date) VALUES (?, ?, ?, NOW())";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, seedId);
            ps.setInt(2, buyerId);
            ps.setString(3, offer);

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- Request trade for ALL available seeds/products ---
    public boolean requestTradeForAll(int buyerId, String offer) {
        // Optional: basic server-side validation to avoid empty offers
        if (offer == null || offer.trim().isEmpty()) {
            return false;
        }

        String sql = "INSERT INTO trade_requests (seed_id, buyer_id, offer, request_date) " +
                "SELECT s.id, ?, ?, NOW() " +
                "FROM seeds s " +
                "WHERE s.status IN ('Available','For Trade') AND s.quantity > 0";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, buyerId);
            ps.setString(2, offer);

            int rows = ps.executeUpdate();
            return rows > 0; // true if at least one trade request was created

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- Update trade request offer ---
    public boolean updateTradeRequest(int requestId, String newOffer) {
        String sql = "UPDATE trade_requests SET offer=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newOffer);
            ps.setInt(2, requestId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- Delete trade request ---
    public boolean deleteTradeRequest(int requestId) {
        String sql = "DELETE FROM trade_requests WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}