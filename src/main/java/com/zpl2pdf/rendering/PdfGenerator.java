package com.zpl2pdf.rendering;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.IOException;
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
        try (PDDocument document = new PDDocument()) {
            for (byte[] imageData : imageDataList) {
                // Create image object from bytes
                PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, imageData, "label");
                
                // Use provided physical dimensions for the page
                PDPage page = new PDPage(new PDRectangle(widthPts, heightPts));
                document.addPage(page);
                
                // Draw the image onto the page, scaled to the page size
                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    contentStream.drawImage(pdImage, 0, 0, widthPts, heightPts);
                }
            }
            document.save(outputPdf);
        }
    }
}
