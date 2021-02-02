/*
 * @(#)LabelAutorotate.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.draw.figure;

/**
 * LabelAutorotate.
 *
 * @author Werner Randelshofer
 */
public enum LabelAutorotate {
    /**
     * Does not automatically rotate the label.
     */
    OFF,

    /**
     * Fully rotates the label from 0 to 360 degrees.
     */
    FULL,

    /**
     * Rotates the label from -90 to +90 degrees.
     */
    HALF
}
