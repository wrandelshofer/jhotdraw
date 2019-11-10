/*
 * @(#)CssFont.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css;

import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a font specified with family, weight, posture and size properties.
 *
 * @author Werner Randelshofer
 */
public class CssFont {

    private final String family;
    @Nullable
    private final FontWeight weight;
    @Nullable
    private final FontPosture posture;
    @NonNull
    private final CssSize size;
    @NonNull
    private final Font font;

    public CssFont(String family, @Nullable FontWeight weight, @Nullable FontPosture posture, @NonNull CssSize size) {
        this.family = family;
        this.weight = weight;
        this.posture = posture;
        this.size = size;

        this.font = (weight == FontWeight.NORMAL || posture == FontPosture.REGULAR
                || weight == null || posture == null)
                ? new Font(family, size.getConvertedValue())
                : Font.font(family, weight, posture, size.getConvertedValue());
    }

    public String getFamily() {
        return family;
    }

    @Nullable
    public FontWeight getWeight() {
        return weight;
    }

    @Nullable
    public FontPosture getPosture() {
        return posture;
    }

    @NonNull
    public CssSize getSize() {
        return size;
    }

    @NonNull
    public Font getFont() {
        return font;
    }

    private final static Map<String, CssFont> cachedFonts = new ConcurrentHashMap<>();

    public static CssFont font(String family, @Nullable FontWeight weight, @Nullable FontPosture posture, @NonNull CssSize size) {
        return cachedFonts.computeIfAbsent(family
                + (weight == null ? "" : weight.name())
                + (posture == null ? "" : posture.name())
                + Double.doubleToRawLongBits(size.getConvertedValue()), str -> new CssFont(family, weight, posture, size));
    }

    public static CssFont font(String family, @Nullable FontWeight weight, @Nullable FontPosture posture, double size) {
        return cachedFonts.computeIfAbsent(family
                + (weight == null ? "" : weight.name())
                + (posture == null ? "" : posture.name())
                + Double.doubleToRawLongBits(size), str -> new CssFont(family, weight, posture, new CssSize(size)));
    }

    @NonNull
    public static CssFont font(String family, double size) {
        return new CssFont(family, FontWeight.NORMAL, FontPosture.REGULAR, new CssSize(size));
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.family);
        hash = 97 * hash + Objects.hashCode(this.weight);
        hash = 97 * hash + Objects.hashCode(this.posture);
        hash = 97 * hash + this.size.hashCode();
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
        final CssFont other = (CssFont) obj;
        if (!Objects.equals(this.size, other.size)) {
            return false;
        }
        if (!Objects.equals(this.family, other.family)) {
            return false;
        }
        if (this.weight != other.weight) {
            return false;
        }
        return this.posture == other.posture;
    }

    @NonNull
    @Override
    public String toString() {
        return "CssFont{" +
                "family='" + family + '\'' +
                ", weight=" + weight +
                ", posture=" + posture +
                ", size=" + size +
                '}';
    }
}
