package Panel;

import javax.swing.*;
import dao.UserDAO;
import Model.User;
import util.SecurityUtil;
import java.awt.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

public class RegisterPanel extends JPanel {
    private final JFrame frame;

    public JFrame getFrame() {
        return frame;
    }

    public RegisterPanel(JFrame frame) {
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

        // --- Title ---
        JLabel title = new JLabel("Create Your Account", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(45, 95, 150));
        add(title, BorderLayout.SOUTH);

        // --- Form Panel ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(250, 250, 250));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        formPanel.add(userLabel, gbc);

        JTextField txtUser = new JTextField(20);
        txtUser.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        txtUser.setPreferredSize(new Dimension(220, 30));
        gbc.gridx = 1;
        formPanel.add(txtUser, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        formPanel.add(emailLabel, gbc);

        JTextField txtEmail = new JTextField(20);
        txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        txtEmail.setPreferredSize(new Dimension(220, 30));
        gbc.gridx = 1;
        formPanel.add(txtEmail, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        formPanel.add(passLabel, gbc);

        JPasswordField txtPass = new JPasswordField(20);
        txtPass.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        txtPass.setPreferredSize(new Dimension(220, 30));
        gbc.gridx = 1;
        formPanel.add(txtPass, gbc);

        // Show/Hide Password
        gbc.gridx = 1;
        gbc.gridy = 3;
        JCheckBox showPassword = new JCheckBox("Show Password");
        showPassword.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        showPassword.addActionListener(e -> {
            if (showPassword.isSelected()) {
                txtPass.setEchoChar((char) 0);
            } else {
                txtPass.setEchoChar('•');
            }
        });
        formPanel.add(showPassword, gbc);

        // Role (only farmer and buyer allowed)
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        formPanel.add(roleLabel, gbc);

        JComboBox<String> roleBox = new JComboBox<>(new String[] { "farmer", "buyer" });
        roleBox.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        roleBox.setPreferredSize(new Dimension(220, 30));
        DefaultListCellRenderer listRenderer = new DefaultListCellRenderer();
        listRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        roleBox.setRenderer(listRenderer);
        gbc.gridx = 1;
        formPanel.add(roleBox, gbc);

        // Birthdate
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel birthLabel = new JLabel("Birthdate:");
        birthLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        formPanel.add(birthLabel, gbc);

        SpinnerDateModel dateModel = new SpinnerDateModel();
        JSpinner birthSpinner = new JSpinner(dateModel);
        birthSpinner.setEditor(new JSpinner.DateEditor(birthSpinner, "yyyy-MM-dd"));
        birthSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridx = 1;
        formPanel.add(birthSpinner, gbc);

        // Wrap form panel
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(new Color(245, 245, 245));
        centerWrapper.add(formPanel, new GridBagConstraints());
        add(centerWrapper, BorderLayout.CENTER);

        // --- Button Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setBackground(new Color(245, 245, 245));

        JButton btnRegister = new JButton("Register");
        JButton btnBack = new JButton("Back to Login");

        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 18));

        btnRegister.setPreferredSize(new Dimension(140, 40));
        btnBack.setPreferredSize(new Dimension(180, 40));

        buttonPanel.add(btnRegister);
        buttonPanel.add(btnBack);

        add(buttonPanel, BorderLayout.SOUTH);

        // --- Register action ---
        btnRegister.addActionListener(e -> {
            String username = txtUser.getText().trim();
            String email = txtEmail.getText().trim();
            String password = new String(txtPass.getPassword()).trim();
            String role = roleBox.getSelectedItem().toString();
            Date birthDate = (Date) birthSpinner.getValue();

            LocalDate birthLocal = birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            int age = Period.between(birthLocal, LocalDate.now()).getYears();

            if (username.length() < 3) {
                JOptionPane.showMessageDialog(this, "Username must be at least 3 characters.");
                return;
            }
            if (!email.contains("@") || !email.contains(".")) {
                JOptionPane.showMessageDialog(this, "Enter a valid email.");
                return;
            }
            if (password.length() < 6) {
                JOptionPane.showMessageDialog(this, "Password must be at least 6 characters.");
                return;
            }
            if (age < 18) {
                JOptionPane.showMessageDialog(this, "You must be at least 18 years old to register.");
                return;
            }

            UserDAO dao = new UserDAO();
            if (dao.isEmailTaken(email)) {
                JOptionPane.showMessageDialog(this, "Email is already used.");
                return;
            }

            String hashed = SecurityUtil.hashPassword(password);
            User newUser = new User(username, email, hashed, role, birthLocal);

            if (dao.registerUser(newUser)) {
                JOptionPane.showMessageDialog(this, "Registration successful.");
                frame.setContentPane(new LoginPanel(frame));
                frame.revalidate();
                frame.repaint();
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed. Check console for details.");
            }
        });

        // --- Back button action ---
        btnBack.addActionListener(e -> {
            frame.setContentPane(new LoginPanel(frame));
            frame.revalidate();
            frame.repaint();
        });
    }
}