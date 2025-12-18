package com.zpl2pdf.rendering;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Responsible for generating a PDF, adding each image (byte[] data) to a page.
 */
public class PdfGenerator {
    
    /**
     * Generates a PDF with one image per page and saves the file to the specified path.
     *
     * @param imageDataList List of image data in byte arrays.
     * @param widthPts Page width in points (1/72 inch).
     * @param heightPts Page height in points (1/72 inch).
     * @param outputPdf Path to save the generated PDF file.
     * @throws IOException If an error occurs during PDF creation or saving.
     */
    public static void generatePdf(List<byte[]> imageDataList, float widthPts, float heightPts, String outputPdf) throws IOException {
        try (PDDocument document = createDocument(imageDataList, widthPts, heightPts)) {
            document.save(outputPdf);
        }
    }

    /**
     * Generates a PDF and writes it to the provided OutputStream.
     */
    public static void generatePdfToStream(List<byte[]> imageDataList, float widthPts, float heightPts, OutputStream outputStream) throws IOException {
        try (PDDocument document = createDocument(imageDataList, widthPts, heightPts)) {
            document.save(outputStream);
        }
    }

    /**
     * Generates a PDF and returns it as a byte array.
     */
    public static byte[] generatePdfToBytes(List<byte[]> imageDataList, float widthPts, float heightPts) throws IOException {
        try (PDDocument document = createDocument(imageDataList, widthPts, heightPts);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            document.save(baos);
            return baos.toByteArray();
        }
    }

    private static PDDocument createDocument(List<byte[]> imageDataList, float widthPts, float heightPts) throws IOException {
        PDDocument document = new PDDocument();
        for (byte[] imageData : imageDataList) {
            PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, imageData, "label");
            PDPage page = new PDPage(new PDRectangle(widthPts, heightPts));
            document.addPage(page);
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.drawImage(pdImage, 0, 0, widthPts, heightPts);
            }
        }
        return document;
    }
}
