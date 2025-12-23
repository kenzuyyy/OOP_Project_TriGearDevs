package Panel;

import javax.swing.*;

public class AgriConnect extends JFrame {

    public AgriConnect() {
        setTitle("AgriConnect");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Load the unified LoginPanel directly
        setContentPane(new LoginPanel(this));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AgriConnect app = new AgriConnect();
            app.setVisible(true);
        });
    }
}