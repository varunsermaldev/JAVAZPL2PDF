package com.zpl2pdf;

import com.zpl2pdf.domain.LabelDimensions;
import com.zpl2pdf.rendering.BinaryKitsZplConverter;
import com.zpl2pdf.rendering.LabelRenderer;
import com.zpl2pdf.rendering.PdfGenerator;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

public class ZPLRenderer {

    /**
     * Renders ZPL string to a PDF file.
     *
     * @param zplData      The ZPL content to render.
     * @param config       The configuration (dimensions, dpi, etc.).
     * @param outputFile   The path where the PDF should be saved.
     * @throws IOException If an error occurs during rendering or saving.
     */
    public static void renderWithConfig(String zplData, ZPLConfig config, String outputFile) throws IOException {
        LabelDimensions dimensions = new LabelDimensions(
                config.getWidth(),
                config.getHeight(),
                config.getUnit(),
                config.getDpi()
        );

        LabelRenderer renderer = new LabelRenderer(dimensions, new BinaryKitsZplConverter());
        List<byte[]> images = renderer.renderLabels(Collections.singletonList(zplData));

        int[] pts = dimensions.toPoints();
        float widthPts = (float) pts[0];
        float heightPts = (float) pts[1];

        PdfGenerator.generatePdf(images, widthPts, heightPts, outputFile);
    }

    /**
     * Renders ZPL string to a PDF byte array.
     *
     * @param zplData The ZPL content to render.
     * @param config  The configuration.
     * @return PDF content as byte array.
     * @throws IOException If an error occurs.
     */
    public static byte[] renderToPdfBytes(String zplData, ZPLConfig config) throws IOException {
        LabelDimensions dimensions = new LabelDimensions(
                config.getWidth(),
                config.getHeight(),
                config.getUnit(),
                config.getDpi()
        );

        LabelRenderer renderer = new LabelRenderer(dimensions, new BinaryKitsZplConverter());
        List<byte[]> images = renderer.renderLabels(Collections.singletonList(zplData));

        int[] pts = dimensions.toPoints();
        float widthPts = (float) pts[0];
        float heightPts = (float) pts[1];

        return PdfGenerator.generatePdfToBytes(images, widthPts, heightPts);
    }

    /**
     * Renders ZPL string to a PDF and writes to an OutputStream.
     *
     * @param zplData      The ZPL content to render.
     * @param config       The configuration.
     * @param outputStream The stream to write the PDF to.
     * @throws IOException If an error occurs.
     */
    public static void renderToStream(String zplData, ZPLConfig config, OutputStream outputStream) throws IOException {
        LabelDimensions dimensions = new LabelDimensions(
                config.getWidth(),
                config.getHeight(),
                config.getUnit(),
                config.getDpi()
        );

        LabelRenderer renderer = new LabelRenderer(dimensions, new BinaryKitsZplConverter());
        List<byte[]> images = renderer.renderLabels(Collections.singletonList(zplData));

        int[] pts = dimensions.toPoints();
        float widthPts = (float) pts[0];
        float heightPts = (float) pts[1];

        PdfGenerator.generatePdfToStream(images, widthPts, heightPts, outputStream);
    }
}
