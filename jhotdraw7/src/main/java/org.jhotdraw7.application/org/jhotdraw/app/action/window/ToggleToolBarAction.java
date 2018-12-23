/* @(#)ToggleToolBarAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.app.action.window;

import org.jhotdraw.app.action.ActionUtil;

import javax.swing.AbstractAction;
import javax.swing.JToolBar;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * ToggleToolBarAction.
 * 
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ToggleToolBarAction extends AbstractAction {
    private static final long serialVersionUID = 1L;
    private JToolBar toolBar;
    private PropertyChangeListener propertyHandler;
    
    /** Creates a new instance. */
    public ToggleToolBarAction(JToolBar toolBar, String label) {
        super(label);
        
        propertyHandler = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String name = evt.getPropertyName();
                if ("visible".equals(name)) {
                    putValue(ActionUtil.SELECTED_KEY, evt.getNewValue());
                }
            }            
        };
        
        putValue(ActionUtil.SELECTED_KEY, true);
        setToolBar(toolBar);
    }
    
    @Override
    public void putValue(String key, Object newValue) {
        super.putValue(key, newValue);
        if (ActionUtil.SELECTED_KEY.equals(key)) {
            if (toolBar != null) {
                toolBar.setVisible((Boolean) newValue);
            }
        }
    }
    
    public void setToolBar(JToolBar newValue) {
        if (toolBar != null) {
            toolBar.removePropertyChangeListener(propertyHandler);
        }
        
        toolBar = newValue;
 
        if (toolBar != null) {
            toolBar.addPropertyChangeListener(propertyHandler);
            putValue(ActionUtil.SELECTED_KEY, toolBar.isVisible());
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (toolBar != null) {
            putValue(ActionUtil.SELECTED_KEY, ! toolBar.isVisible());
        }
    }
}
