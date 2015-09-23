/* @(#)AbstractFocusOwnerAction.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.app.action;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.Scene;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.View;

/**
 * AbstractFocusOwnerAction.
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractFocusOwnerAction extends AbstractApplicationAction {

    private static final long serialVersionUID = 1L;
    private Node target = null;
    private final ChangeListener<View> activeViewListener = (observable, oldValue, newValue) -> {
        disabled.unbind();
        if (newValue == null||newValue.getNode()==null) {
            disabled.set(true);
        } else {
            Scene s = newValue.getNode().getScene();
            BooleanBinding binding = disablers.emptyProperty().not().or(app.disabledProperty());
            if (target!=null) {
                binding = binding.or(s.focusOwnerProperty().isNotEqualTo(target));
            } else {
               binding = binding.or( s.focusOwnerProperty().isNull());
            }
            disabled.bind(binding);
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
    public AbstractFocusOwnerAction(Application app, Node target) {
       super(app);
        this.target=target;
            
        
        app.activeViewProperty().addListener(activeViewListener);
        activeViewListener.changed(null, null,app==null?null: app.getActiveView());
        
    }
}
