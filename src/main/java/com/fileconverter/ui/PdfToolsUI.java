package com.fileconverter.ui;

import com.fileconverter.utils.PdfUtils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PdfToolsUI extends JFrame {

    private JTabbedPane tabbedPane;
    private JTextArea logArea;

    public PdfToolsUI() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("PDF Tools");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Title
        JLabel titleLabel = new JLabel("PDF Advanced Tools");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Tabbed pane for different tools
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Merge PDFs", createMergePanel());
        tabbedPane.addTab("Split PDF", createSplitPanel());
        tabbedPane.addTab("Add Watermark", createWatermarkPanel());
        tabbedPane.addTab("Password Protection", createPasswordPanel());

        add(tabbedPane, BorderLayout.CENTER);

        // Log area
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        logPanel.add(new JLabel("Log:"), BorderLayout.NORTH);
        logArea = new JTextArea(8, 50);
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        logPanel.add(scrollPane, BorderLayout.CENTER);

        add(logPanel, BorderLayout.SOUTH);
    }

    // ========== MERGE PANEL ==========
    private JPanel createMergePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Files list
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> filesList = new JList<>(listModel);
        JScrollPane listScrollPane = new JScrollPane(filesList);
        listScrollPane.setPreferredSize(new Dimension(700, 300));

        List<File> selectedFiles = new ArrayList<>();

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton addButton = new JButton("Add PDFs");
        addButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setMultiSelectionEnabled(true);
            chooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));

            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                for (File file : chooser.getSelectedFiles()) {
                    selectedFiles.add(file);
                    listModel.addElement(file.getName());
                }
                log("Added " + chooser.getSelectedFiles().length + " PDF(s)");
            }
        });

        JButton removeButton = new JButton("Remove Selected");
        removeButton.addActionListener(e -> {
            int[] indices = filesList.getSelectedIndices();
            for (int i = indices.length - 1; i >= 0; i--) {
                selectedFiles.remove(indices[i]);
                listModel.remove(indices[i]);
            }
        });

        JButton clearButton = new JButton("Clear All");
        clearButton.addActionListener(e -> {
            selectedFiles.clear();
            listModel.clear();
        });

        JButton mergeButton = new JButton("Merge PDFs");
        mergeButton.setFont(new Font("Arial", Font.BOLD, 14));
        mergeButton.addActionListener(e -> {
            if (selectedFiles.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please add PDF files to merge!");
                return;
            }

            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));

            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File outputFile = chooser.getSelectedFile();
                if (!outputFile.getName().toLowerCase().endsWith(".pdf")) {
                    outputFile = new File(outputFile.getAbsolutePath() + ".pdf");
                }

                final File finalOutput = outputFile;
                new Thread(() -> {
                    try {
                        log("Merging " + selectedFiles.size() + " PDF(s)...");
                        PdfUtils.mergePdfs(selectedFiles, finalOutput);
                        log("✓ Successfully merged to: " + finalOutput.getName());
                        SwingUtilities.invokeLater(() ->
                                JOptionPane.showMessageDialog(this, "PDFs merged successfully!"));
                    } catch (Exception ex) {
                        log("✗ Error: " + ex.getMessage());
                        SwingUtilities.invokeLater(() ->
                                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                                        "Error", JOptionPane.ERROR_MESSAGE));
                    }
                }).start();
            }
        });

        buttonsPanel.add(addButton);
        buttonsPanel.add(removeButton);
        buttonsPanel.add(clearButton);
        buttonsPanel.add(mergeButton);

        panel.add(new JLabel("Select PDF files to merge (order matters):"), BorderLayout.NORTH);
        panel.add(listScrollPane, BorderLayout.CENTER);
        panel.add(buttonsPanel, BorderLayout.SOUTH);

        return panel;
    }

    // ========== SPLIT PANEL ==========
    private JPanel createSplitPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // Input file selection
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBorder(new TitledBorder("Input PDF"));
        JTextField inputField = new JTextField();
        inputField.setEditable(false);
        JButton browseInputButton = new JButton("Browse...");
        File[] inputFile = new File[1];

        browseInputButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                inputFile[0] = chooser.getSelectedFile();
                inputField.setText(inputFile[0].getAbsolutePath());
                try {
                    int pages = PdfUtils.getPageCount(inputFile[0]);
                    log("PDF loaded: " + pages + " page(s)");
                } catch (Exception ex) {
                    log("Error reading PDF: " + ex.getMessage());
                }
            }
        });

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(browseInputButton, BorderLayout.EAST);
        centerPanel.add(inputPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Split options
        JPanel optionsPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        optionsPanel.setBorder(new TitledBorder("Split Options"));

        JRadioButton splitAllButton = new JRadioButton("Split into individual pages", true);
        JRadioButton splitRangeButton = new JRadioButton("Extract page range");

        ButtonGroup group = new ButtonGroup();
        group.add(splitAllButton);
        group.add(splitRangeButton);

        optionsPanel.add(splitAllButton);
        optionsPanel.add(splitRangeButton);
        centerPanel.add(optionsPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Page range panel
        JPanel rangePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rangePanel.setBorder(new TitledBorder("Page Range (if selected)"));
        rangePanel.add(new JLabel("From:"));
        JSpinner startSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        rangePanel.add(startSpinner);
        rangePanel.add(new JLabel("To:"));
        JSpinner endSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        rangePanel.add(endSpinner);
        centerPanel.add(rangePanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Output folder selection
        JPanel outputPanel = new JPanel(new BorderLayout(5, 5));
        outputPanel.setBorder(new TitledBorder("Output Folder"));
        JTextField outputField = new JTextField();
        outputField.setEditable(false);
        JButton browseOutputButton = new JButton("Browse...");
        File[] outputFolder = new File[1];

        browseOutputButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (chooser.showDialog(this, "Select Output Folder") == JFileChooser.APPROVE_OPTION) {
                outputFolder[0] = chooser.getSelectedFile();
                outputField.setText(outputFolder[0].getAbsolutePath());
            }
        });

        outputPanel.add(outputField, BorderLayout.CENTER);
        outputPanel.add(browseOutputButton, BorderLayout.EAST);
        centerPanel.add(outputPanel);

        panel.add(centerPanel, BorderLayout.CENTER);

        // Split button
        JButton splitButton = new JButton("Split PDF");
        splitButton.setFont(new Font("Arial", Font.BOLD, 14));
        splitButton.addActionListener(e -> {
            if (inputFile[0] == null || outputFolder[0] == null) {
                JOptionPane.showMessageDialog(this, "Please select input file and output folder!");
                return;
            }

            new Thread(() -> {
                try {
                    if (splitAllButton.isSelected()) {
                        log("Splitting PDF into individual pages...");
                        PdfUtils.splitPdf(inputFile[0], outputFolder[0]);
                        log("✓ PDF split successfully!");
                    } else {
                        int start = (int) startSpinner.getValue();
                        int end = (int) endSpinner.getValue();
                        String outputName = inputFile[0].getName().replace(".pdf", "_pages_" + start + "-" + end + ".pdf");
                        File outputFile = new File(outputFolder[0], outputName);

                        log("Extracting pages " + start + " to " + end + "...");
                        PdfUtils.splitPdfByRange(inputFile[0], outputFile, start, end);
                        log("✓ Pages extracted successfully!");
                    }
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(this, "PDF split successfully!"));
                } catch (Exception ex) {
                    log("✗ Error: " + ex.getMessage());
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                                    "Error", JOptionPane.ERROR_MESSAGE));
                }
            }).start();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(splitButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    // ========== WATERMARK PANEL ==========
    private JPanel createWatermarkPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // Input file
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBorder(new TitledBorder("Input PDF"));
        JTextField inputField = new JTextField();
        inputField.setEditable(false);
        JButton browseInputButton = new JButton("Browse...");
        File[] inputFile = new File[1];

        browseInputButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                inputFile[0] = chooser.getSelectedFile();
                inputField.setText(inputFile[0].getAbsolutePath());
            }
        });

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(browseInputButton, BorderLayout.EAST);
        centerPanel.add(inputPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Watermark text
        JPanel textPanel = new JPanel(new BorderLayout(5, 5));
        textPanel.setBorder(new TitledBorder("Watermark Text"));
        JTextField watermarkField = new JTextField("CONFIDENTIAL");
        textPanel.add(watermarkField, BorderLayout.CENTER);
        centerPanel.add(textPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Opacity slider
        JPanel opacityPanel = new JPanel(new BorderLayout(5, 5));
        opacityPanel.setBorder(new TitledBorder("Opacity"));
        JSlider opacitySlider = new JSlider(0, 100, 30);
        opacitySlider.setMajorTickSpacing(25);
        opacitySlider.setMinorTickSpacing(5);
        opacitySlider.setPaintTicks(true);
        opacitySlider.setPaintLabels(true);
        JLabel opacityLabel = new JLabel("30%");
        opacitySlider.addChangeListener(e -> opacityLabel.setText(opacitySlider.getValue() + "%"));
        opacityPanel.add(opacitySlider, BorderLayout.CENTER);
        opacityPanel.add(opacityLabel, BorderLayout.EAST);
        centerPanel.add(opacityPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Output file
        JPanel outputPanel = new JPanel(new BorderLayout(5, 5));
        outputPanel.setBorder(new TitledBorder("Output PDF"));
        JTextField outputField = new JTextField();
        outputField.setEditable(false);
        JButton browseOutputButton = new JButton("Browse...");
        File[] outputFile = new File[1];

        browseOutputButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                outputFile[0] = chooser.getSelectedFile();
                if (!outputFile[0].getName().toLowerCase().endsWith(".pdf")) {
                    outputFile[0] = new File(outputFile[0].getAbsolutePath() + ".pdf");
                }
                outputField.setText(outputFile[0].getAbsolutePath());
            }
        });

        outputPanel.add(outputField, BorderLayout.CENTER);
        outputPanel.add(browseOutputButton, BorderLayout.EAST);
        centerPanel.add(outputPanel);

        panel.add(centerPanel, BorderLayout.CENTER);

        // Apply button
        JButton applyButton = new JButton("Add Watermark");
        applyButton.setFont(new Font("Arial", Font.BOLD, 14));
        applyButton.addActionListener(e -> {
            if (inputFile[0] == null || outputFile[0] == null) {
                JOptionPane.showMessageDialog(this, "Please select input and output files!");
                return;
            }

            String watermarkText = watermarkField.getText();
            float opacity = opacitySlider.getValue() / 100.0f;

            new Thread(() -> {
                try {
                    log("Adding watermark: " + watermarkText);
                    PdfUtils.addWatermark(inputFile[0], outputFile[0], watermarkText, opacity);
                    log("✓ Watermark added successfully!");
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(this, "Watermark added successfully!"));
                } catch (Exception ex) {
                    log("✗ Error: " + ex.getMessage());
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                                    "Error", JOptionPane.ERROR_MESSAGE));
                }
            }).start();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(applyButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    // ========== PASSWORD PROTECTION PANEL ==========
    private JPanel createPasswordPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // Input file
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBorder(new TitledBorder("Input PDF"));
        JTextField inputField = new JTextField();
        inputField.setEditable(false);
        JButton browseInputButton = new JButton("Browse...");
        File[] inputFile = new File[1];

        browseInputButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                inputFile[0] = chooser.getSelectedFile();
                inputField.setText(inputFile[0].getAbsolutePath());
            }
        });

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(browseInputButton, BorderLayout.EAST);
        centerPanel.add(inputPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Passwords
        JPanel passwordsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        passwordsPanel.setBorder(new TitledBorder("Passwords"));
        passwordsPanel.add(new JLabel("User Password (to open):"));
        JPasswordField userPasswordField = new JPasswordField();
        passwordsPanel.add(userPasswordField);
        passwordsPanel.add(new JLabel("Owner Password (to edit):"));
        JPasswordField ownerPasswordField = new JPasswordField();
        passwordsPanel.add(ownerPasswordField);
        centerPanel.add(passwordsPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Output file
        JPanel outputPanel = new JPanel(new BorderLayout(5, 5));
        outputPanel.setBorder(new TitledBorder("Output PDF"));
        JTextField outputField = new JTextField();
        outputField.setEditable(false);
        JButton browseOutputButton = new JButton("Browse...");
        File[] outputFile = new File[1];

        browseOutputButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                outputFile[0] = chooser.getSelectedFile();
                if (!outputFile[0].getName().toLowerCase().endsWith(".pdf")) {
                    outputFile[0] = new File(outputFile[0].getAbsolutePath() + ".pdf");
                }
                outputField.setText(outputFile[0].getAbsolutePath());
            }
        });

        outputPanel.add(outputField, BorderLayout.CENTER);
        outputPanel.add(browseOutputButton, BorderLayout.EAST);
        centerPanel.add(outputPanel);

        panel.add(centerPanel, BorderLayout.CENTER);

        // Apply button
        JButton applyButton = new JButton("Add Password Protection");
        applyButton.setFont(new Font("Arial", Font.BOLD, 14));
        applyButton.addActionListener(e -> {
            if (inputFile[0] == null || outputFile[0] == null) {
                JOptionPane.showMessageDialog(this, "Please select input and output files!");
                return;
            }

            String userPassword = new String(userPasswordField.getPassword());
            String ownerPassword = new String(ownerPasswordField.getPassword());

            if (userPassword.isEmpty() || ownerPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both passwords!");
                return;
            }

            new Thread(() -> {
                try {
                    log("Adding password protection...");
                    PdfUtils.addPasswordProtection(inputFile[0], outputFile[0], userPassword, ownerPassword);
                    log("✓ Password protection added successfully!");
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(this, "Password protection added successfully!"));
                } catch (Exception ex) {
                    log("✗ Error: " + ex.getMessage());
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                                    "Error", JOptionPane.ERROR_MESSAGE));
                }
            }).start();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(applyButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
}