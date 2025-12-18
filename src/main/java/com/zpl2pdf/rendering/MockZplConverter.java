package com.zpl2pdf.rendering;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * A mock implementation of ZplConverter for demonstration purposes.
 * Generates a placeholder image with the ZPL text.
 */
public class MockZplConverter implements ZplConverter {

    @Override
    public List<byte[]> convert(String zplData, double widthMm, double heightMm, int dpmm) {
        // approximate pixels
        int widthPx = (int) (widthMm * dpmm);
        int heightPx = (int) (heightMm * dpmm);
        
        BufferedImage image = new BufferedImage(widthPx, heightPx, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        // Draw white background
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, widthPx, heightPx);
        
        // Draw some text
        g2d.setColor(Color.BLACK);
        g2d.drawString("ZPL Render Placeholder", 10, 20);
        g2d.drawString("Size: " + widthMm + "mm x " + heightMm + "mm", 10, 40);
        
        // rudimentary text rendering of ZPL content
        String[] lines = zplData.split("\n");
        int y = 60;
        for (int i = 0; i < Math.min(lines.length, 20); i++) {
            if (y > heightPx - 10) break;
            g2d.drawString(lines[i], 10, y);
            y += 15;
        }
        
        g2d.dispose();
        
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            return Collections.singletonList(baos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
