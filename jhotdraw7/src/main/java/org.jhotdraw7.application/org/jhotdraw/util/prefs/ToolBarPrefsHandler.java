/* @(#)ToolBarPrefsHandler.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.util.prefs;

import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.plaf.basic.BasicToolBarUI;
import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.prefs.Preferences;

/**
 * ToolBarPrefsHandler.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ToolBarPrefsHandler implements ComponentListener, AncestorListener {
    private JToolBar toolbar;
    private String prefsPrefix;
    private Preferences prefs;
    
    public ToolBarPrefsHandler(JToolBar toolbar, String prefsPrefix, Preferences prefs) {
        this.toolbar = toolbar;
        this.prefsPrefix = prefsPrefix;
        this.prefs = prefs;
        
        String constraint = prefs.get(prefsPrefix+".constraint", BorderLayout.NORTH);
        int orientation = (constraint.equals(BorderLayout.NORTH) || constraint.equals(BorderLayout.SOUTH)) ? JToolBar.HORIZONTAL : JToolBar.VERTICAL;
        toolbar.setOrientation(orientation);
        toolbar.getParent().add(constraint, toolbar);
        toolbar.setVisible(prefs.getBoolean(prefsPrefix+".visible", true));
        /*
        if (prefs.getBoolean(prefsPrefix+".isFloating", false)) {
            makeToolBarFloat();
        }*/
        
        toolbar.addComponentListener(this);
        toolbar.addAncestorListener(this);
    }
    
    
    
    /*
     * XXX - This does not work
    private void makeToolBarFloat() {
        BasicToolBarUI ui = (BasicToolBarUI) toolbar.getUI();
        Window window = SwingUtilities.getWindowAncestor(toolbar);
        System.out.println("Window Ancestor:"+window+" instanceof Frame:"+(window instanceof Frame));
        ui.setFloating(true, new Point(
        prefs.getInt(prefsPrefix+".floatingX", 0),
        prefs.getInt(prefsPrefix+".floatingY", 0)
        ));
        window = SwingUtilities.getWindowAncestor(toolbar);
        window.setLocation(
        prefs.getInt(prefsPrefix+".floatingX", 0),
        prefs.getInt(prefsPrefix+".floatingY", 0)
        );
        window.toFront();
    }*/
    @Override
    public void componentHidden(ComponentEvent e) {
        prefs.putBoolean(prefsPrefix+".visible", false);
    }
    
    @Override
    public void componentMoved(ComponentEvent e) {
        locationChanged();
    }
    private void locationChanged() {
        // FIXME : use reflection to get hold of method 'isFloating'.
        if (toolbar.getUI() instanceof BasicToolBarUI) {
            BasicToolBarUI ui = (BasicToolBarUI) toolbar.getUI();
            boolean floating = ui.isFloating();
            prefs.putBoolean(prefsPrefix+".isFloating", floating);
            if (floating) {
                Window window = SwingUtilities.getWindowAncestor(toolbar);
                prefs.putInt(prefsPrefix+".floatingX", window.getX());
                prefs.putInt(prefsPrefix+".floatingY", window.getY());
            } else if (toolbar.getParent() != null) {
                int x = toolbar.getX();
                int y = toolbar.getY();
                Insets insets = toolbar.getParent().getInsets();
                String constraint;
                if (x == insets.left && y == insets.top) {
                    constraint = (toolbar.getOrientation() == JToolBar.HORIZONTAL) ? BorderLayout.NORTH : BorderLayout.WEST;
                } else {
                    constraint = (toolbar.getOrientation() == JToolBar.HORIZONTAL) ? BorderLayout.SOUTH : BorderLayout.EAST;
                }
                prefs.put(prefsPrefix+".constraint", constraint);
            }
        } else {
            if (toolbar.getParent() != null) {
                int x = toolbar.getX();
                int y = toolbar.getY();
                Insets insets = toolbar.getParent().getInsets();
                String constraint;
                if (x == insets.left && y == insets.top) {
                    constraint = (toolbar.getOrientation() == JToolBar.HORIZONTAL) ? BorderLayout.NORTH : BorderLayout.WEST;
                } else {
                    constraint = (toolbar.getOrientation() == JToolBar.HORIZONTAL) ? BorderLayout.SOUTH : BorderLayout.EAST;
                }
                prefs.put(prefsPrefix+".constraint", constraint);
            }
        }
    }
    
    @Override
    public void componentResized(ComponentEvent e) {
        locationChanged();
    }
    
    @Override
    public void componentShown(ComponentEvent e) {
        prefs.putBoolean(prefsPrefix+".visible", true);
    }
    
    @Override
    public void ancestorAdded(AncestorEvent event) {
        locationChanged();
    }
    
    @Override
    public void ancestorMoved(AncestorEvent event) {
        if (toolbar.getUI() instanceof BasicToolBarUI) {
            if (((BasicToolBarUI) toolbar.getUI()).isFloating()) {
                locationChanged();
            }
        }
    }
    
    @Override
    public void ancestorRemoved(AncestorEvent event) {
        if (toolbar.getUI() instanceof BasicToolBarUI) {
            if (((BasicToolBarUI) toolbar.getUI()).isFloating()) {
                locationChanged();
            }
        }
    }
}
