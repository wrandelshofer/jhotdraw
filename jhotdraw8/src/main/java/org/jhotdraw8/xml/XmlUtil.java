/* @(#)XmlUtil.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jhotdraw8.tree.ChildIterator;
import org.jhotdraw8.tree.PreorderSpliterator;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.Attributes2Impl;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * XmlUtil.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class XmlUtil {

    public static final String LOCATION_ATTRIBUTE = "location";
    public static final String LOCATION_NAMESPACE = "http://location.xmlutil.ch";
    final static String LINE_NUMBER_KEY_NAME = "lineNumber";
    private static final String QUALIFIED_LOCATION_ATTRIBUTE = "xmlutil:location";
    private final static String SEPARATOR = "\0";
    private final static Properties DEFAULT_PROPERTIES = new Properties();

    static {
        DEFAULT_PROPERTIES.put(OutputKeys.INDENT,"yes");
        DEFAULT_PROPERTIES.put(OutputKeys.ENCODING,"UTF-8");
        DEFAULT_PROPERTIES.put("{http://xml.apache.org/xslt}indent-amount","2");
}

    private XmlUtil() {
    }

    /**
     * Creates a namespace aware document.
     *
     * @param nsURI       nullable namespace URI
     * @param nsQualifier nullable namespace qualifier
     * @param docElemName notnull name of the document element
     * @return a new Document
     * @throws IOException if the parser configuration fails
     */
    public static Document createDocument(@Nullable String nsURI, @Nullable String nsQualifier, String docElemName) throws IOException {
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

    @Nonnull
    public static Document readWithLocations(Path in, boolean namespaceAware) throws IOException {
        InputSource inputSource = new InputSource(in.toUri().toASCIIString());
        return XmlUtil.readWithLocations(inputSource, namespaceAware);
    }

    public static Document read(InputSource inputSource, boolean namespaceAware) throws IOException {
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            builderFactory.setNamespaceAware(namespaceAware);
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document doc = builder.parse(inputSource);
            return doc;
        } catch (@Nonnull SAXException | ParserConfigurationException ex) {
            throw new IOException(ex);
        }
    }

    /**
     * Reads the specified input into a document. Each Node contains a
     * "location" attribute specifying the file, the line number and the column
     * number of the node.
     * <p>
     * References:
     * <a href="https://stackoverflow.com/questions/2798376/is-there-a-way-to-parse-xml-via-sax-dom-with-line-numbers-available-per-node">
     * Stackoverflow</a>.
     *
     * @param inputSource    the input source
     * @param namespaceAware whether to be name space aware
     * @return the document
     * @throws java.io.IOException in case of failure
     */
    @Nonnull
    public static Document readWithLocations(InputSource inputSource, boolean namespaceAware) throws IOException {
        try {
            // Create transformer SAX source that adds current element position to
            // the element as attributes.
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            saxParserFactory.setNamespaceAware(namespaceAware);
            XMLReader xmlReader = saxParserFactory.newSAXParser().getXMLReader();
            LocationFilter locationFilter = new LocationFilter(xmlReader);
            SAXSource saxSource = new SAXSource(locationFilter, inputSource);

            // Perform an empty transformation from SAX source to DOM result.
            TransformerFactory transformerFactory = TransformerFactory.newInstance();

            Transformer transformer = transformerFactory.newTransformer();
            DOMResult domResult = new DOMResult();
            transformer.transform(saxSource, domResult);
            Node root = domResult.getNode();
            return (Document) root;
        } catch (@Nonnull TransformerException | SAXException | ParserConfigurationException ex) {
            throw new IOException(ex);
        }
    }

    public static Locator getLocator(Node node) {
        final NamedNodeMap attributes = node.getAttributes();
        Node attrNode = attributes == null ? null : attributes.getNamedItemNS(LOCATION_NAMESPACE, LOCATION_ATTRIBUTE);
        if (attrNode != null) {
            String[] parts = attrNode.getNodeValue().split(SEPARATOR);
            if (parts.length == 4) {
                return new MyLocator(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), parts[2], parts[3]);
            }
        }
        return null;
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

    public static void validate(@Nonnull Path xmlPath, URI schemaUri) throws IOException {
        validate(xmlPath.toUri(), schemaUri);
    }

    public static void validate(@Nonnull URI xmlUri, URI schemaUri) throws IOException {
        try {
            SchemaFactory factory = SchemaFactory
                    .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema
                    = factory.newSchema(schemaUri.toURL());
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(Paths.get(xmlUri).toFile()));
        } catch (SAXParseException e) {
            throw new IOException("Invalid XML file: " + e.getSystemId() + "\nError in line: " + e.getLineNumber() + ", column: " + e.getColumnNumber() + ".\n" + e.getMessage(), e);
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

    public static void write(@Nonnull Path out, Document doc) throws IOException {
        write(out, doc, DEFAULT_PROPERTIES);
    }

    public static void write(@Nonnull Path out, Document doc, Properties outputProperties) throws IOException {
        StreamResult result = new StreamResult(out.toFile());
        write(result, doc, outputProperties);
    }

    public static void write(Result result, Document doc) throws IOException {
        write(result,doc, DEFAULT_PROPERTIES);
    }

    public static void write(Result result, Document doc, @Nullable Properties outputProperties) throws IOException {
        try {
            final TransformerFactory factory = TransformerFactory.newInstance();
            Transformer t = factory.newTransformer();
            if (outputProperties!=null)
            t.setOutputProperties(outputProperties);
            DOMSource source = new DOMSource(doc);
            t.transform(source, result);
        } catch (TransformerException ex) {
            throw new IOException(ex);
        }
    }

    /**
     * Returns a stream which iterates over the subtree starting at the
     * specified node in preorder sequence.
     *
     * @param node a node
     * @return a stream
     */
    public static Stream<Node> preorderStream(Node node) {
        return StreamSupport.stream(new PreorderSpliterator<>(n -> {
            final NodeList childNodes = n.getChildNodes();
            return () -> new ChildIterator<>(childNodes.getLength(), childNodes::item);
        }, node), false);
    }

    private static class LocationFilter extends XMLFilterImpl {

        @Nullable
        private Locator locator = null;

        LocationFilter(XMLReader xmlReader) {
            super(xmlReader);
        }

        @Override
        public void setDocumentLocator(Locator locator) {
            super.setDocumentLocator(locator);
            this.locator = locator;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

            // Add extra attribute to elements to hold location
            Attributes2Impl attrs = new Attributes2Impl(attributes);
            attrs.addAttribute(LOCATION_NAMESPACE, LOCATION_ATTRIBUTE, QUALIFIED_LOCATION_ATTRIBUTE, "CDATA",
                    locator.getLineNumber() + SEPARATOR + locator.getColumnNumber() + SEPARATOR + locator.getSystemId() + SEPARATOR + locator.getPublicId());
            super.startElement(uri, localName, qName, attrs);
        }
    }

    private static class MyLocator implements Locator {

        private final int line;
        private final int column;
        private final String systemId;
        private final String publicId;

        public MyLocator(int line, int column, String systemId, String publicId) {
            this.line = line;
            this.column = column;
            this.systemId = systemId;
            this.publicId = publicId;
        }

        @Override
        public int getColumnNumber() {
            return column;
        }

        @Override
        public int getLineNumber() {
            return line;
        }

        @Override
        public String getPublicId() {
            return publicId;
        }

        @Override
        public String getSystemId() {
            return systemId;
        }

    }

}
