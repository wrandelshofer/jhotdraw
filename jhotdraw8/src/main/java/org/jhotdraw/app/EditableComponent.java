/* @(#)EditableComponent.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.app;

/**
 * EditableComponent.
 * @author Werner Randelshofer
 */
public interface EditableComponent {
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
     */
    boolean isSelectionEmpty();
    
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
