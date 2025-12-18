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
        boolean lastPositionWasFO = true; // Default FO
        
        // Barcode Defaults
        int currentModuleWidth = 2; // Default ZPL module width
        double currentWideBarRatio = 3.0;
        int currentBarcodeHeight = 10;


        
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
                        if (cf.length >= 1) currentFontName = cf[0]; // e.g. '0', 'A'
                        if (cf.length >= 2) currentFontHeight = Integer.parseInt(cf[1]);
                        if (cf.length >= 3) currentFontWidth = Integer.parseInt(cf[2]);
                        break;
                    case "A0":
                        // Scalable Font A0N,h,w
                        // Format: [Orientation],Height,Width
                        // e.g. N,30,30
                        String[] a0 = params.split(",");
                        if (a0.length >= 2) currentFontHeight = Integer.parseInt(a0[1]);
                        if (a0.length >= 3) currentFontWidth = Integer.parseInt(a0[2]);
                        currentFontName = "0"; // or 0
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
                        if (nextIsBarcode) {
                            if ("BC".equals(nextBarcodeType)) {
                                elements.add(new ZplBarcode128(currentX, currentY, params, barcodeHeight, currentModuleWidth, true, false));
                            } else if ("BD".equals(nextBarcodeType)) {
                                // MaxiCode payload
                                int mode = currentBarcodeHeight; // Mode was stored here
                                elements.add(new ZplMaxiCode(currentX, currentY, mode, 0, 0, params));
                            }
                            nextIsBarcode = false;
                            nextBarcodeType = "";
                        } else {
                            elements.add(new ZplTextField(currentX, currentY, params, currentFontName, currentFontHeight, currentFontWidth, lastPositionWasFO));
                        }
                        break;
                    case "GB":
                        String[] gb = params.split(",");
                        int w = gb.length > 0 ? Integer.parseInt(gb[0]) : 0;
                        int h = gb.length > 1 ? Integer.parseInt(gb[1]) : 0;
                        int t = gb.length > 2 ? Integer.parseInt(gb[2]) : 1;
                        String c = gb.length > 3 ? gb[3] : "B";
                        int r = gb.length > 4 ? Integer.parseInt(gb[4]) : 0;
                        elements.add(new ZplGraphicBox(currentX, currentY, w, h, t, c, r));
                        break;
                    case "BC":
                        // Barcode 128
                        // ^BCo,h,f,g,e,m
                        String[] bc = params.split(",");
                        if (bc.length >= 2 && !bc[1].isEmpty()) barcodeHeight = Integer.parseInt(bc[1]);
                        nextIsBarcode = true;
                        nextBarcodeType = "BC";
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
                }
            } catch (Exception e) {
                System.out.println("Parse Error: " + command + " " + e.getMessage());
            }
        }
        
        return elements;
    }
}
