package com.fileconverter.converter;

import com.itextpdf.html2pdf.HtmlConverter;
import java.io.*;

public class HtmlToPdfConverter implements FileConverter {

    @Override
    public void convertToPdf(File inputFile, File outputFile) throws Exception {
        // Read HTML file
        String htmlContent = new String(java.nio.file.Files.readAllBytes(inputFile.toPath()),
                java.nio.charset.StandardCharsets.UTF_8);

        // Convert HTML to PDF
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            HtmlConverter.convertToPdf(htmlContent, fos);
        }
    }

    @Override
    public void convertFromPdf(File inputFile, File outputFile) throws Exception {
        throw new UnsupportedOperationException("PDF to HTML conversion not yet implemented");
    }

    @Override
    public boolean supports(String fileExtension) {
        return "html".equalsIgnoreCase(fileExtension) || "htm".equalsIgnoreCase(fileExtension);
    }
}