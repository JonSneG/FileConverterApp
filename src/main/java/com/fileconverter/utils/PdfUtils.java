package com.fileconverter.utils;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class PdfUtils {

    /**
     * Merge multiple PDF files into one
     */
    public static void mergePdfs(List<File> pdfFiles, File outputFile) throws Exception {
        PDFMergerUtility merger = new PDFMergerUtility();

        for (File file : pdfFiles) {
            merger.addSource(file);
        }

        merger.setDestinationFileName(outputFile.getAbsolutePath());
        merger.mergeDocuments(null);
    }

    /**
     * Split a PDF into individual pages
     */
    public static void splitPdf(File inputFile, File outputFolder) throws Exception {
        PDDocument document = PDDocument.load(inputFile);
        int totalPages = document.getNumberOfPages();

        String baseFileName = inputFile.getName().replace(".pdf", "");

        for (int i = 0; i < totalPages; i++) {
            PDDocument singlePageDoc = new PDDocument();
            singlePageDoc.addPage(document.getPage(i));

            File outputFile = new File(outputFolder, baseFileName + "_page_" + (i + 1) + ".pdf");
            singlePageDoc.save(outputFile);
            singlePageDoc.close();
        }

        document.close();
    }

    /**
     * Split PDF by page range
     */
    public static void splitPdfByRange(File inputFile, File outputFile, int startPage, int endPage) throws Exception {
        PDDocument document = PDDocument.load(inputFile);
        PDDocument newDoc = new PDDocument();

        // Validate page numbers
        int totalPages = document.getNumberOfPages();
        if (startPage < 1 || endPage > totalPages || startPage > endPage) {
            document.close();
            throw new IllegalArgumentException("Invalid page range");
        }

        // Add pages (convert to 0-based index)
        for (int i = startPage - 1; i < endPage; i++) {
            newDoc.addPage(document.getPage(i));
        }

        newDoc.save(outputFile);
        newDoc.close();
        document.close();
    }

    /**
     * Add watermark to PDF
     */
    public static void addWatermark(File inputFile, File outputFile, String watermarkText, float opacity) throws Exception {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(inputFile), new PdfWriter(outputFile));

        int numberOfPages = pdfDoc.getNumberOfPages();
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

        for (int i = 1; i <= numberOfPages; i++) {
            PdfPage page = pdfDoc.getPage(i);
            Rectangle pageSize = page.getPageSize();

            // Create canvas for watermark
            PdfCanvas canvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdfDoc);

            // Save graphics state
            canvas.saveState();

            // Set transparency
            PdfExtGState gs1 = new PdfExtGState();
            gs1.setFillOpacity(opacity);
            canvas.setExtGState(gs1);

            // Calculate center position
            float x = pageSize.getWidth() / 2;
            float y = pageSize.getHeight() / 2;

            // Create paragraph for watermark
            Canvas canvasLayout = new Canvas(canvas, pageSize);
            Paragraph p = new Paragraph(watermarkText)
                    .setFont(font)
                    .setFontSize(60)
                    .setFontColor(com.itextpdf.kernel.colors.ColorConstants.LIGHT_GRAY);

            canvasLayout.showTextAligned(p, x, y, i, TextAlignment.CENTER, VerticalAlignment.MIDDLE, 45);

            // Restore graphics state
            canvas.restoreState();
            canvasLayout.close();
        }

        pdfDoc.close();
    }

    /**
     * Get PDF page count
     */
    public static int getPageCount(File pdfFile) throws Exception {
        PDDocument document = PDDocument.load(pdfFile);
        int pageCount = document.getNumberOfPages();
        document.close();
        return pageCount;
    }

    /**
     * Add password protection to PDF
     */
    public static void addPasswordProtection(File inputFile, File outputFile, String userPassword, String ownerPassword) throws Exception {
        PdfReader reader = new PdfReader(inputFile);

        // 💡 FIX: Wrap the outputFile in a FileOutputStream
        PdfWriter writer = new PdfWriter(new FileOutputStream(outputFile), // Use FileOutputStream
                new WriterProperties()
                        .setStandardEncryption(
                                userPassword.getBytes(),
                                ownerPassword.getBytes(),
                                EncryptionConstants.ALLOW_PRINTING,
                                EncryptionConstants.ENCRYPTION_AES_128));

        PdfDocument pdfDoc = new PdfDocument(reader, writer);
        pdfDoc.close();
    }
}