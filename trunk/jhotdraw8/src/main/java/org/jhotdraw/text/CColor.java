/* @(#)CColor.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw.text;

import javafx.scene.paint.Color;

/**
 * CColor wraps a Color object but also retains the name that was
 * used to create the color.
 * 
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class CColor {
    private final String name;
    private final Color color;

    public CColor(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }
    
    
}
