package org.jhotdraw8.draw.action;

import javafx.event.ActionEvent;
import javafx.geometry.Point2D;
import javafx.scene.transform.Translate;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.Activity;
import org.jhotdraw8.draw.DrawingEditor;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.geom.Transforms;
import org.jhotdraw8.util.Resources;

import java.util.Set;

public class AlignLeftAction extends AbstractSelectedAction {

    public static final String ID = "edit.alignLeft";

    /**
     * Creates a new instance.
     *
     * @param app    the application
     * @param editor the drawing editor
     */
    public AlignLeftAction(Application app, DrawingEditor editor) {
        super(app, editor);
        Resources labels
                = Resources.getResources("org.jhotdraw8.draw.Labels");
        labels.configureAction(this, ID);
    }

    @Override
    protected void handleActionPerformed(ActionEvent event, Activity view) {
        final DrawingView drawingView = getView();
        if (drawingView == null) {
            return;
        }
        final Set<Figure> figures = drawingView.getSelectedFigures();
        Figure lead = drawingView.getSelectionLead();
        alignLeft(drawingView, figures, lead);
    }

    private void alignLeft(DrawingView view, Set<Figure> figures, Figure lead) {
        if (figures.size() < 2) {
            return;
        }
        DrawingModel model = view.getModel();
        double xInWorld = lead.getBoundsInWorld().getMinX();
        Point2D xPointInWorld = new Point2D(xInWorld, 0);
        for (Figure f : figures) {
            if (f != lead && f.isEditable()) {
                double desiredX = Transforms.transform(f.getWorldToParent(), xPointInWorld).getX();
                double actualX = f.getBoundsInParent().getMinX();
                double dx = desiredX - actualX;
                Translate tx = new Translate(dx, 0);
                model.transformInParent(f, tx);
                model.fireLayoutInvalidated(f);
            }
        }
    }
}
