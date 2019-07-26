/*
 * @(#)TransformListStyleableKey.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.collection.NonnullMapAccessor;
import org.jhotdraw8.css.text.CssListConverter;
import org.jhotdraw8.css.text.CssTransformConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.StyleConverterAdapter;

import java.util.function.Function;

/**
 * TransformListStyleableKey.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class TransformListStyleableKey extends AbstractStyleableKey<ImmutableList<Transform>>
        implements WriteableStyleableMapAccessor<ImmutableList<Transform>>, NonnullMapAccessor<ImmutableList<Transform>> {

    private final static long serialVersionUID = 1L;

    @Nonnull
    private final CssMetaData<?, ImmutableList<Transform>> cssMetaData;
    private Converter<ImmutableList<Transform>> converter;

    /**
     * Creates a new instance with the specified name and with an empty list as the
     * default value.
     *
     * @param name The name of the key.
     */
    public TransformListStyleableKey(String name) {
        this(name, ImmutableLists.emptyList());
    }

    /**
     * Creates a new instance with the specified name and default value.
     *
     * @param name         The name of the key.
     * @param defaultValue The default value.
     */
    public TransformListStyleableKey(String name, ImmutableList<Transform> defaultValue) {
        this(name, DirtyMask.of(DirtyBits.NODE), defaultValue);
    }

    /**
     * Creates a new instance with the specified name, mask and default value.
     *
     * @param name         The name of the key.
     * @param mask         The dirty mask.
     * @param defaultValue The default value.
     */
    public TransformListStyleableKey(String name, DirtyMask mask, ImmutableList<Transform> defaultValue) {
        super(name, ImmutableList.class, new Class<?>[]{Transform.class}, defaultValue);

        Function<Styleable, StyleableProperty<ImmutableList<Transform>>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        converter = new CssListConverter<>(new CssTransformConverter());
        CssMetaData<Styleable, ImmutableList<Transform>> md
                = new SimpleCssMetaData<>(property, function,
                new StyleConverterAdapter<>(converter), defaultValue, inherits);
        cssMetaData = md;
    }

    @Nonnull
    @Override
    public CssMetaData<?, ImmutableList<Transform>> getCssMetaData() {
        return cssMetaData;
    }

    @Override
    public Converter<ImmutableList<Transform>> getConverter() {
        return converter;
    }

}
