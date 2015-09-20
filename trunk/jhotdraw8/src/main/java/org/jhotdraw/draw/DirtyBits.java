/* @(#)DirtyBits.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw;

/**
 * {@code DirtyBits} describes how changing a property value of a {@code Figure}
 * affects dependent objects.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public enum DirtyBits {
    /** Affects the state of the figure.
     *//** Affects the state of the figure.
     */
    STATE,
    /** Affects the JavaFX {@code Node} created by the figure.
     * <p>
     * All cached JavaFX {@code Node}s created by the figure, should be updated.</p>
     */
    NODE,
    /** Affects the layout of connection figures.
     * <p>
     * All connection figures which perform layouts should by laid out.</p>
     */
    CONNECTION_LAYOUT,
    /** Affects the layout of parent figures.
     * <p>
     * All parent figures which perform layouts should by laid out.</p>
     */
    LAYOUT;

    private int mask;

    private DirtyBits() {
        mask = 1 << ordinal();
    }

    /** API for DirtyMask. */
    final int getMask() {
        return mask;
    }

}
