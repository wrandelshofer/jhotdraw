/* @(#)DirtyBits.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.key;

/**
 * {@code DirtyBits} describes how changing a property value of a {@code Figure}
 * affects dependent objects.
 *
 * @design.pattern org.jhotdraw.draw.model.DrawingModel Strategy, Context.
 * 
 * @author Werner Randelshofer
 * @version $Id$
 */
public enum DirtyBits {

    /**
     * Affects the state of the figure.
     * <p>
     * A GUI element which shows the state of the figure needs to be updated.
     */
    STATE,
    /**
     * Affects the JavaFX {@code Node} created by the figure.
     * <p>
     * All cached JavaFX {@code Node}s created by the figure need to be
     * updated.</p>
     */
    NODE,
    /**
     * Affects the layout of connected figures.
     * <p>
     * All connected figures which perform layouts need to be laid out.</p>
     */
    CONNECTION_LAYOUT,
    /**
     * Affects the layout of this figure and its ancestors.
     * <p>
     * All parent figures which perform layouts need to be laid out.</p>
     */
    LAYOUT,
    /**
     * Affects the style of the figure.
     * <p>
     * The CSS needs to be applied again on the figure.
     */
    STYLE,
    /**
     * Affects the connection of the figure.
     * <p>
     * Method connectNotify needs to be invoked on the figure.
     */
    CONNECTION,
    /**
     * Affects the transform of the figure.
     * <p>
     * Method transformNotify needs to be invoked on the figure and all its
     * descendants.
     */
    TRANSFORM;

    private int mask;

    private DirtyBits() {
        mask = 1 << ordinal();
    }

    /**
     * API for DirtyMask.
     */
    final int getMask() {
        return mask;
    }

}
