/* @(#)XmlOutputFormatMixin.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.io;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Collection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Figure;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
        write(file, doc);
    }

    default void write(File file, Document doc) throws TransformerFactoryConfigurationError, IOException {
        try {
            final TransformerFactory factory = TransformerFactory.newInstance();
            Transformer t = factory.newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
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
            final TransformerFactory factory = TransformerFactory.newInstance();
            Transformer t = factory.newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
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
   /** Creates a document. 
     * 
     * @param nsURI nullable namespace URI
     * @param nsQualifier nullable namespace qualifier
     * @param docElemName notnull name of the document element
     * @return a new Document
     * @throws IOException if the parser configuration fails
     */
    default Document createDocument(String nsURI, String nsQualifier, String docElemName) throws IOException {
        try {
            Document doc;
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            if (nsURI != null) {
                builderFactory.setNamespaceAware(true);
                DocumentBuilder builder = builderFactory.newDocumentBuilder();
                DOMImplementation domImpl = builder.getDOMImplementation();
                doc = domImpl.createDocument(nsURI, nsQualifier == null ? docElemName : nsQualifier + ":" + docElemName, null);
            } else {
                DocumentBuilder builder = builderFactory.newDocumentBuilder();
                doc = builder.newDocument();
                Element elem = doc.createElement(docElemName);
                doc.appendChild(elem);
            }
            return doc;
        } catch (ParserConfigurationException ex) {
            throw new IOException(ex);
        }
        
    }
}
