package Panel;

import Model.User;
import Model.Seed;
import dao.AdminDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.PrintWriter;
import java.util.List;

public class AdminPanel extends JPanel {
    private final JFrame frame;
    private final AdminDAO adminDAO = new AdminDAO();

    private JTable userTable;
    private JTable seedTable;
    private DefaultTableModel userModel;
    private DefaultTableModel seedModel;

    public AdminPanel(JFrame frame, User admin) {
        this.frame = frame;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 250));

        // --- Title ---
        JLabel title = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(45, 95, 150));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // --- Center Panel with Tabs ---
        JTabbedPane tabbedPane = new JTabbedPane();

        // Users Tab
        userModel = new DefaultTableModel(new String[] { "ID", "Username", "Email", "Role" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(userModel);
        styleTable(userTable);
        JScrollPane userScroll = new JScrollPane(userTable);
        tabbedPane.addTab("Users", userScroll);

        // Seeds Tab
        seedModel = new DefaultTableModel(
                new String[] { "ID", "Seed Name", "Quantity", "Status", "Desired Trade", "Farmer" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        seedTable = new JTable(seedModel);
        styleTable(seedTable);
        JScrollPane seedScroll = new JScrollPane(seedTable);
        tabbedPane.addTab("Seeds", seedScroll);

        add(tabbedPane, BorderLayout.CENTER);

        // --- Bottom Buttons ---
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        bottom.setBackground(new Color(245, 245, 250));

        JButton deleteUserBtn = createStyledButton("Delete User", new Color(200, 60, 60));
        JButton deleteSeedBtn = createStyledButton("Delete Seed", new Color(200, 60, 60));
        JButton exportUsersBtn = createStyledButton("Export Users (CSV)", new Color(45, 95, 150));
        JButton exportSeedsBtn = createStyledButton("Export Seeds (TXT)", new Color(0, 150, 100));
        JButton logoutBtn = createStyledButton("Logout", new Color(200, 60, 60));

        bottom.add(deleteUserBtn);
        bottom.add(deleteSeedBtn);
        bottom.add(exportUsersBtn);
        bottom.add(exportSeedsBtn);
        bottom.add(logoutBtn);
        add(bottom, BorderLayout.SOUTH);

        // --- Load Data ---
        loadUsers();
        loadSeeds();

        // --- Actions ---
        deleteUserBtn.addActionListener(e -> deleteSelectedUser());
        deleteSeedBtn.addActionListener(e -> deleteSelectedSeed());
        exportUsersBtn.addActionListener(e -> exportUsersToCSV());
        exportSeedsBtn.addActionListener(e -> exportSeedsToTXT());
        logoutBtn.addActionListener(e -> logout());

        frame.setMinimumSize(new Dimension(900, 600));
    }

    private void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setBackground(new Color(45, 95, 150));
        header.setForeground(Color.WHITE);
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

    private void loadUsers() {
        userModel.setRowCount(0);
        List<User> users = adminDAO.getAllUsers();
        for (User u : users) {
            userModel.addRow(new Object[] {
                    u.getId(),
                    u.getUsername(),
                    u.getEmail(),
                    u.getRole()
            });
        }
    }

    private void loadSeeds() {
        seedModel.setRowCount(0);
        List<Seed> seeds = adminDAO.getAllSeeds();
        for (Seed s : seeds) {
            seedModel.addRow(new Object[] {
                    s.getId(),
                    s.getSeedName(),
                    s.getQuantity(),
                    s.getStatus(),
                    s.getDesiredTrade(),
                    s.getFarmerUsername()
            });
        }
    }

    private void deleteSelectedUser() {
        int row = userTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a user to delete.");
            return;
        }
        int userId = (int) userModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this user?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = adminDAO.deleteUser(userId);
            if (success) {
                JOptionPane.showMessageDialog(this, "User deleted successfully!");
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete user.");
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
            boolean success = adminDAO.deleteSeed(seedId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Seed deleted successfully!");
                loadSeeds();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete seed.");
            }
        }
    }

    private void exportUsersToCSV() {
        List<User> users = adminDAO.getAllUsers();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Users Report");
        fileChooser.setSelectedFile(new java.io.File("users_report.csv"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            try (PrintWriter pw = new PrintWriter(fileToSave)) {
                pw.println("ID,Username,Email,Role");
                for (User u : users) {
                    pw.printf("%d,%s,%s,%s%n",
                            u.getId(), u.getUsername(), u.getEmail(), u.getRole());
                }
                JOptionPane.showMessageDialog(this, "Users exported to " + fileToSave.getAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error exporting users: " + ex.getMessage());
            }
        }
    }

    private void exportSeedsToTXT() {
        List<Seed> seeds = adminDAO.getAllSeeds();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Seeds Report");
        fileChooser.setSelectedFile(new java.io.File("seeds_report.txt"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            try (PrintWriter pw = new PrintWriter(fileToSave)) {
                for (Seed s : seeds) {
                    pw.printf("ID: %d | Name: %s | Qty: %d | Status: %s | Trade: %s | Farmer: %s%n",
                            s.getId(), s.getSeedName(), s.getQuantity(),
                            s.getStatus(), s.getDesiredTrade(), s.getFarmerUsername());
                }
                JOptionPane.showMessageDialog(this, "Seeds exported to " + fileToSave.getAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error exporting seeds: " + ex.getMessage());
            }
        }
    }

    private void logout() {
        frame.setContentPane(new LoginPanel(frame));
        frame.revalidate();
        frame.repaint();
    }
}