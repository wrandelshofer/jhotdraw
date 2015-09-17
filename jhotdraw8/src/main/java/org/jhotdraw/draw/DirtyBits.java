/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhotdraw.draw;

/**
 * DirtyBits.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public enum DirtyBits {
    /** The appearance of the node which is used to render the figure has changed.
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

    public final long getMask() {
        return mask;
    }
}
