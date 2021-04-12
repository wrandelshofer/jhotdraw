/*
 * @(#)XmlRectangle2DConverter.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.xml.text;

import org.jhotdraw8.css.text.Rectangle2DConverter;

/**
 * Converts a {@code javafx.geometry.Rectangle2D} into a {@code String} and vice
 * versa.
 *
 * @author Werner Randelshofer
 */
public class XmlRectangle2DConverter extends Rectangle2DConverter {

    public XmlRectangle2DConverter() {
        this(false, true);
    }

    public XmlRectangle2DConverter(boolean nullable) {
        this(nullable, true);
    }

    public XmlRectangle2DConverter(boolean nullable, boolean withSpace) {
        super(nullable, withSpace, false);
    }
}
