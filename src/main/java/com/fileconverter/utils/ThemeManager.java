package com.fileconverter.utils;

import javax.swing.*;
import java.awt.*;

public class ThemeManager {

    private static boolean isDarkMode = false;

    // Light theme colors
    private static final Color LIGHT_BG = new Color(240, 240, 240);
    private static final Color LIGHT_FG = Color.BLACK;
    private static final Color LIGHT_PANEL_BG = Color.WHITE;
    private static final Color LIGHT_BUTTON_BG = new Color(230, 230, 230);

    // Dark theme colors
    private static final Color DARK_BG = new Color(45, 45, 48);
    private static final Color DARK_FG = new Color(220, 220, 220);
    private static final Color DARK_PANEL_BG = new Color(30, 30, 30);
    private static final Color DARK_BUTTON_BG = new Color(60, 60, 60);

    public static void toggleTheme(JFrame frame) {
        isDarkMode = !isDarkMode;
        applyTheme(frame);
    }

    public static void applyTheme(JFrame frame) {
        if (isDarkMode) {
            applyDarkTheme(frame);
        } else {
            applyLightTheme(frame);
        }
        frame.repaint();
    }

    private static void applyDarkTheme(JFrame frame) {
        applyThemeToComponent(frame.getContentPane(), DARK_BG, DARK_FG, DARK_PANEL_BG, DARK_BUTTON_BG);
    }

    private static void applyLightTheme(JFrame frame) {
        applyThemeToComponent(frame.getContentPane(), LIGHT_BG, LIGHT_FG, LIGHT_PANEL_BG, LIGHT_BUTTON_BG);
    }

    private static void applyThemeToComponent(Component comp, Color bg, Color fg, Color panelBg, Color buttonBg) {
        if (comp instanceof JPanel) {
            comp.setBackground(panelBg);
            comp.setForeground(fg);
        } else if (comp instanceof JButton) {
            comp.setBackground(buttonBg);
            comp.setForeground(fg);
        } else if (comp instanceof JLabel) {
            comp.setForeground(fg);
        } else if (comp instanceof JTextField || comp instanceof JTextArea) {
            comp.setBackground(isDarkMode ? new Color(50, 50, 50) : Color.WHITE);
            comp.setForeground(fg);
        } else if (comp instanceof JComboBox) {
            comp.setBackground(buttonBg);
            comp.setForeground(fg);
        } else if (comp instanceof JTable) {
            comp.setBackground(isDarkMode ? new Color(40, 40, 40) : Color.WHITE);
            comp.setForeground(fg);
        } else if (comp instanceof JScrollPane) {
            JScrollPane scrollPane = (JScrollPane) comp;
            scrollPane.getViewport().setBackground(isDarkMode ? new Color(40, 40, 40) : Color.WHITE);
        }

        if (comp instanceof Container) {
            for (Component child : ((Container) comp).getComponents()) {
                applyThemeToComponent(child, bg, fg, panelBg, buttonBg);
            }
        }
    }

    public static boolean isDarkMode() {
        return isDarkMode;
    }
}