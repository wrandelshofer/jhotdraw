/* @(#)XmlInputFormatMixin.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
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
        Source xmlFile = new StreamSource(new File(xmlUri));
        SchemaFactory schemaFactory = SchemaFactory
                .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            Schema schema = schemaFactory.newSchema(new File(schemaUri));
            Validator validator = schema.newValidator();
            validator.validate(xmlFile);
        } catch (SAXException e) {
            throw new IOException(e);
        }
    }
}
