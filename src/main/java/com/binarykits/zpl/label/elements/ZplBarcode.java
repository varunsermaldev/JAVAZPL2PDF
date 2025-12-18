package com.binarykits.zpl.label.elements;

public abstract class ZplBarcode extends ZplPositionedElementBase {
    protected int height;
    protected int moduleWidth;
    protected String content;

    public ZplBarcode(int positionX, int positionY, int height, int moduleWidth, String content, boolean bottomToTop) {
        super(positionX, positionY, bottomToTop);
        this.height = height;
        this.moduleWidth = moduleWidth;
        this.content = content;
    }

    public int getHeight() { return height; }
    public int getModuleWidth() { return moduleWidth; }
    public String getContent() { return content; }
}
