package com.binarykits.zpl.viewer;

import com.binarykits.zpl.label.elements.*;
import com.binarykits.zpl.viewer.elementdrawers.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ZplElementDrawer {
    
    private final List<ElementDrawer> drawers;
    private static final double RENDER_SCALE = 1.5; // Final optimized resolution (~304 DPI)

    public ZplElementDrawer() {
        drawers = new ArrayList<>();
        drawers.add(new GraphicBoxElementDrawer());
        drawers.add(new TextFieldElementDrawer());
        drawers.add(new Barcode128ElementDrawer());
        drawers.add(new MaxiCodeElementDrawer());
        // Add more drawers here as we port them
    }

    public BufferedImage draw(List<ZplElementBase> elements, double labelWidthMm, double labelHeightMm, int dpmm) {
        // Add small safety padding to avoid edge cropping on right/bottom
        int widthPx = (int) Math.round((labelWidthMm * dpmm + 5) * RENDER_SCALE); 
        int heightPx = (int) Math.round((labelHeightMm * dpmm + 5) * RENDER_SCALE);

        BufferedImage image = new BufferedImage(widthPx, heightPx, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        // Apply Global Scaling
        g2d.scale(RENDER_SCALE, RENDER_SCALE);

        // White background
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, widthPx, heightPx);
        g2d.setColor(Color.BLACK);
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        for (ZplElementBase element : elements) {
            for (ElementDrawer drawer : drawers) {
                if (drawer.canDraw(element)) {
                    drawer.draw(element, g2d);
                    break;
                }
            }
        }

        g2d.dispose();
        return image;
    }
}
