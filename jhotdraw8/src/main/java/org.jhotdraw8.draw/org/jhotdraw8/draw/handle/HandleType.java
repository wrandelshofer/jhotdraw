/*
 * @(#)HandleType.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.handle;

/**
 * {@code HandleType} is used by tools to request specific handles from figures.
 *
 * @author Werner Randelshofer
 */
public class HandleType {

    /**
     * A Handle of this type should highlight a figure, but should not provide
     * user interaction.
     */
    public static final HandleType SELECT = new HandleType();
    /**
     * A Handle of this type should highlight a figure, but should not provide
     * user interaction.
     */
    public static final HandleType LEAD = new HandleType();
    /**
     * A Handle of this type should highlight a figure, but should not provide
     * user interaction.
     */
    public static final HandleType ANCHOR = new HandleType();
    /**
     * Handles of this type should allow to move (translate) a figure.
     */
    public static final HandleType MOVE = new HandleType();
    /**
     * Handle of this type should allow to reshape (resize) a figure.
     */
    public static final HandleType RESIZE = new HandleType();
    /**
     * Handle of this type should allow to transform (scale and rotate) a
     * figure.
     */
    public static final HandleType TRANSFORM = new HandleType();
    /**
     * Handle of this type should allow to edit a point of a figure.
     */
    public static final HandleType POINT = new HandleType();
}
