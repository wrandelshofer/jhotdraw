/*
 * @(#)DefaultConverter.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.io.IdFactory;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;

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
 */
public class DefaultConverter implements Converter<Object> {

    @Nullable
    @Override
    public Object fromString(@Nullable CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        String str = buf.toString();
        buf.position(buf.limit());
        return "null".equals(str) ? null : str;
    }

    @Override
    public void toString(@Nonnull Appendable out, IdFactory idFactory, @Nullable Object value) throws IOException {
        out.append(value == null ? "null" : value.toString());
    }

    @Nonnull
    @Override
    public String getDefaultValue() {
        return "null";
    }
}
