package bobaapp.views;

import bobaapp.database.OrdersDAO;
import bobaapp.models.Order;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class OrderHistoryPanel extends JPanel {
    public OrderHistoryPanel() {
        setLayout(new BorderLayout());

        JTextArea ordersTextArea = new JTextArea();
        ordersTextArea.setEditable(false);

        List<Order> orders = OrdersDAO.getOrders();
        StringBuilder ordersContent = new StringBuilder("Order ID\tTimestamp\tPrice\n");

        for (Order order : orders) {
            ordersContent.append(order.getOrderID()).append("\t")
                         .append(order.getTimestamp()).append("\t$")
                         .append(order.getPrice()).append("\n");
        }

        ordersTextArea.setText(ordersContent.toString());
        add(new JScrollPane(ordersTextArea), BorderLayout.CENTER);
    }
}
