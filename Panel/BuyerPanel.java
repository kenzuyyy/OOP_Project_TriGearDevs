package Panel;

import Model.Seed;
import Model.User;
import Model.TradeRequest; // <-- new model class for trade requests
import dao.BuyerDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List; // <-- use List instead of ArrayList

public class BuyerPanel extends JPanel {
    private JFrame frame;
    private User buyer;

    private JTable seedTable;
    private DefaultTableModel seedModel;

    private JTable tradeTable;
    private DefaultTableModel tradeModel;

    private BuyerDAO buyerDAO = new BuyerDAO();

    public BuyerPanel(JFrame frame, User buyer) {
        this.frame = frame;
        this.buyer = buyer;

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 250));

        // --- Top Panel (Logo + Title) ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(0, 217, 167));
        topPanel.setPreferredSize(new Dimension(200, 160));

        ImageIcon logoIcon = new ImageIcon("D:\\OOP\\GUI\\AgriConnect\\Logo.png");
        Image scaledLogo = logoIcon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
        logoIcon = new ImageIcon(scaledLogo);

        JLabel logoLabel = new JLabel(logoIcon, SwingConstants.CENTER);
        topPanel.add(logoLabel, BorderLayout.CENTER);

        JLabel title = new JLabel("Available Seeds & Products", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(45, 95, 150));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        topPanel.add(title, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // --- Seeds Table Model ---
        seedModel = new DefaultTableModel(new String[] { "ID", "Name", "Quantity", "Status", "Desired Trade" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        seedTable = new JTable(seedModel);
        seedTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        seedTable.setRowHeight(35);
        seedTable.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        seedTable.setGridColor(new Color(220, 220, 220));
        seedTable.setShowGrid(true);
        seedTable.setFillsViewportHeight(true);
        seedTable.setAutoCreateRowSorter(true);

        JTableHeader seedHeader = seedTable.getTableHeader();
        seedHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        seedHeader.setBackground(new Color(45, 95, 150));
        seedHeader.setForeground(Color.WHITE);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < seedTable.getColumnCount(); i++) {
            seedTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane seedScroll = new JScrollPane(seedTable);
        seedScroll.setBorder(BorderFactory.createTitledBorder("Available Seeds"));

        // --- Trade Requests Table ---
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

        JScrollPane tradeScroll = new JScrollPane(tradeTable);
        tradeScroll.setBorder(BorderFactory.createTitledBorder("Your Trade Requests"));

        // --- Center Panel with both tables stacked ---
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        centerPanel.add(seedScroll);
        centerPanel.add(tradeScroll);
        add(centerPanel, BorderLayout.CENTER);

        // --- Load Data ---
        loadAvailableSeeds();
        loadTradeRequests();

        // --- Bottom Buttons ---
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        bottom.setBackground(new Color(245, 245, 250));

        JButton buyButton = createStyledButton("Buy Selected", new Color(45, 95, 150));
        JButton tradeButton = createStyledButton("Request Trade", new Color(0, 150, 100));
        JButton logoutButton = createStyledButton("Logout", new Color(200, 60, 60));

        bottom.add(buyButton);
        bottom.add(tradeButton);
        bottom.add(logoutButton);
        add(bottom, BorderLayout.SOUTH);

        // --- Actions ---
        buyButton.addActionListener(e -> buySelectedSeed());
        tradeButton.addActionListener(e -> requestTrade());
        logoutButton.addActionListener(e -> logout());

        // --- Minimum size safeguard ---
        frame.setMinimumSize(new Dimension(800, 600));
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 18));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        return button;
    }

    private void loadAvailableSeeds() {
        seedModel.setRowCount(0);
        List<Seed> list = buyerDAO.getAllAvailableSeeds(); // FIXED

        for (Seed s : list) {
            seedModel.addRow(new Object[] {
                    s.getId(),
                    s.getSeedName(),
                    s.getQuantity(),
                    s.getStatus(),
                    s.getDesiredTrade()
            });
        }
    }

    private void loadTradeRequests() {
        tradeModel.setRowCount(0);
        List<TradeRequest> requests = buyerDAO.getTradeRequestsByBuyer(buyer.getId()); // FIXED
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

    private void buySelectedSeed() {
        int row = seedTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a seed to buy.");
            return;
        }

        int seedId = (int) seedModel.getValueAt(row, 0);
        int availableQty = (int) seedModel.getValueAt(row, 2);

        String qtyString = JOptionPane.showInputDialog(this,
                "Enter quantity to buy (Available: " + availableQty + "):");

        if (qtyString == null)
            return;

        int qty;
        try {
            qty = Integer.parseInt(qtyString);
            if (qty <= 0 || qty > availableQty) {
                JOptionPane.showMessageDialog(this, "Invalid quantity.");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantity must be a number!");
            return;
        }

        boolean success = buyerDAO.purchaseSeed(seedId, qty, buyer.getId());

        if (success) {
            JOptionPane.showMessageDialog(this, "Purchase successful!");
            loadAvailableSeeds();
        } else {
            JOptionPane.showMessageDialog(this, "Purchase failed.");
        }
    }

    private void requestTrade() {
        int row = seedTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a seed to request trade.");
            return;
        }

        int seedId = (int) seedModel.getValueAt(row, 0);
        String seedName = (String) seedModel.getValueAt(row, 1);
        String desiredTrade = (String) seedModel.getValueAt(row, 4);

        String offer = JOptionPane.showInputDialog(this,
                "Enter what you want to offer in exchange for '" + seedName + "' (Desired: " + desiredTrade + "):");

        if (offer == null || offer.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "You must specify an offer.");
            return;
        }

        boolean success = buyerDAO.requestTrade(seedId, buyer.getId(), offer);
        if (success) {
            JOptionPane.showMessageDialog(this, "Trade request submitted successfully!");
            loadAvailableSeeds();
            loadTradeRequests(); // refresh trade requests table
        } else {
            JOptionPane.showMessageDialog(this, "Failed to submit trade request.");
        }
    }

    private void logout() {
        frame.setContentPane(new LoginPanel(frame));
        frame.revalidate();
        frame.repaint();
    }
}
