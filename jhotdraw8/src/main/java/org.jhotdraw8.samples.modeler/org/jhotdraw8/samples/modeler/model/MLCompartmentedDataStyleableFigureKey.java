/*
 * @(#)MLCompartmentedDataStyleableFigureKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.modeler.model;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.NonNullMapAccessor;
import org.jhotdraw8.draw.key.AbstractStyleableKey;
import org.jhotdraw8.samples.modeler.text.CssUmlCompartmentalizedDataConverter;
import org.jhotdraw8.styleable.WritableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;

public class MLCompartmentedDataStyleableFigureKey extends AbstractStyleableKey<@NonNull MLCompartmentalizedData>
        implements WritableStyleableMapAccessor<@NonNull MLCompartmentalizedData>, NonNullMapAccessor<@NonNull MLCompartmentalizedData> {

    static final long serialVersionUID = 1L;
    private final @NonNull CssUmlCompartmentalizedDataConverter converter;

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
    }

    @Override
    public @NonNull Converter<MLCompartmentalizedData> getCssConverter() {
        return converter;
    }
}
