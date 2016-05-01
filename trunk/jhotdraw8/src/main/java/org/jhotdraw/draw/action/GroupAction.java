/* @(#)GroupAction.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Supplier;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import org.jhotdraw.app.Application;
import org.jhotdraw.collection.IterableTree;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Layer;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.model.DrawingModel;
import org.jhotdraw.util.Resources;

/**
 * GroupAction.
 *
 * @author Werner Randelshofer
 */
public class GroupAction extends AbstractSelectedAction {

    public static final String ID = "edit.group";
    public final Supplier<Figure> groupFactory;

    /**
     * Creates a new instance.
     *
     * @param app the application
     * @param editor the drawing editor
     */
    public GroupAction(Application app, DrawingEditor editor, Supplier<Figure> groupFactory) {
        super(app, editor);
        Resources labels
                = Resources.getResources("org.jhotdraw.draw.Labels");
        labels.configureAction(this, ID);
        this.groupFactory = groupFactory;
        if (groupFactory == null) {
            addDisabler("groupFactory==null");
        }
    }

    @Override
    protected void onActionPerformed(ActionEvent e) {
        final DrawingView view = getView();
        if (view == null) {
            return;
        }
        final LinkedList<Figure> figures = new LinkedList<>(view.getSelectedFigures());
        group(view, figures, groupFactory);

    }

    public static void group(DrawingView view, Collection<Figure> figures, Supplier<Figure> groupFactory) {
        // We don't add an empty group
        if (figures.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "Empty selection can not be grouped").showAndWait();
            return;
        }
        Figure first = figures.iterator().next();

        Drawing drawing = view.getDrawing();
        for (Figure child : figures) {
            if (child instanceof Layer) {
                // FIXME internationalize me
                new Alert(Alert.AlertType.INFORMATION, "Layers can not be grouped").showAndWait();
                return;
            }
            if (child.getDrawing() != drawing) {
                // FIXME internationalize me
                new Alert(Alert.AlertType.INFORMATION, " Only figures in primary drawing can be grouped").showAndWait();
                return;
            }
            Figure parent = child.getParent();
            if (parent != null && (!parent.isEditable() || !parent.isDecomposable())) {
                // FIXME internationalize me
                new Alert(Alert.AlertType.INFORMATION, "Only figures in editable and decomposable parents can be grouped").showAndWait();
                return;
            }
        }

        Figure parent = first.getParent();
        DrawingModel model = view.getModel();
        Figure group = groupFactory.get();
        model.addChildTo(group, parent);

        // Note: we iterate here over all figures because we must add
        //       the selected figures from back to front to the group
        for (Figure child : IterableTree.toList(drawing.breadthFirstIterable())) {
            if (!figures.contains(child)) {
                continue;
            }
            model.addChildTo(child, group);
        }

        view.getSelectedFigures().clear();
        view.getSelectedFigures().add(group);
    }
}
