/*
 * @(#)Action.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action;

import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.KeyCombination;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.app.Disableable;
import org.jhotdraw8.beans.PropertyBean;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.ObjectKey;
import org.jhotdraw8.collection.StringKey;

/**
 * Action.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @design.pattern org.jhotdraw8.app.Application Framework, KeyAbstraction.
 * @design.pattern Action Command, Command. The command pattern is used to treat
 * commands like objects inside of an application.
 */
public interface Action extends EventHandler<ActionEvent>, PropertyBean, Disableable {

    /**
     * The key used for storing the action in an action map, and for accessing
     * resources in resource bundles.
     */
    StringKey ID_KEY = new StringKey("id");
    /**
     * The key used for storing the {@code String} name for the action, used for
     * a menu or button.
     */
    StringKey LABEL = new StringKey("label");
    /**
     * The key used for storing a short {@code String} description for the
     * action, used for tooltip text.
     */
    StringKey SHORT_DESCRIPTION = new StringKey("ShortDescription");
    /**
     * The key used for storing a longer {@code String} description for the
     * action, could be used for context-sensitive help.
     */
    StringKey LONG_DESCRIPTION = new StringKey("LongDescription");
    /**
     * The key used for storing a small icon, such as {@code ImageView}. This is
     * typically used with menus.
     */
    Key<Node> SMALL_ICON = new ObjectKey<>("SmallIcon", Node.class);

    /**
     * The key used for storing a {@code KeyCombination} to be used as the
     * accelerator for the action.
     */
    Key<KeyCombination> ACCELERATOR_KEY = new ObjectKey<>("AcceleratorKey", KeyCombination.class);

    /**
     * The key used for storing a {@code KeyCombination} to be used as the
     * mnemonic for the action.
     *
     * @since 1.3
     */
    Key<KeyCombination> MNEMONIC_KEY = new ObjectKey<>("MnemonicKey", KeyCombination.class);

    /**
     * The key used for large icon, such as {@code ImageView}. This is typically
     * used by buttons.
     */
    Key<Node> LARGE_ICON_KEY = new ObjectKey<>("SwingLargeIconKey", Node.class);

    /**
     * The selected property.
     */
    String SELECTED_PROPERTY = "selected";

    /**
     * The localized name of the action for use in controls.
     *
     * @return The name
     */
    @Nullable
    default String getLabel() {
        return get(LABEL);
    }

    /**
     * The name of the action for use in action maps and for resource bundles.
     *
     * @return The instance
     */
    default String getId() {
        return get(ID_KEY);
    }

    /**
     * The {@code Boolean} that corresponds to the selected state. This is
     * typically used only for actions that have a meaningful selection state.
     *
     * @return the property
     */
    BooleanProperty selectedProperty();

    default void setSelected(boolean newValue) {
        selectedProperty().set(newValue);
    }

    default boolean isSelected() {
        return selectedProperty().get();
    }
}
