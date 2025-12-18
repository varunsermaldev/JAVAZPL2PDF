package com.binarykits.zpl.viewer;

import com.binarykits.zpl.label.elements.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZplAnalyzer {
    
    public List<ZplElementBase> analyze(String zplData) {
        List<ZplElementBase> elements = new ArrayList<>();
        
        // Remove newlines for easier regex parsing as per original logic
        String cleanZpl = zplData.replace("\n", "").replace("\r", "");
        
        Pattern cmdPattern = Pattern.compile("\\^([A-Z0-9]{2})([^\\^]*)");
        Matcher matcher = cmdPattern.matcher(cleanZpl);
        
        int currentX = 0;
        int currentY = 0;
        
        // Default font state
        String currentFontName = "0";
        int currentFontHeight = 20;
        int currentFontWidth = 20;
        
        int barcodeHeight = 100;
        boolean nextIsBarcode = false;
        String nextBarcodeType = ""; // BC, BD
        int currentBarcodeHeight = 10;
        boolean currentIsReverseDraw = false;
        boolean lastPositionWasFO = true;
        int currentModuleWidth = 2;
        String nextBarcodeParams = "";
        int currentBlockWidth = 0;
        String currentJustification = "L";


        
        while (matcher.find()) {
            String command = matcher.group(1);
            String params = matcher.group(2);
            
            try {
                switch (command) {
                    case "FO": 
                        // Field Origin
                        String[] fo = params.split(",");
                        if (fo.length >= 2) {
                            currentX = Integer.parseInt(fo[0]);
                            currentY = Integer.parseInt(fo[1]);
                        }
                        lastPositionWasFO = true;
                        break;
                    case "FT":
                        // Field Typeset (Bottom-Up usually)
                        String[] ft = params.split(",");
                        if (ft.length >= 2) {
                            currentX = Integer.parseInt(ft[0]);
                            currentY = Integer.parseInt(ft[1]);
                        }
                        lastPositionWasFO = false;
                        break;
                    case "CF":
                        String[] cf = params.split(",");
                        if (cf.length >= 1 && !cf[0].isEmpty()) currentFontName = cf[0]; // e.g. '0', 'A'
                        if (cf.length >= 2 && !cf[1].isEmpty()) {
                            currentFontHeight = Integer.parseInt(cf[1]);
                            // ZPL Standard: If width is not specified, it defaults to height
                            currentFontWidth = currentFontHeight; 
                        }
                        if (cf.length >= 3 && !cf[2].isEmpty()) currentFontWidth = Integer.parseInt(cf[2]);
                        break;
                    case "A0":
                        // Scalable Font A0N,h,w
                        String[] a0 = params.split(",");
                        if (a0.length >= 2 && !a0[1].isEmpty()) {
                            currentFontHeight = Integer.parseInt(a0[1]);
                            currentFontWidth = currentFontHeight; // Default
                        }
                        if (a0.length >= 3 && !a0[2].isEmpty()) currentFontWidth = Integer.parseInt(a0[2]);
                        currentFontName = "0"; 
                        break;
                    case "BY":
                        // ^BYw,r,h
                        String[] by = params.split(",");
                        if (by.length >= 1 && !by[0].isEmpty()) currentModuleWidth = Integer.parseInt(by[0]);
                        // r ratio ignored for now
                        if (by.length >= 3 && !by[2].isEmpty()) barcodeHeight = Integer.parseInt(by[2]);
                        break;
                    case "FD":
                        // Field Data
                        ZplElementBase element = null;
                        if (nextIsBarcode) {
                            if ("BC".equals(nextBarcodeType)) {
                                // Extract f, g from nextBarcodeParameters if stored, or use defaults
                                boolean printLines = true; 
                                boolean printAbove = false;
                                if (nextBarcodeType.equals("BC")) {
                                    String[] bcParams = nextBarcodeParams.split(",");
                                    if (bcParams.length >= 3 && !bcParams[2].isEmpty()) printLines = "Y".equalsIgnoreCase(bcParams[2]);
                                    if (bcParams.length >= 4 && !bcParams[3].isEmpty()) printAbove = "Y".equalsIgnoreCase(bcParams[3]);
                                }
                                element = new ZplBarcode128(currentX, currentY, params, barcodeHeight, currentModuleWidth, printLines, printAbove);
                            } else if ("BD".equals(nextBarcodeType)) {
                                // MaxiCode payload
                                int mode = currentBarcodeHeight; // Mode was stored here
                                element = new ZplMaxiCode(currentX, currentY, mode, 0, 0, params);
                            }
                            nextIsBarcode = false;
                            nextBarcodeType = "";
                        } else {
                            ZplTextField tf = new ZplTextField(currentX, currentY, params, currentFontName, currentFontHeight, currentFontWidth, lastPositionWasFO);
                            tf.setFieldBlock(currentBlockWidth, currentJustification);
                            element = tf;
                            // Reset FB state after use as it typically applies to one FD
                            currentBlockWidth = 0;
                            currentJustification = "L";
                        }
                        if (element != null) {
                            element.setReverseDraw(currentIsReverseDraw);
                            elements.add(element);
                        }
                        currentIsReverseDraw = false; // Reset after use
                        break;
                    case "FR":
                        currentIsReverseDraw = true;
                        break;
                    case "BC":
                        // Barcode 128
                        // ^BCo,h,f,g,e,m
                        String[] bc = params.split(",");
                        if (bc.length >= 2 && !bc[1].isEmpty()) barcodeHeight = Integer.parseInt(bc[1]);
                        nextIsBarcode = true;
                        nextBarcodeType = "BC";
                        nextBarcodeParams = params; // Store to parse flags in FD
                        break;
                    case "GB":
                        String[] gb = params.split(",");
                        int w = gb.length > 0 ? Integer.parseInt(gb[0]) : 0;
                        int h = gb.length > 1 ? Integer.parseInt(gb[1]) : 0;
                        int t = gb.length > 2 ? Integer.parseInt(gb[2]) : 1;
                        String c = gb.length > 3 ? gb[3] : "B";
                        int r = gb.length > 4 ? Integer.parseInt(gb[4]) : 0;
                        ZplGraphicBox box = new ZplGraphicBox(currentX, currentY, w, h, t, c, r);
                        box.setReverseDraw(currentIsReverseDraw);
                        elements.add(box);
                        currentIsReverseDraw = false; // Reset
                        break;

                    case "BD":
                          // MaxiCode ^BDm,n,t
                          String[] bd = params.split(",");
                          int mode = 2; // Default
                          if (bd.length >= 1 && !bd[0].isEmpty()) {
                              try {
                                  mode = Integer.parseInt(bd[0]);
                              } catch (NumberFormatException e) {}
                          }
                          nextIsBarcode = true;
                          nextBarcodeType = "BD";
                          currentBarcodeHeight = mode; // Borrowing variable to store mode temporarily for FD
                          break;
                    case "FB":
                        // ^FB[width],[max_lines],[line_spacing],[alignment],[indent]
                        String[] fb = params.split(",");
                        if (fb.length >= 1 && !fb[0].isEmpty()) currentBlockWidth = Integer.parseInt(fb[0].trim());
                        if (fb.length >= 4 && !fb[3].isEmpty()) currentJustification = fb[3].toUpperCase().trim();
                        break;
                }
            } catch (Exception e) {
                System.out.println("Parse Error: " + command + " " + e.getMessage());
            }
        }
        
        return elements;
    }
}
