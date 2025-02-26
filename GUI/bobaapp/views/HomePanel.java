package bobaapp.views;

import javax.swing.*;
import java.awt.*;
import bobaapp.database.MenuDAO;
import java.util.List;

public class HomePanel extends JPanel {
    public HomePanel() {
        setLayout(new GridLayout(2, 3, 20, 20));
        setBackground(new Color(255, 255, 224));

        List<String> categories = MenuDAO.getCategories();
        System.out.println(categories+"categories");
        for (String category : categories) {
            JButton btn = new JButton(category);
            btn.setBackground(new Color(173, 216, 230));
            add(btn);
        }
    }
}
