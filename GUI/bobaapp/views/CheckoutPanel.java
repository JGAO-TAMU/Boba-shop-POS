package bobaapp.views;

import javax.swing.*;
import java.awt.*;

public class CheckoutPanel extends JPanel {
    public CheckoutPanel() {
        setLayout(new BorderLayout());

        JTextArea checkoutTextArea = new JTextArea("Example Drink 1 - $10.00\nExample Drink 2 - $10.00\nTotal - $40.00");
        checkoutTextArea.setEditable(false);

        JButton confirmButton = new JButton("Confirm Order");
        confirmButton.setBackground(Color.GREEN);

        add(new JScrollPane(checkoutTextArea), BorderLayout.CENTER);
        add(confirmButton, BorderLayout.SOUTH);
    }
}
