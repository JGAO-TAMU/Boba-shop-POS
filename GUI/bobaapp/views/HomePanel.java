package bobaapp.views;

import bobaapp.database.MenuDAO;
import bobaapp.models.MenuItem;
import bobaapp.views.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class HomePanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel parentPanel;

    public HomePanel() {
        setLayout(new GridLayout(0, 4, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(255, 255, 224));
        
        loadDrinks();
    }

    private void loadDrinks() {
        List<MenuItem> menuItems = MenuDAO.getMenuItems();
        
        for (MenuItem item : menuItems) {
            JButton drinkButton = new JButton("<html><center>" + 
                item.getName() + "<br>$" + String.format("%.2f", item.getPrice()) + 
                "</center></html>");
            
            drinkButton.setBackground(new Color(255, 218, 185));
            drinkButton.addActionListener(e -> showModificationsPanel(item));
            add(drinkButton);
        }
    }

    private void showModificationsPanel(MenuItem item) {
        if (getParent() != null) {
            cardLayout = (CardLayout) getParent().getLayout();
            parentPanel = (JPanel) getParent();
            
            // Create new ModificationsPanel with the selected drink
            ModificationsPanel modPanel = new ModificationsPanel();
            modPanel.setDrink(item);
            
            // Add the panel if it's not already there
            parentPanel.add(modPanel, "Modifications");
            cardLayout.show(parentPanel, "Modifications");
        }
    }
}
