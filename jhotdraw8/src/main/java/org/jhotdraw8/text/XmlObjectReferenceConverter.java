/* @(#)XMLFigureConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
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

    private final Class<T> clazz;

    /** 
     * Creates a new instance
     * @param clazz the type class
     * @throws IllegalArgumentException if clazz is null
     */
    public XmlObjectReferenceConverter(Class<T> clazz) {
        if (clazz==null)throw new IllegalArgumentException("clazz is null");
        this.clazz = clazz;
    }

    @Override
    public void toString(Appendable out, IdFactory idFactory, T value) throws IOException {
        out.append(value == null ? "none" : idFactory.getId(value));
    }

    @Override
    public T fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        String str = buf.toString();
        if ("none".equals(str)) {
            return null;
        }
        Object obj = idFactory.getObject(str);

        @SuppressWarnings("unchecked")
        T value = clazz.isInstance(obj) ? (T) obj : null;
        return value;
    }

    @Override
    public T getDefaultValue() {
        return null;
    }
}
