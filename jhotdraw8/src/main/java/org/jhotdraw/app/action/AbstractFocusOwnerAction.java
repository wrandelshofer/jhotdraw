/* @(#)AbstractFocusOwnerAction.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.app.action;

import java.util.Optional;
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
    private Optional<Node> target = Optional.empty();
    private final ChangeListener<View> activeViewListener = (observable, oldValue, newValue) -> {
        disabled.unbind();
        if (newValue == null||newValue.getNode()==null) {
            disabled.set(true);
        } else {
            Scene s = newValue.getNode().getScene();
            BooleanBinding binding = disablers.emptyProperty().not().or(app.disabledProperty());
            if (target.isPresent()) {
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
        this(app,Optional.empty());
    }
    /** Creates a new instance.
     * @param app the application 
    * @param target the target node
    */
    public AbstractFocusOwnerAction(Application app,Optional< Node> target) {
       super(app);
       if (target == null) {
           throw new IllegalArgumentException("target is null");
       }
        this.target=target;
            
        
        app.activeViewProperty().addListener(activeViewListener);
        activeViewListener.changed(null, null, app.getActiveView().orElse(null));
        
    }
}
