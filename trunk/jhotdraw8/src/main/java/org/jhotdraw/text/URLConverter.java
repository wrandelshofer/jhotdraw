/* @(#)URLConverter.java
 * Copyright (c) 2014 Supercomputing Systems AG, Schweiz.
 * Alle Rechte vorbehalten. 
 */

package org.jhotdraw.text;

import java.io.IOException;
import java.net.URL;
import java.nio.CharBuffer;
import java.text.ParseException;

/**
 * URLConverter.
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class URLConverter implements Converter<URL> {

    @Override
    public void toString(Appendable out, URL value) throws IOException {
        out.append(value.toString());
    }

    @Override
    public URL fromString(CharBuffer in) throws ParseException, IOException {
        URL value = new URL(in.toString());
        in.position(in.limit());
        return value;
    }

}
