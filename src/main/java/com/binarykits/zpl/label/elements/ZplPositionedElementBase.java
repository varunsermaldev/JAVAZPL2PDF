package com.binarykits.zpl.label.elements;

public abstract class ZplPositionedElementBase extends ZplElementBase {
    protected int positionX;
    protected int positionY;
    protected boolean bottomToTop = false;

    public ZplPositionedElementBase(int positionX, int positionY, boolean bottomToTop) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.bottomToTop = bottomToTop;
    }

    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }
    
    public boolean isBottomToTop() {
        return bottomToTop;
    }
}
