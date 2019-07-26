/*
 * @(#)Actions.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action;

import javafx.beans.binding.Binding;
import javafx.beans.property.Property;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.input.KeyCombination;
import org.jhotdraw8.annotation.Nonnull;

/**
 * Actions.
 *
 * @author Werner Randelshofer
 */
public class Actions {

    /**
     * Binds a button to an action
     *
     * @param control The menu control
     * @param action  The action
     */
    public static void bindButton(Button control, Action action) {
        // create a strong reference to name binding:
        Binding<String> nameBinding = Action.LABEL.valueAt(action.getProperties());
        control.getProperties().put("ActionsNameBinding", nameBinding);
        control.textProperty().bind(Action.LABEL.valueAt(action.getProperties()));

        control.setOnAction(action);
        control.disableProperty().bind(action.disabledProperty());
    }

    /**
     * Binds a menu control to an action
     *
     * @param control The menu control
     * @param action  The action
     */
    public static void bindMenuItem(@Nonnull MenuItem control, @Nonnull Action action) {
        bindMenuItem(control, action, true);

    }

    /**
     * Binds a menu control to an action
     *
     * @param control   The menu control
     * @param action    The action
     * @param bindLabel whether the the text of the menu item should be bound to
     *                  the label of the action
     */
    public static void bindMenuItem(@Nonnull MenuItem control, @Nonnull Action action, boolean bindLabel) {

        // create a strong reference to name binding:
        if (bindLabel) {
            Binding<String> nameBinding = Action.LABEL.valueAt(action.getProperties());
            control.getProperties().put("ActionsNameBinding", nameBinding);
            control.textProperty().bind(Action.LABEL.valueAt(action.getProperties()));

            Binding<KeyCombination> acceleratorBinding = Action.ACCELERATOR_KEY.valueAt(action.getProperties());
            // create a strong reference to name binding:
            control.getProperties().put("ActionsAcceleratorBinding", acceleratorBinding);
            // this only creates a weak reference to the name binding:
            control.acceleratorProperty().bind(acceleratorBinding);
        }
        control.setOnAction(action);
        control.disableProperty().bind(action.disabledProperty());
        if (control instanceof CheckMenuItem) {
            CheckMenuItem cmi = (CheckMenuItem) control;
            Property<Boolean> selectedBinding = action.selectedProperty();
            selectedBinding.addListener((o, oldv, newv) -> cmi.setSelected(newv));
            cmi.setSelected(action.isSelected());
        } else if (control instanceof RadioMenuItem) {
            Property<Boolean> selectedBinding = action.selectedProperty();
            RadioMenuItem cmi = (RadioMenuItem) control;
            selectedBinding.addListener((o, oldv, newv) -> cmi.setSelected(newv));
            cmi.setSelected(action.isSelected());
        }
    }

    /**
     * Prevent instance creation.
     */
    private Actions() {

    }
}
