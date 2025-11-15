package com.fileconverter.converter;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class TxtToPdfConverter implements FileConverter {

    private PdfFont getUnicodeFont() throws IOException {
        // Try to load DejaVu Sans from resources
        try {
            InputStream fontStream = getClass().getResourceAsStream("/fonts/DejaVuSans.ttf");
            if (fontStream != null) {
                byte[] fontBytes = fontStream.readAllBytes();
                fontStream.close();
                return PdfFontFactory.createFont(fontBytes, PdfEncodings.IDENTITY_H, EmbeddingStrategy.PREFER_EMBEDDED);
            }
        } catch (Exception e) {
            System.err.println("Could not load DejaVu Sans font: " + e.getMessage());
        }

        // Fallback: try to use system fonts
        try {
            // Try Arial Unicode or other system fonts
            return PdfFontFactory.createFont("Arial", PdfEncodings.IDENTITY_H, EmbeddingStrategy.PREFER_EMBEDDED);
        } catch (Exception e) {
            System.err.println("Could not load Arial font: " + e.getMessage());
        }

        throw new IOException("No suitable Unicode font found. Please add DejaVuSans.ttf to resources/fonts/");
    }

    @Override
    public void convertToPdf(File inputFile, File outputFile) throws Exception {
        // Read text file with UTF-8 encoding
        String content = Files.readString(inputFile.toPath(), StandardCharsets.UTF_8);

        // Create PDF with Unicode font
        PdfWriter writer = new PdfWriter(outputFile);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // Get Unicode font that supports Cyrillic
        PdfFont font = getUnicodeFont();

        // Split content into lines and add each as paragraph
        String[] lines = content.split("\n");
        for (String line : lines) {
            Paragraph p = new Paragraph(line);
            p.setFont(font);
            p.setFontSize(12);
            document.add(p);
        }

        document.close();
    }

    @Override
    public void convertFromPdf(File inputFile, File outputFile) throws Exception {
        // Load PDF
        PDDocument document = PDDocument.load(inputFile);
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);
        document.close();

        // Write to text file with UTF-8 encoding
        Files.writeString(outputFile.toPath(), text, StandardCharsets.UTF_8);
    }

    @Override
    public boolean supports(String fileExtension) {
        return "txt".equalsIgnoreCase(fileExtension);
    }
}