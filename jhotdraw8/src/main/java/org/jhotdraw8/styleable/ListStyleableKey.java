package org.jhotdraw8.styleable;

import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.effect.BlendMode;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ListKey;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.key.SimpleCssMetaData;
import org.jhotdraw8.geom.BezierNode;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.CssConverter;
import org.jhotdraw8.text.CssConverterConverterAdapter;
import org.jhotdraw8.text.CssListConverter;
import org.jhotdraw8.text.StyleConverterAdapter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;

public class ListStyleableKey<T> extends ListKey<T> implements WriteableStyleableMapAccessor<ImmutableList<T>> {
    @Nonnull
    private final Converter<ImmutableList<T>> converter;
    private final CssMetaData<? extends Styleable, ImmutableList<T>> cssMetaData;
    private final String cssName;

    public ListStyleableKey(@Nonnull String key, @Nonnull Class<T> elemClass, @Nonnull CssConverter<T> converter) {
        this(key, elemClass, ImmutableList.emptyList(), converter);
    }

    public ListStyleableKey(@Nonnull String key, @Nonnull Class<T> elemClass, @Nonnull ImmutableList<T> defaultValue, @Nonnull CssConverter<T> converter) {
        super(key, elemClass, defaultValue);
        this.converter = new CssConverterConverterAdapter<>(new CssListConverter<>(converter));

        Function<Styleable, StyleableProperty<ImmutableList<T>>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        final StyleConverter<String, ImmutableList<T>> styleConverter
                = new StyleConverterAdapter<>(this.converter);
        cssMetaData = new SimpleCssMetaData<>(key, function, styleConverter, defaultValue, false);
        this.cssName = ReadOnlyStyleableMapAccessor.toCssName(key);

    }

    @Nullable
    @Override
    public CssMetaData<? extends Styleable, ImmutableList<T>> getCssMetaData() {
        return cssMetaData;
    }

    @Override
    public Converter<ImmutableList<T>> getConverter() {
        return converter;
    }

    @Nonnull
    @Override
    public String getCssName() {
        return cssName;
    }
}
