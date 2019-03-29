/* @(#)Rectangle2DStyleableMapAccessor.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.NonnullMapAccessor;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.text.CssRectangle2DConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.StyleConverterAdapter;

import java.util.Map;
import java.util.function.Function;

/**
 * Rectangle2DStyleableMapAccessor.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssRectangle2DStyleableMapAccessor extends AbstractStyleableFigureMapAccessor<CssRectangle2D>
        implements NonnullMapAccessor<CssRectangle2D> {

    private final static long serialVersionUID = 1L;

    @Nonnull
    private final CssMetaData<?, CssRectangle2D> cssMetaData;
    @Nonnull
    private final NonnullMapAccessor<CssSize> xKey;
    @Nonnull
    private final NonnullMapAccessor<CssSize> yKey;
    @Nonnull
    private final NonnullMapAccessor<CssSize> widthKey;
    @Nonnull
    private final NonnullMapAccessor<CssSize> heightKey;

    /**
     * Creates a new instance with the specified name.
     *
     * @param name      the name of the accessor
     * @param xKey      the key for the x coordinate of the rectangle
     * @param yKey      the key for the y coordinate of the rectangle
     * @param widthKey  the key for the width of the rectangle
     * @param heightKey the key for the height of the rectangle
     */
    public CssRectangle2DStyleableMapAccessor(String name, NonnullMapAccessor<CssSize> xKey, NonnullMapAccessor<CssSize> yKey, NonnullMapAccessor<CssSize> widthKey, NonnullMapAccessor<CssSize> heightKey) {
        super(name, CssRectangle2D.class, new NonnullMapAccessor<?>[]{xKey, yKey, widthKey, heightKey}, new CssRectangle2D(
                xKey.getDefaultValueNonnull(),
                yKey.getDefaultValueNonnull(),
                widthKey.getDefaultValueNonnull(),
                heightKey.getDefaultValueNonnull()));

        Function<Styleable, StyleableProperty<CssRectangle2D>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        CssMetaData<Styleable, CssRectangle2D> md
                = new SimpleCssMetaData<>(property, function,
                new StyleConverterAdapter<>(converter), getDefaultValue(), inherits);
        cssMetaData = md;

        this.xKey = xKey;
        this.yKey = yKey;
        this.widthKey = widthKey;
        this.heightKey = heightKey;
    }

    @Nonnull
    @Override
    public CssMetaData<?, CssRectangle2D> getCssMetaData() {
        return cssMetaData;

    }

    private final Converter<CssRectangle2D> converter = new CssRectangle2DConverter(false);

    @Override
    public Converter<CssRectangle2D> getConverter() {
        return converter;
    }

    @Nonnull
    @Override
    public CssRectangle2D get(@Nonnull Map<? super Key<?>, Object> a) {
        return new CssRectangle2D(xKey.get(a),
                yKey.get(a),
                widthKey.get(a),
                heightKey.get(a));
    }

    @Nonnull
    @Override
    public CssRectangle2D put(@Nonnull Map<? super Key<?>, Object> a, @Nullable CssRectangle2D value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        CssRectangle2D oldValue = get(a);
        xKey.put(a, value.getMinX());
        yKey.put(a, value.getMinY());
        widthKey.put(a, value.getWidth());
        heightKey.put(a, value.getHeight());
        return oldValue;
    }

    @Nonnull
    @Override
    public CssRectangle2D remove(@Nonnull Map<? super Key<?>, Object> a) {
        CssRectangle2D oldValue = get(a);
        xKey.remove(a);
        yKey.remove(a);
        widthKey.remove(a);
        heightKey.remove(a);
        return oldValue;
    }

}
