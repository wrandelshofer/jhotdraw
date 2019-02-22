package org.jhotdraw8.samples.diagrammer.model;

import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.key.AbstractStyleableFigureKey;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.SimpleCssMetaData;
import org.jhotdraw8.samples.diagrammer.text.CssUmlCompartmentalizedDataConverter;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.StyleConverterAdapter;

import java.util.function.Function;

public class UmlCompartmentedDataStyleableFigureKey extends AbstractStyleableFigureKey<UmlCompartmentalizedData>
        implements WriteableStyleableMapAccessor<UmlCompartmentalizedData>/*, NonnullMapAccessor<UmlCompartmentalizedData>*/ {

    final static long serialVersionUID = 1L;
    @Nonnull
    private final CssMetaData<? extends Styleable, UmlCompartmentalizedData> cssMetaData;

    /**
     * Creates a new instance with the specified name and with an empty String
     * as the default value.
     *
     * @param name The name of the key.
     */
    public UmlCompartmentedDataStyleableFigureKey(String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name and default value.
     *
     * @param name         The name of the key.
     * @param defaultValue The default value.
     */
    public UmlCompartmentedDataStyleableFigureKey(String name, UmlCompartmentalizedData defaultValue) {
        this(name, DirtyMask.of(DirtyBits.NODE), defaultValue, null);
    }

    /**
     * Creates a new instance with the specified name, mask and default value.
     *
     * @param name         The name of the key.
     * @param mask         The dirty mask.
     * @param defaultValue The default value.
     */
    public UmlCompartmentedDataStyleableFigureKey(String name, DirtyMask mask, UmlCompartmentalizedData defaultValue) {
        this(name, DirtyMask.of(DirtyBits.NODE), defaultValue, null);
    }

    public UmlCompartmentedDataStyleableFigureKey(String name, DirtyMask mask, boolean nullable, UmlCompartmentalizedData defaultValue) {
        this(name, DirtyMask.of(DirtyBits.NODE), nullable, defaultValue, null);
    }

    public UmlCompartmentedDataStyleableFigureKey(String name, DirtyMask mask, UmlCompartmentalizedData defaultValue, String helpText) {
        this(name, DirtyMask.of(DirtyBits.NODE), false, defaultValue, helpText);
    }

    /**
     * Creates a new instance with the specified name, mask and default value.
     *
     * @param name         The name of the key.
     * @param mask         The dirty mask.
     * @param nullable     Whether the value may be set to null
     * @param defaultValue The default value.
     * @param helpText     the help text
     */
    public UmlCompartmentedDataStyleableFigureKey(String name, DirtyMask mask, boolean nullable, UmlCompartmentalizedData defaultValue, String helpText) {
        super(name, UmlCompartmentalizedData.class, nullable, mask, defaultValue);
        converter = new CssUmlCompartmentalizedDataConverter(true);
        Function<Styleable, StyleableProperty<UmlCompartmentalizedData>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, UmlCompartmentalizedData> converter = new StyleConverterAdapter<>(this.converter);
        CssMetaData<Styleable, UmlCompartmentalizedData> md
                = new SimpleCssMetaData<>(property, function,
                converter, defaultValue, inherits);
        cssMetaData = md;
    }

    @Nonnull
    @Override
    public CssMetaData<? extends Styleable, UmlCompartmentalizedData> getCssMetaData() {
        return cssMetaData;

    }

    @Nonnull
    private final CssUmlCompartmentalizedDataConverter converter;

    @Nonnull
    @Override
    public Converter<UmlCompartmentalizedData> getConverter() {
        return converter;
    }
}
