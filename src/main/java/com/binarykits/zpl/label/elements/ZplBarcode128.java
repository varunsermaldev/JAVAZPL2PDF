package com.binarykits.zpl.label.elements;

public class ZplBarcode128 extends ZplBarcode {
    private boolean printInterpretationLine;
    private boolean printInterpretationLineAbove;
    
    public ZplBarcode128(int positionX, int positionY, String content, int height, int moduleWidth, boolean printInterpretationLine, boolean printInterpretationLineAbove) {
        super(positionX, positionY, height, moduleWidth, content, false);
        this.printInterpretationLine = printInterpretationLine;
        this.printInterpretationLineAbove = printInterpretationLineAbove;
    }
}
