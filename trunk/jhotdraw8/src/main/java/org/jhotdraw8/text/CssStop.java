/* @(#)CssStop.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw8.text;

/**
 * CssStop.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class CssStop {
    final Double offset;
    final CssColor color;

    public CssStop(Double offset, CssColor color) {
        this.offset = offset;
        this.color = color;
    }

    public Double getOffset() {
        return offset;
    }

    public CssColor getColor() {
        return color;
    }
    
}
