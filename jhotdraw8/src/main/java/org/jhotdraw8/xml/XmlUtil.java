/* @(#)XmlUtil.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.Attributes2Impl;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * XmlUtil.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class XmlUtil {

    final static String LINE_NUMBER_KEY_NAME = "lineNumber";

    private XmlUtil() {
    }

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

    public static Document read(Reader in, boolean namespaceAware) throws IOException {
        InputSource inputSource = new InputSource(in);
        return XmlUtil.read(inputSource, namespaceAware);

    }

    public static Document read(InputStream in, boolean namespaceAware) throws IOException {
        InputSource inputSource = new InputSource(in);
        return XmlUtil.read(inputSource, namespaceAware);

    }

    public static Document read(Path in, boolean namespaceAware) throws IOException {
        InputSource inputSource = new InputSource(in.toUri().toASCIIString());
        return XmlUtil.read(inputSource, namespaceAware);
    }
    public static Document readWithLocations(Path in, boolean namespaceAware) throws IOException {
        InputSource inputSource = new InputSource(in.toUri().toASCIIString());
        return XmlUtil.readWithLocations(inputSource, namespaceAware);
    }

    public static Document read(InputSource inputSource, boolean namespaceAware) throws IOException {
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            if (namespaceAware) {
                builderFactory.setNamespaceAware(true);
            }
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document doc = builder.parse(inputSource);
            return doc;
        } catch (SAXException | ParserConfigurationException ex) {
            throw new IOException(ex);
        }
    }

    /**
     * Reds the specified input into a document. Each Node contains a "location" attribute
     * specifying the file, the line number and the column number of the node.
     * 
     * References:
     * <a href="https://stackoverflow.com/questions/2798376/is-there-a-way-to-parse-xml-via-sax-dom-with-line-numbers-available-per-node">
     * Stackoverflow</a>.
     */
    public static Document readWithLocations(InputSource inputSource, boolean namespaceAware) throws IOException {
        try {
            // Create transformer SAX source that adds current element position to
            // the element as attributes.
            XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            LocationFilter locationFilter = new LocationFilter(xmlReader);
            SAXSource saxSource = new SAXSource(locationFilter, inputSource);

            // Perform an empty transformation from SAX source to DOM result.
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMResult domResult = new DOMResult();
            transformer.transform(saxSource, domResult);
            Node root = domResult.getNode();
            return (Document) root;
        } catch (TransformerException | SAXException|ParserConfigurationException ex) {
            throw new IOException(ex);
        }
    }
    public static final String SYSTEM_ID_ATTRIBUTE = "systemId";
    public static final String LINE_NUMBER_ATTRIBUTE = "line";
    public static final String COLUMN_NUMBER_ATTRIBUTE = "column";
    private static final String QUALIFIED_SYSTEM_ID_ATTRIBUTE = "xmlutil:systemId";
    private static final String QUALIFIED_LINE_ATTRIBUTE = "xmlutil:line";
    private static final String QUALIFIED_COLUMN_ATTRIBUTE = "xmlutil:column";

    public static final String LOCATION_NAMESPACE = "http://location.xmlutil.ch";

    private static class LocationFilter extends XMLFilterImpl {

        LocationFilter(XMLReader xmlReader) {
            super(xmlReader);
        }

        private Locator locator = null;

        @Override
        public void setDocumentLocator(Locator locator) {
            super.setDocumentLocator(locator);
            this.locator = locator;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

            // Add extra attribute to elements to hold location
            Attributes2Impl attrs = new Attributes2Impl(attributes);
            attrs.addAttribute(LOCATION_NAMESPACE, SYSTEM_ID_ATTRIBUTE, QUALIFIED_SYSTEM_ID_ATTRIBUTE, "CDATA", locator.getSystemId());
            attrs.addAttribute(LOCATION_NAMESPACE, LINE_NUMBER_ATTRIBUTE, QUALIFIED_LINE_ATTRIBUTE, "CDATA",Integer.toString( locator.getLineNumber()));
            attrs.addAttribute(LOCATION_NAMESPACE, COLUMN_NUMBER_ATTRIBUTE, QUALIFIED_COLUMN_ATTRIBUTE, "CDATA", Integer.toString(locator.getColumnNumber()));
            super.startElement(uri, localName, qName, attrs);
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
        try {
            SchemaFactory factory = SchemaFactory
                    .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema
                    = factory.newSchema(schemaUri.toURL());
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new File(xmlUri)));
        } catch (SAXParseException e) {
            throw new IOException("Invalid XML file: "+e.getSystemId()+"\nError in line: "+e.getLineNumber()+", column: "+e.getColumnNumber()+".",e);
        } catch (SAXException e) {
            
            throw new IOException("Invalid XML file: " + xmlUri, e);
        }
    }

    public static void write(OutputStream out, Document doc) throws IOException {
        StreamResult result = new StreamResult(out);
        write(result, doc);
    }

    public static void write(Writer out, Document doc) throws IOException {
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
