package org.jhotdraw8.samples.modeler.model;

import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.key.AbstractStyleableFigureKey;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.SimpleCssMetaData;
import org.jhotdraw8.samples.modeler.text.CssUmlCompartmentalizedDataConverter;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.StyleConverterAdapter;

import java.util.function.Function;

public class MLCompartmentedDataStyleableFigureKey extends AbstractStyleableFigureKey<MLCompartmentalizedData>
        implements WriteableStyleableMapAccessor<MLCompartmentalizedData>/*, NonnullMapAccessor<MLCompartmentalizedData>*/ {

    final static long serialVersionUID = 1L;
    @Nonnull
    private final CssMetaData<? extends Styleable, MLCompartmentalizedData> cssMetaData;

    /**
     * Creates a new instance with the specified name and with an empty String
     * as the default value.
     *
     * @param namespace The namespace
     * @param name The name of the key.
     */
    public MLCompartmentedDataStyleableFigureKey(@Nullable String namespace, String name) {
        this(namespace, name, null);
    }

    /**
     * Creates a new instance with the specified name and default value.
     *
     * @param namespace The namespace
     * @param name         The name of the key.
     * @param defaultValue The default value.
     */
    public MLCompartmentedDataStyleableFigureKey(@Nullable String namespace, String name, MLCompartmentalizedData defaultValue) {
        this(namespace, name, DirtyMask.of(DirtyBits.NODE), defaultValue, null);
    }

    /**
     * Creates a new instance with the specified name, mask and default value.
     *
     * @param namespace The namespace
     * @param name         The name of the key.
     * @param mask         The dirty mask.
     * @param defaultValue The default value.
     */
    public MLCompartmentedDataStyleableFigureKey(@Nullable String namespace, String name, DirtyMask mask, MLCompartmentalizedData defaultValue) {
        this(namespace, name, DirtyMask.of(DirtyBits.NODE), defaultValue, null);
    }

    public MLCompartmentedDataStyleableFigureKey(@Nullable String namespace, String name, DirtyMask mask, boolean nullable, MLCompartmentalizedData defaultValue) {
        this(namespace, name, DirtyMask.of(DirtyBits.NODE), nullable, defaultValue, null);
    }

    public MLCompartmentedDataStyleableFigureKey(@Nullable String namespace, String name, DirtyMask mask, MLCompartmentalizedData defaultValue, String helpText) {
        this(namespace, name, DirtyMask.of(DirtyBits.NODE), false, defaultValue, helpText);
    }

    /**
     * Creates a new instance with the specified name, mask and default value.
     *
     * @param namespace The namespace
     * @param name         The name of the key.
     * @param mask         The dirty mask.
     * @param nullable     Whether the value may be set to null
     * @param defaultValue The default value.
     * @param helpText     the help text
     */
    public MLCompartmentedDataStyleableFigureKey(@Nullable String namespace, String name, DirtyMask mask, boolean nullable, MLCompartmentalizedData defaultValue, String helpText) {
        super(namespace, name, MLCompartmentalizedData.class, nullable, mask, defaultValue);
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

    @Nonnull
    @Override
    public CssMetaData<? extends Styleable, MLCompartmentalizedData> getCssMetaData() {
        return cssMetaData;

    }

    @Nonnull
    private final CssUmlCompartmentalizedDataConverter converter;

    @Nonnull
    @Override
    public Converter<MLCompartmentalizedData> getConverter() {
        return converter;
    }
}
