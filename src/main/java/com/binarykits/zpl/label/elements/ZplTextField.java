package com.binarykits.zpl.label.elements;

public class ZplTextField extends ZplPositionedElementBase {
    private String text;
    private String fontName;
    private int fontHeight;
    private int fontWidth;
    private String orientation;
    private boolean useFieldOrigin; // True if FO, False if FT

    public ZplTextField(int positionX, int positionY, String text, String fontName, int fontHeight, int fontWidth, boolean useFieldOrigin) {
        super(positionX, positionY, false); 
        this.text = text;
        this.fontName = fontName;
        this.fontHeight = fontHeight;
        this.fontWidth = fontWidth;
        this.useFieldOrigin = useFieldOrigin;
    }

    
    // Constructor for Bottom-Up (FT)
    public ZplTextField(int positionX, int positionY, String text, String fontName, int fontHeight, int fontWidth,  boolean useFieldOrigin, boolean bottomToTop) {
        super(positionX, positionY, bottomToTop);
        this.text = text;
        this.fontName = fontName;
        this.fontHeight = fontHeight;
        this.fontWidth = fontWidth;
    }

    public String getText() { return text; }
    public String getFontName() { return fontName; }
    public int getFontHeight() { return fontHeight; }
    public int getFontWidth() { return fontWidth; }
    public boolean isUseFieldOrigin() { return useFieldOrigin; }
}
