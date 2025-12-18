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
    
    // Abstract method to mimic C# behaviour if needed, or just base logic
}
