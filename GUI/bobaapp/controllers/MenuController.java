package GUI.bobaapp.controllers;

import GUI.bobaapp.views.MainFrame;

import javax.swing.*;

public class MenuController {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}
