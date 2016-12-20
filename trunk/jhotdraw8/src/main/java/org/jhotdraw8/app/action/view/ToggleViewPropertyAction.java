/* @(#)ToggleViewPropertyAction.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.app.action.view;

import java.util.function.Function;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.action.AbstractViewAction;
import org.jhotdraw8.util.Resources;
import org.jhotdraw8.app.ProjectView;

/**
 * ToggleViewPropertyAction.
 *
 * @author Werner Randelshofer
 */
public class ToggleViewPropertyAction<V extends ProjectView<V>> extends AbstractViewAction<V> {

    private static final long serialVersionUID = 1L;
    private BooleanProperty property;
    private final Function<V, Node> nodeGetter;

    public ToggleViewPropertyAction(Application<V> app, V view, BooleanProperty property, String id, Resources labels) {
        super(app, view);
        labels.configureAction(this, id);
        this.property = property;
        this.nodeGetter = null;
        property.addListener((o, oldValue, newValue) -> set(SELECTED_KEY, newValue));
        set(SELECTED_KEY, property.get());
    }

    public ToggleViewPropertyAction(Application<V> app, V view, Function<V, Node> nodeGetter, String id, Resources labels) {
        super(app, view);
        labels.configureAction(this, id);
        this.property = null;
        this.nodeGetter = nodeGetter;
    }

    @Override
    protected void onActionPerformed(ActionEvent event) {
        if (property != null) {
            property.set(!property.get());
        } else {
            Node node = nodeGetter.apply(getActiveView());
            node.setVisible(!node.isVisible());
            this.set(SELECTED_KEY, node.isVisible());
        }
    }

}
