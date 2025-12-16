package Panel;

import Model.User;
import Model.Seed;
import dao.AdminDAO;
import dao.AdminSeedDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AdminPanel extends JPanel {

    private final JFrame frame;
    private final User admin;

    public User getAdmin() {
        return admin;
    }

    private JTable usersTable;
    private JTable seedsTable;

    private DefaultTableModel usersModel;
    private DefaultTableModel seedsModel;

    private final AdminDAO adminDAO = new AdminDAO();
    private final AdminSeedDAO seedDAO = new AdminSeedDAO();

    public AdminPanel(JFrame frame, User admin) {
        this.frame = frame;
        this.admin = admin;

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 250));

        // --- Top Header Bar with Logo ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(0, 217, 167));
        topPanel.setPreferredSize(new Dimension(200, 250));

        // Load and scale logo
        ImageIcon logoIcon = new ImageIcon("D:\\OOP\\GUI\\AgriConnect\\Logo.png");
        Image scaledLogo = logoIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        logoIcon = new ImageIcon(scaledLogo);

        JLabel logoLabel = new JLabel(logoIcon, SwingConstants.CENTER);
        topPanel.add(logoLabel, BorderLayout.CENTER);

        // Admin info label below logo
        JLabel adminLabel = new JLabel("Logged in as: " + admin.getUsername() + " (" + admin.getRole() + ")",
                SwingConstants.CENTER);
        adminLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        adminLabel.setForeground(new Color(0, 0, 0));
        adminLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        topPanel.add(adminLabel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // --- Tabs for Users and Seeds ---
        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Users", createUsersTab());
        tabs.add("Seeds", createSeedsTab());

        add(tabs, BorderLayout.CENTER);

        // --- Bottom Buttons ---
        JButton deleteUserBtn = createStyledButton("Delete User", new Color(200, 60, 60));
        deleteUserBtn.addActionListener(e -> deleteUser());

        JButton deleteSeedBtn = createStyledButton("Delete Seed", new Color(200, 60, 60));
        deleteSeedBtn.addActionListener(e -> deleteSeed());

        JButton exportBtn = createStyledButton("Export Report", new Color(0, 150, 80));
        exportBtn.addActionListener(e -> {
            String[] options = { "CSV", "TXT" };
            int choice = JOptionPane.showOptionDialog(this,
                    "Choose export format:",
                    "Export Report",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]);

            if (choice == 0) {
                exportReport(true); // CSV
            } else if (choice == 1) {
                exportReport(false); // TXT
            }
        });

        JButton logoutBtn = createStyledButton("Logout", new Color(45, 95, 150));
        logoutBtn.addActionListener(e -> logout());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottom.setBackground(new Color(245, 245, 250));
        bottom.add(deleteUserBtn);
        bottom.add(deleteSeedBtn);
        bottom.add(exportBtn);
        bottom.add(logoutBtn);

        add(bottom, BorderLayout.SOUTH);

        frame.setMinimumSize(new Dimension(800, 600));
    }

    // ---------------- USERS TAB ----------------
    private JPanel createUsersTab() {
        JPanel panel = new JPanel(new BorderLayout());

        usersModel = new DefaultTableModel(new String[] { "ID", "Name", "Role" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Integer.class : String.class;
            }
        };

        usersTable = new JTable(usersModel);
        styleTable(usersTable);

        loadUsers();

        panel.add(new JScrollPane(usersTable), BorderLayout.CENTER);
        return panel;
    }

    private void loadUsers() {
        usersModel.setRowCount(0);
        List<User> users = adminDAO.getAllUsers();
        if (users == null || users.isEmpty())
            return;

        for (User u : users) {
            usersModel.addRow(new Object[] { u.getId(), u.getUsername(), u.getRole() });
        }
    }

    private void deleteUser() {
        int row = usersTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a user first.");
            return;
        }

        int userId = (int) usersModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this user?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (adminDAO.deleteUser(userId)) {
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this, "Delete failed.");
            }
        }
    }

    // ---------------- SEEDS TAB ----------------
    private JPanel createSeedsTab() {
        JPanel panel = new JPanel(new BorderLayout());

        seedsModel = new DefaultTableModel(new String[] { "ID", "Name", "Quantity", "Status", "Farmer ID" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return (columnIndex == 0 || columnIndex == 2 || columnIndex == 4) ? Integer.class : String.class;
            }
        };

        seedsTable = new JTable(seedsModel);
        styleTable(seedsTable);

        loadSeeds();

        panel.add(new JScrollPane(seedsTable), BorderLayout.CENTER);
        return panel;
    }

    private void loadSeeds() {
        seedsModel.setRowCount(0);
        List<Seed> seeds = seedDAO.getAllSeeds();
        if (seeds == null || seeds.isEmpty())
            return;

        for (Seed s : seeds) {
            seedsModel.addRow(
                    new Object[] { s.getId(), s.getSeedName(), s.getQuantity(), s.getStatus(), s.getFarmerId() });
        }
    }

    private void deleteSeed() {
        int row = seedsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a seed first.");
            return;
        }

        int seedId = (int) seedsModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this seed?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (seedDAO.deleteSeed(seedId)) {
                loadSeeds();
            } else {
                JOptionPane.showMessageDialog(this, "Delete failed.");
            }
        }
    }

    // ---------------- STYLING METHODS ----------------
    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(0, 217, 167));
        table.getTableHeader().setForeground(Color.WHITE);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    // ---------------- LOGOUT ----------------
    private void logout() {
        frame.setContentPane(new LoginPanel(frame));
        frame.revalidate();
        frame.repaint();
    }

    // ---------------- EXPORT REPORT ----------------
    private void exportReport(boolean csvFormat) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Report");
        fileChooser.setSelectedFile(new File(csvFormat ? "report.csv" : "report.txt"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            try (PrintWriter writer = new PrintWriter(fileToSave)) {
                // Timestamp
                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                writer.println("Report generated on: " + timestamp);
                writer.println();

                // Export Users Table
                writer.println("=== USERS REPORT ===");
                for (int i = 0; i < usersModel.getRowCount(); i++) {
                    StringBuilder row = new StringBuilder();
                    for (int j = 0; j < usersModel.getColumnCount(); j++) {
                        row.append(usersModel.getValueAt(i, j));
                        if (csvFormat && j < usersModel.getColumnCount() - 1) {
                            row.append(",");
                        } else {
                            row.append("\t");
                        }
                    }
                    writer.println(row.toString());
                }

                writer.println();

                // Export Seeds Table
                writer.println("=== SEEDS REPORT ===");
                for (int i = 0; i < seedsModel.getRowCount(); i++) {
                    StringBuilder row = new StringBuilder();
                    for (int j = 0; j < seedsModel.getColumnCount(); j++) {
                        row.append(seedsModel.getValueAt(i, j));
                        if (csvFormat && j < seedsModel.getColumnCount() - 1) {
                            row.append(",");
                        } else {
                            row.append("\t");
                        }
                    }
                    writer.println(row.toString());
                }

                JOptionPane.showMessageDialog(this,
                        "Report exported successfully to:\n" + fileToSave.getAbsolutePath(),
                        "Export Success",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error exporting report: " + ex.getMessage(),
                        "Export Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}