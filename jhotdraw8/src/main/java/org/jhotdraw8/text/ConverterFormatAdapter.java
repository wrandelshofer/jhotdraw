/* @(#)FormatConverterWrapper.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import java.nio.CharBuffer;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jhotdraw8.io.IdFactory;

/**
 * Allows to use a {@code java.text.Format} with the {@code Converter} API.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ConverterFormatAdapter implements Converter<Object> {

    private final Format format;

    public ConverterFormatAdapter(Format format) {
        this.format = format;
    }

    @Nonnull
    @Override
    public String toString(Object value) {
        return format.format(value);
    }

    public Object fromString(String string, IdFactory idFactory, @Nonnull ParsePosition pp) {
        Object value = format.parseObject(string, pp);
        return value;
    }

    @Override
    public void toString(Appendable out, IdFactory idFactory, Object value) {
        throw new UnsupportedOperationException("Not supported yet." + format); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object fromString(@Nonnull CharBuffer buf, IdFactory idFactory) throws ParseException {
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

    @Nullable
    @Override
    public Object getDefaultValue() {
        return null;
    }
}
