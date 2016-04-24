/* @(#)UngroupAction.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.transform.Transform;
import org.jhotdraw.app.Application;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Layer;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.GroupFigure;
import org.jhotdraw.draw.figure.TransformableFigure;
import org.jhotdraw.draw.model.DrawingModel;
import org.jhotdraw.util.Resources;

/**
 * UngroupAction.
 *
 * @author Werner Randelshofer
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
                = Resources.getResources("org.jhotdraw.draw.Labels");
        labels.configureAction(this, ID);
    }

    @Override
    protected void onActionPerformed(ActionEvent e) {
        final DrawingView view = getView();
        if (view == null) {
            return;
        }
        final LinkedList<Figure> figures = new LinkedList<>(view.getSelectedFigures());
        ungroup(view, figures);

    }

    public static void ungroup(DrawingView view, Collection<Figure> figures) {
        if (figures.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "Empty selection can not be ungrouped").showAndWait();
            return;
        }

        for (Figure f : figures) {
            if (f instanceof Layer) {
                // FIXME internationalize me
                new Alert(Alert.AlertType.INFORMATION, "Layers can not be ungrouped").showAndWait();
                return;
            }
            
            if (f != null && (!f.isEditable() || !f.isDecomposable())) {
                // FIXME internationalize me
                new Alert(Alert.AlertType.INFORMATION, "Only editable and decomposable figures can be ungrouped").showAndWait();
                return;
            }
            Figure parent = f.getParent();
            if (parent == null || !parent.isEditable() || !parent.isDecomposable()) {
                // FIXME internationalize me
                new Alert(Alert.AlertType.INFORMATION, "Only groups in editable and decomposable parents can be ungrouped").showAndWait();
                return;
            }
        }

        LinkedHashSet<Figure> newSelection = new LinkedHashSet<>();

        for (Figure f : figures) {
                ungroup(view,f, newSelection);
        }
        
        view.getSelectedFigures().clear();
        view.getSelectedFigures().addAll(newSelection);
    }

    private static void ungroup(DrawingView view, Figure group, LinkedHashSet<Figure> newSelection) {
        Figure parent = group.getParent();
        if (parent != null && (!parent.isEditable() || !parent.isDecomposable())) {
            // FIXME internationalize me
            new Alert(Alert.AlertType.INFORMATION, "Only groups in editable and decomposable parents can be ungrouped").showAndWait();
            return;
        }
        DrawingModel model = view.getModel();

        Transform groupTransform = group.getLocalToParent();
        if (groupTransform.isIdentity()) {groupTransform=null;}
        
        int index = parent.getChildren().indexOf(group);
        newSelection.addAll(group.getChildren());
        for (Figure child : new ArrayList<Figure>(group.getChildren())) {
            model.insertChildAt(child, parent, index++);
            
            if (groupTransform!=null) {
                List<Transform> transforms = child.get(TransformableFigure.TRANSFORM);
                ArrayList<Transform> newTransforms = new ArrayList<>();
                newTransforms.add(groupTransform);
                if (transforms!=null) newTransforms.addAll(transforms);
                child.set(TransformableFigure.TRANSFORM, newTransforms);
            }
            
        }

        model.removeFromParent(group);
    }
}
