/* @(#)WordConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import javafx.scene.paint.Paint;
import org.jhotdraw.draw.io.IdFactory;

/**
 * CssEnumConverter. Converts all enum names to lower-case.
 *
 * @author Werner Randelshofer
 */
public class CssEnumConverter<E extends Enum<E>> implements Converter<E> {

    private final Class<E> enumClass;

    public CssEnumConverter(Class<E> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public void toString(Appendable out, IdFactory idFactory, E value) throws IOException {
        if (value == null) {
            out.append("null");
        } else {
            for (char ch : value.toString().toLowerCase().toCharArray()) {
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
        return Enum.valueOf(enumClass, out.toString().toUpperCase());
    }

    @Override
    public E getDefaultValue() {
        return null;
    }

}
