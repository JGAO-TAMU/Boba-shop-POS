package bobaapp.views;

import bobaapp.database.InventoryDAO;
import bobaapp.models.InventoryItem;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class InventoryPanel extends JPanel {
    public InventoryPanel() {
        setLayout(new BorderLayout());

        JTextArea inventoryTextArea = new JTextArea();
        inventoryTextArea.setEditable(false);

        List<InventoryItem> inventory = InventoryDAO.getInventory();
        StringBuilder inventoryContent = new StringBuilder("Item\tQuantity\n");

        for (InventoryItem item : inventory) {
            inventoryContent.append(item.getName()).append("\t").append(item.getQuantity()).append("\n");
        }

        inventoryTextArea.setText(inventoryContent.toString());
        add(new JScrollPane(inventoryTextArea), BorderLayout.CENTER);
    }
}
