/* @(#)XmlOutputFormatMixin.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.io;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URI;
import java.util.Collection;
import java.util.Map;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Figure;
import org.w3c.dom.Document;

/**
 * XmlOutputFormatMixin.
 *
 * @author Werner Randelshofer
 * @version $$Id: XmlOutputFormatMixin.java 1237 2016-12-20 08:57:59Z rawcoder
 * $$
 */
public interface XmlOutputFormatMixin extends OutputFormat {

    URI getExternalHome();

    void setExternalHome(URI uri);

    URI getInternalHome();

    void setInternalHome(URI uri);

    default Document toDocument(Drawing drawing) throws IOException {
        return toDocument(drawing, drawing.getChildren());
    }

    Document toDocument(Drawing drawing, Collection<Figure> selection) throws IOException;

    default URI toExternal(URI uri) {
        if (uri == null) {
            return null;
        }
        URI internal = getInternalHome();
        URI external = getExternalHome();
        if (internal != null) {
            uri = internal.resolve(uri);
        }
        if (external != null) {
            uri = external.relativize(uri);
        }
        return uri;
    }

    default URI toInternal(URI uri) {
        if (uri == null) {
            return null;
        }
        URI internal = getInternalHome();
        URI external = getExternalHome();
        if (external != null) {
            uri = external.resolve(uri);
        }
        if (internal != null) {
            uri = internal.relativize(uri);
        }
        return uri;
    }

    @Override
    default void write(File file, Drawing drawing) throws IOException {
        setExternalHome(file.getParentFile() == null ? new File(System.getProperty("user.home")).toURI() : file.getParentFile().toURI());
        setInternalHome(drawing.get(Drawing.DOCUMENT_HOME));
        Document doc = toDocument(drawing);
        write(file, doc);
    }

    default void write(File file, Document doc) throws TransformerFactoryConfigurationError, IOException {
        try {
            Transformer t = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);
            t.transform(source, result);
        } catch (TransformerException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    default void write(OutputStream out, Drawing drawing) throws IOException {
        write(out, drawing, drawing.getChildren());
    }

    default void write(OutputStream out, Drawing drawing, Collection<Figure> selection) throws IOException {
        Document doc = toDocument(drawing, selection);
        write(out, doc);
    }

    default void write(OutputStream out, Document doc) throws IOException {
        try {
            Transformer t = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(out);
            t.transform(source, result);
        } catch (TransformerException ex) {
            throw new IOException(ex);
        }
    }

    default void write(Writer out, Drawing drawing, Collection<Figure> selection) throws IOException {
        Document doc = toDocument(drawing, selection);
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
