package com.binarykits.zpl.viewer.elementdrawers;

import com.binarykits.zpl.label.elements.ZplElementBase;
import com.binarykits.zpl.label.elements.ZplGraphicBox;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class GraphicBoxElementDrawer implements ElementDrawer {
    @Override
    public boolean canDraw(ZplElementBase element) {
        return element instanceof ZplGraphicBox;
    }

    @Override
    public void draw(ZplElementBase element, Graphics2D g2d) {
        if (element instanceof ZplGraphicBox) {
            drawGraphicBox(g2d, (ZplGraphicBox) element);
        }
    }

    private void drawGraphicBox(Graphics2D g2d, ZplGraphicBox box) {
        int x = box.getPositionX();
        int y = box.getPositionY();
        int w = box.getWidth();
        int h = box.getHeight();
        int t = box.getBorderThickness();
        
        // HACK: ZPL often uses 800px width for 4 inch labels (812px).
        // If box is ~800, snap to canvas width to avoid right-side gap.
        java.awt.Shape clip = g2d.getClip();
        if (clip != null) {
            java.awt.Rectangle bounds = clip.getBounds();
            if (w >= 790 && bounds.width >= 800) {
                 w = bounds.width; 
            }
        }
        
        g2d.setColor(Color.BLACK);
        // Naive logic matching my previous implementation. 
        // Real C# logic checks for specific filling conditions.
        // C# : "if (element.BorderThickness * 2 >= element.Width || element.BorderThickness * 2 >= element.Height)" -> Fill
        
        if (box.isReverseDraw()) {
            g2d.setXORMode(Color.WHITE);
        }

        if (t * 2 >= w || t * 2 >= h) {
             g2d.fillRect(x, y, w, h);
        } else {
            g2d.setStroke(new BasicStroke(t));
            g2d.drawRect(x, y, w, h);
            g2d.setStroke(new BasicStroke(1));
        }
        
        if (box.isReverseDraw()) {
            g2d.setPaintMode();
        }
    }
}
