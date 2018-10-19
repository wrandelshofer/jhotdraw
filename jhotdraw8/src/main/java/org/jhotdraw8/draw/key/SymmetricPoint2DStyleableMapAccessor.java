/* @(#)SymmetricPoint2DStyleableMapAccessor.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.geometry.Point2D;
import javax.annotation.Nonnull;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.css.text.CssSymmetricPoint2DConverter;

/**
 * SymmetricPoint2DStyleableMapAccessor.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SymmetricPoint2DStyleableMapAccessor extends Point2DStyleableMapAccessor  {

    private final static long serialVersionUID = 1L;



    private Converter<Point2D> converter;

    public SymmetricPoint2DStyleableMapAccessor(String name, @Nonnull MapAccessor<Double> xKey, @Nonnull MapAccessor<Double> yKey) {
        super(name, xKey, yKey);
    }

    @Override
    public Converter<Point2D> getConverter() {
        if (converter == null) {
            converter = new CssSymmetricPoint2DConverter();
        }
        return converter;
    }

}
