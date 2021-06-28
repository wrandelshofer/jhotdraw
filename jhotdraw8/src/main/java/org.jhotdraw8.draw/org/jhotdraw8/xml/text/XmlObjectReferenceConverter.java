/*
 * @(#)XmlObjectReferenceConverter.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.xml.text;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.io.IdResolver;
import org.jhotdraw8.io.IdSupplier;
import org.jhotdraw8.text.ResolvingConverter;

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
public class XmlObjectReferenceConverter<T> implements ResolvingConverter<T> {

    private static final Logger LOGGER = Logger.getLogger(XmlObjectReferenceConverter.class.getName());
    private final @NonNull Class<T> clazz;

    /**
     * Creates a new instance
     *
     * @param clazz the type class
     * @throws IllegalArgumentException if clazz is null
     */
    public XmlObjectReferenceConverter(@NonNull Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public <TT extends T> void toString(@NonNull Appendable out, @Nullable IdSupplier idSupplier, @Nullable TT value) throws IOException {
        if (idSupplier == null) {
            throw new IllegalArgumentException("IdSupplier is required for this converter");
        }
        out.append(value == null ? "none" : idSupplier.getId(value));
    }

    @Override
    public @Nullable T fromString(@NonNull CharBuffer buf, @Nullable IdResolver idResolver) throws ParseException, IOException {
        return fromString(buf.toString(),idResolver);
    }

    @Override
    public @Nullable T fromString(@Nullable CharSequence buf, @Nullable IdResolver idResolver) throws ParseException, IOException {
        if (idResolver == null) {
            throw new IllegalArgumentException("IdResolver is required for this converter");
        }
        String str = buf == null ? "none" : buf.toString();
        if ("none".equals(str)) {
            return null;
        }
        Object obj = idResolver.getObject(str);
        if (obj == null) {
            LOGGER.warning("Could not find an object with this id. id=\"" + str + "\".");
        }

        @SuppressWarnings("unchecked")
        T value = clazz.isInstance(obj) ? (T) obj : null;
        return value;
    }

    @Override
    public @Nullable T getDefaultValue() {
        return null;
    }

   public boolean needsIdResolver() {
        return true;
    }

}
