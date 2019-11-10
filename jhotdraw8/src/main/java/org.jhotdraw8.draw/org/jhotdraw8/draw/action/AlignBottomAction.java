/*
 * @(#)AlignBottomAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.action;

import javafx.event.ActionEvent;
import javafx.geometry.Point2D;
import javafx.scene.transform.Translate;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.app.Activity;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.draw.DrawLabels;
import org.jhotdraw8.draw.DrawingEditor;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.geom.Transforms;
import org.jhotdraw8.util.Resources;

import java.util.Set;

public class AlignBottomAction extends AbstractSelectedAction {

    public static final String ID = "edit.alignBottom";

    /**
     * Creates a new instance.
     *
     * @param app    the application
     * @param editor the drawing editor
     */
    public AlignBottomAction(@NonNull Application app, DrawingEditor editor) {
        super(app, editor);
        Resources labels
                = DrawLabels.getResources();
        labels.configureAction(this, ID);
    }

    @Override
    protected void handleActionPerformed(ActionEvent event, Activity activity) {
        final DrawingView drawingView = getView();
        if (drawingView == null) {
            return;
        }
        final Set<Figure> figures = drawingView.getSelectedFigures();
        Figure lead = drawingView.getSelectionLead();
        alignBottom(drawingView, figures, lead);
    }

    private void alignBottom(@NonNull DrawingView view, @NonNull Set<Figure> figures, @NonNull Figure lead) {
        if (figures.size() < 2) {
            return;
        }
        DrawingModel model = view.getModel();
        double yInWorld = lead.getBoundsInWorld().getMaxY();
        Point2D yPointInWorld = new Point2D(0, yInWorld);
        for (Figure f : figures) {
            if (f != lead && f.isEditable()) {
                double desiredY = Transforms.transform(f.getWorldToParent(), yPointInWorld).getY();
                double actualY = f.getBoundsInParent().getMaxY();
                double dy = desiredY - actualY;
                Translate tx = new Translate(0, dy);
                model.transformInParent(f, tx);
                model.fireLayoutInvalidated(f);
            }
        }
    }
}
