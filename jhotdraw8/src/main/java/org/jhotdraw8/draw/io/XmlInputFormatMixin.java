/* @(#)XmlInputFormatMixin.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.xml.XmlUtil;
import org.w3c.dom.Document;

/**
 * XmlInputFormatMixin.
 * <p>
 * FIXME delete me
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface XmlInputFormatMixin {


    boolean isNamespaceAware();

    @Nonnull
    default Figure read(InputStream in, Drawing drawing) throws IOException {
        Document doc = XmlUtil.read(in, isNamespaceAware());
        return read(doc, drawing);
    }

    @Nonnull
    default Figure read(Reader in, Drawing drawing) throws IOException {
        Document doc = XmlUtil.read(in, isNamespaceAware());
        return read(doc, drawing);
    }

    @Nonnull
    default Figure read(@Nonnull String string, Drawing drawing) throws IOException {
        try (StringReader in = new StringReader(string)) {
            return read(in, drawing);
        }
    }

    @Nonnull
    Figure read(Document in, Drawing drawing) throws IOException;

}
