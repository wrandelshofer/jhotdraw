/* @(#)EditableComponent.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.app;

import javafx.beans.property.ReadOnlyBooleanProperty;

/**
 * EditableComponent.
 * @author Werner Randelshofer
 */
public interface EditableComponent {
    /** The name of the selectionEmpty property. */
    public final static String SELECTION_EMPTY="selectionEmpty";
    // ---
    // selection actions
    // ---
    /**
     * Selects all.
     */
    void selectAll();

    /**
     * Clears the selection.
     */
    void clearSelection();
    /**
     * Returns true if the selection is empty.
     * This is a bound property.
     * @return true if empty
     */
    ReadOnlyBooleanProperty selectionEmptyProperty();
    
    // ---
    // edit actions on selection
    // ---
    /**
     * Deletes the selected region or the component at (or after) the
     * caret position.
     */
    void deleteSelection();
    /**
     * Duplicates the selected region.
     */
    void duplicateSelection();
    
    /**
     * Transfers the contents of the current selection to the clipboard,
     * removing the current selection.
     */
    void cut();
    /**
     * Transfers the contents of the current selection to the clipboard,
     * leaving the current selection.
     */
    void copy();
    /**
     * Transfers the contents from the clipboard replacing the
     * current selection. If there is no selection, the contents in the
     * clipboard is inserted at the current insertion point.
     */
    void paste();
}
