/* @(#)XMLFigureConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.io.IdFactory;

/**
 * XmlObjectReferenceConverter.
 * <p>
 * Converts references to figures.
 *
 * @author Werner Randelshofer
 * @param <T> the type
 */
public class XmlObjectReferenceConverter<T> implements Converter<T> {

    @Nullable
    private final Class<T> clazz;

    /** 
     * Creates a new instance
     * @param clazz the type class
     * @throws IllegalArgumentException if clazz is null
     */
    public XmlObjectReferenceConverter(@Nullable Class<T> clazz) {
        if (clazz==null)throw new IllegalArgumentException("clazz is null");
        this.clazz = clazz;
    }

    @Override
    public void toString(@NonNull Appendable out, @NonNull IdFactory idFactory, @Nullable T value) throws IOException {
        out.append(value == null ? "none" : idFactory.getId(value));
    }

    @Nullable
    @Override
    public T fromString(@NonNull CharBuffer buf, @NonNull IdFactory idFactory) throws ParseException, IOException {
        String str = buf.toString();
        if ("none".equals(str)) {
            return null;
        }
        Object obj = idFactory.getObject(str);

        @SuppressWarnings("unchecked")
        T value = clazz.isInstance(obj) ? (T) obj : null;
        return value;
    }

    @Nullable
    @Override
    public T getDefaultValue() {
        return null;
    }
}
