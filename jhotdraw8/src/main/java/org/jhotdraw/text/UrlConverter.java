/* @(#)UrlConverter.java
 * Copyright (c) 2014 Supercomputing Systems AG, Schweiz.
 * Alle Rechte vorbehalten. 
 */

package org.jhotdraw.text;

import java.io.IOException;
import java.net.URL;
import java.nio.CharBuffer;
import java.text.ParseException;
import org.jhotdraw.draw.io.IdFactory;

/**
 * UrlConverter.
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class UrlConverter implements Converter<URL> {

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

}
