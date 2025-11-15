package com.fileconverter.ui;

import com.fileconverter.converter.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class BatchConverterUI extends JFrame {

    private JTable filesTable;
    private DefaultTableModel tableModel;
    private JButton addFilesButton;
    private JButton removeFileButton;
    private JButton clearAllButton;
    private JButton convertAllButton;
    private JComboBox<String> outputFormatCombo;
    private JTextField outputFolderField;
    private JButton selectOutputFolderButton;
    private JProgressBar progressBar;
    private JTextArea logArea;

    private Map<String, FileConverter> converters;
    private File outputFolder;

    public BatchConverterUI() {
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
        setTitle("Batch File Converter");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Title
        JLabel titleLabel = new JLabel("Batch File Converter");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Center panel with table and controls
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));

        // Files table
        String[] columnNames = {"File Name", "Path", "Size (KB)", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        filesTable = new JTable(tableModel);
        filesTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        filesTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        filesTable.getColumnModel().getColumn(1).setPreferredWidth(350);
        filesTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        filesTable.getColumnModel().getColumn(3).setPreferredWidth(100);

        JScrollPane tableScrollPane = new JScrollPane(filesTable);
        tableScrollPane.setPreferredSize(new Dimension(850, 250));
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Button panel for table
        JPanel tableButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addFilesButton = new JButton("Add Files");
        addFilesButton.addActionListener(e -> addFiles());
        removeFileButton = new JButton("Remove Selected");
        removeFileButton.addActionListener(e -> removeSelectedFiles());
        clearAllButton = new JButton("Clear All");
        clearAllButton.addActionListener(e -> clearAllFiles());

        tableButtonPanel.add(addFilesButton);
        tableButtonPanel.add(removeFileButton);
        tableButtonPanel.add(clearAllButton);
        centerPanel.add(tableButtonPanel, BorderLayout.SOUTH);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel with conversion settings
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));

        // Output format selection
        JPanel formatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formatPanel.add(new JLabel("Convert to:"));
        outputFormatCombo = new JComboBox<>(new String[]{"PDF"});
        formatPanel.add(outputFormatCombo);
        bottomPanel.add(formatPanel);

        // Output folder selection
        JPanel outputPanel = new JPanel(new BorderLayout(5, 5));
        outputPanel.add(new JLabel("Output Folder:"), BorderLayout.NORTH);
        JPanel outputFieldPanel = new JPanel(new BorderLayout(5, 5));
        outputFolderField = new JTextField();
        outputFolderField.setEditable(false);
        selectOutputFolderButton = new JButton("Browse...");
        selectOutputFolderButton.addActionListener(e -> selectOutputFolder());
        outputFieldPanel.add(outputFolderField, BorderLayout.CENTER);
        outputFieldPanel.add(selectOutputFolderButton, BorderLayout.EAST);
        outputPanel.add(outputFieldPanel, BorderLayout.CENTER);
        bottomPanel.add(outputPanel);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Progress bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(850, 25));
        bottomPanel.add(progressBar);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Convert button
        convertAllButton = new JButton("Convert All Files");
        convertAllButton.setFont(new Font("Arial", Font.BOLD, 16));
        convertAllButton.setPreferredSize(new Dimension(200, 40));
        convertAllButton.addActionListener(e -> convertAllFiles());
        JPanel convertButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        convertButtonPanel.add(convertAllButton);
        bottomPanel.add(convertButtonPanel);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Log area
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.add(new JLabel("Conversion Log:"), BorderLayout.NORTH);
        logArea = new JTextArea(6, 50);
        logArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logPanel.add(logScrollPane, BorderLayout.CENTER);
        bottomPanel.add(logPanel);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void addFiles() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Supported Files", "txt", "docx", "rtf", "html", "htm"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File[] selectedFiles = fileChooser.getSelectedFiles();
            for (File file : selectedFiles) {
                addFileToTable(file);
            }
            log("Added " + selectedFiles.length + " file(s)");
        }
    }

    private void addFileToTable(File file) {
        String fileName = file.getName();
        String path = file.getAbsolutePath();
        long sizeKB = file.length() / 1024;
        String status = "Pending";

        tableModel.addRow(new Object[]{fileName, path, sizeKB, status});
    }

    private void removeSelectedFiles() {
        int[] selectedRows = filesTable.getSelectedRows();
        for (int i = selectedRows.length - 1; i >= 0; i--) {
            tableModel.removeRow(selectedRows[i]);
        }
        log("Removed " + selectedRows.length + " file(s)");
    }

    private void clearAllFiles() {
        int rowCount = tableModel.getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            tableModel.removeRow(i);
        }
        log("Cleared all files");
    }

    private void selectOutputFolder() {
        JFileChooser folderChooser = new JFileChooser();
        folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int result = folderChooser.showDialog(this, "Select Output Folder");
        if (result == JFileChooser.APPROVE_OPTION) {
            outputFolder = folderChooser.getSelectedFile();
            outputFolderField.setText(outputFolder.getAbsolutePath());
            log("Output folder: " + outputFolder.getName());
        }
    }

    private void convertAllFiles() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Please add files to convert!",
                    "No Files",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (outputFolder == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select an output folder!",
                    "No Output Folder",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Disable buttons during conversion
        convertAllButton.setEnabled(false);
        addFilesButton.setEnabled(false);

        // Start conversion in background thread
        new Thread(() -> {
            int totalFiles = tableModel.getRowCount();
            int successCount = 0;
            int failCount = 0;

            for (int i = 0; i < totalFiles; i++) {
                final int row = i;
                String filePath = (String) tableModel.getValueAt(row, 1);
                File inputFile = new File(filePath);

                // Update status
                SwingUtilities.invokeLater(() -> {
                    tableModel.setValueAt("Converting...", row, 3);
                    progressBar.setValue((row * 100) / totalFiles);
                });

                try {
                    // Get file extension
                    String extension = getFileExtension(inputFile.getName());
                    FileConverter converter = converters.get(extension.toLowerCase());

                    if (converter != null) {
                        // Create output file name
                        String outputFileName = inputFile.getName().substring(0,
                                inputFile.getName().lastIndexOf('.')) + ".pdf";
                        File outputFile = new File(outputFolder, outputFileName);

                        // Convert
                        converter.convertToPdf(inputFile, outputFile);

                        SwingUtilities.invokeLater(() -> {
                            tableModel.setValueAt("✓ Success", row, 3);
                        });
                        successCount++;
                        log("✓ Converted: " + inputFile.getName());
                    } else {
                        throw new Exception("Unsupported file type");
                    }

                } catch (Exception ex) {
                    failCount++;
                    SwingUtilities.invokeLater(() -> {
                        tableModel.setValueAt("✗ Failed", row, 3);
                    });
                    log("✗ Failed: " + inputFile.getName() + " - " + ex.getMessage());
                }
            }

            // Final update
            final int finalSuccess = successCount;
            final int finalFail = failCount;
            SwingUtilities.invokeLater(() -> {
                progressBar.setValue(100);
                convertAllButton.setEnabled(true);
                addFilesButton.setEnabled(true);
                log("=== Conversion Complete ===");
                log("Success: " + finalSuccess + " | Failed: " + finalFail);

                JOptionPane.showMessageDialog(this,
                        "Conversion Complete!\nSuccess: " + finalSuccess + "\nFailed: " + finalFail,
                        "Complete",
                        JOptionPane.INFORMATION_MESSAGE);
            });

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
}