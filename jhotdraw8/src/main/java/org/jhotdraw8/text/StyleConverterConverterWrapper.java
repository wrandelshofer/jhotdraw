/* @(#)StyleConverterConverterWrapper.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.text.ParseException;
import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.scene.text.Font;

/**
 * Allows to use a {@code Converter} with the {@code javafx.css.StyleConverter}
 * API.
 *
 * @author Werner Randelshofer
 * @version $Id: StyleConverterConverterWrapper.java 1149 2016-11-18 11:00:10Z
 * rawcoder $
 */
public class StyleConverterConverterWrapper<T> extends StyleConverter<String, T> {

    private Converter<T> converter;

    public StyleConverterConverterWrapper(Converter<T> converter) {
        this.converter = converter;
    }

    @Override
    public T convert(ParsedValue<String, T> value, Font font) {
        try {
            return converter.fromString(value.getValue());
        } catch (ParseException | IOException ex) {
            return converter.getDefaultValue();
        }
    }
}
