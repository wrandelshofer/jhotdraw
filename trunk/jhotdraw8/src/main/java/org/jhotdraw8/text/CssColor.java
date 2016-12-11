/* @(#)CssColor.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.text;

import java.util.Objects;
import javafx.scene.paint.Color;

/**
 * CssColor wraps a Color object but also retains the name that was used to create
 *  the color.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssColor implements Paintable {
    private final static PatternConverter formatter = new PatternConverter("rgba'('{0,number},{1,number},{2,number},{3,number}')'", new CssConverterFactory());

    private final String name;
    private final Color color;

    public CssColor(Color color) {
        this(null, color);
    }

    public CssColor(String name, Color color) {
        this.name = name == null ? toName(color) : name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public Color getPaint() {
        return color;
    }

    public static String toName(Color c) {
        if (c.getOpacity() == 1.0) {
            int r = (int) Math.round(c.getRed() * 255.0);
            int g = (int) Math.round(c.getGreen() * 255.0);
            int b = (int) Math.round(c.getBlue() * 255.0);
            return String.format("#%02x%02x%02x", r, g, b);
        }else if (c.equals(Color.TRANSPARENT)) {
            return "transparent";
        } else {
            int r = (int) Math.round(c.getRed() * 255.0);
            int g = (int) Math.round(c.getGreen() * 255.0);
            int b = (int) Math.round(c.getBlue() * 255.0);
            double o = c.getOpacity();
            return formatter.format(r,g,b,o);
           // return String.format("rgba(%d,%d,%d,%f)", r, g, b, o);
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.color);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CssColor other = (CssColor) obj;
        if (!Objects.equals(this.color, other.color)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CColor{" + getName() + '}';
    }

}
