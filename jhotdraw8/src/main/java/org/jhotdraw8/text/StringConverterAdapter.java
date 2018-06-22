/* @(#)StringConverterAdapter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.text.ParseException;
import javafx.util.StringConverter;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Allows to use a {@code Converter} with the
 * {@code javafx.util.StringConverter} API.
 *
 * @author Werner Randelshofer
 * @version $Id$
 rawcoder $
 * @param <T> the value type
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
    public T fromString(@NonNull String string) {
        try {
            return converter.fromString(string);
        } catch (@NonNull ParseException | IOException ex) {
            return converter.getDefaultValue();
        }
    }

}
