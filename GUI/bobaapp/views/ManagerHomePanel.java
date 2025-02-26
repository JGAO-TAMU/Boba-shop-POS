package bobaapp.views;

import bobaapp.database.MenuDAO;
import bobaapp.models.MenuItem;
import bobaapp.views.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ManagerHomePanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel parentPanel;

    public ManagerHomePanel() {
        setLayout(new GridLayout(0, 4, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(255, 255, 224));
        
   
    }


}
