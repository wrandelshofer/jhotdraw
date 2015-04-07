/* @(#)ConverterStringConverterWrapper.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */

package org.jhotdraw.text;

import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.StringConverter;

/**
 * ConverterStringConverterWrapper.
 * @author Werner Randelshofer
 * @version $Id$
 * @param <T> the value type
 */
public class ConverterStringConverterWrapper<T> extends StringConverter<T> {
    private final Converter<T> converter;
    
    public ConverterStringConverterWrapper(Converter<T> converter) {
        this.converter = converter;
    }
    

    @Override
    public String toString(T object) {
        return converter.toString(object);
    }

    @Override
    public T fromString(String string) {
        try {
            return converter.toValue(string);
        } catch (ParseException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

  

}
