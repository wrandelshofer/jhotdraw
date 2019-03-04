/* @(#)RemoveTransformationsAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.action;

import javafx.event.ActionEvent;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.app.Activity;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.draw.DrawLabels;
import org.jhotdraw8.draw.DrawingEditor;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.TransformableFigure;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.util.Resources;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

/**
 * RemoveTransformationsAction.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class RemoveTransformationsAction extends AbstractSelectedAction {

    public static final String ID = "edit.removeTransformations";

    /**
     * Creates a new instance.
     *
     * @param app    the application
     * @param editor the drawing editor
     */
    public RemoveTransformationsAction(@Nonnull Application app, DrawingEditor editor) {
        super(app, editor);
        Resources labels
                = DrawLabels.getResources();
        labels.configureAction(this, ID);
    }

    @Override
    protected void handleActionPerformed(ActionEvent e, Activity view) {
        final DrawingView dview = getView();
        if (dview == null) {
            return;
        }
        final LinkedList<Figure> figures = new LinkedList<>(dview.getSelectedFigures());
        removeTransformations(dview, figures);

    }

    public static void removeTransformations(DrawingView view, Collection<Figure> figures) {
        Set<Key<?>> keys = TransformableFigure.getDeclaredKeys();

        DrawingModel model = view.getModel();
        for (Figure child : figures) {
            if (child instanceof TransformableFigure) {
                for (Key<?> k : keys) {
                    model.remove(child, k);
                }
            }
        }
    }
}
