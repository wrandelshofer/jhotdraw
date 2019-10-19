/*
 * @(#)DistributeHorizontallyAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.action;

import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import javafx.scene.transform.Translate;
import org.jhotdraw8.app.Activity;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.draw.DrawLabels;
import org.jhotdraw8.draw.DrawingEditor;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.util.Resources;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DistributeHorizontallyAction extends AbstractSelectedAction {

    public static final String ID = "edit.distributeHorizontally";

    /**
     * Creates a new instance.
     *
     * @param app    the application
     * @param editor the drawing editor
     */
    public DistributeHorizontallyAction(Application app, DrawingEditor editor) {
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
        distributeHorizontally(drawingView, figures);
    }

    /**
     * Distributes the given figure horizontally by their centers on the x-axis.
     *
     * @param view    the drawing view
     * @param figures the figures to be distributed horizontally
     */
    private void distributeHorizontally(DrawingView view, Set<Figure> figures) {
        if (figures.size() < 3) {
            return;
        }
        DrawingModel model = view.getModel();

        // Find min and and max center
        double maxX = Double.NEGATIVE_INFINITY;
        double minX = Double.POSITIVE_INFINITY;
        List<Map.Entry<Double, Figure>> list = new ArrayList<>();
        Outer:
        for (Figure f : figures) {
            for (Figure subject : f.getLayoutSubjects()) {
                if (figures.contains(subject)) {
                    // Filter out figures that base their layout on figures in the set.
                    continue Outer;
                }
            }

            Bounds b = f.getBoundsInWorld();
            double cx = b.getMinX() + b.getWidth() * 0.5;
            list.add(new AbstractMap.SimpleEntry<>(cx, f));
            maxX = Math.max(maxX, cx);
            minX = Math.min(minX, cx);
        }

        // Sort figures by their centers pn the x-axis
        // (Without sorting, we would distribute the list by the sequence
        // they were selected).
        list.sort(Comparator.comparingDouble(Map.Entry::getKey));

        // Distribute the figures by their centers
        double extent = maxX - minX;
        double count = figures.size();
        double index = 0;
        for (Map.Entry<Double, Figure> e : list) {
            Figure f = e.getValue();
            Bounds b = f.getBoundsInWorld();
            double oldcx = b.getMinX() + b.getWidth() * 0.5;
            double newcx = minX + extent * index / (count - 1);
            double dx = newcx - oldcx;
            if (dx != 0) {
                Translate tx = new Translate(dx, 0);
                model.transformInParent(f, tx);
                model.fireLayoutInvalidated(f);
            }

            index++;
        }
    }
}
