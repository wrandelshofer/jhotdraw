package org.jhotdraw8.styleable;

import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.StringKey;
import org.jhotdraw8.css.text.CssStringConverter;
import org.jhotdraw8.draw.key.SimpleCssMetaData;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.StyleConverterAdapter;

import java.util.function.Function;

public class StringStyleableKey extends StringKey implements WriteableStyleableMapAccessor<String> {
    private final static long serialVersionUID = 0L;
    private final String cssName;
    private final CssMetaData<? extends Styleable, String> cssMetaData;
    private final CssStringConverter converter = new CssStringConverter();

    public StringStyleableKey(String key) {
        this(key, null);
    }

    public StringStyleableKey(String key, String defaultValue) {
        super(key, defaultValue);

        Function<Styleable, StyleableProperty<String>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        final StyleConverter<String, String> styleConverter
                = new StyleConverterAdapter<>(this.converter);
        cssMetaData = new SimpleCssMetaData<>(key, function, styleConverter, defaultValue, false);
        cssName = ReadableStyleableMapAccessor.toCssName(getName());
    }

    @Nullable
    @Override
    public CssMetaData<? extends Styleable, String> getCssMetaData() {
        return cssMetaData;
    }

    @Override
    public Converter<String> getConverter() {
        return converter;
    }

    @Nonnull
    @Override
    public String getCssName() {
        return cssName;
    }
}
