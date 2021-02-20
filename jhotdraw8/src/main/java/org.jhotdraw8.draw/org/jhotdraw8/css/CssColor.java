/*
 * @(#)CssColor.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.draw.render.RenderContext;

import java.util.Objects;

/**
 * Abstract base class for a color specified in a specific color system.
 * <p>
 * FIXME - make this class abstract and implement subclasses for each color system
 *
 * @author Werner Randelshofer
 */
public class CssColor implements Paintable {


    private final @NonNull String name;
    private final @NonNull Color color;

    public static final @NonNull CssColor BLACK = CssColor.valueOf("black");
    public static final @NonNull CssColor WHITE = CssColor.valueOf("white");
    public static final @NonNull CssColor TRANSPARENT = CssColor.valueOf("transparent");

    public CssColor(@NonNull Color color) {
        this(null, color);
    }

    public CssColor(@Nullable String name) {
        Color computedColor = DefaultSystemColorConverter.LIGHT_SYSTEM_COLORS.get(name);
        if (computedColor == null && name != null) {
            try {
                computedColor = Color.web(name);
            } catch (IllegalArgumentException e) {
                computedColor = Color.BLACK;
            }
        } else {
            computedColor = Color.BLACK;
        }
        this.color = computedColor;
        this.name = name == null ? toName(computedColor) : name;
    }

    public CssColor(@Nullable String name, @NonNull Color color) {
        this.name = name == null ? toName(color) : name;
        this.color = color;
    }

    public @Nullable String getName() {
        return name;
    }

    public @NonNull Color getColor(SystemColorConverter converter) {
        return converter.convert(this);
    }

    public @NonNull Color getColor() {
        return color;
    }

    @Override
    public @NonNull Color getPaint() {
        return color;
    }

    @Override
    public @Nullable Paint getPaint(RenderContext ctx) {
        // XXX This should only be done for a named color
        return ctx.getNonNull(RenderContext.SYSTEM_COLOR_CONVERTER_KEY)
                .convert(this);
    }

    public static @NonNull String toName(@NonNull Color c) {
        if (c.getOpacity() == 1.0) {
            // The fields in class Color store values as floats, we must
            // not promote it to double because this changes the values!
            return "rgb("
                    + ((float) c.getRed()) * 100 + "%,"
                    + ((float) c.getGreen()) * 100 + "%,"
                    + ((float) c.getBlue()) * 100 + "%"
                    + ")";
            /*
            // This is not precise and will faill in SVG tests.
            int r = (int) Math.round(c.getRed() * 255.0);
            int g = (int) Math.round(c.getGreen() * 255.0);
            int b = (int) Math.round(c.getBlue() * 255.0);
            return String.format("#%02x%02x%02x", r, g, b);
             */
        } else if (c.equals(Color.TRANSPARENT)) {
            return "transparent";
        } else {
            // The fields in class Color store values as floats, we must
            // not promote it to double because this changes the values!
            return "rgba("
                    + ((float) c.getRed()) * 100 + "%,"
                    + ((float) c.getGreen()) * 100 + "%,"
                    + ((float) c.getBlue()) * 100 + "%"
                    + ((float) c.getOpacity()) * 100 + "%"
                    + ")";
            /*
            int r = (int) Math.round(c.getRed() * 255.0);
            int g = (int) Math.round(c.getGreen() * 255.0);
            int b = (int) Math.round(c.getBlue() * 255.0);
            float o = (float) c.getOpacity();// Color represents opacity by a float. We must not promote it.
             */
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.color, this.name);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!CssColor.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final CssColor other = (CssColor) obj;
        return Objects.equals(this.color, other.color)
                && Objects.equals(this.name, other.name);
    }

    @Override
    public @NonNull String toString() {
        return "CssColor{" + getName() + '}';
    }

    public static @NonNull CssColor valueOf(@NonNull String value) {
        return new CssColor(value);
    }

    public static @Nullable CssColor ofColor(@Nullable Color c) {
        return c == null ? null : new CssColor(c);
    }

    public static @Nullable Color toColor(@Nullable CssColor c) {
        return c == null ? null : c.getColor();
    }

}
