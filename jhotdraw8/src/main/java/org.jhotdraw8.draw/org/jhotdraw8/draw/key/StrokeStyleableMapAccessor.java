/*
 * @(#)StrokeStyleableMapAccessor.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.CssStrokeStyle;
import org.jhotdraw8.css.text.CssStrokeStyleConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.StyleConverterAdapter;

import java.util.Map;
import java.util.function.Function;

/**
 * Stroke Style combines all stroke attributes.
 *
 * @author Werner Randelshofer
 */
public class StrokeStyleableMapAccessor extends AbstractStyleableMapAccessor<CssStrokeStyle> {

    private final static long serialVersionUID = 1L;

    @NonNull
    private final CssMetaData<?, CssStrokeStyle> cssMetaData;

    @NonNull
    private final MapAccessor<CssSize> dashOffsetKey;
    @NonNull
    private final MapAccessor<ImmutableList<CssSize>> dashArrayKey;
    @NonNull
    private final MapAccessor<StrokeType> typeKey;
    @NonNull
    private final MapAccessor<StrokeLineJoin> lineJoinKey;
    @NonNull
    private final MapAccessor<StrokeLineCap> lineCapKey;
    @NonNull
    private final MapAccessor<CssSize> miterLimitKey;

    public StrokeStyleableMapAccessor(String name,
                                      @NonNull MapAccessor<StrokeType> typeKey,
                                      @NonNull MapAccessor<StrokeLineCap> lineCapKey,
                                      @NonNull MapAccessor<StrokeLineJoin> lineJoinKey,
                                      @NonNull MapAccessor<CssSize> miterLimitKey,
                                      @NonNull MapAccessor<CssSize> dashOffsetKey,
                                      @NonNull MapAccessor<ImmutableList<CssSize>> dashArrayKey
    ) {
        super(name, CssStrokeStyle.class, new MapAccessor<?>[]{
                        typeKey,
                        lineJoinKey,
                        lineCapKey,
                        miterLimitKey,
                        dashOffsetKey,
                        dashArrayKey
                },
                new CssStrokeStyle(
                        typeKey.getDefaultValue(), lineCapKey.getDefaultValue(), lineJoinKey.getDefaultValue(),
                        miterLimitKey.getDefaultValue(),
                        dashOffsetKey.getDefaultValue(),
                        dashArrayKey.getDefaultValue()
                ));

        Function<Styleable, StyleableProperty<CssStrokeStyle>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        CssMetaData<Styleable, CssStrokeStyle> md
                = new SimpleCssMetaData<>(property, function,
                new StyleConverterAdapter<>(converter), getDefaultValue(), inherits);
        cssMetaData = md;

        this.dashOffsetKey = dashOffsetKey;
        this.dashArrayKey = dashArrayKey;
        this.typeKey = typeKey;
        this.lineJoinKey = lineJoinKey;
        this.lineCapKey = lineCapKey;
        this.miterLimitKey = miterLimitKey;
    }

    @Override
    public @NonNull CssMetaData<? extends @NonNull Styleable, CssStrokeStyle> getCssMetaData() {
        return cssMetaData;

    }

    private final Converter<CssStrokeStyle> converter = new CssStrokeStyleConverter(false);

    @NonNull
    @Override
    public Converter<CssStrokeStyle> getConverter() {
        return converter;
    }

    @Override
    public CssStrokeStyle get(@NonNull Map<? super Key<?>, Object> a) {
        return new CssStrokeStyle(
                typeKey.get(a),
                lineCapKey.get(a),
                lineJoinKey.get(a),
                miterLimitKey.get(a),
                dashOffsetKey.get(a),
                dashArrayKey.get(a)
        );
    }

    @Override
    public void set(@NonNull Map<? super Key<?>, Object> a, @Nullable CssStrokeStyle value) {
        if (value == null) {
            dashOffsetKey.put(a, CssSize.ZERO);
            dashArrayKey.put(a, ImmutableLists.emptyList());
            typeKey.put(a, StrokeType.CENTERED);
            lineJoinKey.put(a, StrokeLineJoin.MITER);
            lineCapKey.put(a, StrokeLineCap.SQUARE);
            miterLimitKey.put(a, new CssSize(10.0));
        } else {
            dashOffsetKey.put(a, value.getDashOffset());
            dashArrayKey.put(a, value.getDashArray());
            typeKey.put(a, value.getType());
            lineJoinKey.put(a, value.getLineJoin());
            lineCapKey.put(a, value.getLineCap());
            miterLimitKey.put(a, value.getMiterLimit());
        }
    }

    @Override
    public CssStrokeStyle remove(@NonNull Map<? super Key<?>, Object> a) {
        CssStrokeStyle oldValue = get(a);
        typeKey.remove(a);
        lineJoinKey.remove(a);
        lineCapKey.remove(a);
        miterLimitKey.remove(a);
        dashOffsetKey.remove(a);
        dashArrayKey.remove(a);
        return oldValue;
    }

    /**
     * This is a non-standard map composite map accessor and thus it is transient.
     * We only used in the GUI to get a more concise presentation of attributes.
     *
     * @return true
     */
    @Override
    public boolean isTransient() {
        return true;
    }
}
