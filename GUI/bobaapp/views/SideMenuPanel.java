package bobaapp.views;

import javax.swing.*;
import java.awt.*;

public class SideMenuPanel extends JPanel {
    public SideMenuPanel() {
        setLayout(new GridLayout(4, 1, 10, 10));
        setBackground(new Color(255, 255, 224));

        add(new JButton("Home"));
        add(new JButton("Inventory"));
        add(new JButton("Order History"));
        add(new JButton("Checkout Customer"));
    }
}
