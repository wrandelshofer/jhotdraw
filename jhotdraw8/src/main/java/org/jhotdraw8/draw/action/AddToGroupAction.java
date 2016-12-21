/* @(#)BringToFrontAction.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.action;

import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.ProjectView;
import org.jhotdraw8.draw.DrawingEditor;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.Group;
import org.jhotdraw8.draw.figure.StyleableFigure;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.util.Resources;

/**
 * AddToGroupAction.
 *
 * @author Werner Randelshofer
 */
public class AddToGroupAction<V extends ProjectView<V>> extends AbstractSelectedAction<V> {

    public static final String ID = "edit.addToGroup";

    /**
     * Creates a new instance.
     *
     * @param app the application
     * @param editor the drawing editor
     */
    public AddToGroupAction(Application<V> app, DrawingEditor editor) {
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
        addToGroup(view, figures);

    }

    public static void addToGroup(DrawingView view, List<Figure> figures) {
        if (figures.size() < 2) {
            // FIXME internationalize me
            new Alert(Alert.AlertType.INFORMATION, "You must select a group and the figures that should be added").showAndWait();
            return;
        }
        Figure anchor = figures.get(0);
        if (!(anchor instanceof Group) && !anchor.isAllowsChildren() || !anchor.isDecomposable()) {
            // FIXME internationalize me
            new Alert(Alert.AlertType.INFORMATION, "The first figure in the selection must be a group.").showAndWait();
            return;
        }
        if (!anchor.isEditable()) {
            // FIXME internationalize me
            new Alert(Alert.AlertType.INFORMATION, "The first figure in the selection is not editable.").showAndWait();
            return;
        }

        List<Figure> reparentableFigures = new ArrayList<>();
        for (int i = 1, n = figures.size(); i < n; i++) {
            Figure f = figures.get(i);
            if (f.isEditable() && f.isSuitableParent(anchor) && (f.getParent() != null && f.getParent().isEditable() && f.getParent().isDecomposable())) {
                reparentableFigures.add(f);
            } else {
                if ((f instanceof StyleableFigure)&&f.get(StyleableFigure.ID)!=null) {
                // FIXME internationalize me
                    new Alert(Alert.AlertType.INFORMATION, "The figure with id \""+f.get(StyleableFigure.ID)+"\" can not be added to the group.").showAndWait();
                } else {
                // FIXME internationalize me
                    new Alert(Alert.AlertType.INFORMATION, "One of the selected figures can not be added to the group.").showAndWait();
                }
                return;
            }
        }

        DrawingModel m = view.getModel();
        for (Figure f : reparentableFigures) {
            m.addChildTo(f, anchor);
        }
    }
}
