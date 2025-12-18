package com.binarykits.zpl.label.elements;

import java.util.ArrayList;
import java.util.List;

public abstract class ZplElementBase {
    protected List<String> comments = new ArrayList<>();
    protected boolean isWhitedraw = false;
    protected boolean isReverseDraw = false;

    public List<String> getComments() {
        return comments;
    }

    public boolean isWhitedraw() {
        return isWhitedraw;
    }

    public void setWhitedraw(boolean whitedraw) {
        isWhitedraw = whitedraw;
    }

    public boolean isReverseDraw() {
        return isReverseDraw;
    }

    public void setReverseDraw(boolean reverseDraw) {
        isReverseDraw = reverseDraw;
    }
}
