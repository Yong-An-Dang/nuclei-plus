package com.g3g4x5x6.nuclei.ui.icon;

import javax.swing.*;
import java.awt.*;


/**
 * An icon that looks like a Windows 98 or XP-style size grip.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class SizeGripIcon implements Icon {

    private static final int SIZE = 16;


    /**
     * Returns the height of this icon.
     *
     * @return This icon's height.
     */
    @Override
    public int getIconHeight() {
        return SIZE;
    }


    /**
     * Returns the width of this icon.
     *
     * @return This icon's width.
     */
    @Override
    public int getIconWidth() {
        return SIZE;
    }


    /**
     * Paints this icon.
     *
     * @param c The component to paint on.
     * @param g The graphics context.
     * @param x The x-coordinate at which to paint.
     * @param y The y-coordinate at which to paint.
     */
    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {

        Dimension dim = c.getSize();
        Color c1 = UIManager.getColor("Label.disabledShadow");
        Color c2 = UIManager.getColor("Label.disabledForeground");

        ComponentOrientation orientation = c.getComponentOrientation();
        int height = dim.height -= 3;

        if (orientation.isLeftToRight()) {
            int width = dim.width  -= 3;
            g.setColor(c1);
            g.fillRect(width-9,height-1, 3,3);
            g.fillRect(width-5,height-1, 3,3);
            g.fillRect(width-1,height-1, 3,3);
            g.fillRect(width-5,height-5, 3,3);
            g.fillRect(width-1,height-5, 3,3);
            g.fillRect(width-1,height-9, 3,3);
            g.setColor(c2);
            g.fillRect(width-9,height-1, 2,2);
            g.fillRect(width-5,height-1, 2,2);
            g.fillRect(width-1,height-1, 2,2);
            g.fillRect(width-5,height-5, 2,2);
            g.fillRect(width-1,height-5, 2,2);
            g.fillRect(width-1,height-9, 2,2);
        }
        else {
            g.setColor(c1);
            g.fillRect(10,height-1, 3,3);
            g.fillRect(6,height-1, 3,3);
            g.fillRect(2,height-1, 3,3);
            g.fillRect(6,height-5, 3,3);
            g.fillRect(2,height-5, 3,3);
            g.fillRect(2,height-9, 3,3);
            g.setColor(c2);
            g.fillRect(10,height-1, 2,2);
            g.fillRect(6,height-1, 2,2);
            g.fillRect(2,height-1, 2,2);
            g.fillRect(6,height-5, 2,2);
            g.fillRect(2,height-5, 2,2);
            g.fillRect(2,height-9, 2,2);
        }

    }


}