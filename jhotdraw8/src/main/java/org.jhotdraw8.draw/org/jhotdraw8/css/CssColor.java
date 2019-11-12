/*
 * @(#)CssColor.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css;

import javafx.scene.paint.Color;
import org.jhotdraw8.annotation.NonNull;
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

    @NonNull
    private final String name;
    @NonNull
    private final Color color;

    @Nullable
    public final static CssColor BLACK = CssColor.valueOf("black");
    @Nullable
    public final static CssColor WHITE = CssColor.valueOf("white");
    @Nullable
    public final static CssColor TRANSPARENT = CssColor.valueOf("transparent");

    public CssColor(@NonNull Color color) {
        this(null, color);
    }

    public CssColor(@Nullable String name, @NonNull Color color) {
        this.name = name == null ? toName(color) : name;
        this.color = color;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public Color getColor() {
        return color;
    }

    @NonNull
    @Override
    public Color getPaint() {
        return color;
    }

    @NonNull
    public static String toName(@NonNull Color c) {
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
        return Objects.equals(this.color, other.color);
    }

    @NonNull
    @Override
    public String toString() {
        return "CssColor{" + getName() + '}';
    }

    @Nullable
    public static CssColor valueOf(@NonNull String value) {
        return new CssColor(value, Color.valueOf(value));
    }

    @Nullable
    public static CssColor ofColor(@Nullable Color c) {
        return c == null ? null : new CssColor(c);
    }

    @Nullable
    public static Color toColor(@Nullable CssColor c) {
        return c == null ? null : c.getColor();
    }

}
