package bobaapp.controllers;

import bobaapp.views.MainFrame;
import bobaapp.views.LoginFrame;

import javax.swing.*;

public class MenuController {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}
