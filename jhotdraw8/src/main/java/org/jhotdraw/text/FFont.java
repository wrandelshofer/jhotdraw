/* @(#)FFont.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.text;

import java.util.Objects;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

/**
 * FFont same as {@code javafx.scene.text.Font} but allows to get all
 * properties that were used to create the font.
 * <p>
 * XXX move this into a package for JavaFX font related stuff
 *
 * @author Werner Randelshofer
 */
public class FFont {

    private final String family;
    private final FontWeight weight;
    private final FontPosture posture;
    private final double size;
    private final transient Font font;

    public FFont(String family, FontWeight weight, FontPosture posture, double size) {
        this.family = family;
        this.weight = weight;
        this.posture = posture;
        this.size = size;
        this.font = Font.font(family, weight, posture, size);
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

    public static FFont font(String family, FontWeight weight, FontPosture posture, double size) {
        return new FFont(family, weight, posture, size);
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
        final FFont other = (FFont) obj;
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
