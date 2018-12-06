/* @(#)EmptyIcon.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.gui;

import javax.swing.Icon;
import java.awt.Component;
import java.awt.Graphics;

/**
 * EmptyIcon.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class EmptyIcon implements Icon {
    private int width;
    private int height;
    
    public EmptyIcon(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
    }

    @Override
    public int getIconWidth() {
        return width;
    }

    @Override
    public int getIconHeight() {
        return height;
    }

}
