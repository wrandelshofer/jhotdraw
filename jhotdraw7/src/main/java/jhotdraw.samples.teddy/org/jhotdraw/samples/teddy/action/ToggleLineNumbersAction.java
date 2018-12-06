/* @(#)ToggleLineNumbersAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.samples.teddy.action;

import org.jhotdraw.app.Application;
import org.jhotdraw.app.View;
import org.jhotdraw.app.action.AbstractViewAction;
import org.jhotdraw.samples.teddy.TeddyLabels;
import org.jhotdraw.samples.teddy.TeddyView;
import org.jhotdraw.util.ResourceBundleUtil;

import javax.swing.Action;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

/**
 * ToggleLineNumbersAction.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class ToggleLineNumbersAction extends AbstractViewAction {
    private static final long serialVersionUID = 1L;
    public static final String ID = "view.toggleLineNumbers";
    private ResourceBundleUtil labels = TeddyLabels.getLabels();
    
    /**
     * Creates a new instance.
     */
    public ToggleLineNumbersAction(Application app, View view) {
        super(app, view);
        labels.configureAction(this, ID);
        setPropertyName("lineNumbersVisible");
    }
    
    @Override
    public TeddyView getActiveView() {
        return (TeddyView) super.getActiveView();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        getActiveView().setLineNumbersVisible(! getActiveView().isLineNumbersVisible());
    }
    
    
    @Override
    protected void updateView() {
        putValue(
                Action.SELECTED_KEY,
                getActiveView() != null && getActiveView().isLineNumbersVisible()
                );
    }
}
