/* @(#)CStop.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw8.text;

/**
 * CStop.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class CStop {
    final Double offset;
    final CColor color;

    public CStop(Double offset, CColor color) {
        this.offset = offset;
        this.color = color;
    }

    public Double getOffset() {
        return offset;
    }

    public CColor getColor() {
        return color;
    }
    
}
