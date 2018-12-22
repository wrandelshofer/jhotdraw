/* @(#)XmlUriConverter.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.xml.text;

import java.io.IOException;
import java.net.URI;
import java.nio.CharBuffer;
import java.text.ParseException;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.text.Converter;

/**
 * XmlUriConverter.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class XmlUriConverter implements Converter<URI> {

    @Override
    public void toString(@Nonnull Appendable out, IdFactory idFactory, @Nonnull URI value) throws IOException {
        out.append(value.toString());
    }

    @Nonnull
    @Override
    public URI fromString(@Nullable CharBuffer in, IdFactory idFactory) throws ParseException, IOException {
        URI value = URI.create(in.toString());
        in.position(in.limit());
        return value;
    }

    @Nullable
    @Override
    public URI getDefaultValue() {
        return null;
    }
}
