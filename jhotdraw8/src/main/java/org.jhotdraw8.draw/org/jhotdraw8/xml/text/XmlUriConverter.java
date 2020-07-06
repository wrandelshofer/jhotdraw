/*
 * @(#)XmlUriConverter.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.xml.text;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.text.Converter;

import java.io.IOException;
import java.net.URI;
import java.nio.CharBuffer;
import java.text.ParseException;

/**
 * XmlUriConverter.
 *
 * @author Werner Randelshofer
 */
public class XmlUriConverter implements Converter<URI> {

    @Override
    public void toString(@NonNull Appendable out, IdFactory idFactory, @NonNull URI value) throws IOException {
        out.append(value.toString());
    }

    @NonNull
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
