package com.binarykits.zpl.viewer.elementdrawers;

import com.binarykits.zpl.label.elements.ZplElementBase;
import com.binarykits.zpl.label.elements.ZplTextField;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public class TextFieldElementDrawer implements ElementDrawer {
    @Override
    public boolean canDraw(ZplElementBase element) {
        return element instanceof ZplTextField;
    }

    @Override
    public void draw(ZplElementBase element, Graphics2D g2d) {
        if (element instanceof ZplTextField) {
            ZplTextField tf = (ZplTextField) element;
            
            // Font logic
            int fontSize = tf.getFontHeight();
            int fontWidth = tf.getFontWidth();
            
            // Default to square aspect if width is 0 or missing
            if (fontWidth <= 0) fontWidth = fontSize;
            
            // ZPL Font 0 is Helvetica-like (SansSerif).
            // Font A is often small and monospaced.
            Font font;
            double condensation = 0.75; // Authentic ZPL condensation (approx 15:12 ratio)
            
            if ("A".equalsIgnoreCase(tf.getFontName())) {
                font = new Font(Font.MONOSPACED, Font.BOLD, fontSize);
                condensation = 0.75; // Font A is also narrow
            } else {
                font = new Font(Font.SANS_SERIF, Font.BOLD, fontSize);
            }
            
            // Calculate Horizontal Scale
            double scaleX = (double) fontWidth / fontSize;
            
            if (scaleX > 0) {
                 AffineTransform id = new AffineTransform();
                 id.scale(scaleX * condensation, 1.0); 
                 font = font.deriveFont(id);
            }
            
            g2d.setFont(font);

            if (tf.isReverseDraw()) {
                g2d.setXORMode(java.awt.Color.WHITE);
            }

            // Positioning Logic
            java.awt.FontMetrics fm = g2d.getFontMetrics();
            int x = tf.getPositionX();
            int y = tf.getPositionY();
            
            // FB Centering/Justification
            if (tf.getBlockWidth() > 0) {
                // Use precise string bounds to handle scaled fonts correctly
                double textWidth = font.getStringBounds(tf.getText(), g2d.getFontRenderContext()).getWidth();
                if ("C".equals(tf.getJustification())) {
                    x += (int)((tf.getBlockWidth() - textWidth) / 2.0);
                } else if ("R".equals(tf.getJustification())) {
                    x += (int)(tf.getBlockWidth() - textWidth);
                }
            }
            
            if (tf.isUseFieldOrigin()) {
                // ^FO: x,y is Top-Left
                // Java drawString y is Baseline.
                // Add ascent to get baseline y.
                y += fm.getAscent();
            } else {
                // ^FT: x,y is Baseline used by ZPL.
                // Java drawString y is also Baseline.
                // Do nothing.
            }

            g2d.drawString(tf.getText(), x, y);
            
            if (tf.isReverseDraw()) {
                g2d.setPaintMode();
            }
        }
    }
}
