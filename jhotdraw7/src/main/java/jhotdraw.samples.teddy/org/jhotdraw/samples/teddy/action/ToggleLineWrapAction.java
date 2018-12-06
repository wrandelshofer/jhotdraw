/* @(#)ToggleLineWrapAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.samples.teddy.action;

import javax.annotation.Nullable;
import org.jhotdraw.app.*;
import org.jhotdraw.app.action.*;
import org.jhotdraw.samples.teddy.*;
import org.jhotdraw.util.*;
import java.awt.event.*;
import java.util.ResourceBundle;
import javax.swing.Action;

/**
 * ToggleLineWrapAction.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class ToggleLineWrapAction extends AbstractViewAction {
    private static final long serialVersionUID = 1L;
    public static final String ID = "view.toggleLineWrap";
    private ResourceBundleUtil labels = new ResourceBundleUtil(ResourceBundle.getBundle("org.jhotdraw.samples.teddy.Labels"));
    
    /**
     * Creates a new instance.
     */
    public ToggleLineWrapAction(Application app, @Nullable View view) {
        super(app, view);
        labels.configureAction(this, ID);
        setPropertyName("lineWrap");
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        getActiveView().setLineWrap(! getActiveView().isLineWrap());
    }
    
    @Override
    public TeddyView getActiveView() {
        return (TeddyView) super.getActiveView();
    }
    
    @Override
    protected void updateView() {
        putValue(
                Action.SELECTED_KEY,
                getActiveView() != null && getActiveView().isLineWrap()
                );
    }
}
