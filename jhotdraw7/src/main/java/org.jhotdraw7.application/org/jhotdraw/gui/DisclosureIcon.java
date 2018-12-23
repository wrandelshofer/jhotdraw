/* @(#)DisclosureIcon.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.gui;

import javax.swing.Icon;
import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

/**
 * DisclosureIcon.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DisclosureIcon implements Icon {
    /**
     * This client property needs to be set on the component.
     * The value must be a positive Integer. 
     */
    public static final String STATE_COUNT_PROPERTY = "DisclosureIcon.numberOfStates";
    /**
     * This client property needs to be set on the component.
     * The value must be a positive Integer. 
     */
    public static final String CURRENT_STATE_PROPERTY = "DisclosureIcon.currentState";

    @Override
    public void paintIcon(Component component, Graphics g, int x, int y) {
        JComponent c = (JComponent) component;
        
        int nbOfStates = (c.getClientProperty(STATE_COUNT_PROPERTY) instanceof Integer) ?
        (Integer) c.getClientProperty(STATE_COUNT_PROPERTY) : 2;
        
        int currentState = (c.getClientProperty(CURRENT_STATE_PROPERTY) instanceof Integer) ?
        (Integer) c.getClientProperty(CURRENT_STATE_PROPERTY) : 1;
        
        g.setColor(Color.black);
        g.drawRect(x, y, getIconWidth() - 1, getIconHeight() - 1);
        g.setColor(new Color(0x666666));
        g.fillRect(x + 1, y + 1, 
                1 + (getIconWidth() - 3) * currentState / (nbOfStates - 1), 
                getIconHeight() - 2
                );
    }

    @Override
    public int getIconWidth() {
        return 10;
    }

    @Override
    public int getIconHeight() {
       return 8;
    }

}
