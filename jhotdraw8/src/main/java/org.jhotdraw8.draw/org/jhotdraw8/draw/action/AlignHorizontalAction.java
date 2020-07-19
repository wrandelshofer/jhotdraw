/*
 * @(#)AlignHorizontalAction.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.action;

import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.transform.Translate;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.draw.DrawLabels;
import org.jhotdraw8.draw.DrawingEditor;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.geom.FXTransforms;
import org.jhotdraw8.util.Resources;

import java.util.Set;

public class AlignHorizontalAction extends AbstractDrawingViewAction {

    public static final String ID = "edit.alignHorizontal";

    /**
     * Creates a new instance.
     *
     * @param editor the drawing editor
     */
    public AlignHorizontalAction(@NonNull DrawingEditor editor) {
        super(editor);
        Resources labels
                = DrawLabels.getResources();
        labels.configureAction(this, ID);
    }

    @Override
    protected void onActionPerformed(@NonNull ActionEvent e, @NonNull DrawingView drawingView) {
        final Set<Figure> figures = drawingView.getSelectedFigures();
        Figure lead = drawingView.getSelectionLead();
        alignHorizontal(drawingView, figures, lead);
    }

    private void alignHorizontal(@NonNull DrawingView view, @NonNull Set<Figure> figures, @NonNull Figure lead) {
        if (figures.size() < 2) {
            return;
        }
        DrawingModel model = view.getModel();
        Bounds leadBounds = lead.getLayoutBoundsInWorld();
        double yInWorld = leadBounds.getMinY() + leadBounds.getHeight() * 0.5;
        Point2D yPointInWorld = new Point2D(0, yInWorld);
        for (Figure f : figures) {
            if (f != lead && f.isEditable()) {
                double desiredY = FXTransforms.transform(f.getWorldToParent(), yPointInWorld).getY();
                Bounds bounds = f.getLayoutBoundsInParent();
                double actualY = bounds.getMinY() + bounds.getHeight() * 0.5;
                double dy = desiredY - actualY;
                Translate tx = new Translate(0, dy);
                model.transformInParent(f, tx);
                model.fireLayoutInvalidated(f);
            }
        }
    }
}
