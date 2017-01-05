/*
 * @(#)UndoAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw8.app.action.edit;

import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.action.AbstractViewAction;
import org.jhotdraw8.util.Resources;
import org.jhotdraw8.app.Project;

/**
 * Undoes the last user action.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class UndoAction<V extends Project<V>> extends AbstractViewAction<V> {

    private static final long serialVersionUID = 1L;

    public static final String ID = "edit.undo";
    private Resources labels = Resources.getResources("org.jhotdraw8.app.Labels");

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
     * @param app the application
     * @param view the view
     */
    public UndoAction(Application<V> app, V view) {
        super(app, view);
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
    protected void updateView(Project oldValue, Project newValue) {
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
    protected void installViewListeners(Project p) {
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
    protected void uninstallViewListeners(Project p) {
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
    protected void onActionPerformed(javafx.event.ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
