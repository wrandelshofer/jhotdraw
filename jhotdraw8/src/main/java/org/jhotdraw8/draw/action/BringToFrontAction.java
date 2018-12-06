/* @(#)BringToFrontAction.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javafx.event.ActionEvent;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.draw.DrawingEditor;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.util.Resources;
import org.jhotdraw8.app.Activity;

/**
 * BringToFrontAction.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class BringToFrontAction extends AbstractSelectedAction {

    public static final String ID = "edit.bringToFront";

    /**
     * Creates a new instance.
     *
     * @param app the application
     * @param editor the drawing editor
     */
    public BringToFrontAction(Application app, DrawingEditor editor) {
        super(app, editor);
        Resources labels
                = Resources.getResources("org.jhotdraw8.draw.Labels");
        labels.configureAction(this, ID);
    }

    @Override
    protected void handleActionPerformed(ActionEvent e, Activity view) {
        final DrawingView drawingView = getView();
        if (drawingView == null) {
            return;
        }
        final List<Figure> figures = new ArrayList<>(drawingView.getSelectedFigures());
        bringToFront(drawingView, figures);

    }

    public void bringToFront(DrawingView view, Collection<Figure> figures) {
        DrawingModel model = view.getModel();
        for (Figure child : figures) {
            Figure parent = child.getParent();
            if (parent != null && parent.isEditable() && parent.isDecomposable()) {
                model.insertChildAt(child, parent, parent.getChildren().size() - 1);
            }
        }
    }
}
