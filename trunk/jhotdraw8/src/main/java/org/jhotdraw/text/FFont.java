/* @(#)FFont.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.text;

import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

/**
 * FFont.
 *
 * @author Werner Randelshofer
 */
public class FFont {

    private final String family;
    private final FontWeight weight;
    private final FontPosture posture;
    private final double size;
    private final Font font;

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
}
