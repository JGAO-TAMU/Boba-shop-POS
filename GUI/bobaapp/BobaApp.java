package bobaapp;

import javax.swing.*;
import bobaapp.utils.SequenceUtil;
import bobaapp.views.MainPanel;

public class BobaApp {
    public static void main(String[] args) {
        // Reset database sequences to prevent primary key conflicts
        try {
            SequenceUtil.resetSequences();
        } catch (Exception e) {
            System.err.println("Error resetting sequences: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Start the application UI
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            JFrame frame = new JFrame("Boba Tea Order System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 800);
            
            MainPanel mainPanel = new MainPanel();
            frame.add(mainPanel);
            
            // Center on screen
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
