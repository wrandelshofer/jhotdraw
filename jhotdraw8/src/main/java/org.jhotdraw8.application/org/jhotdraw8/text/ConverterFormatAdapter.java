/*
 * @(#)ConverterFormatAdapter.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.io.IdResolver;
import org.jhotdraw8.io.IdSupplier;

import java.nio.CharBuffer;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;

/**
 * Allows to use a {@code java.text.Format} with the {@code Converter} API.
 *
 * @author Werner Randelshofer
 */
public class ConverterFormatAdapter implements Converter<Object> {

    private final Format format;

    public ConverterFormatAdapter(Format format) {
        this.format = format;
    }

    @Override
    public @NonNull String toString(Object value) {
        return format.format(value);
    }

    public Object fromString(String string, IdFactory idFactory, @NonNull ParsePosition pp) {
        Object value = format.parseObject(string, pp);
        return value;
    }

    @Override
    public void toString(Appendable out, @Nullable IdSupplier idSupplier, Object value) {
        throw new UnsupportedOperationException("Not supported yet." + format); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object fromString(@NonNull CharBuffer buf, @Nullable IdResolver idResolver) throws ParseException {
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
    public @Nullable Object getDefaultValue() {
        return null;
    }
}
