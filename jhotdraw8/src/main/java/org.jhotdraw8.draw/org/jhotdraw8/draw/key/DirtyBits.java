/*
 * @(#)DirtyBits.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

/**
 * {@code DirtyBits} describes how changing a property value of a {@code Figure}
 * affects dependent objects.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @design.pattern org.jhotdraw8.draw.model.DrawingModel Strategy, Context.
 */
public enum DirtyBits {

    /**
     * Affects the state of the figure.
     * <p>
     * All objects which depend on the state of the figure need to be updated.
     */
    STATE,
    /**
     * Affects the JavaFX {@code Node} created by the figure.
     * <p>
     * All cached JavaFX {@code Node}s created by the figure need to be updated.
     */
    NODE,
    /**
     * Affects the layout of the figure, the layout of its ancestors and the
     * layout of layout observing figures.
     * <p>
     * Method {@code Figure#layoutNotify} must be called on the figure, then in
     * ascending order on all its ancestors which perform layout, and then on
     * all dependent figures and their ancestors.
     */
    LAYOUT,
    /**
     * Affects the layout of layout observing figures.
     * <p>
     * Method {@code Figure#layoutNotify} must be called on all dependent
     * figures and their ancestors.
     */
    LAYOUT_OBSERVERS,
    /**
     * Affects the style of the figure.
     * <p>
     * Method {@code Figure#stylesheetNotify} must be called on the figure and
     * all its descendants.
     */
    STYLE,
    /**
     * Affects the layout subject(s) of the figure.
     * <p>
     * Method {@code Figure#layoutSubjectChangeNotify} must be called on the figure.
     */
    LAYOUT_SUBJECT,
    /**
     * Affects a figure which is layout subject of other figures.
     * <p>
     * Method {@code Figure#layoutObserverChangeNotify} must be called on the figure.
     */
    LAYOUT_OBSERVERS_ADDED_OR_REMOVED,
    /**
     * Affects the transform of the figure and all descendant figures.
     * <p>
     * Method {@code Figure#transformNotify} must be called on the figure and all its descendant figures.
     */
    TRANSFORM,
    /**
     * This is internally used by DrawingModel for marking figures which need
     * transformNotify.
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
