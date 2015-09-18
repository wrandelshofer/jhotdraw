/*
 * @(#)Tool.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.tool;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.input.KeyCombination;
import org.jhotdraw.app.Disableable;
import org.jhotdraw.beans.PropertyBean;
import org.jhotdraw.collection.BooleanKey;
import org.jhotdraw.collection.Key;
import org.jhotdraw.collection.StringKey;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.event.Listener;

/**
 * Tool.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface Tool extends PropertyBean, Disableable {

    // ---
    // Property Names
    // ----

    /**
     * The name of the drawing view property.
     */
    public final static String DRAWING_VIEW_PROPERTY = "drawingView";
    // ---
    // Property Keys
    // ---
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
    public static final Key<Node> SMALL_ICON = new Key<>("SmallIcon", Node.class);

    /**
     * The key used for storing a {@code KeyCombination} to be used as the
     * accelerator for the action.
     */
    public static final Key<KeyCombination> ACCELERATOR_KEY = new Key<>("AcceleratorKey", KeyCombination.class);

    /**
     * The key used for storing a {@code KeyCombination} to be used as the
     * mnemonic for the action.
     *
     * @since 1.3
     */
    public static final Key<KeyCombination> MNEMONIC_KEY = new Key<>("MnemonicKey", KeyCombination.class);

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
    public static final Key<Node> LARGE_ICON_KEY = new Key<>("SwingLargeIconKey", Node.class);

    // ---
    // Properties
    // ----
    /**
     * The currently active drawing view. By convention, this property is only
     * set by {@code DrawingView}.
     *
     * @return the drawingView property, with {@code getBean()} returning this
     * tool, and {@code getLabel()} returning {@code DRAWING_VIEW_PROPERTY}.
     */
    ObjectProperty<DrawingView> drawingViewProperty();

    // ---
    // Behaviors
    // ----
    /**
     * Returns the node which presents the tool and which handles input events.
     *
     * @return a node
     */
    Node getNode();

    /**
     * Deletes the selection. Depending on the tool, this could be selected
     * figures, selected points or selected text.
     */
    public void editDelete();

    /**
     * Cuts the selection into the clipboard. Depending on the tool, this could
     * be selected figures, selected points or selected text.
     */
    public void editCut();

    /**
     * Copies the selection into the clipboard. Depending on the tool, this
     * could be selected figures, selected points or selected text.
     */
    public void editCopy();

    /**
     * Duplicates the selection. Depending on the tool, this could be selected
     * figures, selected points or selected text.
     */
    public void editDuplicate();

    /**
     * Pastes the contents of the clipboard. Depending on the tool, this could
     * be selected figures, selected points or selected text.
     */
    public void editPaste();

    // ---
    // Listeners
    // ---

    /**
     * Adds a listener for this tool.
     *
     * @param l a listener
     */
    void addToolListener(Listener<ToolEvent> l);

    /**
     * Removes a listener for this tool.
     *
     * @param l a previously added listener
     */
    void removeToolListener(Listener<ToolEvent> l);

    // ---
    // Convenience Methods
    // ----

    /**
     * The localized name of the action for use in controls.
     *
     * @return The name
     */
    default public String getLabel() {
        return get(LABEL);
    }

    /**
     * The name of the action for use in action maps and for resource bundles.
     *
     * @return The id
     */
    default public String getName() {
        return get(NAME);
    }

    /**
     * Gets the active drawing view.
     *
     * @return a drawing view
     */
    default DrawingView getDrawingView() {
        return drawingViewProperty().get();
    }

    /**
     * Sets the active drawing view.
     * <p>
     * This method is invoked by {@link DrawingView} when the tool is set or
     * unset on the drawing view.
     *
     * @param drawingView a drawing view
     */
    default void setDrawingView(DrawingView drawingView) {
        drawingViewProperty().set(drawingView);
    }

}
