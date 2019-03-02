/* @(#)SendToBackAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.action;

import javafx.event.ActionEvent;
import org.jhotdraw8.app.Activity;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.ApplicationLabels;
import org.jhotdraw8.draw.DrawingEditor;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.util.Resources;

import java.util.Collection;
import java.util.LinkedList;

/**
 * SendToBackAction.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SendToBackAction extends AbstractSelectedAction {

    public static final String ID = "edit.sendToBack";

    /**
     * Creates a new instance.
     *
     * @param app    the application
     * @param editor the drawing editor
     */
    public SendToBackAction(Application app, DrawingEditor editor) {
        super(app, editor);
        Resources labels
                = ApplicationLabels.getResources();
        labels.configureAction(this, ID);
    }

    @Override
    protected void handleActionPerformed(ActionEvent e, Activity view) {
        final DrawingView dview = getView();
        if (dview == null) {
            return;
        }
        final LinkedList<Figure> figures = new LinkedList<>(dview.getSelectedFigures());
        sendToBack(dview, figures);

    }

    public void sendToBack(DrawingView view, Collection<Figure> figures) {
        DrawingModel model = view.getModel();
        for (Figure child : figures) {
            Figure parent = child.getParent();
            if (parent != null && parent.isEditable() && parent.isDecomposable()) {
                assert parent.isAllowsChildren();
                model.insertChildAt(child, parent, 0);
            }
        }
    }
}
