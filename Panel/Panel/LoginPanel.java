package Panel;

import javax.swing.*;
import Model.User;
import dao.UserDAO;
import java.awt.*;

public class LoginPanel extends JPanel {
    private final JFrame frame;

    public JFrame getFrame() {
        return frame;
    }

    public LoginPanel(JFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        // --- Logo Panel ---
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(new Color(0, 217, 167));
        logoPanel.setPreferredSize(new Dimension(200, 200));

        ImageIcon logoIcon = new ImageIcon("D:\\OOP\\GUI\\AgriConnect\\Logo.png");
        Image scaledLogo = logoIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        logoIcon = new ImageIcon(scaledLogo);

        JLabel logoLabel = new JLabel(logoIcon, SwingConstants.CENTER);
        logoPanel.add(logoLabel, BorderLayout.CENTER);

        add(logoPanel, BorderLayout.NORTH);

        // --- Form Panel ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 245, 245));
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel title = new JLabel("Welcome to AgriConnect", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 32)); // ✅ Bigger title
        formPanel.add(title, gbc);

        // Username
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 20)); // ✅ Bigger label
        formPanel.add(usernameLabel, gbc);

        JTextField usernameField = new JTextField(20);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 20)); // ✅ Bigger text
        usernameField.setPreferredSize(new Dimension(250, 40)); // ✅ Bigger box
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        // Password
        gbc.gridy = 2;
        gbc.gridx = 0;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 20)); // ✅ Bigger label
        formPanel.add(passwordLabel, gbc);

        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 20)); // ✅ Bigger text
        passwordField.setPreferredSize(new Dimension(250, 40)); // ✅ Bigger box
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        // Show/Hide Password
        gbc.gridy = 3;
        gbc.gridx = 1;
        JCheckBox showPassword = new JCheckBox("Show Password");
        showPassword.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // ✅ Bigger checkbox text
        showPassword.addActionListener(e -> {
            if (showPassword.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('•');
            }
        });
        formPanel.add(showPassword, gbc);

        add(formPanel, BorderLayout.CENTER);

        // --- Buttons Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setBackground(new Color(245, 245, 245));

        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 18)); // ✅ Bigger button text
        JButton registerButton = new JButton("Register");
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 18));

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // --- Login action ---
        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            UserDAO userDAO = new UserDAO();
            User loggedInUser = userDAO.login(username, password);

            if (loggedInUser != null) {
                switch (loggedInUser.getRole()) {
                    case "farmer":
                        frame.setContentPane(new FarmerPanel(frame, loggedInUser));
                        break;
                    case "buyer":
                        frame.setContentPane(new BuyerPanel(frame, loggedInUser));
                        break;
                    case "admin":
                        frame.setContentPane(new AdminPanel(frame, loggedInUser));
                        break;
                }
                frame.revalidate();
                frame.repaint();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.");
            }
        });

        // --- Register action ---
        registerButton.addActionListener(e -> {
            frame.setContentPane(new RegisterPanel(frame));
            frame.revalidate();
            frame.repaint();
        });

        frame.setMinimumSize(new Dimension(800, 600));
    }
}