package com.fileconverter.converter;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.*;

public class DocxToPdfConverter implements FileConverter {

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
            return PdfFontFactory.createFont("Arial", PdfEncodings.IDENTITY_H, EmbeddingStrategy.PREFER_EMBEDDED);
        } catch (Exception e) {
            System.err.println("Could not load Arial font: " + e.getMessage());
        }

        throw new IOException("No suitable Unicode font found. Please add DejaVuSans.ttf to resources/fonts/");
    }

    @Override
    public void convertToPdf(File inputFile, File outputFile) throws Exception {
        // Read DOCX file
        FileInputStream fis = new FileInputStream(inputFile);
        XWPFDocument document = new XWPFDocument(fis);

        // Create PDF with Unicode font
        PdfWriter writer = new PdfWriter(outputFile);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document pdfDocument = new Document(pdfDoc);

        // Get Unicode font that supports Cyrillic
        PdfFont font = getUnicodeFont();

        // Extract paragraphs and add to PDF
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            String text = paragraph.getText();
            if (text != null && !text.trim().isEmpty()) {
                Paragraph p = new Paragraph(text);
                p.setFont(font);
                p.setFontSize(12);
                pdfDocument.add(p);
            }
        }

        pdfDocument.close();
        document.close();
        fis.close();
    }

    @Override
    public void convertFromPdf(File inputFile, File outputFile) throws Exception {
        throw new UnsupportedOperationException("PDF to DOCX conversion not yet implemented");
    }

    @Override
    public boolean supports(String fileExtension) {
        return "docx".equalsIgnoreCase(fileExtension);
    }
}