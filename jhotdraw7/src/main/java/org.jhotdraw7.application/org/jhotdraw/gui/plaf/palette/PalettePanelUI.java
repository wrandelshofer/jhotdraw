/* @(#)QuaquaPanelUI.java  
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.gui.plaf.palette;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTabbedPane;
import javax.swing.RootPaneContainer;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.PanelUI;
import javax.swing.plaf.basic.BasicPanelUI;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Window;

/**
 * PalettePanelUI.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class PalettePanelUI extends BasicPanelUI {
    // Shared UI object
    private static PanelUI panelUI;
    
    public static ComponentUI createUI(JComponent c) {
        if(panelUI == null) {
            panelUI = new PalettePanelUI();
        }
        return panelUI;
    }
    @Override
    protected void installDefaults(JPanel p) {
        PaletteLookAndFeel.installColorsAndFont(p,
					 "Panel.background",
					 "Panel.foreground",
					 "Panel.font");
        PaletteLookAndFeel.installBorder(p,"Panel.border");
	PaletteLookAndFeel.installProperty(p, "opaque", PaletteLookAndFeel.getInstance().get("Panel.opaque"));
    }
    
    @Override
    protected void uninstallDefaults(JPanel p) {
        super.uninstallDefaults(p);
    }
    
    public static boolean isInTabbedPane(Component comp) {
        if(comp == null)
            return false;
        Container parent = comp.getParent();
        while (parent != null) {
            if (parent instanceof JTabbedPane) {
                return true;
            } else if (parent instanceof JRootPane) {
                return false;
            } else if (parent instanceof RootPaneContainer) {
                return false;
            } else if (parent instanceof Window) {
                return false;
            }
            parent = parent.getParent();
        }
        return false;
    }
    
    @Override
    public void paint(Graphics gr, JComponent c) {
            Graphics2D g = (Graphics2D) gr;
        if (c.isOpaque()) {
            g.setColor(c.getBackground());
            g.fillRect(0, 0, c.getWidth(), c.getHeight());
        }
        /*
        Border backgroundBorder = null;
        Insets insets = new Insets(0,0,0,0);

        if (backgroundBorder != null) {
            backgroundBorder.paintBorder(c, gr, insets.left, insets.top, c.getWidth() - insets.left - insets.right, c.getHeight() - insets.top - insets.bottom);
        }
        */
    }
}
