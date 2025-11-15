package com.fileconverter.converter;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.rtf.RTFEditorKit;
import java.io.*;

public class RtfToPdfConverter implements FileConverter {

    private PdfFont getUnicodeFont() throws IOException {
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

        try {
            return PdfFontFactory.createFont("Arial", PdfEncodings.IDENTITY_H, EmbeddingStrategy.PREFER_EMBEDDED);
        } catch (Exception e) {
            System.err.println("Could not load Arial font: " + e.getMessage());
        }

        throw new IOException("No suitable Unicode font found.");
    }

    @Override
    public void convertToPdf(File inputFile, File outputFile) throws Exception {
        // Read RTF file
        DefaultStyledDocument styledDoc = new DefaultStyledDocument();
        RTFEditorKit rtfKit = new RTFEditorKit();

        try (FileInputStream fis = new FileInputStream(inputFile)) {
            rtfKit.read(fis, styledDoc, 0);
        } catch (BadLocationException e) {
            throw new Exception("Error reading RTF file: " + e.getMessage());
        }

        // Extract text
        String text = styledDoc.getText(0, styledDoc.getLength());

        // Create PDF
        PdfWriter writer = new PdfWriter(outputFile);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        PdfFont font = getUnicodeFont();

        // Split into lines and add to PDF
        String[] lines = text.split("\n");
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                Paragraph p = new Paragraph(line);
                p.setFont(font);
                p.setFontSize(12);
                document.add(p);
            }
        }

        document.close();
    }

    @Override
    public void convertFromPdf(File inputFile, File outputFile) throws Exception {
        throw new UnsupportedOperationException("PDF to RTF conversion not yet implemented");
    }

    @Override
    public boolean supports(String fileExtension) {
        return "rtf".equalsIgnoreCase(fileExtension);
    }
}