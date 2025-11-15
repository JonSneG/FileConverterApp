package com.fileconverter.ui;

import com.fileconverter.converter.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConverterUI extends JFrame {

    private JTextField inputFileField;
    private JTextField outputFileField;
    private JComboBox<String> conversionTypeCombo;
    private JButton selectInputButton;
    private JButton selectOutputButton;
    private JButton convertButton;
    private JTextArea logArea;

    private Map<String, FileConverter> converters;
    private File inputFile;
    private File outputFile;

    public ConverterUI() {
        initializeConverters();
        initializeUI();
    }

    private void initializeConverters() {
        converters = new HashMap<>();
        converters.put("txt", new TxtToPdfConverter());
        converters.put("docx", new DocxToPdfConverter());
        converters.put("rtf", new RtfToPdfConverter());
        converters.put("html", new HtmlToPdfConverter());
        converters.put("htm", new HtmlToPdfConverter());
    }

    private void initializeUI() {
        setTitle("File to PDF Converter");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Create main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("File Converter Application");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Batch mode button
        JButton batchModeButton = new JButton("Open Batch Converter");
        batchModeButton.setFont(new Font("Arial", Font.PLAIN, 14));
        batchModeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        batchModeButton.addActionListener(e -> openBatchConverter());
        mainPanel.add(batchModeButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        batchModeButton.addActionListener(e -> openBatchConverter());
        mainPanel.add(batchModeButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // PDF Tools button
        JButton pdfToolsButton = new JButton("Open PDF Tools");
        pdfToolsButton.setFont(new Font("Arial", Font.PLAIN, 14));
        pdfToolsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        pdfToolsButton.addActionListener(e -> openPdfTools());
        mainPanel.add(pdfToolsButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Conversion type selection
        JPanel conversionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        conversionPanel.add(new JLabel("Conversion Type:"));
        conversionTypeCombo = new JComboBox<>(new String[]{
                "TXT to PDF",
                "DOCX to PDF",
                "RTF to PDF",
                "HTML to PDF",
                "PDF to TXT"
        });
        conversionPanel.add(conversionTypeCombo);
        mainPanel.add(conversionPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Input file selection
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.add(new JLabel("Input File:"), BorderLayout.NORTH);
        JPanel inputFieldPanel = new JPanel(new BorderLayout(5, 5));
        inputFileField = new JTextField();
        inputFileField.setEditable(false);
        selectInputButton = new JButton("Browse...");
        selectInputButton.addActionListener(e -> selectInputFile());
        inputFieldPanel.add(inputFileField, BorderLayout.CENTER);
        inputFieldPanel.add(selectInputButton, BorderLayout.EAST);
        inputPanel.add(inputFieldPanel, BorderLayout.CENTER);
        mainPanel.add(inputPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Output file selection
        JPanel outputPanel = new JPanel(new BorderLayout(5, 5));
        outputPanel.add(new JLabel("Output File:"), BorderLayout.NORTH);
        JPanel outputFieldPanel = new JPanel(new BorderLayout(5, 5));
        outputFileField = new JTextField();
        outputFileField.setEditable(false);
        selectOutputButton = new JButton("Browse...");
        selectOutputButton.addActionListener(e -> selectOutputFile());
        outputFieldPanel.add(outputFileField, BorderLayout.CENTER);
        outputFieldPanel.add(selectOutputButton, BorderLayout.EAST);
        outputPanel.add(outputFieldPanel, BorderLayout.CENTER);
        mainPanel.add(outputPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Convert button
        convertButton = new JButton("Convert File");
        convertButton.setFont(new Font("Arial", Font.BOLD, 16));
        convertButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        convertButton.addActionListener(e -> convertFile());
        mainPanel.add(convertButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Log area
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.add(new JLabel("Log:"), BorderLayout.NORTH);
        logArea = new JTextArea(10, 50);
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        logPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(logPanel);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void selectInputFile() {
        JFileChooser fileChooser = new JFileChooser();
        String selectedType = (String) conversionTypeCombo.getSelectedItem();

        if (selectedType.contains("TXT to PDF")) {
            fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
        } else if (selectedType.contains("DOCX to PDF")) {
            fileChooser.setFileFilter(new FileNameExtensionFilter("Word Documents", "docx"));
        } else if (selectedType.contains("RTF to PDF")) {
            fileChooser.setFileFilter(new FileNameExtensionFilter("RTF Documents", "rtf"));
        } else if (selectedType.contains("HTML to PDF")) {
            fileChooser.setFileFilter(new FileNameExtensionFilter("HTML Files", "html", "htm"));
        } else if (selectedType.contains("PDF to TXT")) {
            fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));
        }

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            inputFile = fileChooser.getSelectedFile();
            inputFileField.setText(inputFile.getAbsolutePath());
            log("Input file selected: " + inputFile.getName());
        }
    }

    private void selectOutputFile() {
        JFileChooser fileChooser = new JFileChooser();
        String selectedType = (String) conversionTypeCombo.getSelectedItem();

        if (selectedType.contains("to PDF")) {
            fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));
        } else if (selectedType.contains("PDF to TXT")) {
            fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
        }

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            outputFile = fileChooser.getSelectedFile();

            // Add extension if not present
            String path = outputFile.getAbsolutePath();
            if (selectedType.contains("to PDF") && !path.toLowerCase().endsWith(".pdf")) {
                outputFile = new File(path + ".pdf");
            } else if (selectedType.contains("PDF to TXT") && !path.toLowerCase().endsWith(".txt")) {
                outputFile = new File(path + ".txt");
            }

            outputFileField.setText(outputFile.getAbsolutePath());
            log("Output file selected: " + outputFile.getName());
        }
    }

    private void convertFile() {
        if (inputFile == null || outputFile == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select both input and output files!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String selectedType = (String) conversionTypeCombo.getSelectedItem();

        convertButton.setEnabled(false);
        log("Starting conversion: " + selectedType);

        // Run conversion in background thread
        new Thread(() -> {
            try {
                String extension = getFileExtension(inputFile.getName());
                FileConverter converter = converters.get(extension.toLowerCase());

                if (converter != null && selectedType.contains("to PDF")) {
                    converter.convertToPdf(inputFile, outputFile);
                    log("✓ Conversion successful!");
                    log("Output saved to: " + outputFile.getAbsolutePath());
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this,
                                "File converted successfully!",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    });
                } else if (extension.equalsIgnoreCase("pdf") && selectedType.contains("PDF to TXT")) {
                    TxtToPdfConverter txtConverter = new TxtToPdfConverter();
                    txtConverter.convertFromPdf(inputFile, outputFile);
                    log("✓ Conversion successful!");
                    log("Output saved to: " + outputFile.getAbsolutePath());
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this,
                                "File converted successfully!",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    });
                } else {
                    throw new Exception("Unsupported conversion type");
                }

            } catch (Exception ex) {
                log("✗ Error: " + ex.getMessage());
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                            "Conversion failed: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                });
            } finally {
                SwingUtilities.invokeLater(() -> convertButton.setEnabled(true));
            }
        }).start();
    }

    private String getFileExtension(String fileName) {
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "";
        }
        return fileName.substring(lastIndexOf + 1);
    }

    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private void openBatchConverter() {
        BatchConverterUI batchUI = new BatchConverterUI();
        batchUI.setVisible(true);
    }

    private void openPdfTools() {
        PdfToolsUI pdfToolsUI = new PdfToolsUI();
        pdfToolsUI.setVisible(true);
    }
}