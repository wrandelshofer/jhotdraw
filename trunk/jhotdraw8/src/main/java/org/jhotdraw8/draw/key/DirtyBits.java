/* @(#)DirtyBits.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.key;

/**
 * {@code DirtyBits} describes how changing a property value of a {@code Figure}
 * affects dependent objects.
 *
 * @design.pattern org.jhotdraw8.draw.model.DrawingModel Strategy, Context.
 * 
 * @author Werner Randelshofer
 * @version $Id$
 */
public enum DirtyBits {

    /**
     * Affects the state of the figure.
     * <p>
     * All objects which depend on the state of the figure need to be updated.
     *//**
     * Affects the state of the figure.
     * <p>
     * All objects which depend on the state of the figure need to be updated.
     */
    STATE,
    /**
     * Affects the JavaFX {@code Node} created by the figure.
     * <p>
     * All cached JavaFX {@code Node}s created by the figure need to be
     * updated.
     */
    NODE,
    /**
     * Affects the layout of the figure, the layout of its ancestors and
     * the layout of dependent figures.
     * <p>
     * Method {@code Figure#layoutNotify} must be called on the figure, then in ascending
     * order on all its ancestors which perform layout, and then on all
     * dependent figures and their ancestors.
     */
    LAYOUT,
    /**
     * Affects the layout of dependent figures.
     * <p>
     * Method {@code Figure#layoutNotify} must be called on all
     * dependent figures and their ancestors.
     */
    DEPENDENT_LAYOUT,
    /**
     * Affects the style of the figure.
     * <p>
     * Method {@code Figure#stylesheetNotify} must be called on the figure and
     * all its descendants.
     */
    STYLE,
    /**
     * Affects the dependency of the figure.
     * <p>
     * Method {@code Figure#connectNotify} must be called on the figure.
     */
    DEPENDENCY,
    /**
     * Affects the transform of the figure.
     * <p>
     * Method {@code Figure#transformNotify} must be called on the figure.
     */
    TRANSFORM,
    /**
     * This is internally used by DrawingModel for marking figures which need transformNotify.
     * <p>
     * Method {@code Figure#transformNotify} must be called on the figure.
     */
    TRANSFORM_NOTIFY;

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
