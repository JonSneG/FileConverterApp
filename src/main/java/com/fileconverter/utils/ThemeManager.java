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
    private static final Color DARK_BUTTON_BG = new Color(70, 70, 75);

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
            JButton button = (JButton) comp;
            button.setBackground(buttonBg);
            button.setForeground(fg);
            button.setOpaque(true);
            button.setBorderPainted(true);
            button.setFocusPainted(false);

            // Ensure text is visible
            if (isDarkMode) {
                button.setForeground(Color.WHITE);
            } else {
                button.setForeground(Color.BLACK);
            }
        } else if (comp instanceof JLabel) {
            comp.setForeground(fg);
        } else if (comp instanceof JTextField) {
            JTextField field = (JTextField) comp;
            field.setBackground(isDarkMode ? new Color(50, 50, 50) : Color.WHITE);
            field.setForeground(fg);
            field.setCaretColor(fg);
        } else if (comp instanceof JTextArea) {
            JTextArea area = (JTextArea) comp;
            area.setBackground(isDarkMode ? new Color(40, 40, 40) : Color.WHITE);
            area.setForeground(fg);
            area.setCaretColor(fg);
        } else if (comp instanceof JComboBox) {
            JComboBox<?> combo = (JComboBox<?>) comp;
            combo.setBackground(buttonBg);
            combo.setForeground(fg);
        } else if (comp instanceof JTable) {
            JTable table = (JTable) comp;
            table.setBackground(isDarkMode ? new Color(40, 40, 40) : Color.WHITE);
            table.setForeground(fg);
            table.setGridColor(isDarkMode ? new Color(60, 60, 60) : Color.GRAY);

            // Update table header
            if (table.getTableHeader() != null) {
                table.getTableHeader().setBackground(isDarkMode ? new Color(50, 50, 50) : new Color(230, 230, 230));
                table.getTableHeader().setForeground(fg);
            }
        } else if (comp instanceof JScrollPane) {
            JScrollPane scrollPane = (JScrollPane) comp;
            scrollPane.setBackground(isDarkMode ? new Color(40, 40, 40) : Color.WHITE);
            scrollPane.getViewport().setBackground(isDarkMode ? new Color(40, 40, 40) : Color.WHITE);
        } else if (comp instanceof JProgressBar) {
            JProgressBar progressBar = (JProgressBar) comp;
            progressBar.setBackground(isDarkMode ? new Color(50, 50, 50) : new Color(230, 230, 230));
            progressBar.setForeground(isDarkMode ? new Color(100, 150, 255) : new Color(0, 120, 215));
        } else if (comp instanceof JMenuBar) {
            JMenuBar menuBar = (JMenuBar) comp;
            menuBar.setBackground(isDarkMode ? new Color(45, 45, 48) : new Color(240, 240, 240));
            menuBar.setForeground(fg);
        } else if (comp instanceof JMenu) {
            JMenu menu = (JMenu) comp;
            menu.setBackground(isDarkMode ? new Color(45, 45, 48) : new Color(240, 240, 240));
            menu.setForeground(fg);
            menu.setOpaque(true);
        } else if (comp instanceof JMenuItem) {
            JMenuItem menuItem = (JMenuItem) comp;
            menuItem.setBackground(isDarkMode ? new Color(60, 60, 60) : Color.WHITE);
            menuItem.setForeground(fg);
            menuItem.setOpaque(true);
        } else if (comp instanceof JSpinner) {
            JSpinner spinner = (JSpinner) comp;
            spinner.setBackground(isDarkMode ? new Color(50, 50, 50) : Color.WHITE);
            spinner.setForeground(fg);

            // Update spinner editor
            JComponent editor = spinner.getEditor();
            if (editor instanceof JSpinner.DefaultEditor) {
                JTextField textField = ((JSpinner.DefaultEditor) editor).getTextField();
                textField.setBackground(isDarkMode ? new Color(50, 50, 50) : Color.WHITE);
                textField.setForeground(fg);
                textField.setCaretColor(fg);
            }
        } else if (comp instanceof JSlider) {
            JSlider slider = (JSlider) comp;
            slider.setBackground(panelBg);
            slider.setForeground(fg);
        } else if (comp instanceof JList) {
            JList<?> list = (JList<?>) comp;
            list.setBackground(isDarkMode ? new Color(40, 40, 40) : Color.WHITE);
            list.setForeground(fg);
        } else if (comp instanceof JTabbedPane) {
            JTabbedPane tabbedPane = (JTabbedPane) comp;
            tabbedPane.setBackground(panelBg);
            tabbedPane.setForeground(fg);
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