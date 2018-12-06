/* @(#)StringConverterAdapter.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.text.ParseException;
import javafx.util.StringConverter;
import javax.annotation.Nonnull;

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
    public T fromString(@Nonnull String string) {
        try {
            return converter.fromString(string);
        } catch (@Nonnull ParseException | IOException ex) {
            return converter.getDefaultValue();
        }
    }

}
