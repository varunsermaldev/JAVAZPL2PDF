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
        String zplData = "^XA^CI28^PW813\n" +
                "^PON\n" +
                "^LH0,0\n" +
                "^FX[(<START>)]^FS\n" +
                "^FT480,33^A0N,34,34^FD49 LBS^FS\n" +
                "^FT480,34^A0N,34,34^FD49 LBS^FS\n" +
                "^FT481,33^A0N,34,34^FD49 LBS^FS\n" +
                "^FT680,29^A0N,28,28^FD1 OF 1^FS\n" +
                "^FT680,30^A0N,28,28^FD1 OF 1^FS\n" +
                "^FT681,29^A0N,28,28^FD1 OF 1^FS\n" +
                "^FT20,25^A0N,23,23^FDVICTORIAS SECRET^FS\n" +
                "^FT20,45^A0N,23,23^FD6145775000^FS\n" +
                "^FT20,65^A0N,23,23^FDDC04 - VS&CO. SHIPPING FACILITY^FS\n" +
                "^FT20,85^A0N,23,23^FD2 LIMITED PARKWAY^FS\n" +
                "^FT20,105^A0N,23,23^FDCOLUMBUS OH 43230^FS\n" +
                "^FT160,176^A0N,28,28^FDSHIP^FS\n" +
                "^FT160,177^A0N,28,28^FDSHIP^FS\n" +
                "^FT161,176^A0N,28,28^FDSHIP^FS\n" +
                "^FT160,208^A0N,28,28^FDTO:^FS\n" +
                "^FT160,209^A0N,28,28^FDTO:^FS\n" +
                "^FT161,208^A0N,28,28^FDTO:^FS\n" +
                "^FT260,176^A0N,28,28^FDVICTORIA'S SECRET STORES # 0001^FS\n" +
                "^FT260,201^A0N,28,28^FD614-415-8926^FS\n" +
                "^FT260,226^A0N,28,28^FDVICTORIA'S SECRET STORES # 0001^FS\n" +
                "^FT260,251^A0N,28,28^FDSTANFORD SHOPPING CENTER^FS\n" +
                "^FT260,280^A0N,34,34^FDPALO ALTO CA 94304^FS\n" +
                "^FT260,281^A0N,34,34^FDPALO ALTO CA 94304^FS\n" +
                "^FT261,280^A0N,34,34^FDPALO ALTO CA 94304^FS\n" +
                "^FO0,338^GB238,2,2,B^FS\n" +
                "^FO236,368^GB576,2,2,B^FS\n" +
                "^FO236,340^GB2,224,2,B^FS\n" +
                "^FO20,352^BD2^FH^FD003840943040000[)>_1E01_1D961Z03062622_1DUPSN_1DH4E079_1D322_1D_1D1/1_1D49_1DN_1DSTANFORD SHOPPING_1DPALO ALTO_1DCA_1E_04^FS\n" +
                "^FT320,430^A0N,68,68^FDCA 943 0-01^FS\n" +
                "^BY3\n" +
                "^FO320,444^BCN,104,N^FD>;420943040000^FS\n" +
                "^FO0,564^GB800,2,2,B^FS\n" +
                "^FT20,597^A0N,34,34^FDUPS GROUND^FS\n" +
                "^FT20,598^A0N,34,34^FDUPS GROUND^FS\n" +
                "^FT21,597^A0N,34,34^FDUPS GROUND^FS\n" +
                "^FT20,625^A0N,28,28^FDTRACKING #: 1Z H4E 079 03 0306 2622^FS\n" +
                "^FO712,564^GB88,72,48,B^FS\n" +
                "^FO0,636^GB800,2,2,B^FS\n" +
                "^BY3\n" +
                "^FO80,652^BCN,144,N^FD>:1ZH4E0>5790303062622^FS\n" +
                "^FO0,812^GB800,2,2,B^FS\n" +
                "^FT20,841^A0N,28,28^FDBILLING: P/P^FS\n" +
                "^FT20,865^A0N,28,28^FDDESC: HOLIDAY 2025 STORE FRONT SIGNAGE^FS\n" +
                "^FT20,889^A0N,28,28^FDREF 1: 90506675249970055210^FS\n" +
                "^FT480,905^A0N,17,17^FD943.B.000 000000000000 URC36.5V 08/2025^FS\n" +
                "^FO0,1212^GB800,2,2,B^FS\n" +
                "^FT281,1273^A0N,30,30^FD90506675249970055210^FS\n" +
                "^BY4\n" +
                "^FO116,1284^BCN,152,N^FD>;90506675249970055210^FS\n" +
                "^FT30,1468^A0N,30,30^FDStore: 1^FS\n" +
                "^FT30,1506^A0N,30,30^FDSort Code:^FS\n" +
                "^FT30,1545^A0N,30,30^FDUnits: 2^FS\n" +
                "^FT30,1584^A0N,30,30^FD20251118^FS\n" +
                "^FT356,1527^A0N,79,79^FDGW^FS\n" +
                "^FT305,1598^A0N,54,54^FD21:19:16^FS\n" +
                "^FT610,1584^A0N,30,30^FDClass: 60^FS\n" +
                "^FX[(<END>)]^FS\n" +
                "^LH0,0^PON\n" +
                "^XZ\n";
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
