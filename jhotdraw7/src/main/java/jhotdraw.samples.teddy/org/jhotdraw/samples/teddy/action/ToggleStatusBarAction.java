/* @(#)ToggleStatusBarAction.java
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
 * ToggleStatusBarAction.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class ToggleStatusBarAction extends AbstractViewAction {
    private static final long serialVersionUID = 1L;

    public static final String ID = "view.toggleStatusBar";
    private ResourceBundleUtil labels = new ResourceBundleUtil(ResourceBundle.getBundle("org.jhotdraw.samples.teddy.Labels"));

    /**
     * Creates a new instance.
     */
    public ToggleStatusBarAction(Application app, @Nullable View view) {
        super(app, view);
        labels.configureAction(this, ID);
        setPropertyName("statusBarVisible");
    }

    @Override
    public TeddyView getActiveView() {
        return (TeddyView) super.getActiveView();
    }

    @Override
    protected void updateView() {
        putValue(
                Action.SELECTED_KEY,
                getActiveView() != null && getActiveView().isStatusBarVisible());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        getActiveView().setStatusBarVisible(!getActiveView().isStatusBarVisible());
    }
}

