package com.binarykits.zpl.viewer.elementdrawers;

import com.binarykits.zpl.label.elements.ZplElementBase;
import com.binarykits.zpl.label.elements.ZplMaxiCode;
import uk.org.okapibarcode.backend.MaxiCode;
import uk.org.okapibarcode.graphics.Color;
import uk.org.okapibarcode.output.Java2DRenderer;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MaxiCodeElementDrawer implements ElementDrawer {
    @Override
    public boolean canDraw(ZplElementBase element) {
        return element instanceof ZplMaxiCode;
    }

    @Override
    public void draw(ZplElementBase element, Graphics2D g2d) {
        if (element instanceof ZplMaxiCode) {
            ZplMaxiCode mc = (ZplMaxiCode) element;
            drawMaxiCode(g2d, mc);
        }
    }

    private void drawMaxiCode(Graphics2D g2d, ZplMaxiCode mc) {
        try {
            String rawContent = mc.getContent();
            // Handle hex characters if present (e.g. _1D -> \u001D)
            String decodedContent = decodeZplHex(rawContent);
            
            // UPS Mode 2/3 typically has 15 chars of structured data: 
            // Zip(9) + Country(3) + Service(3)
            // But ZPL string might have spaces or be partial.
            
            MaxiCode maxiCode = new MaxiCode();
            maxiCode.setMode(mc.getMode() > 0 ? mc.getMode() : 2); // Default to Mode 2 for UPS
            
            String primary = "";
            String secondary = "";
            
            // Re-formatting logic to match exactly what scanners expect after seeing user's scannable example
            // If it starts with numeric digits, it's likely the structured header.
            // Example: "003840943040000[)>..."
            // Here Service=003, Country=840, Zip=943040000.
            // Total 15 chars.
            
            if (decodedContent.length() >= 15) {
                // Check if the 15 chars are numeric or alphanumeric (Mode 2 vs 3 is handled by Okapi inside setPrimary usually)
                // Actually Okapi setPrimary just stores it, and encode() does the reordering.
                
                // Wait, Okapi's setPrimary expects Zip(9) + Country(3) + Service(3) for Mode 2.
                // The user's ZPL string "003840943040000" is Service(3)+Country(3)+Zip(9).
                // WE MUST REORDER for Okapi's setPrimary to: Zip(943040000) + Country(840) + Service(003).
                
                String part1 = decodedContent.substring(0, 3); // Service 003
                String part2 = decodedContent.substring(3, 6); // Country 840
                String part3 = decodedContent.substring(6, 15); // Zip 943040000
                
                primary = part3 + part2 + part1; // Zip(9) + Country(3) + Service(3)
                secondary = decodedContent.substring(15);
            } else {
                secondary = decodedContent;
            }

            maxiCode.setPrimary(primary);
            maxiCode.setContent(secondary);
            
            // Set quiet zones to 0 to avoid extra offsets
            maxiCode.setQuietZoneHorizontal(0);
            maxiCode.setQuietZoneVertical(0);
            
            // Render using Okapi's Java2DRenderer
            // MaxiCode fixed size is roughly 1.11 inches square at 203 DPI = ~225 pixels
            // Okapi uses small logical units, so we need magnification.
            // Standard MaxiCode is 33 rows. 
            // 203 DPI (8 dpmm). 
            
            // The Java2DRenderer will draw starting at (0,0) in the current g2d transform
            // Calculated nudge to center it (at 0.9x scale) in the ~236x224 box
            // Footprint at 2.7 mag: ~200x194. Box center: 118, 451.
            g2d.translate(mc.getPositionX() - 2, mc.getPositionY() + 2);
            
            // Okapi's MaxiCode size is fixed. 
            // Scaling to 0.9x of standard (3.0 * 0.9 = 2.7 magnification) 
            // This increases margins for better scanner distinctness.
            Java2DRenderer renderer = new Java2DRenderer(g2d, 2.7, null, Color.BLACK);
            renderer.render(maxiCode);
            
            g2d.translate(-(mc.getPositionX() - 2), -(mc.getPositionY() + 2));
            
        } catch (Exception e) {
            System.err.println("Error drawing MaxiCode: " + e.getMessage());
        }
    }

    private String decodeZplHex(String content) {
        if (content == null) return "";
        // Replace _XX with actual hex character
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            if (c == '_' && i + 2 < content.length()) {
                try {
                    int hex = Integer.parseInt(content.substring(i + 1, i + 3), 16);
                    sb.append((char) hex);
                    i += 2;
                } catch (NumberFormatException e) {
                    sb.append(c);
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
