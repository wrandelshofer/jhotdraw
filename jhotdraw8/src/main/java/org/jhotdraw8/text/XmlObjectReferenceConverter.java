/* @(#)XMLFigureConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
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
 */
public class XmlObjectReferenceConverter<T> implements Converter<T> {

    @Override
    public void toString(Appendable out, IdFactory idFactory, T value) throws IOException {
        out.append(idFactory.getId(value));
    }

    @Override
    public T fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        String str = buf.toString();
        return (T) idFactory.getObject(str);
    }

    @Override
    public T getDefaultValue() {
        return null;
    }
}
