/*
 * @(#)ToggleViewPropertyAction.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.view;

import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.app.Activity;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.action.AbstractActivityAction;
import org.jhotdraw8.util.Resources;

import java.util.function.Function;

/**
 * ToggleViewPropertyAction.
 *
 * @author Werner Randelshofer
 */
public class ToggleViewPropertyAction extends AbstractActivityAction<Activity> {

@Nullable
    private BooleanProperty property;
    @Nullable
    private final Function<Activity, Node> nodeGetter;

    public ToggleViewPropertyAction(@NonNull Application app, Activity view, @NonNull BooleanProperty property, String id, @NonNull Resources labels) {
        super(app, view, null);
        labels.configureAction(this, id);
        this.property = property;
        this.nodeGetter = null;
        selectedProperty().bindBidirectional(property);
    }

    public ToggleViewPropertyAction(@NonNull Application app, Activity view, Function<Activity, Node> nodeGetter, String id, @NonNull Resources labels) {
        super(app, view, null);
        labels.configureAction(this, id);
        this.property = null;
        this.nodeGetter = nodeGetter;
    }

    @Override
    protected void onActionPerformed(ActionEvent event, Activity activity) {
        if (property != null) {
            property.set(!property.get());
        } else {
            Node node = nodeGetter.apply(activity);
            node.setVisible(!node.isVisible());
            this.setSelected(node.isVisible());
        }
    }

}
