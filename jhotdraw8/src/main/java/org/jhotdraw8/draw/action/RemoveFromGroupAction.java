/* @(#)BringToFrontAction.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.action;

import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javax.annotation.Nonnull;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.draw.DrawingEditor;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.Layer;
import org.jhotdraw8.draw.figure.StyleableFigure;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.util.Resources;
import org.jhotdraw8.draw.figure.Grouping;
import org.jhotdraw8.app.ViewController;

/**
 * AddToGroupAction.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class RemoveFromGroupAction extends AbstractSelectedAction {

    public static final String ID = "edit.removeFromGroup";

    /**
     * Creates a new instance.
     *
     * @param app the application
     * @param editor the drawing editor
     */
    public RemoveFromGroupAction(Application app, DrawingEditor editor) {
        super(app, editor);
        Resources labels
                = Resources.getResources("org.jhotdraw8.draw.Labels");
        labels.configureAction(this, ID);
    }

    @Override
    protected void handleActionPerformed(ActionEvent e, ViewController view) {
        final DrawingView dview = getView();
        if (dview == null) {
            return;
        }
        final List<Figure> figures = new ArrayList<>(dview.getSelectedFigures());
        removeFromGroup(dview, figures);

    }

    public void removeFromGroup(@Nonnull DrawingView view, List<Figure> figures) {
        List<Figure> reparentableFigures = new ArrayList<>();
        for (Figure f : figures) {
            Layer layer = f.getAncestor(Layer.class);
            if (layer.isEditable()) {
                if (f.getParent() != null && f.getParent().isDecomposable()
                        && f.getParent().isEditable() && (f.getParent() instanceof Grouping)) {
                    if (f.isEditable()) {
                        reparentableFigures.add(f);
                    } else {
                        if ((f instanceof StyleableFigure) && f.get(StyleableFigure.ID) != null) {
                            // FIXME internationalize me
                            final Alert alert = new Alert(Alert.AlertType.INFORMATION, "The figure with id \"" + f.get(StyleableFigure.ID) + "\" can not be removed from the group.");
                            alert.getDialogPane().setMaxWidth(640.0);
                            alert.showAndWait();
                        } else {
                            // FIXME internationalize me
                            final Alert alert = new Alert(Alert.AlertType.INFORMATION, "One of the selected figures can not be removed from the group.");
                            alert.getDialogPane().setMaxWidth(640.0);
                            alert.showAndWait();
                        }
                        return;
                    }
                } else {
                    if ((f instanceof StyleableFigure) && f.get(StyleableFigure.ID) != null) {
                        // FIXME internationalize me
                        final Alert alert = new Alert(Alert.AlertType.INFORMATION, "The figure with id \"" + f.get(StyleableFigure.ID) + "\" is not inside an editable group.");
                        alert.getDialogPane().setMaxWidth(640.0);
                        alert.showAndWait();
                    } else {
                        // FIXME internationalize me
                        final Alert alert = new Alert(Alert.AlertType.INFORMATION, "One of the selected figures is not inside an editable group.");
                        alert.getDialogPane().setMaxWidth(640.0);
                        alert.showAndWait();
                    }
                    return;
                }
            }
        }

        DrawingModel m = view.getModel();
        for (Figure f : reparentableFigures) {
            Layer layer = f.getAncestor(Layer.class);
            m.addChildTo(f, layer);
        }
    }
}
