package com.zpl2pdf.rendering;



import com.binarykits.zpl.label.elements.ZplElementBase;
import com.binarykits.zpl.viewer.ZplAnalyzer;
import com.binarykits.zpl.viewer.ZplElementDrawer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class BinaryKitsZplConverter implements ZplConverter {

    private final ZplAnalyzer analyzer;
    private final ZplElementDrawer drawer;

    public BinaryKitsZplConverter() {
        this.analyzer = new ZplAnalyzer();
        this.drawer = new ZplElementDrawer();
    }

    @Override
    public List<byte[]> convert(String zplData, double widthMm, double heightMm, int dpmm) {
        // 1. Analyze (Parse)
        List<ZplElementBase> elements = analyzer.analyze(zplData);

        // 2. Draw
        BufferedImage image = drawer.draw(elements, widthMm, heightMm, dpmm);

        // 3. Convert to Bytes
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            return Collections.singletonList(baos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
