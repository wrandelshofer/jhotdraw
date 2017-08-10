/* @(#)PlacardScrollPaneLayout.java
 * Copyright © 1996-2017 The authors and contributors of JHotDraw.
 * MIT License, CC-by License, or LGPL License.
 */
package org.jhotdraw.gui;

import java.awt.*;
import javax.swing.*;

/**
 * PlacardScrollPaneLayout.
 *
 * @author Werner Randelshofer.
 * @version $Id$
 */
public class PlacardScrollPaneLayout extends ScrollPaneLayout {
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance.
     */
    public PlacardScrollPaneLayout() {
    }

    @Override
    public void layoutContainer(Container parent) {
        super.layoutContainer(parent);

        if (lowerLeft != null && hsb != null) {
            Dimension llp = lowerLeft.getPreferredSize();
//Insets insets = parent.getInsets();
            lowerLeft.setBounds(hsb.getX(), hsb.getY(), llp.width, hsb.getHeight());
            hsb.setBounds(hsb.getX() + llp.width, hsb.getY(), hsb.getWidth() - llp.width, hsb.getHeight());

        }
    }

    /**
     * The UI resource version of <code>ScrollPaneLayout</code>.
     */
    public static class UIResource extends PlacardScrollPaneLayout implements javax.swing.plaf.UIResource {
    private static final long serialVersionUID = 1L;
    }
}



