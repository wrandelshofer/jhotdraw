/* @(#)BringToFrontAction.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
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
import org.jhotdraw8.app.Project;

/**
 * SelectChildrenAction.
 *
 * @author Werner Randelshofer
 */
public class SelectChildrenAction<V extends Project<V>> extends AbstractSelectedAction<V> {

    public static final String ID = "edit.selectChildren";

    /**
     * Creates a new instance.
     *
     * @param app the application
     * @param editor the drawing editor
     */
    public SelectChildrenAction(Application<V> app, DrawingEditor editor) {
        super(app, editor);
        Resources labels
                = Resources.getResources("org.jhotdraw8.draw.Labels");
        labels.configureAction(this, ID);
    }

    @Override
    protected void onActionPerformed(ActionEvent e) {
        final DrawingView view = getView();
        if (view == null) {
            return;
        }
        final List<Figure> figures = new ArrayList<>(view.getSelectedFigures());
        selectChildren(view, figures);

    }

    public static void selectChildren(DrawingView view, Collection<Figure> figures) {
        List<Figure> selectedChildren=new ArrayList<>();
        for (Figure f : figures) { 
            for (Figure child:f.getChildren()) {
                selectedChildren.add(child);
            }
        }
        view.getSelectedFigures().clear();
        view.getSelectedFigures().addAll(selectedChildren);
    }
}
