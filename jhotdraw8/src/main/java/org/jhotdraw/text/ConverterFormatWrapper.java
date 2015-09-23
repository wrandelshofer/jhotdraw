/*
 * @(#)FormatConverterWrapper.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.text;

import java.nio.CharBuffer;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;

/**
 * Allows to use a {@code java.text.Format} with the {@code Converter} API.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ConverterFormatWrapper implements Converter<Object> {

    private final Format format;

    public ConverterFormatWrapper(Format format) {
        this.format = format;
    }

    @Override
    public String toString(Object value) {
        return format.format(value);
    }

  
    public Object fromString(String string, ParsePosition pp) {
        Object value = format.parseObject(string, pp);
        return value;
    }

    @Override
    public void toString(Object value, Appendable out) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object fromString(CharBuffer buf) throws ParseException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
