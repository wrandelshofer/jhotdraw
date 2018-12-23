/* @(#)ColorListCellRenderer.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.color;

import org.jhotdraw.annotation.Nullable;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JList;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

/**
 * ColorListCellRenderer.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ColorListCellRenderer extends DefaultListCellRenderer {
    private static final long serialVersionUID = 1L;

    
    private static class ColorIcon implements Icon {

        @Nullable private Color color;

        public void setColor(@Nullable Color newValue) {
            color = newValue;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            if (color != null) {
            g.setColor(new Color(0x333333));
            g.drawRect(x, y, getIconWidth() - 1, getIconHeight() - 1);
            g.setColor(Color.WHITE);
            g.drawRect(x + 1, y + 1, getIconWidth() - 3, getIconHeight() - 3);
                g.setColor(color);
                g.fillRect(x + 2, y + 2, getIconWidth() - 4, getIconHeight() - 4);
            }
        }

        @Override
        public int getIconWidth() {
            return 24;
        }

        @Override
        public int getIconHeight() {
            return 18;
        }
    }

    private ColorIcon icon;
    
    public ColorListCellRenderer() {
    icon = new ColorIcon();
    setIcon(icon);
    }

    @Override
    public Component getListCellRendererComponent(
            JList<?> list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof Color) {
            Color c = (Color) value;
            icon.setColor(c);
            setToolTipText(ColorUtil.toToolTipText(c));
            setText("");
        } else {
            icon.setColor(null);
            setText("");
        }
        setIcon(icon);
        return this;
    }
}
