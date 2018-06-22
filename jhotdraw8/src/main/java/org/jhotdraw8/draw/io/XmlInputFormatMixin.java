/* @(#)XmlInputFormatMixin.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;
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

    @NonNull
    default Figure read(InputStream in, Drawing drawing) throws IOException {
        Document doc = XmlUtil.read(in, isNamespaceAware());
        return read(doc, drawing);
    }

    @NonNull
    default Figure read(Reader in, Drawing drawing) throws IOException {
        Document doc = XmlUtil.read(in, isNamespaceAware());
        return read(doc, drawing);
    }

    @NonNull
    default Figure read(@NonNull String string, Drawing drawing) throws IOException {
        try (StringReader in = new StringReader(string)) {
            return read(in, drawing);
        }
    }

    @NonNull
    Figure read(Document in, Drawing drawing) throws IOException;

}
