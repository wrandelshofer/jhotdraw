/*
 * @(#)AddToGroupAction.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.action;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.draw.DrawLabels;
import org.jhotdraw8.draw.DrawingEditor;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.Grouping;
import org.jhotdraw8.draw.figure.StyleableFigure;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.util.Resources;

import java.util.ArrayList;
import java.util.List;

/**
 * AddToGroupAction.
 *
 * @author Werner Randelshofer
 */
public class AddToGroupAction extends AbstractDrawingViewAction {

    public static final String ID = "edit.addToGroup";

    /**
     * Creates a new instance.
     *
     * @param editor the drawing editor
     */
    public AddToGroupAction(@NonNull DrawingEditor editor) {
        super(editor);
        Resources labels = DrawLabels.getResources();
        labels.configureAction(this, ID);
    }

    @Override
    protected void onActionPerformed(@NonNull ActionEvent e, @NonNull DrawingView drawingView) {
        final List<Figure> figures = new ArrayList<>(drawingView.getSelectedFigures());
        addToGroup(drawingView, figures);

    }

    public static void addToGroup(@NonNull DrawingView view, @NonNull List<Figure> figures) {
        if (figures.size() < 2) {
            // FIXME internationalize me
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "You must select the figures and a group to which the figures should be added");
            alert.getDialogPane().setMaxWidth(640.0);
            alert.showAndWait();
            return;
        }
        Figure lead = figures.get(figures.size() - 1);
        if (!(lead instanceof Grouping) && !lead.isAllowsChildren() || !lead.isDecomposable()) {
            // FIXME internationalize me
            final Alert alert = new Alert(Alert.AlertType.INFORMATION, "The last figure in the selection must be a group.");
            alert.getDialogPane().setMaxWidth(640.0);
            alert.showAndWait();
            return;
        }
        if (!lead.isEditable()) {
            // FIXME internationalize me
            final Alert alert = new Alert(Alert.AlertType.INFORMATION, "The last figure in the selection is not editable.");
            alert.getDialogPane().setMaxWidth(640.0);
            alert.showAndWait();
            return;
        }

        List<Figure> reparentableFigures = new ArrayList<>();
        for (int i = 0, n = figures.size() - 1; i < n; i++) {
            Figure f = figures.get(i);
            if (f.isEditable() && f.isSuitableParent(lead) && (f.getParent() != null && f.getParent().isEditable() && f.getParent().isDecomposable())) {
                reparentableFigures.add(f);
            } else {
                if ((f instanceof StyleableFigure) && f.get(StyleableFigure.ID) != null) {
                    // FIXME internationalize me
                    final Alert alert = new Alert(Alert.AlertType.INFORMATION, "The figure with id \"" + f.get(StyleableFigure.ID) + "\" can not be added to the group.");
                    alert.getDialogPane().setMaxWidth(640.0);

                    alert.showAndWait();
                } else {
                    // FIXME internationalize me
                    final Alert alert = new Alert(Alert.AlertType.INFORMATION, "One of the selected figures can not be added to the group.");
                    alert.getDialogPane().setMaxWidth(640.0);
                    alert.showAndWait();
                }
                return;
            }
        }

        DrawingModel m = view.getModel();
        for (Figure f : reparentableFigures) {
            m.addChildTo(f, lead);
        }
    }
}
