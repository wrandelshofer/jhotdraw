/*
 * @(#)XmlUriConverter.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.xml.text;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.io.IdResolver;
import org.jhotdraw8.io.IdSupplier;
import org.jhotdraw8.text.Converter;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.CharBuffer;
import java.text.ParseException;

/**
 * XmlUriConverter.
 *
 * @author Werner Randelshofer
 */
public class XmlUriConverter implements Converter<URI> {

    @Override
    public void toString(@NonNull Appendable out, @Nullable IdSupplier idSupplier, @Nullable URI value) throws IOException {
        out.append(value == null ? CssTokenType.IDENT_NONE : value.toString());
    }

    @Override
    public @Nullable URI fromString(@NonNull CharBuffer in, @Nullable IdResolver idResolver) throws ParseException, IOException {
        if (in == null) return null;
        String str = in.toString().trim();
        in.position(in.limit());// fully consume the buffer
        if (CssTokenType.IDENT_NONE.equals(str)) return null;
        try {
            return new URI(str);
        } catch (URISyntaxException e) {
            throw new ParseException(e.getMessage(), 0);
        }
    }

    @Override
    public @Nullable URI getDefaultValue() {
        return null;
    }
}
