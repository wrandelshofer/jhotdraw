/*
 * @(#)SimpleXmlIO.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import org.jhotdraw.draw.Drawing;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.jhotdraw.collection.Key;
import org.jhotdraw.draw.Figure;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * SimpleXmlIO.
 * <p>
 * Represents each Figure by an element, and each figure property by an
 * attribute.
 * <p>
 * All attribute values are treated as value types, except if an attribute type
 * is an instance of Figure.
 * <p>
 * This i/o-format only works, if a drawing can be described entirely based on
 * the getProperties of its figures.
 * <p>
 *
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleXmlIO implements InputFormat, OutputFormat {

    private FigureFactory factory;
    private IdFactory ids;
    private ArrayList<Element> figureElements = new ArrayList<>();
    private String namespaceURI;
    private String namespaceQualifier;

    public SimpleXmlIO(FigureFactory factory) {
        this(factory, null, null, null);
    }

    public SimpleXmlIO(FigureFactory factory, IdFactory idFactory, String namespaceURI, String namespaceQualifier) {
        this.factory = factory;
        this.ids = idFactory == null ? new SimpleIdFactory() : idFactory;
        this.namespaceURI = namespaceURI;
        this.namespaceQualifier = namespaceQualifier;
    }

    @Override
    public Drawing read(InputStream in, Drawing drawing) throws IOException {
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            if (namespaceURI != null) {
                builderFactory.setNamespaceAware(true);
            }
            builderFactory.setNamespaceAware(true);
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document doc = builder.parse(in);
            return read(doc, drawing);
        } catch (SAXException | ParserConfigurationException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void write(File file, Drawing drawing) throws IOException {
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
    public void write(OutputStream out, Drawing drawing) throws IOException {
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

    public Drawing read(Document in, Drawing drawing) throws IOException {
        return fromDocument(in);
    }

    public Document toDocument(Drawing internal) throws IOException {
        try {
            Drawing external = factory.toExternalDrawing(internal);

            ids.reset();
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            Document doc;
            if (namespaceURI != null) {
                builderFactory.setNamespaceAware(true);
                DocumentBuilder builder = builderFactory.newDocumentBuilder();
                DOMImplementation domImpl = builder.getDOMImplementation();
                doc = domImpl.createDocument(namespaceURI, namespaceQualifier == null ? factory.figureToName(external) : namespaceQualifier + ":" + factory.figureToName(internal), null);
            } else {
                DocumentBuilder builder = builderFactory.newDocumentBuilder();
                doc = builder.newDocument();
                Element elem = doc.createElement(factory.figureToName(external));
                doc.appendChild(elem);
            }
            Element docElement = doc.getDocumentElement();
            writeElementAttributes(docElement, external);
            for (Figure child : external.getChildren()) {
                Node childNode = writeNodeRecursively(doc, child);
                if (childNode != null) {
                    // => the factory decided that we should skip the figure
                    docElement.appendChild(childNode);
                }
            }
            return doc;
        } catch (ParserConfigurationException ex) {
            throw new IOException(ex);
        }
    }

    private Element createElement(Document doc, String unqualifiedName) throws IOException {
        if (namespaceURI == null || namespaceQualifier == null) {
            return doc.createElement(unqualifiedName);
        }
        if (namespaceQualifier == null) {
            return doc.createElementNS(namespaceURI, unqualifiedName);
        } else {
            return doc.createElementNS(namespaceURI, namespaceQualifier + ":" + unqualifiedName);
        }
    }

    private Node writeNodeRecursively(Document doc, Figure figure) throws IOException {
        String elementName = factory.figureToName(figure);
        if (elementName == null) {
            // => the factory decided that we should skip the figure
            return null;
        }
        Element elem = createElement(doc, elementName);
        writeElementAttributes(elem, figure);

        for (Figure child : figure.childrenProperty()) {
            if (factory.figureToName(child) != null) {
                elem.appendChild(doc.createTextNode("\n"));
                Node childNode = writeNodeRecursively(doc, child);
                if (childNode != null) {
                    // => the factory decided that we should skip the figure
                    elem.appendChild(childNode);
                }
            }
        }
        if (!figure.childrenProperty().isEmpty()) {
            elem.appendChild(doc.createTextNode("\n"));
        }
        return elem;
    }

    private void setAttribute(Element elem, String unqualifiedName, String value) throws IOException {
        if (namespaceURI == null || namespaceQualifier == null) {
            elem.setAttribute(unqualifiedName, value);
        } else {
            elem.setAttributeNS(namespaceURI, namespaceQualifier + ":" + unqualifiedName, value);
        }
    }

    private void writeElementAttributes(Element elem, Figure figure) throws IOException {
        setAttribute(elem, "id", ids.createId(figure));
        for (Key<?> k : factory.figureKeys(figure)) {
            @SuppressWarnings("unchecked")
            Key<Object> key = (Key<Object>) k;
            Object value = figure.get(key);
            if (!factory.isDefaultValue(key, value)) {
                if (Figure.class.isAssignableFrom(key.getValueType())) {
                    setAttribute(elem, factory.keyToName(figure, key), ids.createId(value));
                } else {
                    setAttribute(elem, factory.keyToName(figure, key), factory.valueToString(key, value));
                }
            }
        }
    }

    public Drawing fromDocument(Document doc) throws IOException {
        ids.reset();
        figureElements.clear();
        Drawing external = null;
        NodeList list = doc.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Figure f = readNodeRecursively(list.item(i));
            if (f instanceof Drawing) {
                external = (Drawing) f;
                break;
            }
        }
        try {
            for (Element elem : figureElements) {
                readElementAttributes(elem);
            }
        } finally {
            figureElements.clear();
        }
        if (external != null) {
            return factory.fromExternalDrawing(external);
        } else {
            throw new IOException(//
                    namespaceURI == null//
                            ? "document does not contain a drawing"//
                            : "document does not contain a drawing in namespace " + namespaceURI//
            );
        }
    }

    private String getAttribute(Element elem, String unqualifiedName) {
        if (namespaceURI == null) {
            return elem.getAttribute(unqualifiedName);
        } else {
            if (elem.hasAttributeNS(namespaceURI, unqualifiedName)) {
                return elem.getAttributeNS(namespaceURI, unqualifiedName);
            } else {
                if (elem.isDefaultNamespace(namespaceURI)) {
                    return elem.getAttribute(unqualifiedName);
                }
            }
        }
        return null;
    }

    /**
     * Creates a figure but does not process the getProperties.
     */
    private Figure readNodeRecursively(Node node) throws IOException {
        if (node instanceof Element) {
            Element elem = (Element) node;
            if (namespaceURI != null) {
                if (!namespaceURI.equals(elem.getNamespaceURI())) {
                    return null;
                }
            }
            Figure figure = factory.nameToFigure(elem.getLocalName());
            if (figure == null) {
                return null;
            }
            figureElements.add(elem);
            String id = getAttribute(elem, "id");
            if (id != null) {
                ids.putId(figure, id);
            }
            NodeList list = elem.getChildNodes();
            for (int i = 0; i < list.getLength(); i++) {
                Figure child = readNodeRecursively(list.item(i));
                if (child instanceof Figure) {
                    figure.add(child);
                }
            }
            return figure;
        }
        return null;
    }

    /**
     * Reads the attributes of the specified element.
     */
    private void readElementAttributes(Element elem) throws IOException {
        if (namespaceURI != null && !namespaceURI.equals(elem.getNamespaceURI())) {
            return;
        }
        Figure figure = null;
        String id = getAttribute(elem, "id");
        if (id != null) {
            figure = getFigure(id);
        }
        if (figure != null) {
            NamedNodeMap attrs = elem.getAttributes();
            for (int i = 0, n = attrs.getLength(); i < n; i++) {
                Attr attr = (Attr) attrs.item(i);
                if (attr.getNamespaceURI() != null && !attr.getNamespaceURI().equals(namespaceURI)) {
                    continue;
                }

                if ("id".equals(attr.getLocalName())) {
                    continue;
                }
                @SuppressWarnings("unchecked")
                Key<Object> key = (Key<Object>) factory.nameToKey(figure, attr.getLocalName());
                if (key != null && factory.figureKeys(figure).contains(key)) {
                    Object value = null;
                    if (Figure.class.isAssignableFrom(key.getValueType())) {
                        value = getFigure(attr.getValue());
                    } else {
                        value = factory.stringToValue(key, attr.getValue());
                    }
                    figure.set(key, value);
                }
            }
        }
    }

    public Figure getFigure(String id) throws IOException {
        Figure f = (Figure) ids.getObject(id);
        if (f == null) {
            throw new IOException("no figure for id:" + id);
        }
        return f;
    }
}
