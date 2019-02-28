/* @(#)CssStringConverter.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.StreamCssTokenizer;
import org.jhotdraw8.io.CharBufferReader;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.text.Converter;

import java.io.IOException;
import java.net.URI;
import java.nio.CharBuffer;
import java.text.ParseException;

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
        StreamCssTokenizer tt = new StreamCssTokenizer(new CharBufferReader(buf));
        if (tt.next() == CssTokenType.TT_IDENT //
                && "none".equals(tt.currentString())) {
            return null;
        }
        if (tt.current() != CssTokenType.TT_URL) {
            throw new ParseException("Css URI expected. " + tt.current(), buf.position());
        }
        return URI.create(tt.currentString());
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
