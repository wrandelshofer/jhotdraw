/* @(#)WordListConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableSet;
import org.jhotdraw8.io.IdFactory;

/**
 * CssSetConverter.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssSetConverter<E> implements Converter<ImmutableSet<E>> {

    @Override
    public void toString(@Nonnull Appendable out, IdFactory idFactory, @Nullable ImmutableSet<E> value) throws IOException {
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
    public ImmutableSet<E> fromString(@Nullable CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        return ImmutableSet.emptySet();
    }

    @Override
    public ImmutableSet<E> getDefaultValue() {
        return ImmutableSet.emptySet();
    }
}
