/* @(#)WordConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.CharBuffer;
import java.text.ParseException;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.CssTokenizerInterface;
import org.jhotdraw8.io.IdFactory;

/**
 * CssEnumConverter. Converts all enum names to lower-case.
 *
 * @author Werner Randelshofer
 */
public class CssEnumConverter<E extends Enum<E>> implements Converter<E> {

    private final Class<E> enumClass;
    private final String name;

    public CssEnumConverter(Class<E> enumClass) {
        this.enumClass = enumClass;
        this.name = enumClass.getName().substring(enumClass.getName().lastIndexOf('.') + 1);
    }

    @Override
    public void toString(Appendable out, IdFactory idFactory, E value) throws IOException {
        if (value == null) {
            out.append("null");
        } else {
            for (char ch : value.toString().toLowerCase().replace('_', '-').toCharArray()) {
                if (Character.isWhitespace(ch)) {
                    break;
                }
                out.append(ch);
            }
        }
    }

    @Override
    public E fromString(CharBuffer in, IdFactory idFactory) throws ParseException, IOException {
        int pos = in.position();
        StringBuilder out = new StringBuilder();
        while (in.remaining() > 0 && !Character.isWhitespace(in.charAt(0))) {
            out.append(in.get());
        }
        if (out.length() == 0) {
            in.position(pos);
            throw new ParseException("word expected", pos);
        }
        if (out.toString().equals("null")) {
            return null;
        }
        try {
            return Enum.valueOf(enumClass, out.toString().toUpperCase().replace('-', '_'));
        } catch (IllegalArgumentException e) {
            return getDefaultValue();
        }
    }

    public E parse(CssTokenizerInterface tt) throws ParseException, IOException {
        if (tt.nextToken()!=CssTokenizer.TT_IDENT) throw new ParseException("identifier expected",tt.getStartPosition());
        
        String identifier=tt.currentStringValue();
        try {
            return Enum.valueOf(enumClass, identifier.toUpperCase().replace('-', '_'));
        } catch (IllegalArgumentException e) {
            throw new ParseException("illegal identifier:"+identifier, tt.getStartPosition());
        }
    }
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

    @Override
    public String getHelpText() {
        StringBuilder buf = new StringBuilder("Format of ⟨");
        buf.append(name).append("⟩: ");
        boolean first = true;
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
