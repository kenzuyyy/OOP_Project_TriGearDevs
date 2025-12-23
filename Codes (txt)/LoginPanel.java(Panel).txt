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
        logoPanel.setPreferredSize(new Dimension(200, 200)); // reduced height

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

        // Title inside form
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel title = new JLabel("Welcome to AgriConnect", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(0, 0, 0));
        formPanel.add(title, gbc);

        // Username
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        formPanel.add(usernameLabel, gbc);

        JTextField usernameField = new JTextField(20);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        // Password
        gbc.gridy = 2;
        gbc.gridx = 0;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        formPanel.add(passwordLabel, gbc);

        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        // Show/Hide Password
        gbc.gridy = 3;
        gbc.gridx = 1;
        JCheckBox showPassword = new JCheckBox("Show Password");
        showPassword.setFont(new Font("Segoe UI", Font.PLAIN, 16));
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
        JButton registerButton = new JButton("Register");

        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 18));

        loginButton.setPreferredSize(new Dimension(120, 40));
        registerButton.setPreferredSize(new Dimension(120, 40));

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // --- Login action ---
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            UserDAO userDAO = new UserDAO();
            User loggedInUser = userDAO.validateLogin(username, password);
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

        // --- Minimum size safeguard ---
        frame.setMinimumSize(new Dimension(800, 600));
    }
}