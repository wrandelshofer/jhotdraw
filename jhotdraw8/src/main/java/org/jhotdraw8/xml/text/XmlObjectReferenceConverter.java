/* @(#)XMLFigureConverter.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.xml.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;

import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.text.Converter;

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
    public <TT extends T> void toString(@Nonnull Appendable out, @Nonnull IdFactory idFactory, @Nullable TT value) throws IOException {
        out.append(value == null ? "none" : idFactory.getId(value));
    }

    @Nullable
    @Override
    public T fromString(@Nullable CharBuffer buf, @Nonnull IdFactory idFactory) throws ParseException, IOException {
        String str = buf.toString();
        if ("none".equals(str)) {
            return null;
        }
        Object obj = idFactory.getObject(str);
        if (obj == null) {
            System.err.println("WARNING Could not find an object with this id. id=\""+str+"\".");
        }

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
