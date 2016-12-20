/*
 * @(#)FormatConverterWrapper.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.text;

import java.nio.CharBuffer;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import org.jhotdraw8.draw.io.IdFactory;

/**
 * Allows to use a {@code java.text.Format} with the {@code Converter} API.
 *
 * @author Werner Randelshofer
 * @version $Id: ConverterFormatAdapter.java 1149 2016-11-18 11:00:10Z rawcoder
 * $
 */
public class ConverterFormatAdapter implements Converter<Object> {

    private final Format format;

    public ConverterFormatAdapter(Format format) {
        this.format = format;
    }

    @Override
    public String toString(Object value) {
        return format.format(value);
    }

    public Object fromString(String string, IdFactory idFactory, ParsePosition pp) {
        Object value = format.parseObject(string, pp);
        return value;
    }

    @Override
    public void toString(Appendable out, IdFactory idFactory, Object value) {
        throw new UnsupportedOperationException("Not supported yet." + format); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object fromString(CharBuffer buf, IdFactory idFactory) throws ParseException {
        int pos = buf.position();
        String str = buf.toString();
        ParsePosition pp = new ParsePosition(0);
        Object value = format.parseObject(str, pp);
        if (pp.getErrorIndex() != -1) {
            buf.position(pos + pp.getErrorIndex());
            throw new ParseException("Parse error", buf.position());
        } else {
            buf.position(pos + pp.getIndex());
        }
        return value;
    }

    @Override
    public Object getDefaultValue() {
        return null;
    }
}
