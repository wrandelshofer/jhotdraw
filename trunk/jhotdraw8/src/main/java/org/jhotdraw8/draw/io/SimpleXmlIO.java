/*
 * @(#)SimpleXmlIO.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import org.jhotdraw8.draw.Drawing;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.draw.Clipping;
import org.jhotdraw8.draw.Layer;
import org.jhotdraw8.draw.SimpleClipping;
import org.jhotdraw8.draw.SimpleLayer;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.StyleableFigure;
import org.jhotdraw8.draw.input.ClipboardInputFormat;
import org.jhotdraw8.draw.input.ClipboardOutputFormat;
import org.jhotdraw8.draw.model.DrawingModel;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;

/**
 * SimpleXmlIO.
 * <p>
 * Represents each Figure by an element, and each figure property by an
 * attribute.
 * <p>
 * All attribute values are treated as value types, except if an attribute type
 * is an instance of Figure.
 * <p>
 * This i/o-format only works for drawings which can be described entirely by
 * the properties of its figures.
 * <p>
 *
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleXmlIO implements InputFormat, OutputFormat, XmlOutputFormatMixin, XmlInputFormatMixin, ClipboardOutputFormat, ClipboardInputFormat {

    private FigureFactory factory;
    private String namespaceURI;
    private String namespaceQualifier;
    private HashMap<Figure, Element> figureToElementMap = new HashMap<>();
    private URI externalHome;
    private URI internalHome;

    public SimpleXmlIO(FigureFactory factory) {
        this(factory, null, null, null);
    }

    public SimpleXmlIO(FigureFactory factory, IdFactory idFactory, String namespaceURI, String namespaceQualifier) {
        this.factory = factory;
        this.namespaceURI = namespaceURI;
        this.namespaceQualifier = namespaceQualifier;
    }

    /**
     * Must be a directory and not a file.
     */
    public void setExternalHome(URI uri) {
        externalHome = uri;
    }

    public URI getExternalHome() {
        return externalHome;
    }

    /**
     * Must be a directory and not a file.
     */
    public void setInternalHome(URI uri) {
        internalHome = uri;
    }

    public URI getInternalHome() {
        return internalHome;
    }

    @Override
    public Figure read(File file, Drawing drawing) throws IOException {
        setExternalHome(file.getParentFile() == null ? new File(System.getProperty("user.home")).toURI() : file.getParentFile().toURI());
        setInternalHome(drawing == null ? getExternalHome() : drawing.get(Drawing.DOCUMENT_HOME));
        return InputFormat.super.read(file, drawing);
    }

    public Figure read(Document in, Drawing drawing) throws IOException {
        return fromDocument(in, drawing);
    }

    public Document toDocument(Drawing internal, Collection<Figure> selection) throws IOException {
        if (selection.isEmpty() || selection.contains(internal)) {
            return toDocument(internal);
        }

        try {
            Clipping external = new SimpleClipping();

            factory.reset();
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

            String commentText = factory.createFileComment();
            if (commentText != null) {
                docElement.getParentNode().insertBefore(doc.createComment(commentText), docElement);
            }

            for (Figure child : selection) {
                Node childNode = writeNodeRecursively(doc, child);
                if (childNode != null) {
                    // => the factory decided that we should not skip the figure
                    docElement.appendChild(doc.createTextNode("\n"));
                    docElement.appendChild(childNode);
                }
            }
            docElement.appendChild(doc.createTextNode("\n"));
            return doc;
        } catch (ParserConfigurationException ex) {
            throw new IOException(ex);
        }
    }

    public Document toDocument(Drawing internal) throws IOException {
        try {
            Drawing external = factory.toExternalDrawing(internal);

            factory.reset();
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

            writeProcessingInstructions(doc, external);

            String commentText = factory.createFileComment();
            if (commentText != null) {
                docElement.getParentNode().insertBefore(doc.createComment(commentText), docElement);
            }

            writeElementAttributes(docElement, external);
            for (Figure child : external.getChildren()) {
                Node childNode = writeNodeRecursively(doc, child);
                if (childNode != null) {
                    // => the factory decided that we should not skip the figure
                    docElement.appendChild(doc.createTextNode("\n"));
                    docElement.appendChild(childNode);
                }
            }
            docElement.appendChild(doc.createTextNode("\n"));
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
        try {
            String elementName = factory.figureToName(figure);
            if (elementName == null) {
                // => the factory decided that we should skip the figure
                return null;
            }
            Element elem = createElement(doc, elementName);
            writeElementAttributes(elem, figure);
            writeElementNodeList(doc, elem, figure);

            for (Figure child : figure.getChildren()) {
                if (factory.figureToName(child) != null) {
                    elem.appendChild(doc.createTextNode("\n"));
                    Node childNode = writeNodeRecursively(doc, child);
                    if (childNode != null) {
                        // => the factory decided that we should skip the figure
                        elem.appendChild(childNode);
                    }
                }
            }
            if (!figure.getChildren().isEmpty()) {
                elem.appendChild(doc.createTextNode("\n"));
            }
            return elem;
        } catch (IOException e) {
            throw new IOException("Error writing figure " + figure, e);
        }
    }

    private void setAttribute(Element elem, String unqualifiedName, String value) throws IOException {
        if (namespaceURI == null || namespaceQualifier == null) {
            if (!elem.hasAttribute(unqualifiedName)) {
                elem.setAttribute(unqualifiedName, value);
            }
        } else {
            if (!elem.hasAttributeNS(namespaceURI, namespaceQualifier + ":" + unqualifiedName)) {
                elem.setAttributeNS(namespaceURI, namespaceQualifier + ":" + unqualifiedName, value);
            }
        }
    }

    private void writeElementAttributes(Element elem, Figure figure) throws IOException {
        setAttribute(elem, factory.getObjectIdAttribute(), factory.createId(figure));
        for (MapAccessor<?> k : factory.figureAttributeKeys(figure)) {
            if (k.isTransient()) {
                continue;
            }
            @SuppressWarnings("unchecked")
            MapAccessor<Object> key = (MapAccessor<Object>) k;
            Object value = figure.get(key);

            if (value instanceof URI) {
                value = internalToExternal(figure.getDrawing(), (URI) value);
            }

            if (!factory.isDefaultValue(figure, key, value)) {
                String name = factory.keyToName(figure, key);
                if (Figure.class.isAssignableFrom(key.getValueType())) {
                    setAttribute(elem, name, factory.createId(value));
                } else {
                    setAttribute(elem, name, factory.valueToString(key, value));
                }
            }
        }
    }

    private void writeElementNodeList(Document document, Element elem, Figure figure) throws IOException {
        for (MapAccessor<?> k : factory.figureNodeListKeys(figure)) {
            @SuppressWarnings("unchecked")
            MapAccessor<Object> key = (MapAccessor<Object>) k;
            Object value = figure.get(key);
            if (!factory.isDefaultValue(figure, key, value)) {
                for (Node node : factory.valueToNodeList(key, value, document)) {
                    elem.appendChild(node);
                }
            }
        }
    }

    public Figure fromDocument(Document doc, Drawing oldDrawing) throws IOException {
        factory.reset();
        if (oldDrawing != null) {
            for (Figure f : oldDrawing.preorderIterable()) {
                factory.createId(f);
            }
        }
        figureToElementMap.clear();
        Drawing external = null;
        Clipping clipping = null;
        NodeList list = doc.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Figure f = readNodesRecursively(list.item(i));
            if (f instanceof Drawing) {
                external = (Drawing) f;
                break;
            } else if (f instanceof Clipping) {
                clipping = (Clipping) f;
                break;
            }
        }
        if (external == null && clipping == null) {
            if (namespaceURI == null) {
                throw new IOException("The document does not contain a drawing.");
            } else {
                throw new IOException("The document does not contain a drawing in namespace \"" + namespaceURI + "\".");
            }
        }
        if (external != null) {
            external.set(Drawing.DOCUMENT_HOME, getExternalHome());
            readProcessingInstructions(doc, external);
        }
        try {
            for (Map.Entry<Figure, Element> entry : figureToElementMap.entrySet()) {
                readElementAttributes(entry.getKey(), entry.getValue());
                readElementNodeList(entry.getKey(), entry.getValue());
            }
        } finally {
            figureToElementMap.clear();
        }

        if (external != null) {
            Drawing internal = factory.fromExternalDrawing(external);
            internal.updateCss();
            return internal;
        } else {
            return clipping;
        }
    }

    private String getAttribute(Element elem, String unqualifiedName) {
        if (namespaceURI == null) {
            return elem.getAttribute(unqualifiedName);
        } else if (elem.hasAttributeNS(namespaceURI, unqualifiedName)) {
            return elem.getAttributeNS(namespaceURI, unqualifiedName);
        } else if (elem.isDefaultNamespace(namespaceURI)) {
            return elem.getAttribute(unqualifiedName);
        }
        return null;
    }

    /**
     * Creates a figure but does not process the getProperties.
     */
    private Figure readNodesRecursively(Node node) throws IOException {
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
            figureToElementMap.put(figure, elem);
            String id = getAttribute(elem, factory.getObjectIdAttribute());

            if (id != null && !id.isEmpty()) {
                if (factory.getObject(id) != null) {
                    System.err.println("warning: duplicate id " + id + " in element " + elem.getTagName());
                    factory.putId(figure, id);
                } else {
                    factory.putId(figure, id);
                }
            }
            NodeList list = elem.getChildNodes();
            for (int i = 0; i < list.getLength(); i++) {
                Figure child = readNodesRecursively(list.item(i));
                if (child instanceof Figure) {
                    if (!child.isSuitableParent(figure)) {
                        throw new IOException(list.item(i).getNodeName() + " is not a suitable child for " + elem.getTagName() + ".");
                    }
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
    private void readElementAttributes(Figure figure, Element elem) throws IOException {
        for (MapAccessor<?> ma : factory.figureAttributeKeys(figure)) {
            @SuppressWarnings("unchecked")
            MapAccessor<Object> mao = (MapAccessor<Object>) ma;
            Object defaultValue = factory.getDefaultValue(figure, ma);
            figure.set(mao, defaultValue);
        }

        NamedNodeMap attrs = elem.getAttributes();
        for (int i = 0, n = attrs.getLength(); i < n; i++) {
            Attr attr = (Attr) attrs.item(i);
            if (attr.getNamespaceURI() != null && !attr.getNamespaceURI().equals(namespaceURI)) {
                continue;
            }

            /*if (factory.getObjectIdAttribute().equals(attr.getLocalName())) {
                continue;
            }*/
            @SuppressWarnings("unchecked")
            MapAccessor<Object> key = (MapAccessor<Object>) factory.nameToKey(figure, attr.getLocalName());
            if (key != null && factory.figureAttributeKeys(figure).contains(key)) {
                Object value = null;
                if (Figure.class.isAssignableFrom(key.getValueType())) {
                    value = getFigure(attr.getValue());
                } else {
                    value = factory.stringToValue(key, attr.getValue());
                }

                if (value instanceof URI) {
                    value = externalToInternal(figure.getDrawing(), (URI) value);
                }

                figure.set(key, value);
            }
        }
    }

    /**
     * Reads the children of the specified element as a node list.
     */
    private void readElementNodeList(Figure figure, Element elem) throws IOException {
        Set<MapAccessor<?>> keys = factory.figureNodeListKeys(figure);
        for (MapAccessor<?> ky : keys) {
            @SuppressWarnings("unchecked")
            MapAccessor<Object> key = (MapAccessor<Object>) ky;
            String name = factory.keyToElementName(figure, key);
            if ("".equals(name)) {
                List<Node> nodeList = new ArrayList<>();
                NodeList children = elem.getChildNodes();
                for (int i = 0, n = children.getLength(); i < n; i++) {
                    Node child = children.item(i);
                    if (child.getNodeType() == Node.TEXT_NODE) {
                        nodeList.add(child);
                    } else if (child.getNodeType() == Node.ELEMENT_NODE) {
                        // filter out element nodes of child figures
                        Element childElem = (Element) child;
                        if (namespaceURI != null) {
                            if (!namespaceURI.equals(childElem.getNamespaceURI())) {
                                nodeList.add(child);
                            }
                        } else {
                            try {
                                if (factory.nameToFigure(childElem.getLocalName()) != null) {
                                    continue;
                                }
                            } catch (IOException e) {
                                continue;
                            }
                            nodeList.add(child);
                        }
                    }
                }
                Object value = factory.nodeListToValue(key, nodeList);
                figure.set(key, value);
            } else {
                throw new UnsupportedOperationException("Reading of sub-elements is not yet supported");
            }
        }

    }

    public Figure getFigure(String id) throws IOException {
        Figure f = (Figure) factory.getObject(id);
        /*        if (f == null) {
            throw new IOException("no figure for id:" + id);
        }*/
        return f;
    }

    // XXX maybe this should not be in SimpleXmlIO?
    private void writeProcessingInstructions(Document doc, Drawing external) {
        Element docElement = doc.getDocumentElement();
        if (factory.getStylesheetsKey() != null && external.get(factory.getStylesheetsKey()) != null) {
            for (Object stylesheet : external.get(factory.getStylesheetsKey())) {
                if (stylesheet instanceof URI) {
                    stylesheet = internalToExternal(external, (URI) stylesheet);

                    String stylesheetString = stylesheet.toString();
                    String type = "text/" + stylesheetString.substring(stylesheetString.lastIndexOf('.') + 1);
                    if ("text/".equals(type)) {
                        type = "text/css";
                    }
                    ProcessingInstruction pi = doc.createProcessingInstruction("xml-stylesheet", //
                            "type=\"" + type + "\" href=\"" + stylesheet + "\"");
                    doc.insertBefore(pi, docElement);
                }
            }
        }

    }

    // XXX maybe this should not be in SimpleXmlIO?
    private void readProcessingInstructions(Document doc, Drawing external) {
        if (factory.getStylesheetsKey() != null) {
            Pattern hrefPattern = Pattern.compile("(?:^|.* )href=\"([^\"]*)\".*");
            ArrayList<URI> stylesheets = new ArrayList<URI>();
            NodeList list = doc.getChildNodes();
            for (int i = 0, n = list.getLength(); i < n; i++) {
                Node node = list.item(i);
                if (node.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE) {
                    ProcessingInstruction pi = (ProcessingInstruction) node;
                    if ("xml-stylesheet".equals(pi.getNodeName()) && pi.getData() != null) {
                        Matcher m = hrefPattern.matcher(pi.getData());
                        if (m.matches()) {
                            String href = m.group(1);

                            URI uri = URI.create(href);
                            uri = externalToInternal(external, uri);
                            stylesheets.add(uri);
                        }
                    }
                }
            }
            external.set(factory.getStylesheetsKey(), stylesheets);
        }
    }

    /**
     * Internal URI is relative to document home. Make it relative to the file.
     * we are writing.
     *
     * @param drawing the drawing
     * @param internal the internal uri
     * @return the external uri
     */
    private URI internalToExternal(Drawing drawing, URI internal) {
        URI external = internal;
        if (internalHome != null) {
            external = internalHome.resolve(external);
        }
        if (externalHome != null) {
            external = externalHome.relativize(external);
        }
        return external;
    }

    /**
     * External URI is relative to file that we are reading. Make it relative to
     * document home.
     *
     * @param drawing the drawing
     * @param external the external uri
     * @return the internal uri
     */
    private URI externalToInternal(Drawing drawing, URI external) {
        URI internal = external;
        if (externalHome != null) {
            internal = externalHome.resolve(internal);
        }
        if (internalHome != null) {
            internal = internalHome.relativize(internal);
        }
        return internal;
    }

    private DataFormat getDataFormat() {
        String mimeType = "text/xml";
        DataFormat df = DataFormat.lookupMimeType(mimeType);
        if (df == null) {
            df = new DataFormat(mimeType);
        }
        return df;
    }

    @Override
    public void write(Map<DataFormat, Object> out, Drawing drawing, Collection<Figure> selection) throws IOException {
        setInternalHome(drawing.get(Drawing.DOCUMENT_HOME));
        setExternalHome(new File(System.getProperty("user.home")).toURI());
        StringWriter sw = new StringWriter();
        write(sw, drawing, selection);
        out.put(getDataFormat(), sw.toString());
    }

    @Override
    public Set<Figure> read(Clipboard clipboard, DrawingModel model, Drawing drawing, Layer layer) throws IOException {
        setInternalHome(drawing.get(Drawing.DOCUMENT_HOME));
        setExternalHome(new File(System.getProperty("user.home")).toURI());
        Object content = clipboard.getContent(getDataFormat());
        if (content instanceof String) {
            Set<Figure> figures = new LinkedHashSet<>();
            Figure newDrawing = read((String) content, drawing);
            factory.reset();
            for (Figure f : drawing.preorderIterable()) {
                factory.createId(f);
            }
            if (layer == null) {
                layer = (Layer) drawing.getLastChild();
                if (layer == null) {
                    layer = new SimpleLayer();
                    layer.set(StyleableFigure.STYLE_ID, factory.createId(layer));
                    model.addChildTo(layer, drawing);
                }
            }
            for (Figure f : new ArrayList<>(newDrawing.getChildren())) {
                newDrawing.remove(f);
                String id = factory.createId(f);
                f.set(StyleableFigure.STYLE_ID, id);
                if (f instanceof Layer) {
                    model.addChildTo(f, drawing);
                } else {
                    model.addChildTo(f, layer);
                }
            }
            return figures;
        } else {
            throw new IOException("no data found");
        }
    }

    @Override
    public boolean isNamespaceAware() {
        return namespaceURI != null;
    }

    @Override
    public Figure read(InputStream in, Drawing drawing) throws IOException {
        return XmlInputFormatMixin.super.read(in, drawing);
    }
}