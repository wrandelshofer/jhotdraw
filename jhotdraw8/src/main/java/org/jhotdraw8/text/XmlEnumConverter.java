/* @(#)WordConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.CharBuffer;
import java.text.ParseException;
import org.jhotdraw8.draw.io.IdFactory;

/**
 * WordConverter.
 *
 * @author Werner Randelshofer
 */
public class XmlEnumConverter<E extends Enum<E>> implements Converter<E> {

    private final Class<E> enumClass;

    public XmlEnumConverter(Class<E> enumClass) {
        this.enumClass = enumClass;
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
            ex.printStackTrace();
            return null;
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
