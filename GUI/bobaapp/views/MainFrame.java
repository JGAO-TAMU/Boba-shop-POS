package bobaapp.views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;

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
        topBar.add(new JLabel("Time: 8:50 am"));
        add(topBar, BorderLayout.NORTH);

        // Side Menu
        JPanel sideMenu = new JPanel();
        sideMenu.setLayout(new GridLayout(5, 1, 10, 10));
        sideMenu.setBackground(new Color(255, 255, 224));

        JButton homeBtn = new JButton("Home");
        JButton inventoryBtn = new JButton("Inventory");
        JButton menuBtn = new JButton("Menu");
        JButton orderHistoryBtn = new JButton("Order History");
        JButton checkoutBtn = new JButton("Checkout");

        sideMenu.add(homeBtn);
        sideMenu.add(inventoryBtn);
        sideMenu.add(menuBtn);
        sideMenu.add(orderHistoryBtn);
        sideMenu.add(checkoutBtn);

        add(sideMenu, BorderLayout.WEST);

        // Main Content Panel with CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(new HomePanel(), "Home");
        mainPanel.add(new InventoryPanel(), "Inventory");
        mainPanel.add(new MenuPanel(), "Menu");
        mainPanel.add(new OrderHistoryPanel(), "OrderHistory");
        mainPanel.add(new CheckoutPanel(), "Checkout");
        mainPanel.add(new ModificationsPanel(), "Modifications"); // Add the new panel

        add(mainPanel, BorderLayout.CENTER);

        // Button Actions
        homeBtn.addActionListener(e -> cardLayout.show(mainPanel, "Home"));
        inventoryBtn.addActionListener(e -> cardLayout.show(mainPanel, "Inventory"));
        menuBtn.addActionListener(e -> cardLayout.show(mainPanel, "Menu"));
        orderHistoryBtn.addActionListener(e -> cardLayout.show(mainPanel, "OrderHistory"));
        checkoutBtn.addActionListener(e -> cardLayout.show(mainPanel, "Checkout"));

        setVisible(true);
    }
}
