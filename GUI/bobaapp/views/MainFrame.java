package bobaapp.views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainFrame extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JLabel timeLabel;

    public MainFrame() {
        setTitle("Boba POS System");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top Bar
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topBar.setBackground(Color.LIGHT_GRAY);
        topBar.add(new JLabel("Printer"));
        topBar.add(new JLabel("Customer Display"));
        topBar.add(new JLabel("My Devices"));
        // Create time label with current time
        timeLabel = new JLabel();
        updateTime(); // Set initial time
        topBar.add(timeLabel);
        
        // Set up a timer to update the time every second
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTime();
            }
        });
        timer.start();
        add(topBar, BorderLayout.NORTH);

        // Side Menu
        JPanel sideMenu = new JPanel();
        sideMenu.setLayout(new GridLayout(5, 1, 10, 10));
        sideMenu.setBackground(new Color(255, 255, 224));

        JButton homeBtn = new JButton("Home");
        JButton checkoutBtn = new JButton("Checkout");

        sideMenu.add(homeBtn);
        sideMenu.add(checkoutBtn);

        add(sideMenu, BorderLayout.WEST);

        // Main Content Panel with CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(new HomePanel(), "Home");
        mainPanel.add(new CheckoutPanel(), "Checkout");
        mainPanel.add(new ModificationsPanel(), "Modifications"); // Add the new panel

        add(mainPanel, BorderLayout.CENTER);

        // Button Actions
        homeBtn.addActionListener(e -> cardLayout.show(mainPanel, "Home"));
        checkoutBtn.addActionListener(e -> cardLayout.show(mainPanel, "Checkout"));

        setVisible(true);
    }
    // Method to update the time display
    private void updateTime() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm:ss a");
        String time = timeFormat.format(new Date());
        timeLabel.setText("Time: " + time);
    }
}