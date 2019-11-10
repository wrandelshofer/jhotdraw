/*
 * @(#)SendBackwardAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.action;

import javafx.event.ActionEvent;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.app.Activity;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.draw.DrawLabels;
import org.jhotdraw8.draw.DrawingEditor;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.util.Resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * MoveUpAction.
 *
 * @author Werner Randelshofer
 */
public class SendBackwardAction extends AbstractSelectedAction {

    public static final String ID = "edit.sendBackward";

    /**
     * Creates a new instance.
     *
     * @param app    the application
     * @param editor the drawing editor
     */
    public SendBackwardAction(@NonNull Application app, DrawingEditor editor) {
        super(app, editor);
        Resources labels
                = DrawLabels.getResources();
        labels.configureAction(this, ID);
    }

    @Override
    protected void handleActionPerformed(ActionEvent e, Activity activity) {
        final DrawingView drawingView = getView();
        if (drawingView == null) {
            return;
        }
        final List<Figure> figures = new ArrayList<>(drawingView.getSelectedFigures());
        moveDown(drawingView, figures);

    }

    public void moveDown(@NonNull DrawingView view, @NonNull Collection<Figure> figures) {
        DrawingModel model = view.getModel();
        for (Figure child : figures) {
            Figure parent = child.getParent();
            if (parent != null && parent.isEditable() && parent.isDecomposable()) {
                int index = parent.getChildren().indexOf(child);
                if (index > 0) {
                    model.insertChildAt(child, parent, index - 1);
                }
            }
        }
    }
}
