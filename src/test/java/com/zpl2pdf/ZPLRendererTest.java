package com.zpl2pdf;

import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ZPLRendererTest {

    @Test
    public void testRenderWithConfig() throws IOException {
        String zplData = "^XA^FO50,50^A0N,50,50^FDTest Label^FS^XZ";
        ZPLConfig config = ZPLConfig.default4x6();
        String outputPath = "test_verification.pdf";
        
        ZPLRenderer.renderWithConfig(zplData, config, outputPath);
        
        File file = new File(outputPath);
        assertTrue(file.exists(), "PDF should be created");
        assertTrue(file.length() > 0, "PDF should not be empty");
        
        // Clean up
        if (file.exists()) {
            file.delete();
        }
    }
}
