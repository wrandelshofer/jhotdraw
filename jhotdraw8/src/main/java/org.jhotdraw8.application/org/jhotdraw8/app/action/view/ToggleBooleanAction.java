/*
 * @(#)ToggleBooleanAction.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.view;

import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.app.Activity;
import org.jhotdraw8.app.action.AbstractActivityAction;
import org.jhotdraw8.util.Resources;

/**
 * This action toggles the state of its boolean property.
 *
 * @author Werner Randelshofer
 */
public class ToggleBooleanAction extends AbstractActivityAction<Activity> {
    private final BooleanProperty value;

    public ToggleBooleanAction(@NonNull Activity activity, @Nullable String id, @Nullable Resources labels, BooleanProperty value) {
        super(activity);
        if (labels != null && id != null) {
            labels.configureAction(this, id);
        }
        this.value = value;
        selectedProperty().bind(value);
    }

    @Override
    protected void onActionPerformed(ActionEvent event, Activity activity) {
        value.set(!value.get());
    }
}
