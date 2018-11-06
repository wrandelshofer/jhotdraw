/* @(#)FontStyleableMapAccessor.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.css.CssStroke;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.Paintable;
import org.jhotdraw8.css.text.CssStrokeConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.StyleConverterAdapter;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Function;

/**
 * StrokeStyleableMapAccessor.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class StrokeStyleableMapAccessor extends AbstractStyleableFigureMapAccessor<CssStroke> {

    private final static long serialVersionUID = 1L;

    @Nonnull
    private final CssMetaData<?, CssStroke> cssMetaData;

    @Nonnull
    private final MapAccessor<CssSize> widthKey;
    private final MapAccessor<Paintable> paintKey;
    private final MapAccessor<CssSize> dashOffsetKey;
    private final MapAccessor<ImmutableList<CssSize>> dashArrayKey;
    private final MapAccessor<StrokeType> typeKey;
    private final MapAccessor<StrokeLineJoin> lineJoinKey;
    private final MapAccessor<StrokeLineCap> lineCapKey;
    private final MapAccessor<CssSize> miterLimitKey;

    public StrokeStyleableMapAccessor(String name,
                                      MapAccessor<CssSize> widthKey,
                                      MapAccessor<Paintable> paintKey,
                                      MapAccessor<StrokeType> typeKey,
                                      MapAccessor<StrokeLineCap> lineCapKey, MapAccessor<StrokeLineJoin> lineJoinKey,
                                      MapAccessor<CssSize> miterLimitKey,
                                      MapAccessor<CssSize> dashOffsetKey,
                                      MapAccessor<ImmutableList<CssSize>> dashArrayKey
    ) {
        super(name, CssStroke.class, new MapAccessor<?>[]{
                        widthKey,
                        paintKey,
                        typeKey,
                        lineJoinKey,
                        lineCapKey,
                        miterLimitKey,
                        dashOffsetKey,
                        dashArrayKey
                },
                new CssStroke(widthKey.getDefaultValue(), paintKey.getDefaultValue(),
                        typeKey.getDefaultValue(), lineCapKey.getDefaultValue(), lineJoinKey.getDefaultValue(),
                        miterLimitKey.getDefaultValue(),
                        dashOffsetKey.getDefaultValue(),
                        dashArrayKey.getDefaultValue()
                ));

        Function<Styleable, StyleableProperty<CssStroke>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        CssMetaData<Styleable, CssStroke> md
                = new SimpleCssMetaData<>(property, function,
                new StyleConverterAdapter<>(converter), getDefaultValue(), inherits);
        cssMetaData = md;

        this.widthKey = widthKey;
        this.paintKey = paintKey;
        this.dashOffsetKey = dashOffsetKey;
        this.dashArrayKey = dashArrayKey;
        this.typeKey = typeKey;
        this.lineJoinKey = lineJoinKey;
        this.lineCapKey = lineCapKey;
        this.miterLimitKey = miterLimitKey;
    }

    @Nonnull
    @Override
    public CssMetaData<?, CssStroke> getCssMetaData() {
        return cssMetaData;

    }

    private final Converter<CssStroke> converter = new CssStrokeConverter(false);
    ;

    @Override
    public Converter<CssStroke> getConverter() {
        return converter;
    }

    @Override
    public CssStroke get(Map<? super Key<?>, Object> a) {
        return new CssStroke(
                widthKey.get(a), paintKey.get(a),
                typeKey.get(a), lineCapKey.get(a), lineJoinKey.get(a),
                miterLimitKey.get(a),
                dashOffsetKey.get(a),
                dashArrayKey.get(a)
        );
    }

    @Override
    public CssStroke put(Map<? super Key<?>, Object> a, @Nonnull CssStroke value) {
        CssStroke oldValue = get(a);
        widthKey.put(a, value.getWidth());
        paintKey.put(a, value.getPaint());
        dashOffsetKey.put(a, value.getDashOffset());
        dashArrayKey.put(a, value.getDashArray());
        typeKey.put(a, value.getType());
        lineJoinKey.put(a, value.getLineJoin());
        lineCapKey.put(a, value.getLineCap());
        miterLimitKey.put(a, value.getMiterLimit());

        return oldValue;
    }

    @Override
    public CssStroke remove(Map<? super Key<?>, Object> a) {
        CssStroke oldValue = get(a);
        widthKey.remove(a);
        paintKey.remove(a);
        typeKey.remove(a);
        lineJoinKey.remove(a);
        lineCapKey.remove(a);
        miterLimitKey.remove(a);
        dashOffsetKey.remove(a);
        dashArrayKey.remove(a);
        return oldValue;
    }

    @Override
    public boolean isNullable() {
        return false;
    }
}
