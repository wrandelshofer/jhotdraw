/*
 * @(#)CssColor.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css;

import javafx.scene.paint.Color;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.text.CssConverterFactory;
import org.jhotdraw8.text.PatternConverter;

import java.util.Objects;

/**
 * Abstract base class for a color specified in a specific color system.
 * <p>
 * FIXME - make this class abstract and implement subclasses for each color system
 *
 * @author Werner Randelshofer
 */
public class CssColor implements Paintable {

    private final static PatternConverter formatter = new PatternConverter("rgba'('{0,number},{1,number},{2,number},{3,number}')'", new CssConverterFactory());

    @Nonnull
    private final String name;
    @Nonnull
    private final Color color;

    public final static CssColor BLACK = CssColor.valueOf("black");
    public final static CssColor WHITE = CssColor.valueOf("white");

    public CssColor(@Nonnull Color color) {
        this(null, color);
    }

    public CssColor(@Nullable String name, @Nonnull Color color) {
        this.name = name == null ? toName(color) : name;
        this.color = color;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public Color getColor() {
        return color;
    }

    @Nonnull
    @Override
    public Color getPaint() {
        return color;
    }

    @Nonnull
    public static String toName(Color c) {
        if (c.getOpacity() == 1.0) {
            int r = (int) Math.round(c.getRed() * 255.0);
            int g = (int) Math.round(c.getGreen() * 255.0);
            int b = (int) Math.round(c.getBlue() * 255.0);
            return String.format("#%02x%02x%02x", r, g, b);
        } else if (c.equals(Color.TRANSPARENT)) {
            return "transparent";
        } else {
            int r = (int) Math.round(c.getRed() * 255.0);
            int g = (int) Math.round(c.getGreen() * 255.0);
            int b = (int) Math.round(c.getBlue() * 255.0);
            float o = (float) c.getOpacity();// Color represents opacity by a float. We must not promote it.
            return formatter.format(r, g, b, o);
            //return String.format("rgba(%d,%d,%d,%f)", r, g, b, o);
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.color);
        return hash;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
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

    @Nonnull
    @Override
    public String toString() {
        return "CColor{" + getName() + '}';
    }

    @Nullable
    public static CssColor valueOf(@Nonnull String value) {
        return new CssColor(value, Color.valueOf(value));
    }

}
