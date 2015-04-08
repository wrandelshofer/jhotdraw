/* @(#)SimpleXmlIO.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jhotdraw.draw.Drawing;
import org.w3c.dom.DOMImplementation;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.jhotdraw.collection.Key;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.text.CDataConverter;
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
 * Represents each Figure by an element, and each figure property by
 * an attribute.
 * <p>
 * All attribute values are treated as value types, except if an attribute
 * type is an instance of Figure.
 * <p>
 * This i/o-format only works, if a drawing can be described entirely based
 * on the properties of its figures.
 * <p>
 *
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleXmlIO implements InputFormat, OutputFormat {

    private FigureFactory factory;
    private IdFactory ids = new SimpleIdFactory();
    private final CDataConverter cdataConverter = new CDataConverter();

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
        Drawing tmp = ofDocument(in);
        if (drawing != null) {
            drawing.children().addAll(tmp.children());
            drawing.properties().putAll(tmp.properties());
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
                doc.appendChild(writeNode(doc, drawing));
            }
            return doc;
        } catch (ParserConfigurationException ex) {
            throw new IOException(ex);
        }
    }

    private Node writeNode(Document doc, Figure figure) throws IOException {
        Element elem = doc.createElement(factory.figureToName(figure));
        elem.setAttribute("id", ids.createId(figure));
        for (Key<?> k : factory.figureKeys(figure)) {
            Key<Object> key = (Key<Object>) k;
            Object value = figure.get(key);
            if (!factory.isDefaultValue(key, value)) {
                if (Figure.class.isAssignableFrom(key.getValueType())) {
                    elem.setAttribute(factory.keyToName(key), cdataConverter.toString(ids.createId(value)));
                } else {
                    elem.setAttribute(factory.keyToName(key), cdataConverter.toString(factory.valueToString(key, value)));
                }
            }
        }

        for (Figure child : figure.children()) {
            if (factory.figureToName(child) != null) {
                elem.appendChild(doc.createTextNode("\n"));
                elem.appendChild(writeNode(doc, child));
            }
        }
        if (!figure.children().isEmpty()) {
            elem.appendChild(doc.createTextNode("\n"));
        }
        return elem;
    }

    public Drawing ofDocument(Document doc) throws IOException {
        ids.reset();
        Drawing drawing = null;
        NodeList list = doc.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Figure f = readNode(list.item(i));
            if (f instanceof Drawing) {
                drawing = (Drawing) f;
                break;
            }
        }
        for (int i = 0; i < list.getLength(); i++) {
            readElementAttributes(list.item(i));
        }
        if (drawing != null) {
            return drawing;
        } else {
            throw new IOException("document does not contain a drawing");
        }
    }

    /** Creates a figure but does not process the properties. */
    private Figure readNode(Node node) throws IOException {
        if (node instanceof Element) {
            Element elem = (Element) node;

            Figure figure = factory.nameToFigure(elem.getTagName());
            if (figure == null) {
                return null;
            }
            String id = elem.getAttribute("id");
            if (id != null) {
                ids.putId(figure, id);
            }
            NodeList list = elem.getChildNodes();
            for (int i = 0; i < list.getLength(); i++) {
                Figure child = readNode(list.item(i));
                if (child instanceof Figure) {
                    figure.add(child);
                }
            }
            return figure;
        }
        return null;
    }

    /** Creates a figure but does not process the properties. */
    private void readElementAttributes(Node node) throws IOException {
        if (node instanceof Element) {
            Element elem = (Element) node;

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
                    Key<Object> key = (Key<Object>) factory.nameToKey(attr.getName());
                    if (key != null && factory.figureKeys(figure).contains(key)) {
                        Object value = null;
                        if (Figure.class.isAssignableFrom(key.getValueType())) {
                            value = getFigure(cdataConverter.toValue(attr.getValue()));
                        } else {
                            value = factory.stringToValue(key, cdataConverter.toValue(attr.getValue()));
                        }
                        figure.set(key, value);
                    }
                }
            }
            NodeList list = elem.getChildNodes();
            for (int i = 0, n = list.getLength(); i < n; i++) {
                readElementAttributes(list.item(i));
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
