package Panel;

import Model.TradeRequest;
import dao.BuyerDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TradeRequestsPanel extends JPanel {
    private JFrame frame;

    public JFrame getFrame() {
        return frame;
    }

    public void setFrame(JFrame frame) {
        this.frame = frame;
    }

    private int buyerId;
    private BuyerDAO buyerDAO = new BuyerDAO();
    private JTable tradeTable;
    private DefaultTableModel tradeModel;

    public TradeRequestsPanel(JFrame frame, int buyerId) {
        this.frame = frame;
        this.buyerId = buyerId;

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 250));

        JLabel title = new JLabel("Your Trade Requests", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(45, 95, 150));
        add(title, BorderLayout.NORTH);

        tradeModel = new DefaultTableModel(
                new String[] { "ID", "Seed Name", "Desired Trade", "Your Offer", "Request Date" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tradeTable = new JTable(tradeModel);
        tradeTable.setRowHeight(30);
        tradeTable.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JScrollPane scroll = new JScrollPane(tradeTable);
        add(scroll, BorderLayout.CENTER);

        loadTradeRequests();
    }

    private void loadTradeRequests() {
        tradeModel.setRowCount(0);
        List<TradeRequest> requests = buyerDAO.getTradeRequestsByBuyer(buyerId);
        for (TradeRequest tr : requests) {
            tradeModel.addRow(new Object[] {
                    tr.getId(),
                    tr.getSeedName(),
                    tr.getDesiredTrade(),
                    tr.getOffer(),
                    tr.getRequestDate()
            });
        }
    }
}