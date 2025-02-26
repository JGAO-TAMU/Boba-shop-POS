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
        setLayout(new BorderLayout());
        
        // add logout button to top right
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setBackground(new Color(255, 255, 224));
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());
        topPanel.add(logoutButton);
        add(topPanel, BorderLayout.NORTH);
        
        // menu grid in center
        JPanel menuGrid = new JPanel(new GridLayout(0, 4, 10, 10));
        menuGrid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        menuGrid.setBackground(new Color(255, 255, 224));
        add(menuGrid, BorderLayout.CENTER);
        
        // load drinks into the grid
        loadDrinksToGrid(menuGrid);
    }

    private void loadDrinksToGrid(JPanel menuGrid) {
        List<MenuItem> menuItems = MenuDAO.getMenuItems();
        
        for (MenuItem item : menuItems) {
            JButton drinkButton = new JButton("<html><center>" + 
                item.getName() + "<br>$" + String.format("%.2f", item.getPrice()) + 
                "</center></html>");
            
            drinkButton.setBackground(new Color(255, 218, 185));
            drinkButton.addActionListener(e -> showModificationsPanel(item));
            menuGrid.add(drinkButton);
        }
    }

    private void showModificationsPanel(MenuItem item) {
        if (getParent() != null) {
            cardLayout = (CardLayout) getParent().getLayout();
            parentPanel = (JPanel) getParent();
            
            // create new ModificationsPanel with the selected drink
            ModificationsPanel modPanel = new ModificationsPanel();
            modPanel.setDrink(item);
            
            parentPanel.add(modPanel, "Modifications");
            cardLayout.show(parentPanel, "Modifications");
        }
    }
    
    // method to handle logout
    private void logout() {
        // close the current frame
        JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        currentFrame.dispose();
        
        // open the login frame
        SwingUtilities.invokeLater(() -> {
            new LoginFrame();
        });
    }
}
