/*
 * @(#)StrokeStyleableMapAccessor.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

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
import org.jhotdraw8.text.Converter;

import java.util.Map;

/**
 * Stroke Style combines all stroke attributes.
 *
 * @author Werner Randelshofer
 */
public class StrokeStyleableMapAccessor extends AbstractStyleableMapAccessor<CssStrokeStyle> {

    private static final long serialVersionUID = 1L;


    private final @NonNull MapAccessor<CssSize> dashOffsetKey;
    private final @NonNull MapAccessor<ImmutableList<CssSize>> dashArrayKey;
    private final @NonNull MapAccessor<StrokeType> typeKey;
    private final @NonNull MapAccessor<StrokeLineJoin> lineJoinKey;
    private final @NonNull MapAccessor<StrokeLineCap> lineCapKey;
    private final @NonNull MapAccessor<CssSize> miterLimitKey;
    private final Converter<CssStrokeStyle> converter = new CssStrokeStyleConverter(false);

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

        this.dashOffsetKey = dashOffsetKey;
        this.dashArrayKey = dashArrayKey;
        this.typeKey = typeKey;
        this.lineJoinKey = lineJoinKey;
        this.lineCapKey = lineCapKey;
        this.miterLimitKey = miterLimitKey;
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
    public @NonNull Converter<CssStrokeStyle> getCssConverter() {
        return converter;
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
}
