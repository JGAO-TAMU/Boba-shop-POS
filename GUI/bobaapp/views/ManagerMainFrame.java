package bobaapp.views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ManagerMainFrame extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JLabel timeLabel;

    public ManagerMainFrame() {
        setTitle("Boba POS System");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top Bar
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topBar.setBackground(Color.LIGHT_GRAY);
        topBar.add(new JLabel("Printer"));
        topBar.add(new JLabel("Manager Display"));
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
        JButton inventoryBtn = new JButton("Inventory");
        JButton menuBtn = new JButton("Menu");
        JButton orderHistoryBtn = new JButton("Order History");
        JButton employeesBtn = new JButton("Employees");

        sideMenu.add(homeBtn);
        sideMenu.add(inventoryBtn);
        sideMenu.add(menuBtn);
        sideMenu.add(orderHistoryBtn);
        sideMenu.add(employeesBtn);

        add(sideMenu, BorderLayout.WEST);

        // Main Content Panel with CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(new ManagerHomePanel(), "Home");
        mainPanel.add(new InventoryPanel(), "Inventory");
        mainPanel.add(new MenuPanel(), "Menu");
        mainPanel.add(new OrderHistoryPanel(), "OrderHistory");
        mainPanel.add(new EmployeesPanel(), "Employees");

        add(mainPanel, BorderLayout.CENTER);

        // Button Actions
        homeBtn.addActionListener(e -> cardLayout.show(mainPanel, "Home"));
        inventoryBtn.addActionListener(e -> cardLayout.show(mainPanel, "Inventory"));
        menuBtn.addActionListener(e -> cardLayout.show(mainPanel, "Menu"));
        orderHistoryBtn.addActionListener(e -> cardLayout.show(mainPanel, "OrderHistory"));
        employeesBtn.addActionListener(e -> cardLayout.show(mainPanel, "Employees"));

        setVisible(true);
    }
    // Method to update the time display
    private void updateTime() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm:ss a");
        String time = timeFormat.format(new Date());
        timeLabel.setText("Time: " + time);
    }
}
