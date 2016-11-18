/* @(#)XMLFigureConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.io.IdFactory;

/**
 * XmlFigureReferenceConverter.
 * <p>
 * Converts references to figures.
 *
 * @author Werner Randelshofer
 */
public class XmlFigureReferenceConverter implements Converter<Figure> {

    @Override
    public void toString(Appendable out, IdFactory idFactory, Figure value) throws IOException {
      out.append(idFactory.getId(value));
    }

    @Override
    public Figure fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
      String str = buf.toString();
      return (Figure) idFactory.getObject(str);
    }
    
    @Override
    public Figure getDefaultValue() {
        return null;
    }
}
