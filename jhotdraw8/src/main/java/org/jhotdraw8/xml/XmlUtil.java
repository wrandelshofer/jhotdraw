/* @(#)XmlUtil.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw8.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
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
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * XmlUtil.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class XmlUtil {
private XmlUtil(){}
    /**
     * Creates a document.
     *
     * @param nsURI nullable namespace URI
     * @param nsQualifier nullable namespace qualifier
     * @param docElemName notnull name of the document element
     * @return a new Document
     * @throws IOException if the parser configuration fails
     */
    public static Document createDocument(String nsURI, String nsQualifier, String docElemName) throws IOException {
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


    public static void validate(Document doc, URI schemaUri) throws IOException {
        XmlUtil.validate(doc, schemaUri.toURL());
    }
    public static void validate(Document doc, URL schemaUrl) throws IOException {
        SchemaFactory factory = SchemaFactory
                .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try (InputStream schemaStream = schemaUrl.openStream()) {
            Schema schema
                    = factory.newSchema(new StreamSource(schemaStream));
            Validator validator = schema.newValidator();
            validator.validate(new DOMSource(doc));
        } catch (SAXException e) {
            throw new IOException("The document is invalid.\n" + e.getMessage(), e);
        }
    }
    public static void validate(URI xmlUri, URI schemaUri) throws IOException {
        try (InputStream schemaStream = schemaUri.toURL().openStream()) {
            SchemaFactory factory = SchemaFactory
                    .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema
                    = factory.newSchema(schemaUri.toURL());
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new File(xmlUri)));
        } catch (SAXException e) {
            throw new IOException("Invalid XML file: "+xmlUri,e);
        }
    }
    public static void write(OutputStream out, Document doc) throws IOException {
        StreamResult result = new StreamResult(out);
        write(result, doc);
    }
    public static void write(File out, Document doc) throws IOException {
        StreamResult result = new StreamResult(out);
        write(result, doc);
    }
    public static void write(Result result, Document doc) throws IOException {
        try {
            final TransformerFactory factory = TransformerFactory.newInstance();
            Transformer t = factory.newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource source = new DOMSource(doc);
            t.transform(source, result);
        } catch (TransformerException ex) {
            throw new IOException(ex);
        }
    }

}
