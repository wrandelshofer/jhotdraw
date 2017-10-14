/* @(#)Rectangle2DStyleableMapAccessor.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import static java.lang.Double.max;
import java.util.Map;
import java.util.function.Function;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.geometry.Rectangle2D;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.CssRectangle2DConverter;
import org.jhotdraw8.text.StyleConverterAdapter;

/**
 * Rectangle2DStyleableMapAccessor.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class Rectangle2DMapAccessor extends AbstractFigureMapAccessor<Rectangle2D> {

    private final static long serialVersionUID = 1L;

    private final MapAccessor<Double> xKey;
    private final MapAccessor<Double> yKey;
    private final MapAccessor<Double> widthKey;
    private final MapAccessor<Double> heightKey;

    /**
     * Creates a new instance with the specified name.
     *
     * @param name the name of the accessor
     * @param xKey the key for the x coordinate of the rectangle
     * @param yKey the key for the y coordinate of the rectangle
     * @param widthKey the key for the width of the rectangle
     * @param heightKey the key for the height of the rectangle
     */
    public Rectangle2DMapAccessor(String name, MapAccessor<Double> xKey, MapAccessor<Double> yKey, MapAccessor<Double> widthKey, MapAccessor<Double> heightKey) {
        super(name, Rectangle2D.class, new MapAccessor<?>[]{xKey, yKey, widthKey, heightKey}, new Rectangle2D(xKey.getDefaultValue(), yKey.getDefaultValue(), widthKey.getDefaultValue(), heightKey.getDefaultValue()));

        this.xKey = xKey;
        this.yKey = yKey;
        this.widthKey = widthKey;
        this.heightKey = heightKey;
    }


    @Override
    public Rectangle2D get(Map<? super Key<?>, Object> a) {
        return new Rectangle2D(xKey.get(a), yKey.get(a), max(0.0,widthKey.get(a)), max(0.0,heightKey.get(a)));
    }

    @Override
    public Rectangle2D put(Map<? super Key<?>, Object> a, Rectangle2D value) {
        Rectangle2D oldValue = get(a);
        xKey.put(a, value.getMinX());
        yKey.put(a, value.getMinY());
        widthKey.put(a, value.getWidth());
        heightKey.put(a, value.getHeight());
        return oldValue;
    }

    @Override
    public Rectangle2D remove(Map<? super Key<?>, Object> a) {
        Rectangle2D oldValue = get(a);
        xKey.remove(a);
        yKey.remove(a);
        widthKey.remove(a);
        heightKey.remove(a);
        return oldValue;
    }

}
