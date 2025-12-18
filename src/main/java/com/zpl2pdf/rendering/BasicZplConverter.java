package com.zpl2pdf.rendering;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.zpl2pdf.shared.ApplicationConstants;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BasicZplConverter implements ZplConverter {

    private static final int DEFAULT_FONT_SIZE = 24;

    @Override
    public List<byte[]> convert(String zplData, double widthMm, double heightMm, int dpmm) {
        // Calculate pixel dimensions
        int widthPx = (int) Math.round(widthMm * dpmm);
        int heightPx = (int) Math.round(heightMm * dpmm);

        // Create Image
        BufferedImage image = new BufferedImage(widthPx, heightPx, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // White background
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, widthPx, heightPx);
        g2d.setColor(Color.BLACK);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Basic State
        int currentX = 0;
        int currentY = 0;
        Font currentFont = new Font("Arial", Font.PLAIN, DEFAULT_FONT_SIZE);
        boolean invertNext = false;
        
        // Split by commands (simple regex based parser)
        // This is a naive parser for the specific subset of commands needed
        String[] commands = zplData.split("\\^");
        
        for (String cmd : commands) {
            if (cmd.isEmpty()) continue;
            
            String commandCode = cmd.substring(0, Math.min(2, cmd.length()));
            String params = cmd.length() > 2 ? cmd.substring(2) : "";
            
            try {
                switch (commandCode) {
                    case "XA": // Start Format
                    case "XZ": // End Format
                    case "FS": // Field Separator
                    case "FX": // Comment
                        break;
                        
                    case "LH": // Label Home
                        // ^LHx,y
                        String[] lhParts = params.split(",");
                        if (lhParts.length >= 2) {
                            // In a real engine, this offsets the origin. 
                            // For now we assume 0,0 or handle it locally if needed.
                        }
                        break;
                        
                    case "FO": // Field Origin
                        // ^FOx,y
                        String[] foParts = params.split(",");
                        if (foParts.length >= 2) {
                            currentX = Integer.parseInt(foParts[0]);
                            currentY = Integer.parseInt(foParts[1]);
                        }
                        break;
                        
                    case "FT": // Field Typeset (Origin at baseline)
                        // ^FTx,y
                        String[] ftParts = params.split(",");
                        if (ftParts.length >= 2) {
                            currentX = Integer.parseInt(ftParts[0]);
                            currentY = Integer.parseInt(ftParts[1]);
                        }
                        break;
                        
                    case "CF": // Change Default Font
                        // ^CFf,h,w
                        // Simplify: just parsing height
                        String[] cfParts = params.split(",");
                        if (cfParts.length >= 2) {
                             int h = Integer.parseInt(cfParts[1]);
                             currentFont = new Font("Arial", Font.PLAIN, h);
                        }
                        break;
                    
                    case "A0": // scalable font
                    case "ADN": 
                         // ^A0N,h,w
                        // Assuming params starts with N,height,width
                         String[] aParts = params.split(",");
                         if (aParts.length >= 2) {
                             int h = Integer.parseInt(aParts[1]);
                             currentFont = new Font("Arial", Font.BOLD, h);
                         }
                        break;

                    case "FD": // Field Data
                        // draw text at currentX, currentY using currentFont
                        String text = params;
                        g2d.setFont(currentFont);
                        g2d.drawString(text, currentX, currentY);
                        break;
                        
                    case "GB": // Graphic Box
                        // ^GBw,h,t,c,r
                        String[] gbParts = params.split(",");
                        int w = gbParts.length > 0 ? Integer.parseInt(gbParts[0]) : 1;
                        int h = gbParts.length > 1 ? Integer.parseInt(gbParts[1]) : 1;
                        int t = gbParts.length > 2 ? Integer.parseInt(gbParts[2]) : 1;
                        // Draw box (filled or outline? ZPL GB is complex)
                        // Simple approach: if w or h is small, it's a line
                        g2d.fillRect(currentX, currentY, w, h);
                        break;
                        
                    case "BC": // Code 128
                        // ^BCo,h,f,g,e,m
                        String[] bcParts = params.split(",");
                        int barHeight = bcParts.length > 1 && !bcParts[1].isEmpty() ? Integer.parseInt(bcParts[1]) : 100;
                        // Next command should be ^FD with data
                        // We need to look ahead for ^FD
                        // For this naive parser, we'll store state that we are waiting for barcode data?
                        // Actually, looking at the user snippet: ^FO...^BC...^FD...^FS
                        // so we can set a flag or just handle it when we hit FD
                        break;
                        
                    case "BY": // Barcode Field Default
                        // ^BYw,r,h
                        break;
                }
                
                // Special handling for Barcode Data which comes in FD but needs context
                // Limitation of simple split parser: context is lost. 
                // Let's iterate manually or refactor.
            } catch (Exception e) {
                // Ignore parse errors for robustness
                System.err.println("Error parsing command ^" + commandCode + params + ": " + e.getMessage());
            }
        }
        
        // Second Pass or Refined Logic:
        // The split approach makes it hard to link BC to FD.
        // Let's do a slightly better regex loop for the content.
        parseAndRender(g2d, zplData);

        g2d.dispose();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            return Collections.singletonList(baos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    private void parseAndRender(Graphics2D g2d, String zpl) {
        // Reset Graphics context
        g2d.setColor(Color.BLACK);
        
        // Regex to find commands: ^[A-Z0-9]{2} followed by arguments up to next ^ or end
        Pattern cmdPattern = Pattern.compile("\\^([A-Z0-9]{2})([^\\^]*)");
        Matcher matcher = cmdPattern.matcher(zpl);
        
        int currentX = 0;
        int currentY = 0;
        Font font = new Font("Arial", Font.PLAIN, 24);
        BarcodeContext barcodeContext = null;
        
        while (matcher.find()) {
            String command = matcher.group(1);
            String params = matcher.group(2).trim(); // trim whitespace (newlines)
            
            try {
                switch (command) {
                    case "FO":
                    case "FT":
                        String[] pos = params.split(",");
                        if (pos.length >= 2) {
                            currentX = Integer.parseInt(pos[0]);
                            currentY = Integer.parseInt(pos[1]);
                        }
                        // Reset barcode context on move
                        barcodeContext = null; 
                        break;
                        
                    case "A0":
                    case "CF":
                        // Naive font parsing
                        String[] fontParts = params.split(",");
                        // A0N,h,w vs CFf,h,w
                        int height = 24;
                        if (fontParts.length >= 2) { // A0
                             // check if first part is rotation (N, R, I, B)
                             if (fontParts[0].length() == 1 && Character.isLetter(fontParts[0].charAt(0))) {
                                 height = Integer.parseInt(fontParts[1]);
                             } else {
                                 // CF
                                 height = Integer.parseInt(fontParts[0]); // or 1 depending on CF
                             }
                        }
                        font = new Font("Arial", Font.BOLD, height);
                        break;
                        
                    case "GB":
                        String[] gb = params.split(",");
                        int w = gb.length > 0 ? Integer.parseInt(gb[0]) : 0;
                        int h = gb.length > 1 ? Integer.parseInt(gb[1]) : 0;
                        int t = gb.length > 2 ? Integer.parseInt(gb[2]) : 1;
                        // Drawing a box with thickness t. 
                        // If fully filled loop...
                        // ZPL GB: w, h, thickness, color, rounding
                        if (w > 0 && h > 0) {
                             if (w == t || h == t) {
                                 // Line
                                 g2d.fillRect(currentX, currentY, w, h);
                             } else {
                                 // Box outline
                                 float thick = t;
                                 g2d.setStroke(new BasicStroke(thick));
                                 g2d.drawRect(currentX, currentY, w, h);
                                 g2d.setStroke(new BasicStroke(1));
                             }
                        }
                        break;
                        
                    case "BC":
                        // Code 128
                        // ^BCo,h,f,g,e,m
                        String[] bc = params.split(",");
                        int barH = bc.length > 1 && !bc[1].isEmpty() ? Integer.parseInt(bc[1]) : 50;
                        barcodeContext = new BarcodeContext(BarcodeFormat.CODE_128, barH);
                        break;
                        
                    case "FD":
                        // Data to print
                        if (barcodeContext != null) {
                            // Render Barcode
                            renderBarcode(g2d, params, currentX, currentY, barcodeContext.height);
                            barcodeContext = null;
                        } else {
                            // Render Text
                            g2d.setFont(font);
                            g2d.drawString(params, currentX, currentY);
                        }
                        break;
                }
            } catch (Exception e) {
                // System.out.println("Cmd Error: " + command + " " + e.getMessage());
            }
        }
    }

    private void renderBarcode(Graphics2D g2d, String data, int x, int y, int height) {
        try {
            // ZPL often puts >: or >; in data for subtypes, strip them for basic rendering if ZXing fails
            // ZXing Code 128 handles some, but let's clean strictly for robustness
            // String cleanData = data.replaceAll("[^A-Za-z0-9]", ""); 
            
            // Actually ZPL control chars >: start subset, etc. keep them if ZXing supports, 
            // or strip. Let's try raw first, fall back to clean.
            
            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix matrix = writer.encode(data, BarcodeFormat.CODE_128, 200, height); // Width fits content
            
            BufferedImage barImg = MatrixToImageWriter.toBufferedImage(matrix);
            g2d.drawImage(barImg, x, y, null);
            
        } catch (Exception e) {
             g2d.drawString("[BARCODE ERROR: " + data + "]", x, y);
        }
    }
    
    // Helper class for state
    private static class BarcodeContext {
        BarcodeFormat format;
        int height;
        
        public BarcodeContext(BarcodeFormat format, int height) {
            this.format = format;
            this.height = height;
        }
    }
}
