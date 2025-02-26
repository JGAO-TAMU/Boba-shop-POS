package bobaapp.views;

import bobaapp.database.EmployeeDAO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginFrame extends JFrame {
    private JTextField employeeIDField;

    public LoginFrame() {
        setTitle("Boba POS System");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(255, 255, 204)); // Light yellow background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel titleLabel = new JLabel("POS Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, gbc);

        gbc.gridy++;
        employeeIDField = new JTextField(15);
        employeeIDField.setHorizontalAlignment(JTextField.CENTER);
        employeeIDField.setFont(new Font("Arial", Font.PLAIN, 16));
        employeeIDField.setBackground(new Color(173, 216, 230)); // Light blue background
        panel.add(employeeIDField, gbc);

        gbc.gridy++;
        JButton loginButton = new JButton("LOGIN");
        loginButton.setBackground(new Color(255, 204, 153)); // Peach color
        loginButton.addActionListener(new LoginButtonListener());
        panel.add(loginButton, gbc);

        add(panel, BorderLayout.CENTER);
        setVisible(true);
    }

    private class LoginButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String employeeIDText = employeeIDField.getText().trim();
            if (employeeIDText.matches("\\d+")) {
                int employeeID = Integer.parseInt(employeeIDText);
                int accessLevel = EmployeeDAO.getAccessLevel(employeeID);

                if (accessLevel == -1) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Invalid Employee ID.", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (accessLevel == 0) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Logged in as Cashier.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    // Navigate to cashier view
                    new MainFrame();
                    LoginFrame.this.dispose();
                } else if (accessLevel == 1) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Logged in as Manager.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    // Navigate to manager view
                    new ManagerMainFrame();
                    LoginFrame.this.dispose();

                } else {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Access level not recognized.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(LoginFrame.this, "Please enter a valid numeric Employee ID.", "Input Error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

}
