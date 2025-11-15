package com.fileconverter;

import com.fileconverter.ui.ConverterUI;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create and show UI
        SwingUtilities.invokeLater(() -> {
            ConverterUI ui = new ConverterUI();
            ui.setVisible(true);
        });
    }
}