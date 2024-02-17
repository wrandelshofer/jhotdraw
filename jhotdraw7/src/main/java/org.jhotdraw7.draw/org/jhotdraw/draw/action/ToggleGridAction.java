/* @(#)ToggleGridAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.draw.action;

import org.jhotdraw.app.action.ActionUtil;
import org.jhotdraw.draw.DrawLabels;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * Toggles the grid of the current view.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class ToggleGridAction extends AbstractDrawingViewAction {
    private static final long serialVersionUID = 1L;
    public static final String ID = "view.toggleGrid";
    /**
     * Creates a new instance.
     */
    public ToggleGridAction(DrawingEditor editor) {
        super(editor);
        ResourceBundleUtil labels =
                DrawLabels.getLabels();
        labels.configureAction(this, ID);
        updateViewState();
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        DrawingView view = getView();
        if (view != null) {
            view.setConstrainerVisible(! view.isConstrainerVisible());
        }
    }
    
    @Override
    protected void updateViewState() {
        DrawingView view = getView();
        putValue(ActionUtil.SELECTED_KEY, view != null && view.isConstrainerVisible());
    }
}
