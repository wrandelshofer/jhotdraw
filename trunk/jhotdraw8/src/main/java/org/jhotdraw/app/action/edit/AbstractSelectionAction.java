/*
 * @(#)AbstractSelectionAction.java
 * 
 * Copyright (c) 2010 The authors and contributors of JHotDraw.
 * 
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.app.action.edit;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.Scene;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.action.AbstractApplicationAction;
import org.jhotdraw.app.ProjectView;

/**
 * {@code AbstractSelectionAction} acts on the selection of a target component.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractSelectionAction extends AbstractApplicationAction {

    private static final long serialVersionUID = 1L;
    private Node target;
    private final ChangeListener<ProjectView> activeViewListener = (observable, oldValue, newValue) -> {
        disabled.unbind();
        if (newValue == null || newValue.getNode()== null) {
            disabled.set(true);
        } else {
            Scene s = newValue.getNode().getScene();
            if (target==null) {
            disabled.bind(
                    s.focusOwnerProperty().isNull().or(app.disabledProperty()).or(newValue.disabledProperty()).or(Bindings.isNotEmpty(disablers)));
            } else {
            disabled.bind(
                    s.focusOwnerProperty().isNotEqualTo(target).or(app.disabledProperty()).or(newValue.disabledProperty()).or(Bindings.isNotEmpty(disablers)));
            }
        }
    };

    /** Creates a new instance.
     * @param app the application */
    public AbstractSelectionAction(Application app) {
        this(app, null);
    }
    /** Creates a new instance.
     * @param app the application 
    * @param target the target node
    */
    public AbstractSelectionAction(Application app, Node target) {
        super(app);
        this.target=target;
            
        
        app.activeViewProperty().addListener(activeViewListener);
        activeViewListener.changed(null, null, app.getActiveView());
        
    }
}
