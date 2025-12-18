package com.binarykits.zpl.label.elements;

public class ZplMaxiCode extends ZplPositionedElementBase {
    private int mode;
    private int symbolNumber;
    private int totalSymbols;
    private String content;

    public ZplMaxiCode(int positionX, int positionY, int mode, int symbolNumber, int totalSymbols, String content) {
        super(positionX, positionY, false);
        this.mode = mode;
        this.symbolNumber = symbolNumber;
        this.totalSymbols = totalSymbols;
        this.content = content;
    }
    
    public String getContent() { return content; }
    public int getMode() { return mode; }
}
