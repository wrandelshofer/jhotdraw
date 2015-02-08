/* @(#)Actions.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.app.action;

import javafx.beans.property.Property;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;

/**
 * Actions.
 * @author Werner Randelshofer
 * @version $Id$
 */
public class Actions {

    /** Prevent instance creation. */
    private Actions() {

    }

    /**
     * Binds a button to an action
     * @param control The menu control
     * @param action The action
     */
    public static void bindButton(Button control, Action action) {
        control.textProperty().bind(action.getValueProperty(Action.NAME));
        control.setOnAction(action);
        control.disableProperty().bind(action.disabledProperty());
    }

    /**
     * Binds a menu control to an action
     * @param control The menu control
     * @param action The action
     */
    public static void bindMenuItem(MenuItem control, Action action) {
        control.textProperty().bind(action.getValueProperty(Action.NAME));
        if (control instanceof CheckMenuItem) {
            ((CheckMenuItem) control).selectedProperty().bindBidirectional(action.getValueProperty(Action.SELECTED_KEY));
        }else
        if (control instanceof RadioMenuItem) {
            ((RadioMenuItem) control).selectedProperty().bindBidirectional(action.getValueProperty(Action.SELECTED_KEY));
        }
        control.setOnAction(action);
        control.disableProperty().bind(action.disabledProperty());
        control.acceleratorProperty().bind(action.getValueProperty(Action.ACCELERATOR_KEY));
    }
}
