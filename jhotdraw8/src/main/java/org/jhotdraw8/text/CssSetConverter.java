/* @(#)WordListConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import org.jhotdraw8.collection.ImmutableObservableSet;
import org.jhotdraw8.io.IdFactory;

/**
 * CssSetConverter.
 *
 * @author Werner Randelshofer
 */
public class CssSetConverter<E> implements Converter<ImmutableObservableSet<E>> {

    @Override
    public void toString(Appendable out, IdFactory idFactory, ImmutableObservableSet<E> value) throws IOException {
        if (value == null) {
            return;
        }
        StringBuilder buf = new StringBuilder();
        for (E e : value) {
            if (buf.length() != 0) {
                buf.append(", ");
            }
            buf.append(e.toString());
        }
        out.append(buf.toString());
    }

    @Override
    public ImmutableObservableSet<E> fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        return ImmutableObservableSet.emptySet();
    }

    @Override
    public ImmutableObservableSet<E> getDefaultValue() {
        return ImmutableObservableSet.emptySet();
    }
}
