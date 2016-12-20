/* @(#)XmlUriConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.net.URI;
import java.nio.CharBuffer;
import java.text.ParseException;
import org.jhotdraw8.draw.io.IdFactory;

/**
 * XmlUriConverter.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class XmlUriConverter implements Converter<URI> {

    @Override
    public void toString(Appendable out, IdFactory idFactory, URI value) throws IOException {
        out.append(value.toString());
    }

    @Override
    public URI fromString(CharBuffer in, IdFactory idFactory) throws ParseException, IOException {
        URI value = URI.create(in.toString());
        in.position(in.limit());
        return value;
    }

    @Override
    public URI getDefaultValue() {
        return null;
    }
}
