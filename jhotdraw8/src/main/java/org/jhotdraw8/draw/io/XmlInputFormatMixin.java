/* @(#)XmlInputFormatMixin.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.io;

import ch.systransis.arl.ferro.io.ManifestReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.URL;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Figure;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * XmlInputFormatMixin.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public interface XmlInputFormatMixin {

    void setExternalHome(URI uri);

    boolean isNamespaceAware();

    default Figure read(InputStream in, Drawing drawing) throws IOException {
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            if (isNamespaceAware()) {
                builderFactory.setNamespaceAware(true);
            }
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document doc = builder.parse(in);
            return read(doc, drawing);
        } catch (SAXException | ParserConfigurationException ex) {
            throw new IOException(ex);
        }
    }

    default Figure read(Reader in, Drawing drawing) throws IOException {
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            if (isNamespaceAware()) {
                builderFactory.setNamespaceAware(true);
            }
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            InputSource inputSource = new InputSource(in);
            Document doc = builder.parse(inputSource);
            return read(doc, drawing);
        } catch (SAXException | ParserConfigurationException ex) {
            throw new IOException(ex);
        }
    }

    default Figure read(String string, Drawing drawing) throws IOException {
        try (StringReader in = new StringReader(string)) {
            return read(in, drawing);
        }
    }

    Figure read(Document in, Drawing drawing) throws IOException;

    static void validateXML(URI xmlUri, URI schemaUri) throws IOException {
        try (InputStream schemaStream = schemaUri.toURL().openStream();
                InputStream xmlStream = xmlUri.toURL().openStream()) {
            SchemaFactory factory = SchemaFactory
                    .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema
                    = factory.newSchema(new StreamSource(schemaStream));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(xmlStream));
        } catch (SAXException e) {
            throw new IOException(e);
        }
    }

    static void validateDocument(Document doc, URI schemaUri) throws IOException {
        validateDocument(doc, schemaUri.toURL());
    }
    static void validateDocument(Document doc, URL schemaUrl) throws IOException {
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
}
