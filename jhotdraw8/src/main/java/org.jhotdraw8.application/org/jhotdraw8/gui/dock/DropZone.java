/*
 * @(#)DropZone.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.dock;

/**
 * Defines a drop zone for the drag and drop operation of a DockChild.
 *
 * @author Werner Randelshofer
 */
public enum DropZone {
    /**
     * Denotes the left edge of a Dock.
     */
    TOP,
    /**
     * Denotes the left edge of a Dock.
     */
    LEFT,
    /**
     * Denotes the right edge of a Dock.
     */
    RIGHT,
    /**
     * Denotes the bottom edge of a Dock.
     */
    BOTTOM,
    /**
     * Denotes the center of a Dock.
     */
    CENTER
}
