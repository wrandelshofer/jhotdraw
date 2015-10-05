/*
 * @(#)FormatConverterWrapper.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.text;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

/**
 * Allows to use a {@code Converter} with the {@code java.text.Format} API.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class FormatConverterWrapper extends Format {
    private final static long serialVersionUID = 1L;


    private final Converter<Object> converter;

    public FormatConverterWrapper(Converter<?> converter) {
        @SuppressWarnings("unchecked")
        Converter<Object> temp=(Converter<Object>) converter;
        this.converter = temp;
    }

    @Override
    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        toAppendTo.append(converter.toString(obj));
        return toAppendTo;
    }

    @Override
    public Object parseObject(String source, ParsePosition pos) {
        return null;
     //   return converter.fromString(source, pos);
    }
}
