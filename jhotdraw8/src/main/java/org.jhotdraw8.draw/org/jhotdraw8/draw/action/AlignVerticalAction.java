/*
 * @(#)AlignVerticalAction.java
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
import org.jhotdraw8.geom.Transforms;
import org.jhotdraw8.util.Resources;

import java.util.Set;

public class AlignVerticalAction extends AbstractDrawingViewAction {

    public static final String ID = "edit.alignVertical";

    /**
     * Creates a new instance.
     *
     * @param editor the drawing editor
     */
    public AlignVerticalAction(@NonNull DrawingEditor editor) {
        super(editor);
        Resources labels
                = DrawLabels.getResources();
        labels.configureAction(this, ID);
    }

    @Override
    protected void onActionPerformed(@NonNull ActionEvent e, @NonNull DrawingView drawingView) {
        final Set<Figure> figures = drawingView.getSelectedFigures();
        Figure lead = drawingView.getSelectionLead();
        alignVertical(drawingView, figures, lead);
    }

    private void alignVertical(@NonNull DrawingView view, @NonNull Set<Figure> figures, @NonNull Figure lead) {
        if (figures.size() < 2) {
            return;
        }
        DrawingModel model = view.getModel();
        Bounds leadBounds = lead.getLayoutBoundsInWorld();
        double xInWorld = leadBounds.getMinX() + leadBounds.getWidth() * 0.5;
        Point2D xPointInWorld = new Point2D(xInWorld, 0);
        for (Figure f : figures) {
            if (f != lead && f.isEditable()) {
                double desiredX = Transforms.transform(f.getWorldToParent(), xPointInWorld).getX();
                Bounds bounds = f.getLayoutBoundsInParent();
                double actualX = bounds.getMinX() + bounds.getWidth() * 0.5;
                double dx = desiredX - actualX;
                Translate tx = new Translate(dx, 0);
                model.transformInParent(f, tx);
                model.fireLayoutInvalidated(f);
            }
        }
    }
}
