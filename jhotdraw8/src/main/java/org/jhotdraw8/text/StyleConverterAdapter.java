/* @(#)StyleConverterAdapter.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.text.ParseException;
import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.scene.text.Font;
import javax.annotation.Nonnull;

/**
 * Allows to use a {@code Converter} with the {@code javafx.css.StyleConverter}
 * API.
 *
 * @author Werner Randelshofer
 * @version $Id$
 rawcoder $
 */
public class StyleConverterAdapter<T> extends StyleConverter<String, T> {

    private Converter<T> converter;

    public StyleConverterAdapter(Converter<T> converter) {
        this.converter = converter;
    }

    @Override
    public T convert(ParsedValue<String, T> value, Font font) {
        try {
            return converter.fromString(value.getValue());
        } catch (@Nonnull ParseException | IOException ex) {
            return converter.getDefaultValue();
        }
    }
}
