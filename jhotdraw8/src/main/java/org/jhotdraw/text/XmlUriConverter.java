/* @(#)XmlUriConverter.java
 * Copyright (c) 2014 Supercomputing Systems AG, Schweiz.
 * Alle Rechte vorbehalten. 
 */

package org.jhotdraw.text;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.CharBuffer;
import java.text.ParseException;
import javafx.scene.paint.Paint;
import org.jhotdraw.draw.io.IdFactory;

/**
 * XmlUriConverter.
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class XmlUriConverter implements Converter<URI> {

    @Override
    public void toString(Appendable out, IdFactory idFactory, URI value) throws IOException {
        out.append(value.toString());
    }

    @Override
    public URI fromString(CharBuffer in, IdFactory idFactory) throws ParseException, IOException {
        URI value =  URI.create(in.toString());
        in.position(in.limit());
        return value;
    }
    @Override
    public URI getDefaultValue() {
        return null;
    }
}
