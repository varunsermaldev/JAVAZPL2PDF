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
            Font font = new Font(Font.SANS_SERIF, Font.BOLD, fontSize);
            
            // Calculate Horizontal Scale
            // Java Font Size ~ Height. 
            // We want width to match fontWidth.
            // But 'fontSize' in Java is point size.
            // Let's rely on affine transform for aspect ratio.
            double scaleX = (double) fontWidth / fontSize;
            
            // ZPL fonts are often more condensed than Java standard fonts.
            // E.g. ^A0N,34,34 -> standard ratio 1.0
            // But Helvetica Bold at 34pt is wider than ZPL Font 0 at 34,34.
            // We apply a slight compression factor (0.85) to match Zebra visuals better
            // based on empirical observation of "cropped text".
            
            if (scaleX > 0) {
                 AffineTransform id = new AffineTransform();
                 id.scale(scaleX * 0.90, 1.0); // 0.90 fudge factor to match ZPL condensation
                 font = font.deriveFont(id);
            }
            
            g2d.setFont(font);

            // Positioning Logic
            java.awt.FontMetrics fm = g2d.getFontMetrics();
            int x = tf.getPositionX();
            int y = tf.getPositionY();
            
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
        }
    }
}
