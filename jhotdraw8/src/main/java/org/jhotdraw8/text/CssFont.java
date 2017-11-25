/* @(#)CssFont.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

/**
 * CssFont same as {@code javafx.scene.text.Font} but allows to get all
 * properties that were used to create the font.
 * <p>
 * XXX move this into a package for JavaFX font related stuff
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssFont {

    private final String family;
    private final FontWeight weight;
    private final FontPosture posture;
    private final double size;
    private final Font font;

    public CssFont(String family, FontWeight weight, FontPosture posture, double size) {
        this.family = family;
        this.weight = weight;
        this.posture = posture;
        this.size = size;
        this.font = (weight == FontWeight.NORMAL || posture == FontPosture.REGULAR
                || weight == null || posture == null)
                        ? new Font(family, size)
                        : Font.font(family, weight, posture, size);
    }

    public String getFamily() {
        return family;
    }

    public FontWeight getWeight() {
        return weight;
    }

    public FontPosture getPosture() {
        return posture;
    }

    public double getSize() {
        return size;
    }

    public Font getFont() {
        return font;
    }
private final static Map<String,CssFont> cachedFonts=new ConcurrentHashMap<>();
    public static CssFont font(String family, FontWeight weight, FontPosture posture, double size) {
        return cachedFonts.computeIfAbsent(family+weight.name()+posture.name()+Double.doubleToRawLongBits(size),str->new CssFont(family, weight, posture, size));
    }

    public static CssFont font(String family, double size) {
        return new CssFont(family, FontWeight.NORMAL, FontPosture.REGULAR, size);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.family);
        hash = 97 * hash + Objects.hashCode(this.weight);
        hash = 97 * hash + Objects.hashCode(this.posture);
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.size) ^ (Double.doubleToLongBits(this.size) >>> 32));
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
        final CssFont other = (CssFont) obj;
        if (Double.doubleToLongBits(this.size) != Double.doubleToLongBits(other.size)) {
            return false;
        }
        if (!Objects.equals(this.family, other.family)) {
            return false;
        }
        if (this.weight != other.weight) {
            return false;
        }
        if (this.posture != other.posture) {
            return false;
        }
        return true;
    }

}
