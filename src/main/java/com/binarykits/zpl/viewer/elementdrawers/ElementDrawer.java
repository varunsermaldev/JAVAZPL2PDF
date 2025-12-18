package com.binarykits.zpl.viewer.elementdrawers;

import com.binarykits.zpl.label.elements.ZplElementBase;
import java.awt.Graphics2D;

public interface ElementDrawer {
    boolean canDraw(ZplElementBase element);
    void draw(ZplElementBase element, Graphics2D g2d);
}
