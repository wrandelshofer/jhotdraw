/* @(#)XmlUrlConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.net.URL;
import java.nio.CharBuffer;
import java.text.ParseException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jhotdraw8.io.IdFactory;

/**
 * XmlUrlConverter.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class XmlUrlConverter implements Converter<URL> {

    @Override
    public void toString(@Nonnull Appendable out, IdFactory idFactory, @Nonnull URL value) throws IOException {
        out.append(value.toString());
    }

    @Nonnull
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
