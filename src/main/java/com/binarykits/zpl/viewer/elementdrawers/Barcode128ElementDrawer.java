package com.binarykits.zpl.viewer.elementdrawers;

import com.binarykits.zpl.label.elements.ZplElementBase;
import com.binarykits.zpl.label.elements.ZplBarcode128;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;

public class Barcode128ElementDrawer implements ElementDrawer {
    @Override
    public boolean canDraw(ZplElementBase element) {
        return element instanceof ZplBarcode128;
    }

    @Override
    public void draw(ZplElementBase element, Graphics2D g2d) {
        if (element instanceof ZplBarcode128) {
            drawBarcode(g2d, (ZplBarcode128) element);
        }
    }

    private void drawBarcode(Graphics2D g2d, ZplBarcode128 bc) {
        try {
            String content = bc.getContent();
            
            // Clean ZPL Code 128 specific control sequences that mess up the barcode content
            // if passed literally to ZXing.
            // >9, >:, >; are Start Codes A, B, C
            // >5, >6 are Mode Switches
            // >8 is FNC1
            if (content != null) {
                content = content.replace(">;", "")
                                 .replace(">:", "")
                                 .replace(">9", "") // Start A
                                 .replace(">5", "") // Switch to C
                                 .replace(">6", "") // Switch to B
                                 .replace(">8", ""); // FNC1
            }
            
            // Calculate exact width:
            // Code 128: (11 * chars + 11 (start) + 11 (stop) + 11 (check) ) * moduleWidth
            // Rough calc for ZXing hint
            // Actually ZXing renders with min module width of 1 pixel usually. 
            // We want 'moduleWidth' pixels per module.
            // So we request 0 width (min) and then scale up manually or let transform do it?
            // "BarcodeDrawerBase" in C# does: Resize(new SKSizeI(image.Width * moduleWidth, height))
            
            int moduleWidth = bc.getModuleWidth();
            // Width 0 lets ZXing calc minimum width
            
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.MARGIN, 0); 
            
            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix matrix = writer.encode(content, BarcodeFormat.CODE_128, 0, 1, hints);
            
            int baseWidth = matrix.getWidth();
            int baseHeight = matrix.getHeight(); // 1 usually
            
            // Now create scaled image
            int finalWidth = baseWidth * moduleWidth;
            int finalHeight = bc.getHeight();
            
            BufferedImage barImg = new BufferedImage(finalWidth, finalHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D bG = barImg.createGraphics();
            bG.setColor(Color.BLACK);
            
            for (int x = 0; x < baseWidth; x++) {
                if (matrix.get(x, 0)) {
                    bG.fillRect(x * moduleWidth, 0, moduleWidth, finalHeight);
                }
            }
            bG.dispose();
            
            // Positioning
            // FO: Top-Left
            int x = bc.getPositionX();
            int y = bc.getPositionY(); // If FO, Y is top.
            
            // The C# code handling FO vs FT for barcode is complex in BarcodeDrawerBase:
            /*
             if (!useFieldOrigin) {
                 y -= barcodeHeight; 
                 if (y < 0) y = 0;
             }
            */
            // User snippet uses FO for barcodes: ^FO20,352...
            // So Y is top.
            
            if (bc.isReverseDraw()) {
                g2d.setXORMode(Color.WHITE);
            }
            
            g2d.drawImage(barImg, x, y, null); 
            
            // Draw Interpretation Line
            if (bc.isPrintInterpretationLine()) {
                // Clear XOR for text if needed, or keep it? ZPL usually reverses text too.
                // Standard ZPL interpretation line font is usually small Font 0.
                int textHeight = (int) Math.max(10, bc.getHeight() * 0.15); // Rough guess for height if not specified
                Font font = new Font(Font.SANS_SERIF, Font.PLAIN, textHeight);
                // Apply condensation to match our text drawer
                AffineTransform at = new AffineTransform();
                at.scale(0.75, 1.0);
                g2d.setFont(font.deriveFont(at));
                
                String labelText = content; 
                java.awt.FontMetrics fm = g2d.getFontMetrics();
                int textX = x + (finalWidth - fm.stringWidth(labelText)) / 2;
                int textY = y + finalHeight + fm.getAscent();
                
                if (bc.isPrintInterpretationLineAbove()) {
                     textY = y - fm.getDescent();
                }
                
                g2d.drawString(labelText, textX, textY);
            }
            
            if (bc.isReverseDraw()) {
                g2d.setPaintMode();
            }
            
        } catch (Exception e) {
             g2d.drawString("ERR: " + e.getMessage(), bc.getPositionX(), bc.getPositionY());
        }
    }
}
