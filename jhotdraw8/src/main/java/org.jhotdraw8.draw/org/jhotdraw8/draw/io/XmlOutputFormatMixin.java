/*
 * @(#)XmlOutputFormatMixin.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.io;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.concurrent.WorkState;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.xml.XmlUtil;
import org.w3c.dom.Document;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URI;
import java.nio.file.Path;
import java.util.Collection;

/**
 * XmlOutputFormatMixin.
 * <p>
 * FIXME delete me
 *
 * @author Werner Randelshofer
 */
public interface XmlOutputFormatMixin extends OutputFormat {


    default Document toDocument(URI documentHome, @Nonnull Drawing drawing) throws IOException {
        return toDocument(documentHome, drawing, drawing.getChildren());
    }

    Document toDocument(URI documentHome, Drawing drawing, Collection<Figure> selection) throws IOException;

    @Override
    default void write(@Nonnull Path file, @Nonnull Drawing drawing, WorkState workState) throws IOException {
        Document doc = toDocument(file.getParent().toUri(), drawing);
        XmlUtil.write(file, doc);
    }

    @Override
    default void write(URI documentHome, OutputStream out, @Nonnull Drawing drawing, WorkState workState) throws IOException {
        write(documentHome, out, drawing, drawing.getChildren());
    }

    default void write(URI documentHome, OutputStream out, Drawing drawing, Collection<Figure> selection) throws IOException {
        Document doc = toDocument(documentHome, drawing, selection);
        XmlUtil.write(out, doc);
    }


    default void write(URI documentHome, Writer out, Drawing drawing, Collection<Figure> selection) throws IOException {
        Document doc = toDocument(documentHome, drawing, selection);
        try {
            Transformer t = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(out);
            t.transform(source, result);
        } catch (TransformerException ex) {
            throw new IOException(ex);
        }
    }

}
