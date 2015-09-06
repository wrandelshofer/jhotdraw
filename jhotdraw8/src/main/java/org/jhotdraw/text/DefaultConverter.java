/*
 * @(#)DefaultConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.text;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.CharBuffer;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Converts an {@code Object} to a {@code String} but can not a {@code String}
 * back to an {@code Object}.
 * <p>
 * This converter is not bijective, and thus only useful for one-way conversions
 * to a String. For example for generating a message text.
 * <ul>
 * <li>The conversion to string is performed by invoking the {@code toString}
 * method on the value object.</li>
 * <li>The {@code fromString} method always returns a String object regardless
 * of the type that was converted {@code toString}.</li>
 * <li>If a null value is converted to string, the string contains the text
 * {@code "null"}. On the conversion from string, a String object with the value
 * {@code "null"} is returned.</li>
 * </ul>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DefaultConverter implements Converter<Object> {

    @Override
    public Object fromString(CharBuffer buf) throws ParseException, IOException {
        CharBuffer out = CharBuffer.allocate(buf.remaining());
        int count = buf.read(out);
        out.position(0);
        out.limit(count);
        return out.toString();
    }

    @Override
    public void toString(Object value, Appendable out) throws IOException {
        out.append(value == null ? "null" : value.toString());
    }

}
