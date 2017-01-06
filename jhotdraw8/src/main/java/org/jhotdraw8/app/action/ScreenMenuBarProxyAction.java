/* @(#)ScreenMenuBarProxyAction.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.app.action;

import java.util.Objects;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import org.jhotdraw8.app.Application;

/**
 * ScreenMenuBarProxyAction.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class ScreenMenuBarProxyAction extends AbstractAction {

    private final Application app;
    private final String id;
    private Action currentAction;

    public ScreenMenuBarProxyAction(Application app, String id) {
        this.app = app;
        this.id = id;
        disabled.unbind();
        disabled.set(true);
        selectedProperty().set(false);

        app.activeProjectProperty().addListener((o, oldv, newv) -> {
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
            }
        });
    }

    @Override
    protected void handleActionPerformed(ActionEvent event) {
        if (currentAction != null) {
            currentAction.handle(event);
        }
    }

}
