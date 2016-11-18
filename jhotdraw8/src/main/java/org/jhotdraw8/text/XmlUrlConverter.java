/* @(#)XmlUrlConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw8.text;

import java.io.IOException;
import java.net.URL;
import java.nio.CharBuffer;
import java.text.ParseException;
import org.jhotdraw8.draw.io.IdFactory;

/**
 * XmlUrlConverter.
 * @author Werner Randelshofer
 * @version $Id$
 */
public class XmlUrlConverter implements Converter<URL> {

    @Override
    public void toString(Appendable out, IdFactory idFactory, URL value) throws IOException {
        out.append(value.toString());
    }

    @Override
    public URL fromString(CharBuffer in, IdFactory idFactory) throws ParseException, IOException {
        URL value = new URL(in.toString());
        in.position(in.limit());
        return value;
    }
    @Override
    public URL getDefaultValue() {
        return null;
    }
}
