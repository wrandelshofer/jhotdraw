/*
 * @(#)XmlWordConverter.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.io.IdFactory;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;

/**
 * XmlWordConverter.
 *
 * @author Werner Randelshofer
 */
public class XmlWordConverter implements Converter<String> {

    @Override
    public void toString(@NonNull Appendable out, IdFactory idFactory, @NonNull String value) throws IOException {
        for (char ch : value.toCharArray()) {
            if (Character.isWhitespace(ch)) {
                break;
            }
            out.append(ch);
        }
    }

    @NonNull
    @Override
    public String fromString(@Nullable CharBuffer in, IdFactory idFactory) throws ParseException, IOException {
        int pos = in.position();
        StringBuilder out = new StringBuilder();
        while (in.remaining() > 0 && !Character.isWhitespace(in.charAt(0))) {
            out.append(in.get());
        }
        if (out.length() == 0) {
            in.position(pos);
            throw new ParseException("word expected", pos);
        }
        return out.toString();
    }

    @NonNull
    @Override
    public String getDefaultValue() {
        return "";
    }
}
