/*
 * @(#)XmlUrlConverter.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.xml.text;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.text.Converter;

import java.io.IOException;
import java.net.URL;
import java.nio.CharBuffer;
import java.text.ParseException;

/**
 * XmlUrlConverter.
 *
 * @author Werner Randelshofer
 */
public class XmlUrlConverter implements Converter<URL> {

    @Override
    public void toString(@NonNull Appendable out, IdFactory idFactory, @NonNull URL value) throws IOException {
        out.append(value.toString());
    }

    @NonNull
    @Override
    public URL fromString(@Nullable CharBuffer in, IdFactory idFactory) throws ParseException, IOException {
        URL value = new URL(in.toString());
        in.position(in.limit());
        return value;
    }

    @Nullable
    @Override
    public URL getDefaultValue() {
        return null;
    }
}
