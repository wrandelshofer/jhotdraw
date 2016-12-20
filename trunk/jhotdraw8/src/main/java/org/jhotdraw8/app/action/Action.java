/* @(#)Action.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.app.action;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.KeyCombination;
import org.jhotdraw8.app.Disableable;
import org.jhotdraw8.collection.BooleanKey;
import org.jhotdraw8.beans.PropertyBean;
import org.jhotdraw8.collection.StringKey;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.SimpleKey;

/**
 * Action.
 *
 * @design.pattern org.jhotdraw8.app.Application Framework, KeyAbstraction.
 *
 * @design.pattern Action Command, Command. The command pattern is used to treat
 * commands like objects inside of an application.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface Action extends EventHandler<ActionEvent>, PropertyBean, Disableable {

    /**
     * The key used for storing the action in an action map, and for accessing
     * resources in resource bundles.
     */
    public static final StringKey NAME = new StringKey("name");
    /**
     * The key used for storing the {@code String} name for the action, used for
     * a menu or button.
     */
    public static final StringKey LABEL = new StringKey("label");
    /**
     * The key used for storing a short {@code String} description for the
     * action, used for tooltip text.
     */
    public static final StringKey SHORT_DESCRIPTION = new StringKey("ShortDescription");
    /**
     * The key used for storing a longer {@code String} description for the
     * action, could be used for context-sensitive help.
     */
    public static final StringKey LONG_DESCRIPTION = new StringKey("LongDescription");
    /**
     * The key used for storing a small icon, such as {@code ImageView}. This is
     * typically used with menus.
     */
    public static final Key<Node> SMALL_ICON = new SimpleKey<>("SmallIcon", Node.class);

    /**
     * The key used for storing a {@code KeyCombination} to be used as the
     * accelerator for the action.
     */
    public static final Key<KeyCombination> ACCELERATOR_KEY = new SimpleKey<>("AcceleratorKey", KeyCombination.class);

    /**
     * The key used for storing a {@code KeyCombination} to be used as the
     * mnemonic for the action.
     *
     * @since 1.3
     */
    public static final Key<KeyCombination> MNEMONIC_KEY = new SimpleKey<>("MnemonicKey", KeyCombination.class);

    /**
     * The key used for storing a {@code Boolean} that corresponds to the
     * selected state. This is typically used only for components that have a
     * meaningful selection state. For example,
     * {@code RadioButton</code> and <code>CheckBox} make use of this but
     * instances of {@code Menu} don't.
     */
    public static final BooleanKey SELECTED_KEY = new BooleanKey("SwingSelectedKey");

    /**
     * The key used for large icon, such as {@code ImageView}. This is typically
     * used by buttons.
     */
    public static final Key<Node> LARGE_ICON_KEY = new SimpleKey<>("SwingLargeIconKey", Node.class);

    /**
     * The localized name of the action for use in controls.
     *
     * @return The name
     */
    default String getLabel() {
        return get(LABEL);
    }

    /**
     * The name of the action for use in action maps and for resource bundles.
     *
     * @return The instance
     */
    default String getName() {
        return get(NAME);
    }
}
