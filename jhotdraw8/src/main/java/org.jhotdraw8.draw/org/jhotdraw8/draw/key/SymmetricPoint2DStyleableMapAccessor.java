/*
 * @(#)SymmetricPoint2DStyleableMapAccessor.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.geometry.Point2D;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.collection.NonnullMapAccessor;
import org.jhotdraw8.css.text.CssSymmetricPoint2DConverterOLD;

/**
 * SymmetricPoint2DStyleableMapAccessor.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SymmetricPoint2DStyleableMapAccessor extends Point2DStyleableMapAccessor
        implements NonnullMapAccessor<Point2D> {

    private final static long serialVersionUID = 1L;

    public SymmetricPoint2DStyleableMapAccessor(String name, @Nonnull NonnullMapAccessor<Double> xKey, @Nonnull NonnullMapAccessor<Double> yKey) {
        super(name, xKey, yKey, new CssSymmetricPoint2DConverterOLD(false));
    }
}
