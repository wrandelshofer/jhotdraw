/* @(#)XmlOutputFormatMixin.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.xml.XmlUtil;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * XmlOutputFormatMixin.
 *
 * @author Werner Randelshofer
 * @version $$Id: XmlOutputFormatMixin.java 1237 2016-12-20 08:57:59Z rawcoder
 * $$
 */
public interface XmlOutputFormatMixin extends OutputFormat, InternalExternalUriMixin {

    default Document toDocument(Drawing drawing) throws IOException {
        return toDocument(drawing, drawing.getChildren());
    }

    Document toDocument(Drawing drawing, Collection<Figure> selection) throws IOException;

    @Override
    default void write(File file, Drawing drawing) throws IOException {
        setExternalHome(file.getParentFile() == null ? new File(System.getProperty("user.home")).toURI() : file.getParentFile().toURI());
        setInternalHome(drawing.get(Drawing.DOCUMENT_HOME));
        Document doc = toDocument(drawing);
        XmlUtil.write(file, doc);
    }

    @Override
    default void write(OutputStream out, Drawing drawing) throws IOException {
        write(out, drawing, drawing.getChildren());
    }

    default void write(OutputStream out, Drawing drawing, Collection<Figure> selection) throws IOException {
        Document doc = toDocument(drawing, selection);
        XmlUtil.write(out, doc);
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
