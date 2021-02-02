/*
 * @(#)GroupAction.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.action;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.Iterators;
import org.jhotdraw8.draw.DrawLabels;
import org.jhotdraw8.draw.DrawingEditor;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.Layer;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.util.Resources;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Supplier;

/**
 * GroupAction.
 *
 * @author Werner Randelshofer
 */
public class GroupAction extends AbstractDrawingViewAction {

    public static final String ID = "edit.group";
    public static final String COMBINE_PATHS_ID = "edit.combinePaths";
    @Nullable
    public final Supplier<Figure> groupFactory;

    /**
     * Creates a new instance.
     *
     * @param editor       the drawing editor
     * @param groupFactory the group factory
     */
    public GroupAction(@NonNull DrawingEditor editor, Supplier<Figure> groupFactory) {
        this(ID, editor, groupFactory);
    }

    public GroupAction(String id, @NonNull DrawingEditor editor, @Nullable Supplier<Figure> groupFactory) {
        super(editor);
        Resources labels
                = DrawLabels.getResources();
        labels.configureAction(this, id);
        this.groupFactory = groupFactory;
        if (groupFactory == null) {
            addDisabler("groupFactory==null");
        }
    }

    @Override
    protected void onActionPerformed(@NonNull ActionEvent e, @NonNull DrawingView drawingView) {
        final LinkedList<Figure> figures = new LinkedList<>(drawingView.getSelectedFigures());
        group(drawingView, figures, groupFactory);

    }

    public static void group(@NonNull DrawingView view, @NonNull Collection<Figure> figures, @NonNull Supplier<Figure> groupFactory) {
        // We don't addChild an empty group
        if (figures.isEmpty()) {
            final Alert alert = new Alert(Alert.AlertType.INFORMATION, "Empty selection can not be grouped");
            alert.getDialogPane().setMaxWidth(640.0);
            alert.showAndWait();
            return;
        }
        Figure first = figures.iterator().next();

        Drawing drawing = view.getDrawing();
        for (Figure child : figures) {
            if (child instanceof Layer) {
                // FIXME internationalize me
                final Alert alert = new Alert(Alert.AlertType.INFORMATION, "Layers can not be grouped");
                alert.getDialogPane().setMaxWidth(640.0);
                alert.showAndWait();
                return;
            }
            if (child.getDrawing() != drawing) {
                // FIXME internationalize me
                final Alert alert = new Alert(Alert.AlertType.INFORMATION, " Only figures in primary drawing can be grouped");
                alert.getDialogPane().setMaxWidth(640.0);
                alert.showAndWait();
                return;
            }
            Figure parent = child.getParent();
            if (parent != null && (!parent.isEditable() || !parent.isDecomposable())) {
                // FIXME internationalize me
                final Alert alert = new Alert(Alert.AlertType.INFORMATION, "Only figures in editable and decomposable parents can be grouped");
                alert.getDialogPane().setMaxWidth(640.0);
                alert.showAndWait();
                return;
            }
        }

        Figure parent = first.getParent();
        DrawingModel model = view.getModel();
        Figure group = groupFactory.get();
        model.addChildTo(group, parent);

        // Note: we iterate here over all figures because we must addChild
        //       the selected figures from back to front to the group
        for (Figure child : Iterators.toList(drawing.breadthFirstIterable())) {
            if (!figures.contains(child)) {
                continue;
            }
            model.addChildTo(child, group);
        }

        view.getSelectedFigures().clear();
        view.getSelectedFigures().add(group);
    }
}
