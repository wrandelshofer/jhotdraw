/* @(#)CssStringConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.text;

import java.io.IOException;
import java.net.URI;
import java.nio.CharBuffer;
import java.text.ParseException;
import org.jhotdraw.css.CssTokenizer;
import org.jhotdraw.draw.io.IdFactory;
import org.jhotdraw.io.CharBufferReader;

/**
 * Converts an {@code URI} to a CSS {@code URI}.
 * <pre>
 * URI = uriFunction | none ;
 * none = "none" ;
 * uriFunction = "url(" , [ uri ] , ")" ;
 * uri =  (* css uri *) ;
 * <pre>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssUriConverter implements Converter<URI> {


    @Override
    public URI fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        CssTokenizer tt = new CssTokenizer(new CharBufferReader(buf));
        if (tt.nextToken() == CssTokenizer.TT_IDENT //
                && "none".equals(tt.currentStringValue())) {
            return null;
        }
        if (tt.currentToken()!= CssTokenizer.TT_URI) {
            throw new ParseException("Css URI expected. "+tt.currentToken(), buf.position());
        }
        return URI.create(tt.currentStringValue());
    }

    @Override
    public void toString(Appendable out, IdFactory idFactory, URI value) throws IOException {
        out.append("url(");
        if (value != null) {
            out.append(value.toASCIIString());
        }
        out.append(')');
    }

    @Override
    public URI getDefaultValue() {
        return null;
    }
}
