/*
 * @(#)SelectSameAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.action;

import javafx.collections.ObservableSet;
import javafx.event.ActionEvent;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.app.Activity;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.action.Action;
import org.jhotdraw8.draw.DrawLabels;
import org.jhotdraw8.draw.DrawingEditor;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.util.Resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * SelectSameAction.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SelectSameAction extends AbstractSelectedAction {

    public static final String ID = "edit.selectSame";

    /**
     * Creates a new instance.
     *
     * @param app    the application
     * @param editor the drawing editor
     */
    public SelectSameAction(Application app, DrawingEditor editor) {
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
        ObservableSet<Figure> selection = dview.getSelectedFigures();
        selectSame(dview, selection.isEmpty() ? null : selection.iterator().next());

    }

    public static void selectSame(@Nonnull DrawingView view, @Nullable Figure prototype) {
        if (prototype == null) {
            return;
        }

        String stype = prototype.getTypeSelector();
        List<String> sclass = prototype.getStyleClass();

        List<Figure> selectedSame = new ArrayList<>();
        for (Figure f : view.getDrawing().preorderIterable()) {
            if (f.isSelectable() && f.isShowing()) {
                if (Objects.equals(f.getTypeSelector(), stype)
                        && Objects.equals(f.getStyleClass(), sclass) && f != prototype) {
                    selectedSame.add(f);
                }
            }
        }
        view.getSelectedFigures().clear();
        view.getSelectedFigures().add(prototype);
        view.getSelectedFigures().addAll(selectedSame);
    }
}
