/*
 * @(#)MLCompartmentedDataStyleableFigureKey.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.modeler.model;

import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.NonNullMapAccessor;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.key.AbstractStyleableKey;
import org.jhotdraw8.draw.key.SimpleCssMetaData;
import org.jhotdraw8.samples.modeler.text.CssUmlCompartmentalizedDataConverter;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.StyleConverterAdapter;

import java.util.function.Function;

public class MLCompartmentedDataStyleableFigureKey extends AbstractStyleableKey<MLCompartmentalizedData>
        implements WriteableStyleableMapAccessor<MLCompartmentalizedData>, NonNullMapAccessor<MLCompartmentalizedData> {

    final static long serialVersionUID = 1L;
    @NonNull
    private final CssMetaData<? extends Styleable, MLCompartmentalizedData> cssMetaData;

    /**
     * Creates a new instance with the specified name and with an empty String
     * as the default value.
     *
     * @param namespace The namespace
     * @param name      The name of the key.
     */
    public MLCompartmentedDataStyleableFigureKey(@Nullable String namespace, @NonNull String name) {
        this(namespace, name, new MLCompartmentalizedData());
    }

    /**
     * Creates a new instance with the specified name, mask and default value.
     *  @param namespace    The namespace
     * @param name         The name of the key.
     * @param defaultValue The default value.
     */
    public MLCompartmentedDataStyleableFigureKey(@Nullable String namespace, @NonNull String name, MLCompartmentalizedData defaultValue) {
        this(namespace, name, defaultValue, null);
    }


    /**
     * Creates a new instance with the specified name, mask and default value.
     * @param namespace    The namespace
     * @param name         The name of the key.
     * @param defaultValue The default value.
     * @param helpText     the help text
     */
    public MLCompartmentedDataStyleableFigureKey(@Nullable String namespace, @NonNull String name, MLCompartmentalizedData defaultValue, String helpText) {
        super(namespace, name, MLCompartmentalizedData.class, false, defaultValue);
        converter = new CssUmlCompartmentalizedDataConverter(true);
        Function<Styleable, StyleableProperty<MLCompartmentalizedData>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, MLCompartmentalizedData> converter = new StyleConverterAdapter<>(this.converter);
        CssMetaData<Styleable, MLCompartmentalizedData> md
                = new SimpleCssMetaData<>(property, function,
                converter, defaultValue, inherits);
        cssMetaData = md;
    }

    @NonNull
    @Override
    public CssMetaData<? extends Styleable, MLCompartmentalizedData> getCssMetaData() {
        return cssMetaData;

    }

    @NonNull
    private final CssUmlCompartmentalizedDataConverter converter;

    @NonNull
    @Override
    public Converter<MLCompartmentalizedData> getConverter() {
        return converter;
    }
}
