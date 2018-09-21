/* @(#)WordConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.CharBuffer;
import java.text.ParseException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.CssTokenizerInterface;
import org.jhotdraw8.io.IdFactory;

/**
 * CssEnumConverter. Converts all enum names to lower-case.
 * <p>
 * FIXME change this class, so that it can map internal enum names to external names using 
 * a hash-map.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssEnumConverter<E extends Enum<E>> implements Converter<E> {

    @Nonnull
    private final Class<E> enumClass;
    @Nonnull
    private final String name;
    private final boolean nullable;

    public CssEnumConverter(Class<E> enumClass, boolean nullable) {
        this.enumClass = enumClass;
        this.name = enumClass.getName().substring(enumClass.getName().lastIndexOf('.') + 1);
        this.nullable = nullable;
    }

    @Override
    public void toString(@Nonnull Appendable out, IdFactory idFactory, @Nullable E value) throws IOException {
        if (value == null) {
            if (!nullable) {
                throw new IllegalArgumentException("value is not nullable. enum type:" + enumClass + " value:" + value);
            }
            out.append("none");
        } else {
            for (char ch : value.toString().toLowerCase().replace('_', '-').toCharArray()) {
                if (Character.isWhitespace(ch)) {
                    break;
                }
                out.append(ch);
            }
        }
    }

    @Nullable
    @Override
    public E fromString(@Nonnull CharBuffer in, IdFactory idFactory) throws ParseException, IOException {
        int pos = in.position();
        StringBuilder out = new StringBuilder();
        while (in.remaining() > 0 && !Character.isWhitespace(in.charAt(0))) {
            out.append(in.get());
        }
        if (out.length() == 0) {
            in.position(pos);
            throw new ParseException("word expected", pos);
        }
        if (out.toString().equals("none")) {
            return nullable ? null : getDefaultValue();
        }
        try {
            return Enum.valueOf(enumClass, out.toString().toUpperCase().replace('-', '_'));
        } catch (IllegalArgumentException e) {
            return getDefaultValue();
        }
    }

    @Nonnull
    public E parse(@Nonnull CssTokenizerInterface tt) throws ParseException, IOException {
        if (tt.nextToken() != CssTokenizer.TT_IDENT) {
            throw new ParseException("identifier expected", tt.getStartPosition());
        }

        String identifier = tt.currentStringValue();
        try {
            return Enum.valueOf(enumClass, identifier.toUpperCase().replace('-', '_'));
        } catch (IllegalArgumentException e) {
            throw new ParseException("illegal identifier:" + identifier, tt.getStartPosition());
        }
    }

    @Nullable
    @Override
    public E getDefaultValue() {
        try {
            for (Field f : enumClass.getDeclaredFields()) {
                if (f.isEnumConstant()) {
                    @SuppressWarnings("unchecked")
                    E e = (E) f.get(null);
                    return e;
                }
            }
            return null;
        } catch (IllegalArgumentException ex) {
            return null;
        } catch (IllegalAccessException ex) {
            return null;
        }
    }

    @Nonnull
    @Override
    public String getHelpText() {
        StringBuilder buf = new StringBuilder("Format of ⟨");
        buf.append(name).append("⟩: ");
        boolean first = true;
        if (nullable) {
            buf.append("none");
            first=false;
        }
        for (Field f : enumClass.getDeclaredFields()) {
            if (f.isEnumConstant()) {
                if (first) {
                    first = false;
                } else {
                    buf.append('｜');
                }
                buf.append(f.getName().toLowerCase().replace('_', '-'));
            }
        }
        return buf.toString();
    }
}
