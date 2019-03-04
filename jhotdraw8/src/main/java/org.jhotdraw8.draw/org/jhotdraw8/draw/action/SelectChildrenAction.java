/* @(#)SelectChildrenAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.action;

import javafx.event.ActionEvent;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.app.Activity;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.action.Action;
import org.jhotdraw8.draw.DrawLabels;
import org.jhotdraw8.draw.DrawingEditor;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.util.Resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * SelectChildrenAction.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SelectChildrenAction extends AbstractSelectedAction {

    public static final String ID = "edit.selectChildren";

    /**
     * Creates a new instance.
     *
     * @param app    the application
     * @param editor the drawing editor
     */
    public SelectChildrenAction(Application app, DrawingEditor editor) {
        super(app, editor);
        Resources labels = DrawLabels.getResources();
        set(Action.ID_KEY, ID);
        labels.configureAction(this, ID);
    }

    @Override
    protected void handleActionPerformed(ActionEvent e, Activity view) {
        final DrawingView dview = getView();
        if (dview == null) {
            return;
        }
        final List<Figure> figures = new ArrayList<>(dview.getSelectedFigures());
        selectChildren(dview, figures);

    }

    public static void selectChildren(@Nonnull DrawingView view, Collection<Figure> figures) {
        List<Figure> selectedChildren = new ArrayList<>();
        for (Figure f : figures) {
            for (Figure child : f.getChildren()) {
                selectedChildren.add(child);
            }
        }
        view.getSelectedFigures().clear();
        view.getSelectedFigures().addAll(selectedChildren);
    }
}
