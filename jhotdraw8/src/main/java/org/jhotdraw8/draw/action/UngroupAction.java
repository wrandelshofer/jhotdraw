/* @(#)UngroupAction.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.transform.Transform;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.draw.DrawingEditor;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.TransformableFigure;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.util.Resources;
import org.jhotdraw8.app.Project;
import org.jhotdraw8.draw.figure.Grouping;

/**
 * UngroupAction.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class UngroupAction extends AbstractSelectedAction {

    public static final String ID = "edit.ungroup";

    /**
     * Creates a new instance.
     *
     * @param app the application
     * @param editor the drawing editor
     */
    public UngroupAction(Application app, DrawingEditor editor) {
        super(app, editor);
        Resources labels
                = Resources.getResources("org.jhotdraw8.draw.Labels");
        labels.configureAction(this, ID);
    }

    @Override
    protected void handleActionPerformed(ActionEvent e, Project project) {
        final DrawingView view = getView();
        if (view == null) {
            return;
        }
        final LinkedList<Figure> figures = new LinkedList<>(view.getSelectedFigures());
        ungroup(view, figures);

    }

    public static void ungroup(DrawingView view, Collection<Figure> figures) {
        if (figures.isEmpty()) {
            final Alert alert = new Alert(Alert.AlertType.INFORMATION, "Empty selection can not be ungrouped");
            alert.getDialogPane().setMaxWidth(640.0);
            alert.showAndWait();
            return;
        }

        for (Figure f : figures) {
            if (!(f instanceof Grouping)) {
                // FIXME internationalize me
                final Alert alert = new Alert(Alert.AlertType.INFORMATION, "Only groups can be ungrouped");
                alert.getDialogPane().setMaxWidth(640.0);
                alert.showAndWait();
                return;
            }

            if (f != null && (!f.isEditable() || !f.isDecomposable())) {
                // FIXME internationalize me
                final Alert alert = new Alert(Alert.AlertType.INFORMATION, "Only editable and decomposable figures can be ungrouped");
                alert.getDialogPane().setMaxWidth(640.0);
                alert.showAndWait();
                return;
            }
            Figure parent = f.getParent();
            if (parent == null || !parent.isEditable() || !parent.isDecomposable()) {
                // FIXME internationalize me
                final Alert alert = new Alert(Alert.AlertType.INFORMATION, "Only groups in editable and decomposable parents can be ungrouped");
                alert.getDialogPane().setMaxWidth(640.0);
                alert.showAndWait();
                return;
            }
        }

        LinkedHashSet<Figure> newSelection = new LinkedHashSet<>();

        for (Figure f : figures) {
            ungroup(view, f, newSelection);
        }

        view.getSelectedFigures().clear();
        view.getSelectedFigures().addAll(newSelection);
    }

    private static void ungroup(DrawingView view, Figure group, LinkedHashSet<Figure> newSelection) {
        Figure parent = group.getParent();
        if (parent != null && (!parent.isEditable() || !parent.isDecomposable())) {
            // FIXME internationalize me
            final Alert alert = new Alert(Alert.AlertType.INFORMATION, "Only groups in editable and decomposable parents can be ungrouped");
            alert.getDialogPane().setMaxWidth(640.0);
            alert.showAndWait();
            return;
        }
        DrawingModel model = view.getModel();

        Transform groupTransform = group.getLocalToParent();
        if (groupTransform.isIdentity()) {
            groupTransform = null;
        }
        boolean isGroupTranslateScaleRotateOnly = group.get(TransformableFigure.TRANSFORMS) == null || group.get(TransformableFigure.TRANSFORMS).isEmpty();

        int index = parent.getChildren().indexOf(group);
        newSelection.addAll(group.getChildren());
        for (Figure child : new ArrayList<Figure>(group.getChildren())) {
            model.insertChildAt(child, parent, index++);

            if (groupTransform != null) {
                model.transformInParent(child, groupTransform);
                /*
                List<Transform> childTransforms = child.get(TransformableFigure.TRANSFORMS);
                if (!isGroupTranslateScaleRotateOnly||childTransforms != null && !childTransforms.isEmpty()) {
                    ArrayList<Transform> newTransforms = new ArrayList<>();
                    newTransforms.addChild(groupTransform);
                    if (childTransforms != null) {
                        newTransforms.addAll(childTransforms);
                    }
                   model.set(child,TransformableFigure.TRANSFORMS, newTransforms);
                } else {
                    ArrayList<Transform> newTransforms = new ArrayList<>();
                    newTransforms.addChild(groupTransform);
                   model.set(child,TransformableFigure.TRANSFORMS, newTransforms);
                }*/
            }
        }

        model.removeFromParent(group);
    }
}
