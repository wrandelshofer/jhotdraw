/* @(#)XmlInputFormatMixin.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.xml.XmlUtil;
import org.w3c.dom.Document;

/**
 * XmlInputFormatMixin.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface XmlInputFormatMixin {

    void setExternalHome(@Nullable URI uri);

    boolean isNamespaceAware();

    default Figure read(@Nonnull InputStream in, @Nonnull Drawing drawing) throws IOException {
        Document doc = XmlUtil.read(in, isNamespaceAware());
        return read(doc, drawing);
    }

    default Figure read(@Nonnull Reader in, @Nonnull Drawing drawing) throws IOException {
        Document doc = XmlUtil.read(in, isNamespaceAware());
        return read(doc, drawing);
    }

    @Nonnull
    default Figure read(@Nonnull String string, @Nonnull Drawing drawing) throws IOException {
        try (StringReader in = new StringReader(string)) {
            return read(in, drawing);
        }
    }

    @Nonnull
    Figure read(@Nonnull Document in, @Nonnull Drawing drawing) throws IOException;

}
