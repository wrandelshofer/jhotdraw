/* @(#)SymmetricPoint2DStyleableMapAccessor.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import java.util.Map;
import java.util.function.Function;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.geometry.Point2D;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.CssSymmetricPoint2DConverter;
import org.jhotdraw8.text.StyleConverterAdapter;

/**
 * SymmetricPoint2DStyleableMapAccessor.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SymmetricPoint2DStyleableMapAccessor extends Point2DStyleableMapAccessor  {

    private final static long serialVersionUID = 1L;



    private Converter<Point2D> converter;

    public SymmetricPoint2DStyleableMapAccessor(String name, MapAccessor<Double> xKey, MapAccessor<Double> yKey) {
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
