/*
 * @(#)StringConverterConverterWrapper.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.text;

import java.io.IOException;
import java.text.ParseException;
import javafx.util.StringConverter;

/**
 * Allows to use a {@code Converter} with the {@code javafx.util.StringConverter}
 * API.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @param <T> the value type
 */
public class StringConverterConverterWrapper<T> extends StringConverter<T> {

    private final Converter<T> converter;

    public StringConverterConverterWrapper(Converter<T> converter) {
        this.converter = converter;
    }

    @Override
    public String toString(T object) {
        return converter.toString(object);
    }

    @Override
    public T fromString(String string) {
        try {
            return converter.fromString(string);
        } catch (ParseException|IOException ex) {
            return converter.getDefaultValue();
        }
    }

}
