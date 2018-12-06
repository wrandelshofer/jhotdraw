/* @(#)Rectangle2DStyleableMapAccessor.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import static java.lang.Double.max;
import java.util.Map;

import javafx.geometry.Rectangle2D;
import javax.annotation.Nonnull;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;

/**
 * Rectangle2DStyleableMapAccessor.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class Rectangle2DMapAccessor extends AbstractFigureMapAccessor<Rectangle2D> {

    private final static long serialVersionUID = 1L;

    @Nonnull
    private final MapAccessor<Double> xKey;
    @Nonnull
    private final MapAccessor<Double> yKey;
    @Nonnull
    private final MapAccessor<Double> widthKey;
    @Nonnull
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


    @Nonnull
    @Override
    public Rectangle2D get(Map<? super Key<?>, Object> a) {
        return new Rectangle2D(xKey.get(a), yKey.get(a), max(0.0,widthKey.get(a)), max(0.0,heightKey.get(a)));
    }

    @Nonnull
    @Override
    public Rectangle2D put(Map<? super Key<?>, Object> a, @Nonnull Rectangle2D value) {
        Rectangle2D oldValue = get(a);
        xKey.put(a, value.getMinX());
        yKey.put(a, value.getMinY());
        widthKey.put(a, value.getWidth());
        heightKey.put(a, value.getHeight());
        return oldValue;
    }

    @Nonnull
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
