package Panel;

import Model.Seed;
import Model.User;
import dao.FarmerDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class FarmerPanel extends JPanel {
    private final JFrame frame;
    private final User farmer;

    private JTable seedTable;
    private DefaultTableModel seedModel;

    private final FarmerDAO farmerDAO = new FarmerDAO();

    public FarmerPanel(JFrame frame, User farmer) {
        this.frame = frame;
        this.farmer = farmer;

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 250));

        // --- Top Header Bar with Logo ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(0, 217, 167));
        topPanel.setPreferredSize(new Dimension(200, 160));

        ImageIcon logoIcon = new ImageIcon("D:\\OOP\\GUI\\AgriConnect\\Logo.png");
        Image scaledLogo = logoIcon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
        logoIcon = new ImageIcon(scaledLogo);

        JLabel logoLabel = new JLabel(logoIcon, SwingConstants.CENTER);
        topPanel.add(logoLabel, BorderLayout.CENTER);

        JLabel title = new JLabel("Farmer Dashboard - Manage Seeds", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
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
        seedTable.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        seedTable.setGridColor(new Color(220, 220, 220));
        seedTable.setShowGrid(true);
        seedTable.setFillsViewportHeight(true);
        seedTable.setAutoCreateRowSorter(true);

        JTableHeader header = seedTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setBackground(new Color(45, 95, 150));
        header.setForeground(Color.WHITE);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < seedTable.getColumnCount(); i++) {
            seedTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(seedTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Your Seeds"));
        add(scrollPane, BorderLayout.CENTER);

        // --- Load Seeds ---
        loadSeeds();

        // --- Bottom Buttons ---
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        bottom.setBackground(new Color(245, 245, 250));

        JButton addButton = createStyledButton("Add Seed", new Color(45, 95, 150));
        JButton editButton = createStyledButton("Edit Seed", new Color(0, 150, 100));
        JButton deleteButton = createStyledButton("Delete Seed", new Color(200, 60, 60));
        JButton logoutButton = createStyledButton("Logout", new Color(200, 60, 60));

        bottom.add(addButton);
        bottom.add(editButton);
        bottom.add(deleteButton);
        bottom.add(logoutButton);
        add(bottom, BorderLayout.SOUTH);

        // --- Actions ---
        addButton.addActionListener(e -> addSeed());
        editButton.addActionListener(e -> editSelectedSeed());
        deleteButton.addActionListener(e -> deleteSelectedSeed());
        logoutButton.addActionListener(e -> logout());

        frame.setMinimumSize(new Dimension(800, 600));
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

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

    private void loadSeeds() {
        seedModel.setRowCount(0);
        List<Seed> list = farmerDAO.getSeedsByFarmer(farmer.getId());
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

    private void addSeed() {
        JTextField nameField = new JTextField();
        JTextField qtyField = new JTextField();
        JTextField statusField = new JTextField();
        JTextField tradeField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Quantity:"));
        panel.add(qtyField);
        panel.add(new JLabel("Status:"));
        panel.add(statusField);
        panel.add(new JLabel("Desired Trade:"));
        panel.add(tradeField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Seed", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                int qty = Integer.parseInt(qtyField.getText().trim());
                String status = statusField.getText().trim();
                String trade = tradeField.getText().trim();

                if (qty <= 0) {
                    JOptionPane.showMessageDialog(this, "Quantity must be greater than zero!");
                    return;
                }

                int seedId = farmerDAO.getSeedIdByName(name);
                if (seedId == -1) {
                    JOptionPane.showMessageDialog(this, "Invalid seed/product name. Please enter a valid seed.");
                    return;
                }

                boolean success = farmerDAO.addSeed(farmer.getId(), seedId, qty, status, trade);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Seed added successfully!");
                    loadSeeds();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add seed.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Quantity must be a number!");
            }
        }
    }

    private void editSelectedSeed() {
        int row = seedTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a seed to edit.");
            return;
        }

        int seedId = (int) seedModel.getValueAt(row, 0);
        String currentName = (String) seedModel.getValueAt(row, 1);
        int currentQty = (int) seedModel.getValueAt(row, 2);
        String currentStatus = (String) seedModel.getValueAt(row, 3);
        String currentTrade = (String) seedModel.getValueAt(row, 4);

        JTextField nameField = new JTextField(currentName);
        JTextField qtyField = new JTextField(String.valueOf(currentQty));
        JTextField statusField = new JTextField(currentStatus);
        JTextField tradeField = new JTextField(currentTrade);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Quantity:"));
        panel.add(qtyField);
        panel.add(new JLabel("Status:"));
        panel.add(statusField);
        panel.add(new JLabel("Desired Trade:"));
        panel.add(tradeField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Seed", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String newName = nameField.getText().trim();
                int newQty = Integer.parseInt(qtyField.getText().trim());
                String newStatus = statusField.getText().trim();
                String newTrade = tradeField.getText().trim();

                if (newQty <= 0) {
                    JOptionPane.showMessageDialog(this, "Quantity must be greater than zero!");
                    return;
                }

                int seedMasterId = farmerDAO.getSeedIdByName(newName);
                if (seedMasterId == -1) {
                    JOptionPane.showMessageDialog(this, "Invalid seed/product name. Please enter a valid seed.");
                    return;
                }

                boolean success = farmerDAO.updateSeed(seedId, seedMasterId, newQty, newStatus, newTrade);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Seed updated successfully!");
                    loadSeeds();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update seed.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Quantity must be a number!");
            }
        }
    }

    private void deleteSelectedSeed() {
        int row = seedTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a seed to delete.");
            return;
        }

        int seedId = (int) seedModel.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this seed?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = farmerDAO.deleteSeed(seedId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Seed deleted successfully!");
                loadSeeds();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete seed.");
            }
        }
    }

    private void logout() {
        frame.setContentPane(new LoginPanel(frame));
        frame.revalidate();
        frame.repaint();
    }
}