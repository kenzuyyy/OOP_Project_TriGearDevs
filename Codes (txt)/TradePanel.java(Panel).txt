package Panel;

import Model.Seed;
import Model.User;
import dao.SeedDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;

public class TradePanel extends JPanel {
    private JFrame frame;

    public JFrame getFrame() {
        return frame;
    }

    public void setFrame(JFrame frame) {
        this.frame = frame;
    }

    private User farmer;

    private JTable seedTable;
    private DefaultTableModel model;
    private SeedDAO seedDAO = new SeedDAO();

    public TradePanel(JFrame frame, User farmer) {
        this.frame = frame;
        this.farmer = farmer;

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        // --- Top Panel (Logo + Title) ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(0, 217, 167));
        topPanel.setPreferredSize(new Dimension(200, 200));

        ImageIcon logoIcon = new ImageIcon("D:\\OOP\\GUI\\AgriConnect\\Logo.png");
        Image scaledLogo = logoIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        logoIcon = new ImageIcon(scaledLogo);

        JLabel logoLabel = new JLabel(logoIcon, SwingConstants.CENTER);
        topPanel.add(logoLabel, BorderLayout.CENTER);

        JLabel title = new JLabel("Trade Seeds / Products", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(0, 0, 0));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        topPanel.add(title, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // --- Table Model ---
        model = new DefaultTableModel(new String[] { "ID", "Name", "Quantity", "Status", "Desired Trade" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return (columnIndex == 0 || columnIndex == 2) ? Integer.class : String.class;
            }
        };

        // --- Table Setup ---
        seedTable = new JTable(model);
        seedTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        seedTable.setRowHeight(35);
        seedTable.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        seedTable.setGridColor(new Color(220, 220, 220));
        seedTable.setShowGrid(true);
        seedTable.setFillsViewportHeight(true);
        seedTable.setAutoCreateRowSorter(true);
        seedTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader header = seedTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setBackground(new Color(45, 95, 150));
        header.setForeground(Color.WHITE);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < seedTable.getColumnCount(); i++) {
            seedTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            seedTable.getColumnModel().getColumn(i).setPreferredWidth(150);
        }

        // Alternating row colors
        seedTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? new Color(250, 250, 250) : new Color(235, 245, 255));
                } else {
                    c.setBackground(new Color(180, 210, 250));
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(seedTable);
        scrollPane.setPreferredSize(new Dimension(900, 400));
        scrollPane.setBorder(BorderFactory.createTitledBorder("Your Seeds"));

        add(scrollPane, BorderLayout.CENTER);

        // --- Load Seeds ---
        loadFarmerSeeds();

        // --- Bottom Buttons ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        bottomPanel.setBackground(new Color(245, 245, 245));

        JButton markTradeButton = new JButton("Mark as For Trade");
        JButton tradeButton = new JButton("Trade"); // NEW BUTTON
        JButton backButton = new JButton("Back");

        markTradeButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tradeButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 18));

        markTradeButton.setPreferredSize(new Dimension(180, 40));
        tradeButton.setPreferredSize(new Dimension(140, 40));
        backButton.setPreferredSize(new Dimension(140, 40));

        bottomPanel.add(markTradeButton);
        bottomPanel.add(tradeButton);
        bottomPanel.add(backButton);

        add(bottomPanel, BorderLayout.SOUTH);

        // --- Actions ---
        markTradeButton.addActionListener(e -> markSeedForTrade());
        tradeButton.addActionListener(e -> performTrade()); // NEW ACTION
        backButton.addActionListener(e -> {
            frame.setContentPane(new FarmerPanel(frame, farmer));
            frame.revalidate();
            frame.repaint();
        });

        // --- Minimum size safeguard ---
        frame.setMinimumSize(new Dimension(800, 600));
    }

    private void loadFarmerSeeds() {
        model.setRowCount(0);
        ArrayList<Seed> seeds = seedDAO.loadSeedsByFarmer(farmer.getId());
        for (Seed s : seeds) {
            model.addRow(new Object[] {
                    s.getId(),
                    s.getSeedName(),
                    s.getQuantity(),
                    s.getStatus(),
                    s.getDesiredTrade()
            });
        }
    }

    private void markSeedForTrade() {
        int row = seedTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a seed first.");
            return;
        }

        int seedId = (int) model.getValueAt(row, 0);

        String desiredTrade = JOptionPane.showInputDialog(this,
                "Enter the product/seed you want in exchange:");

        if (desiredTrade == null || desiredTrade.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "You must specify a desired trade item.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Mark this seed as 'For Trade' with desired item: " + desiredTrade + "?",
                "Confirm Trade", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = seedDAO.updateSeedWithTrade(seedId,
                    (int) model.getValueAt(row, 2),
                    "For Trade",
                    desiredTrade);

            if (success) {
                JOptionPane.showMessageDialog(this, "Seed marked for trade with desired item: " + desiredTrade);
                loadFarmerSeeds();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update seed.");
            }
        }
    }

    // --- NEW METHOD for Trade Button ---
    private void performTrade() {
        int row = seedTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a seed to trade.");
            return;
        }

        int seedId = (int) model.getValueAt(row, 0);
        String seedName = (String) model.getValueAt(row, 1);
        String desiredTrade = (String) model.getValueAt(row, 4);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Confirm trade of seed '" + seedName + "' for '" + desiredTrade + "'?",
                "Confirm Trade", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Update seed status to "Traded"
            boolean success = seedDAO.updateSeedWithTrade(seedId,
                    (int) model.getValueAt(row, 2),
                    "Traded",
                    desiredTrade);

            if (success) {
                JOptionPane.showMessageDialog(this, "Trade executed successfully!");
                loadFarmerSeeds();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to execute trade.");
            }
        }
    }
}