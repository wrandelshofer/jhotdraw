/* @(#)DirtyBits.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw;

/**
 * DirtyBits.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public enum DirtyBits {
    /** The state of the figure has changed.
     * Such as when a comment or another non-visual property has changed.
     */
    STATE,
    /** The appearance of the node which is used to render the figure has
     * changed.
     * Such as when the fill color changed.
     */
    NODE,
    /** The geometry of the figure has changed.
     * Such as when a point has been added to or removed from a path.
     */
    GEOMETRY,
    /** The layout bounds of the figure have changed.
     * Such as when a shape has been resized or scaled.
     */
    LAYOUT_BOUNDS,
    /** The visual bounds of the figure has changed.
     * Such as when the stroke width has been changed.
     */
    VISUAL_BOUNDS;

    private int mask;

    private DirtyBits() {
        mask = 1 << ordinal();
    }

    /** API for DirtyMask. */
    final int getMask() {
        return mask;
    }


}
