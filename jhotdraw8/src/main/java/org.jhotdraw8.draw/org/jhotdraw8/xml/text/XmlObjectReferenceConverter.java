/*
 * @(#)XmlObjectReferenceConverter.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.xml.text;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.text.Converter;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import java.util.logging.Logger;

/**
 * XmlObjectReferenceConverter.
 * <p>
 * Converts references to figures.
 *
 * @param <T> the type
 * @author Werner Randelshofer
 */
public class XmlObjectReferenceConverter<T> implements Converter<T> {

    private static final Logger LOGGER = Logger.getLogger(XmlObjectReferenceConverter.class.getName());
    @Nonnull
    private final Class<T> clazz;

    /**
     * Creates a new instance
     *
     * @param clazz the type class
     * @throws IllegalArgumentException if clazz is null
     */
    public XmlObjectReferenceConverter(@Nonnull Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public <TT extends T> void toString(@Nonnull Appendable out, @Nullable IdFactory idFactory, @Nullable TT value) throws IOException {
        if (idFactory == null) {
            throw new UnsupportedOperationException("idFactory is required for this converter");
        }
        out.append(value == null ? "none" : idFactory.getId(value));
    }

    @Nullable
    @Override
    public T fromString(@Nullable CharBuffer buf, @Nullable IdFactory idFactory) throws ParseException, IOException {
        if (idFactory == null) {
            throw new UnsupportedOperationException("idFactory is required for this converter");
        }
        String str = buf == null ? "none" : buf.toString();
        if ("none".equals(str)) {
            return null;
        }
        Object obj = idFactory.getObject(str);
        if (obj == null) {
            LOGGER.warning("Could not find an object with this id. id=\"" + str + "\".");
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
