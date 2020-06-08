/*
 * @(#)UndoAction.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.edit;

import javafx.event.ActionEvent;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.app.Activity;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.ApplicationLabels;
import org.jhotdraw8.app.action.AbstractActivityAction;
import org.jhotdraw8.util.Resources;

/**
 * Undoes the last user action.
 *
 * @author Werner Randelshofer
 */
public class UndoAction extends AbstractActivityAction<Activity> {

    private static final long serialVersionUID = 1L;

    public static final String ID = "edit.undo";
    private Resources labels = ApplicationLabels.getResources();

    /*private PropertyChangeListener redoActionPropertyListener = new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            String name = evt.getPropertyName();
            if (name == AbstractAction.NAME) {
                putValue(AbstractAction.NAME, evt.getNewValue());
            } else if (name == "enabled") {
                updateEnabledState();
            }
        }
    };*/

    /**
     * Creates a new instance.
     *
     * @param app  the application
     * @param view the view
     */
    public UndoAction(@NonNull Application app, Activity view) {
        super(app, view, null);
        labels.configureAction(this, ID);
    }

    /*
    protected void updateEnabledState() {
        boolean isEnabled = false;
        Action realAction = getRealUndoAction();
        if (realAction != null && realAction != this) {
            isEnabled = realAction.isEnabled();
        }
        setEnabled(isEnabled);
    }

    @Override
    protected void updateView(Activity oldValue, Activity newValue) {
        super.updateView(oldValue, newValue);
        if (newValue != null && //
                newValue.getActionMap().get(ID) != null && //
                newValue.getActionMap().get(ID) != this) {
            putValue(AbstractAction.NAME, newValue.getActionMap().get(ID).
                    getValue(AbstractAction.NAME));
            updateEnabledState();
        }
    }

    /**
     * Installs listeners on the view object.
     * /
    @Override
    protected void installViewListeners(Activity p) {
        super.installViewListeners(p);
        Action undoActionInView = p.getActionMap().get(ID);
        if (undoActionInView != null && undoActionInView != this) {
            undoActionInView.addPropertyChangeListener(redoActionPropertyListener);
        }
    }

    /**
     * Installs listeners on the view object.
     * /
    @Override
    protected void uninstallViewListeners(Activity p) {
        super.uninstallViewListeners(p);
        Action undoActionInView = p.getActionMap().get(ID);
        if (undoActionInView != null && undoActionInView != this) {
            undoActionInView.removePropertyChangeListener(redoActionPropertyListener);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Action realUndoAction = getRealUndoAction();
        if (realUndoAction != null && realUndoAction != this) {
            realUndoAction.actionPerformed(e);
        }
    }

    @Nullable
    private Action getRealUndoAction() {
        return (getActiveView() == null) ? null : getActiveView().getActionMap().get(ID);
    }*/

    @Override
    protected void onActionPerformed(ActionEvent event, Activity activity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
