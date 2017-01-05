/* @(#)AbstractFocusOwnerAction.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.app.action;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.Scene;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.Project;

/**
 * AbstractFocusOwnerAction.
 *
 * @author Werner Randelshofer
 * @version $Id: AbstractFocusOwnerAction.java 1169 2016-12-11 12:51:19Z
 * rawcoder $
 */
public abstract class AbstractFocusOwnerAction<V extends Project<V>> extends AbstractApplicationAction<V> {

    private static final long serialVersionUID = 1L;
    private Node target = null;

    private final ChangeListener<V> activeViewListener = (observable, oldValue, newValue) -> {
        disabled.unbind();
        if (newValue == null || newValue.getNode() == null) {
            disabled.set(true);
        } else {
            Scene s = newValue.getNode().getScene();
            BooleanBinding binding = Bindings.isNotEmpty(disablers).or(app.disabledProperty());
            if (target != null) {
                binding = binding.or(s.focusOwnerProperty().isNotEqualTo(target));
            } else {
                binding = binding.or(s.focusOwnerProperty().isNull());
            }
            disabled.bind(binding);
        }
    };

    /**
     * Creates a new instance.
     *
     * @param app the application
     */
    public AbstractFocusOwnerAction(Application<V> app) {
        this(app, null);
    }

    /**
     * Creates a new instance.
     *
     * @param app the application
     * @param target the target node
     */
    public AbstractFocusOwnerAction(Application<V> app, Node target) {
        super(app);
        this.target = target;

        app.activeProjectProperty().addListener(activeViewListener);
        activeViewListener.changed(null, null, app == null ? null : app.getActiveProject());

    }
}
