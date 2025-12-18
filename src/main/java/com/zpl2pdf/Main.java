package com.zpl2pdf;

import com.zpl2pdf.domain.LabelDimensions;
import com.zpl2pdf.rendering.BinaryKitsZplConverter;
import com.zpl2pdf.rendering.LabelRenderer;
import com.zpl2pdf.rendering.PdfGenerator;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting ZPL2PDF Java Port Demo...");

        // 1. Define dimensions
        LabelDimensions dimensions = new LabelDimensions(4, 6, "in", 203);
        System.out.println("Dimensions: " + dimensions);

        // 2. Initialize Renderer with Mock Converter
        LabelRenderer renderer = new LabelRenderer(dimensions, new BinaryKitsZplConverter());

        // 3. Define dummy ZPL
        String zplData = "^XA\n" +
                "\n" +
                "^FX Top section with logo, name and address.\n" +
                "^CF0,60\n" +
                "^FO50,50^GB100,100,100^FS\n" +
                "^FO75,75^FR^GB100,100,100^FS\n" +
                "^FO93,93^GB40,40,40^FS\n" +
                "^FO220,50^FDIntershipping, Inc.^FS\n" +
                "^CF0,30\n" +
                "^FO220,115^FD1000 Shipping Lane^FS\n" +
                "^FO220,155^FDShelbyville TN 38102^FS\n" +
                "^FO220,195^FDUnited States (USA)^FS\n" +
                "^FO50,250^GB700,3,3^FS\n" +
                "\n" +
                "^FX Second section with recipient address and permit information.\n" +
                "^CFA,30\n" +
                "^FO50,300^FDJohn Doe^FS\n" +
                "^FO50,340^FD100 Main Street^FS\n" +
                "^FO50,380^FDSpringfield TN 39021^FS\n" +
                "^FO50,420^FDUnited States (USA)^FS\n" +
                "^CFA,15\n" +
                "^FO600,300^GB150,150,3^FS\n" +
                "^FO638,340^FDPermit^FS\n" +
                "^FO638,390^FD123456^FS\n" +
                "^FO50,500^GB700,3,3^FS\n" +
                "\n" +
                "^FX Third section with bar code.\n" +
                "^BY5,2,270\n" +
                "^FO100,550^BC^FD12345678^FS\n" +
                "\n" +
                "^FX Fourth section (the two boxes on the bottom).\n" +
                "^FO50,900^GB700,250,3^FS\n" +
                "^FO400,900^GB3,250,3^FS\n" +
                "^CF0,40\n" +
                "^FO100,960^FDCtr. X34B-1^FS\n" +
                "^FO100,1010^FDREF1 F00B47^FS\n" +
                "^FO100,1060^FDREF2 BL4H8^FS\n" +
                "^CF0,190\n" +
                "^FO470,955^FDCA^FS\n" +
                "\n" +
                "^XZ";
        List<String> zplFiles = Collections.singletonList(zplData);

        // 4. Render to Images (byte arrays)
        System.out.println("Rendering labels...");
        List<byte[]> images = renderer.renderLabels(zplFiles);
        System.out.println("Generated " + images.size() + " image(s).");

        // 5. Generate PDF
        String outputPdf = "output_test.pdf";
        int[] pts = dimensions.toPoints();
        float widthPts = (float) pts[0];
        float heightPts = (float) pts[1];
        
        System.out.println("Generating PDF: " + outputPdf + " (" + widthPts + "x" + heightPts + " pts)");
        try {
            PdfGenerator.generatePdf(images, widthPts, heightPts, outputPdf);
            System.out.println("PDF generated successfully at: " + new File(outputPdf).getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error generating PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
