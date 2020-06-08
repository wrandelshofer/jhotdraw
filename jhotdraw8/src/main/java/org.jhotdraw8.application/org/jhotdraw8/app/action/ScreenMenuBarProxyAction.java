/*
 * @(#)ScreenMenuBarProxyAction.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.app.Application;

/**
 * ScreenMenuBarProxyAction.
 *
 * @author Werner Randelshofer
 */
public class ScreenMenuBarProxyAction extends AbstractAction {

    @NonNull
    private final Application app;
    @Nullable
    private Action currentAction;

    public ScreenMenuBarProxyAction(@NonNull Application app, String id) {
        this.app = app;
        set(ID_KEY, id);
        disabled.unbind();
        disabled.set(true);
        selectedProperty().set(false);

        app.activeActivityProperty().addListener((o, oldv, newv) -> {
            if (currentAction != null) {
                disabled.unbind();
                disabled.set(true);
                selectedProperty().unbind();
                selectedProperty().set(false);
            }
            if (newv != null) {
                currentAction = newv.getActionMap().get(id);
            }
            if (currentAction != null) {
                disabled.bind(Bindings.isNotEmpty(disablers).or(currentAction.disabledProperty()));
                selectedProperty().bind(currentAction.selectedProperty());
                set(LABEL, currentAction.get(LABEL));
                set(MNEMONIC_KEY, currentAction.get(MNEMONIC_KEY));
                set(ACCELERATOR_KEY, currentAction.get(ACCELERATOR_KEY));
            }
        });
    }

    @Override
    protected void onActionPerformed(ActionEvent event) {
        if (currentAction != null) {
            currentAction.handle(event);
        }
    }

}
