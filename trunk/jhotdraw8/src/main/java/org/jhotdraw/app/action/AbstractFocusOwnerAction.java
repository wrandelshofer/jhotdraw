/* @(#)AbstractFocusOwnerAction.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.app.action;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.Scene;
import javax.annotation.Nullable;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.View;

/**
 * AbstractFocusOwnerAction.
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractFocusOwnerAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    @Nullable
    protected Application app;
    @Nullable
    private Node target;
    private final ChangeListener<View> activeViewListener = (observable, oldValue, newValue) -> {
        disabled.unbind();
        if (newValue == null) {
            disabled.set(true);
        } else {
            Scene s = newValue.getNode().getScene();
            if (target==null) {
            disabled.bind(
                    s.focusOwnerProperty().isNull().or(app.disabledProperty()).or(newValue.disabledProperty()).or(disablers.emptyProperty().not()));
            } else {
            disabled.bind(
                    s.focusOwnerProperty().isNotEqualTo(target).or(app.disabledProperty()).or(newValue.disabledProperty()).or(disablers.emptyProperty().not()));
            }
        }
    };

    /** Creates a new instance.
     * @param app the application */
    public AbstractFocusOwnerAction(Application app) {
        this(app,null);
    }
    /** Creates a new instance.
     * @param app the application 
    * @param target the target node
    */
    public AbstractFocusOwnerAction(Application app,@Nullable Node target) {
        this.app = app;
        this.target=target;
            
        
        app.activeViewProperty().addListener(activeViewListener);
        activeViewListener.changed(null, null, app.getActiveView());
        
    }

    public Application getApplication() {
        return app;
    }
}
