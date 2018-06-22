/* @(#)CssStringConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.net.URI;
import java.nio.CharBuffer;
import java.text.ParseException;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.io.CharBufferReader;

/**
 * Converts an {@code URI} to a CSS {@code URI}.
 * <pre>
 * URI = uriFunction | none ;
 * none = "none" ;
 * uriFunction = "url(" , [ uri ] , ")" ;
 * uri =  (* css uri *) ;
 * </pre>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssUriConverter implements Converter<URI> {

    @Nullable
    @Override
    public URI fromString(@NonNull CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        CssTokenizer tt = new CssTokenizer(new CharBufferReader(buf));
        if (tt.nextToken() == CssTokenizer.TT_IDENT //
                && "none".equals(tt.currentStringValue())) {
            return null;
        }
        if (tt.currentToken() != CssTokenizer.TT_URI) {
            throw new ParseException("Css URI expected. " + tt.currentToken(), buf.position());
        }
        return URI.create(tt.currentStringValue());
    }

    @Override
    public void toString(@NonNull Appendable out, IdFactory idFactory, @Nullable URI value) throws IOException {
        out.append("url(");
        if (value != null) {
            out.append(value.toASCIIString());
        }
        out.append(')');
    }

    @Nullable
    @Override
    public URI getDefaultValue() {
        return null;
    }
}
