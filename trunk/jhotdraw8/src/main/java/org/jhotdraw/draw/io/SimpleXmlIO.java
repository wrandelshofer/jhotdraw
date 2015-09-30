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
 This i/o-format only works, if a drawing can be described entirely based on
 the getProperties of its figures.
 <p>
 *
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleXmlIO implements InputFormat, OutputFormat {

    private FigureFactory factory;
    private IdFactory ids = new SimpleIdFactory();
    private ArrayList<Element> figureElements = new ArrayList<>();

    public SimpleXmlIO(FigureFactory factory) {
        this.factory = factory;
    }

    @Override
    public Drawing read(InputStream in, Drawing drawing) throws IOException {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
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
        Drawing tmp = fromDocument(in);
        if (drawing != null) {
            drawing.childrenProperty().addAll(tmp.childrenProperty());
            drawing.getProperties().putAll(tmp.getProperties());
            tmp = drawing;
        }
        return tmp;
    }

    public Document toDocument(Drawing drawing) throws IOException {
        try {
            ids.reset();
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.newDocument();
            if (factory.figureToName(drawing) != null) {
                doc.appendChild(writeNodeRecursively(doc, drawing));
            }
            return doc;
        } catch (ParserConfigurationException ex) {
            throw new IOException(ex);
        }
    }

    private Node writeNodeRecursively(Document doc, Figure figure) throws IOException {
        Element elem = doc.createElement(factory.figureToName(figure));
        elem.setAttribute("id", ids.createId(figure));
        for (Key<?> k : factory.figureKeys(figure)) {
            Key<Object> key = (Key<Object>) k;
            Object value = figure.get(key);
            if (!factory.isDefaultValue(key, value)) {
                if (Figure.class.isAssignableFrom(key.getValueType())) {
                    elem.setAttribute(factory.keyToName(figure, key), ids.createId(value));
                } else {
                    elem.setAttribute(factory.keyToName(figure, key), factory.valueToString(key, value));
                }
            }
        }

        for (Figure child : figure.childrenProperty()) {
            if (factory.figureToName(child) != null) {
                elem.appendChild(doc.createTextNode("\n"));
                elem.appendChild(writeNodeRecursively(doc, child));
            }
        }
        if (!figure.childrenProperty().isEmpty()) {
            elem.appendChild(doc.createTextNode("\n"));
        }
        return elem;
    }

    public Drawing fromDocument(Document doc) throws IOException {
        ids.reset();
        figureElements.clear();
        Drawing drawing = null;
        NodeList list = doc.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Figure f = readNodeRecursively(list.item(i));
            if (f instanceof Drawing) {
                drawing = (Drawing) f;
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
        if (drawing != null) {
            return drawing;
        } else {
            throw new IOException("document does not contain a drawing");
        }
    }

    /**
     * Creates a figure but does not process the getProperties.
     */
    private Figure readNodeRecursively(Node node) throws IOException {
        if (node instanceof Element) {
            Element elem = (Element) node;

            Figure figure = factory.nameToFigure(elem.getTagName());
            if (figure == null) {
                return null;
            }
            figureElements.add(elem);
            String id = elem.getAttribute("id");
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
     * Creates a figure but does not process the getProperties.
     */
    private void readElementAttributes(Element elem) throws IOException {

        Figure figure = null;
        String id = elem.getAttribute("id");
        if (id != null) {
            figure = getFigure(id);
        }
        if (figure != null) {
            NamedNodeMap attrs = elem.getAttributes();
            for (int i = 0, n = attrs.getLength(); i < n; i++) {
                Attr attr = (Attr) attrs.item(i);
                if ("id".equals(attr.getName())) {
                    continue;
                }
                Key<Object> key = (Key<Object>) factory.nameToKey(figure, attr.getName());
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
