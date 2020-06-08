/*
 * @(#)SymmetricPoint2DStyleableMapAccessor.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.geometry.Point2D;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.NonNullMapAccessor;
import org.jhotdraw8.css.text.CssSymmetricPoint2DConverterOLD;

/**
 * SymmetricPoint2DStyleableMapAccessor.
 *
 * @author Werner Randelshofer
 */
public class SymmetricPoint2DStyleableMapAccessor extends Point2DStyleableMapAccessor
        implements NonNullMapAccessor<Point2D> {

    private final static long serialVersionUID = 1L;

    public SymmetricPoint2DStyleableMapAccessor(String name, @NonNull NonNullMapAccessor<Double> xKey, @NonNull NonNullMapAccessor<Double> yKey) {
        super(name, xKey, yKey, new CssSymmetricPoint2DConverterOLD(false));
    }
}
