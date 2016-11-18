/* @(#)XmlOutputFormatMixin.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw8.draw.io;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.jhotdraw8.draw.Drawing;
import org.w3c.dom.Document;

/**
 * XmlOutputFormatMixin.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public interface XmlOutputFormatMixin extends OutputFormat {
    @Override
    default void write(File file, Drawing drawing) throws IOException {
        setDocumentHome(file.getParentFile()==null?new File(System.getProperty("user.home")).toURI():file.getParentFile().toURI());
        Document doc = toDocument(drawing);
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
        Document doc = toDocument(drawing);
        try {
            Transformer t = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(out);
            t.transform(source, result);
        } catch (TransformerException ex) {
            throw new IOException(ex);
        }

    }

    Document toDocument(Drawing drawing) throws IOException;
    void setDocumentHome(URI uri);
}
