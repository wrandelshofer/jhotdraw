/* @(#)ToggleStatusBarAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.samples.teddy.action;

import org.jhotdraw.app.Application;
import org.jhotdraw.app.View;
import org.jhotdraw.app.action.AbstractViewAction;
import org.jhotdraw.samples.teddy.TeddyLabels;
import org.jhotdraw.samples.teddy.TeddyView;
import org.jhotdraw.util.ResourceBundleUtil;

import org.jhotdraw.annotation.Nullable;
import javax.swing.Action;
import java.awt.event.ActionEvent;

/**
 * ToggleStatusBarAction.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class ToggleStatusBarAction extends AbstractViewAction {
    private static final long serialVersionUID = 1L;

    public static final String ID = "view.toggleStatusBar";
    private ResourceBundleUtil labels = TeddyLabels.getLabels();

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

