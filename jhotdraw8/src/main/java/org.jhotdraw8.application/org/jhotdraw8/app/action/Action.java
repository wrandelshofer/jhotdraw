/*
 * @(#)Action.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action;

import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.KeyCombination;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.app.Disableable;
import org.jhotdraw8.beans.PropertyBean;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.NullableKey;
import org.jhotdraw8.collection.SimpleNullableKey;

/**
 * Action.
 *
 * @author Werner Randelshofer
 * @design.pattern org.jhotdraw8.app.Application Framework, KeyAbstraction.
 * @design.pattern Action Command, Command. The command pattern is used to treat
 * commands like objects inside of an application.
 */
public interface Action extends EventHandler<ActionEvent>, PropertyBean, Disableable {

    /**
     * The key used for storing the action in an action map, and for accessing
     * resources in resource bundles.
     */
    NullableKey<String> ID_KEY = new SimpleNullableKey<String>("id", String.class);
    /**
     * The key used for storing the {@code String} name for the action, used for
     * a menu or button.
     */
    NullableKey<String> LABEL = new SimpleNullableKey<String>("label", String.class);
    /**
     * The key used for storing a short {@code String} description for the
     * action, used for tooltip text.
     */
    NullableKey<String> SHORT_DESCRIPTION = new SimpleNullableKey<String>("ShortDescription", String.class);
    /**
     * The key used for storing a longer {@code String} description for the
     * action, could be used for context-sensitive help.
     */
    NullableKey<String> LONG_DESCRIPTION = new SimpleNullableKey<String>("LongDescription", String.class);
    /**
     * The key used for storing a small icon, such as {@code ImageView}. This is
     * typically used with menus.
     */
    Key<Node> SMALL_ICON = new SimpleNullableKey<>("SmallIcon", Node.class, null);

    /**
     * The key used for storing a {@code KeyCombination} to be used as the
     * accelerator for the action.
     */
    Key<KeyCombination> ACCELERATOR_KEY = new SimpleNullableKey<>("AcceleratorKey", KeyCombination.class, null);

    /**
     * The key used for storing a {@code KeyCombination} to be used as the
     * mnemonic for the action.
     *
     * @since 1.3
     */
    Key<KeyCombination> MNEMONIC_KEY = new SimpleNullableKey<>("MnemonicKey", KeyCombination.class, null);

    /**
     * The key used for large icon, such as {@code ImageView}. This is typically
     * used by buttons.
     */
    Key<Node> LARGE_ICON_KEY = new SimpleNullableKey<>("SwingLargeIconKey", Node.class, null);

    /**
     * The selected property.
     */
    String SELECTED_PROPERTY = "selected";

    /**
     * The localized name of the action for use in controls.
     *
     * @return The name
     */
    default @Nullable String getLabel() {
        return get(LABEL);
    }

    /**
     * The name of the action for use in action maps and for resource bundles.
     *
     * @return The instance
     */
    default @Nullable String getId() {
        return get(ID_KEY);
    }

    /**
     * The {@code Boolean} that corresponds to the selected state. This is
     * typically used only for actions that have a meaningful selection state.
     *
     * @return the property
     */
    @NonNull BooleanProperty selectedProperty();

    default void setSelected(boolean newValue) {
        selectedProperty().set(newValue);
    }

    default boolean isSelected() {
        return selectedProperty().get();
    }
}
