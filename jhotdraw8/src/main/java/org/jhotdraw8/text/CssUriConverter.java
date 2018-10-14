/* @(#)CssStringConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.net.URI;
import java.nio.CharBuffer;
import java.text.ParseException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jhotdraw8.css.CssToken;
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
    public URI fromString(@Nullable CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        CssTokenizer tt = new CssTokenizer(new CharBufferReader(buf));
        if (tt.nextToken() == CssToken.TT_IDENT //
                && "none".equals(tt.currentStringValue())) {
            return null;
        }
        if (tt.currentToken() != CssToken.TT_URL) {
            throw new ParseException("Css URI expected. " + tt.currentToken(), buf.position());
        }
        return URI.create(tt.currentStringValue());
    }

    @Override
    public void toString(@Nonnull Appendable out, IdFactory idFactory, @Nullable URI value) throws IOException {
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
