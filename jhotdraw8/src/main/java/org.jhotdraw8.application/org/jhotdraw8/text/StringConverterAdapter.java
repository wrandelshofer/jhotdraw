/*
 * @(#)StringConverterAdapter.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import javafx.util.StringConverter;
import org.jhotdraw8.annotation.Nonnull;

import java.io.IOException;
import java.text.ParseException;

/**
 * Allows to use a {@code Converter} with the
 * {@code javafx.util.StringConverter} API.
 *
 * @param <T> the value type
 * @author Werner Randelshofer
 */
public class StringConverterAdapter<T> extends StringConverter<T> {

    private final Converter<T> converter;

    public StringConverterAdapter(Converter<T> converter) {
        this.converter = converter;
    }

    @Override
    public String toString(T object) {
        return converter.toString(object);
    }

    @Override
    public T fromString(@Nonnull String string) {
        try {
            return converter.fromString(string);
        } catch (ParseException | IOException ex) {
            return converter.getDefaultValue();
        }
    }

}
