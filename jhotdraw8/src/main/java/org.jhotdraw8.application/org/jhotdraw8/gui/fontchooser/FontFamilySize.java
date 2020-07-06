/*
 * @(#)FontFamilySize.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.fontchooser;

public class FontFamilySize {
    private final String family;
    private final double size;

    public FontFamilySize(String family, double size) {
        this.family = family;
        this.size = size;
    }

    public String getFamily() {
        return family;
    }

    public double getSize() {
        return size;
    }
}
