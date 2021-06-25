/*
 * @(#)SimpleXmlDomReader.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.io;

import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.beans.AbstractPropertyBean;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.concurrent.WorkState;
import org.jhotdraw8.draw.figure.Clipping;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.Layer;
import org.jhotdraw8.draw.figure.LayerFigure;
import org.jhotdraw8.draw.figure.StyleableFigure;
import org.jhotdraw8.draw.input.ClipboardInputFormat;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.draw.render.SimpleRenderContext;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.util.Exceptions;
import org.jhotdraw8.xml.XmlUtil;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SimpleXmlReader.
 * <p>
 * Represents each Figure by an element, and each figure property by an
 * attribute.
 * <p>
 * All attribute values are treated as value types, except if an attribute type
 * is an instance of Figure.
 * <p>
 * This reader only works for drawings which can be described entirely by
 * the properties of its figures.
 * <p>
 * Attempts to preserve comments in the XML file, by associating
 * them to the figures and to the drawing.
 * <p>
 * Does not preserve whitespace in the XML file.
 *
 * @author Werner Randelshofer
 */
public class SimpleXmlDomReader extends AbstractPropertyBean implements InputFormat, ClipboardInputFormat {
    private static final Pattern hrefPattern = Pattern.compile("(?:^|.* )href=\"([^\"]*)\".*");
    protected List<String> comments;
    protected FigureFactory figureFactory;
    /**
     * Performance: This does not need to be a linked hash map, because we iterate in parallel over it.
     */
    protected @NonNull Map<Figure, Element> figureToElementMap = new ConcurrentHashMap<>();
    protected IdFactory idFactory;
    protected String namespaceQualifier;
    protected String namespaceURI;
    /**
     * This is a cache which checks if Figure.class is assignable from the value
     * type of a map accessor.
     */
    @NonNull
    Map<MapAccessor<Object>, Boolean> keyValueTypeIsFigure = new ConcurrentHashMap<>();
    private boolean doAddNotifyAndUpdateCss = true;
    private Supplier<Layer> layerFactory = LayerFigure::new;

    public SimpleXmlDomReader(FigureFactory factory, IdFactory idFactory) {
        this(factory, idFactory, null, null);
    }

    public SimpleXmlDomReader(FigureFactory factory, IdFactory idFactory, String namespaceURI, String namespaceQualifier) {
        this.figureFactory = factory;
        this.idFactory = idFactory;
        this.namespaceURI = namespaceURI;
        this.namespaceQualifier = namespaceQualifier;
    }

    private IOException createIOException(@NonNull Element elem, IOException ex) {
        Locator locator = XmlUtil.getLocator(elem);
        if (locator == null) {
            return ex;
        } else {
            return new IOException(
                    "In " + ((locator.getSystemId() == null) ? "" : "file: \"" + locator.getSystemId() + "\", ")
                            + "line: " + locator.getLineNumber()
                            + ", column: " + locator.getColumnNumber() + ".",
                    ex);
        }
    }

    public @Nullable Figure fromDocument(@NonNull Document doc, @Nullable Drawing oldDrawing, URI documentHome) throws IOException {
        idFactory.reset();
        idFactory.setDocumentHome(documentHome);

        if (oldDrawing != null) {
            if (isClipping(doc.getDocumentElement())) {
                for (Figure f : oldDrawing.preorderIterable()) {
                    idFactory.createId(f);
                }
            } else {
                idFactory.reset();
            }
        }
        return readDrawingOrClippingFromDocument(doc, oldDrawing, documentHome);
    }

    private @Nullable String getAttribute(@NonNull Element elem, String unqualifiedName) {
        String value;
        if (namespaceURI == null) {
            return elem.getAttribute(unqualifiedName);
        } else if (!(value = elem.getAttributeNS(namespaceURI, unqualifiedName)).isEmpty()) {
            return value;
        } else if (elem.isDefaultNamespace(namespaceURI)) {
            return elem.getAttribute(unqualifiedName);
        }
        return null;
    }

    private DataFormat getDataFormat() {
        String mimeType = "application/xml";
        DataFormat df = DataFormat.lookupMimeType(mimeType);
        if (df == null) {
            df = new DataFormat(mimeType);
        }
        return df;
    }

    public @NonNull Figure getFigure(String id) throws IOException {
        Figure f = (Figure) idFactory.getObject(id);
        /*        if (f == null) {
        throw new IOException("no figure for id:" + id);
        }*/
        return f;
    }

    protected boolean isClipping(Element elem) throws IOException {
        Figure probe = readNode(elem);
        return probe instanceof Clipping;

    }

    public boolean isNamespaceAware() {
        return namespaceURI != null;
    }

    @Override
    public @NonNull Figure read(@NonNull Path file, Drawing drawing, @NonNull WorkState workState) throws IOException {
        try {
            URI documentHome = file.getParent() == null ? Paths.get(System.getProperty("user.home")).toUri() : file.getParent().toUri();
            final Drawing newDrawing;
            newDrawing = (Drawing) read(new InputSource(file.toUri().toASCIIString()), drawing, documentHome);
            return newDrawing;
        } catch (IOException e) {
            String message = "Error reading file \"" + file + "\".";
            workState.updateMessage(message + " " + Exceptions.getLocalizedMessage(e) + ".");
            throw new IOException("Error reading file \"" + file + "\".", e);
        }
    }

    public @Nullable Figure read(@NonNull InputStream in, Drawing drawing, URI documentHome, @NonNull WorkState workState) throws IOException {
        Document doc = XmlUtil.readWithLocations(new InputSource(in), isNamespaceAware());
        return read(doc, drawing, documentHome);
    }

    public @Nullable Figure read(InputSource in, Drawing drawing, URI documentHome) throws IOException {
        Document doc = XmlUtil.readWithLocations(in, isNamespaceAware());
        return read(doc, drawing, documentHome);
    }

    public @Nullable Figure read(@NonNull Document in, Drawing drawing, URI documentHome) throws IOException {
        return fromDocument(in, drawing, documentHome);
    }

    protected @Nullable Figure read(@NonNull String string, Drawing drawing, URI documentHome) throws IOException {
        try (StringReader in = new StringReader(string)) {
            return read(in, drawing, documentHome);
        }
    }

    protected @Nullable Figure read(Reader in, Drawing drawing, URI documentHome) throws IOException {
        Document doc = XmlUtil.read(in, isNamespaceAware());
        return read(doc, drawing, documentHome);
    }

    @Override
    public @NonNull Set<Figure> read(@NonNull Clipboard clipboard, @NonNull DrawingModel model, @NonNull Drawing drawing, @Nullable Figure layer) throws IOException {
        Object content = clipboard.getContent(getDataFormat());
        if (content instanceof String) {
            Set<Figure> figures = new LinkedHashSet<>();
            Figure newDrawing = read((String) content, drawing, drawing.get(Drawing.DOCUMENT_HOME));
            idFactory.reset();
            idFactory.setDocumentHome(null);
            for (Figure f : drawing.preorderIterable()) {
                idFactory.createId(f);
            }
            if (layer == null) {
                layer = (Layer) drawing.getLastChild();
                if (layer == null) {
                    layer = layerFactory.get();
                    layer.set(StyleableFigure.ID, idFactory.createId(layer));
                    model.addChildTo(layer, drawing);
                }
            }
            for (Figure f : new ArrayList<>(newDrawing.getChildren())) {
                figures.add(f);
                newDrawing.removeChild(f);
                String id = idFactory.createId(f);
                f.set(StyleableFigure.ID, id);
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

    /**
     * Reads drawing or clipping starting from the specified node. The idFactory
     * must have been initialized before this method is called.
     *
     * @param drawingElement the drawing element
     * @param oldDrawing     a drawing or null
     * @return the figure
     * @throws IOException in case of failure
     */
    protected @Nullable Figure readDrawingOrClipping(@NonNull Element drawingElement, Drawing oldDrawing) throws IOException {

        figureToElementMap.clear();
        Drawing external = null;
        Clipping clipping = null;
        NodeList list = drawingElement.getChildNodes();
        comments = new ArrayList<>();
        Figure f = readNodesRecursively(drawingElement);
        if (f instanceof Drawing) {
            external = (Drawing) f;
        } else if (f instanceof Clipping) {
            clipping = (Clipping) f;
        }
        if (external != null) {
            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                switch (node.getNodeType()) {
                case Node.PROCESSING_INSTRUCTION_NODE:
                    readProcessingInstruction(drawingElement.getOwnerDocument(), (ProcessingInstruction) node, external);
                    break;
                }
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
            //external.set(Drawing.DOCUMENT_HOME, getExternalHome());
            //external.set(XML_EPILOG_COMMENT_KEY, comments);
        }
        try {
            for (Map.Entry<Figure, Element> entry : figureToElementMap.entrySet()) {
                readElementAttributes(entry.getKey(), entry.getValue());
                readElementNodeList(entry.getKey(), entry.getValue());
            }
        } finally {
            figureToElementMap.clear();
        }
        comments = null;
        if (external != null) {
            Drawing internal = figureFactory.fromExternalDrawing(external);
            internal.preorderIterable().forEach(figure -> figure.addedToDrawing(internal));
            final RenderContext ctx = new SimpleRenderContext();
            internal.preorderIterable().forEach(figure -> figure.updateCss(ctx));
            return internal;
        } else {
            return clipping;
        }
    }

    /**
     * Reads drawing or clipping starting from the specified node. The idFactory
     * must have been initialized before this method is called.
     *
     * @param doc          the document
     * @param oldDrawing   the drawing
     * @param documentHome documentHome
     * @return a figure
     * @throws IOException in case of failure
     */
    protected @Nullable Figure readDrawingOrClippingFromDocument(@NonNull Document doc, Drawing oldDrawing, URI documentHome) throws IOException {

        figureToElementMap.clear();
        Drawing external = null;
        Clipping clipping = null;
        NodeList list = doc.getChildNodes();
        comments = new ArrayList<>();
        for (int i = 0, n = list.getLength(); i < n; i++) {
            Node node = list.item(i);
            switch (node.getNodeType()) {
            case Node.COMMENT_NODE:
                comments.add(node.getTextContent());
                break;
            case Node.ELEMENT_NODE:
                Figure f = readNodesRecursively(node);
                if (f instanceof Drawing) {
                    external = (Drawing) f;
                } else if (f instanceof Clipping) {
                    clipping = (Clipping) f;
                }
            }
        }
        if (external != null) {
            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                switch (node.getNodeType()) {
                case Node.PROCESSING_INSTRUCTION_NODE:
                    readProcessingInstruction(doc.getOwnerDocument(), (ProcessingInstruction) node, external);
                    break;
                }
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
            external.set(Drawing.DOCUMENT_HOME, documentHome);
            //external.set(XML_EPILOG_COMMENT_KEY, comments);
        }
        try {
            for (Map.Entry<Figure, Element> entry : figureToElementMap.entrySet()) {
                Figure key = entry.getKey();
                Element value = entry.getValue();
                try {
                    readElementAttributes(key, value);
                    readElementNodeList(key, value);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        } finally {
            figureToElementMap.clear();
        }
        comments = null;
        if (external != null) {
            Drawing internal = figureFactory.fromExternalDrawing(external);
            if (doAddNotifyAndUpdateCss) {
                internal.preorderIterable().forEach(figure -> figure.addedToDrawing(internal));
                final RenderContext ctx = new SimpleRenderContext();
                internal.preorderIterable().forEach(figure -> figure.updateCss(ctx));
            }
            return internal;
        } else {
            return clipping;
        }
    }

    /**
     * Reads the attributes of the specified element.
     *
     * @param figure the figure
     * @param elem   an element with attributes for the figure
     * @throws IOException in case of failure
     */
    protected void readElementAttributes(@NonNull Figure figure, @NonNull Element elem) throws IOException {
        NamedNodeMap attrs = elem.getAttributes();
        for (int i = 0, n = attrs.getLength(); i < n; i++) {
            Attr attr = (Attr) attrs.item(i);
            String namespaceURI = attr.getNamespaceURI();
            if (namespaceURI != null && !namespaceURI.equals(this.namespaceURI)) {
                continue;
            }

            @SuppressWarnings("unchecked")
            MapAccessor<Object> key = (MapAccessor<Object>) figureFactory.getKeyByAttributeName(figure, attr.getLocalName());
            if (key != null && figureFactory.figureAttributeKeys(figure).contains(key)) {
                Object value = null;

                if (keyValueTypeIsFigure.computeIfAbsent(key, k -> Figure.class.isAssignableFrom(k.getRawValueType()))) {
                    value = getFigure(attr.getValue());
                } else {
                    try {
                        value = figureFactory.stringToValue(key, attr.getValue());
                    } catch (IOException e) {
                        throw createIOException(elem, e);
                    }
                }

                figure.set(key, value);
            }
        }
    }

    /**
     * Reads the children of the specified element as a node list.
     *
     * @param figure the figure to which the node list will be applied
     * @param elem   the element
     * @throws IOException in case of failure
     */
    protected void readElementNodeList(@NonNull Figure figure, @NonNull Element elem) throws IOException {
        Set<MapAccessor<?>> keys = figureFactory.figureNodeListKeys(figure);
        for (MapAccessor<?> ky : keys) {
            @SuppressWarnings("unchecked")
            MapAccessor<Object> key = (MapAccessor<Object>) ky;
            String name = figureFactory.getElementNameByKey(figure, key);
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
                                if (figureFactory.createFigureByElementName(childElem.getLocalName()) != null) {
                                    continue;
                                }
                            } catch (IOException e) {
                                continue;
                            }
                            nodeList.add(child);
                        }
                    }
                }
                Object value = figureFactory.nodeListToValue(key, nodeList);
                figure.set(key, value);
            } else {
                throw new UnsupportedOperationException("Reading of sub-elements is not yet supported");
            }
        }

    }

    /**
     * Creates a figure but does not process the properties.
     *
     * @param node the node, which defines the figure
     * @return the created figure
     * @throws IOException in case of failure
     */
    protected @Nullable Figure readNode(Node node) throws IOException {
        if (node instanceof Element) {
            Element elem = (Element) node;
            if (namespaceURI != null) {
                if (!namespaceURI.equals(elem.getNamespaceURI())) {
                    return null;
                }
            }
            Figure figure = figureFactory.createFigureByElementName(elem.getLocalName());
            if (figure == null) {
                return null;
            }
            figureToElementMap.put(figure, elem);
            String id = getAttribute(elem, figureFactory.getObjectIdAttribute());

            if (id != null && !id.isEmpty()) {
                idFactory.putIdAndObject(id, figure);
                /*
                if (idFactory.getObject(id) != null) {
                    throw new IOException("SimpleXmlIO: duplicate id " + id + " in element " + elem.getTagName());
                } else {
                }
                 */
            }
            return figure;
        }
        return null;
    }

    /**
     * Creates a figure but does not process the properties.
     *
     * @param node a node
     * @return a figure
     * @throws IOException in case of failure
     */
    protected @Nullable Figure readNodesRecursively(@NonNull Node node) throws IOException {
        switch (node.getNodeType()) {
        case Node.ELEMENT_NODE:
            Figure figure = readNode(node);
            if (!comments.isEmpty()) {
                //figure.set(XML_HEAD_COMMENT_KEY, comments);
                comments = new ArrayList<>();
            }
            NodeList list = node.getChildNodes();
            for (int i = 0; i < list.getLength(); i++) {
                Figure child = readNodesRecursively(list.item(i));
                if (child != null) {
                    if (!child.isSuitableParent(figure)) {
                        throw new IOException(list.item(i).getNodeName() + " is not a suitable child for " + ((Element) node).getTagName() + ".");
                    }
                    figure.addChild(child);
                }
            }
            if (!comments.isEmpty()) {
                //figure.set(XML_BODY_COMMENT_KEY, comments);
                comments = new ArrayList<>();
            }
            return figure;
        case Node.COMMENT_NODE:
            comments.add(node.getTextContent());
            return null;
        default:
            return null;
        }
    }

    protected void readProcessingInstruction(Document doc, @NonNull ProcessingInstruction pi, @NonNull Drawing external) {
        if (figureFactory.getStylesheetsKey() != null) {
            if ("xml-stylesheet".equals(pi.getNodeName()) && pi.getData() != null) {
                Matcher m = hrefPattern.matcher(pi.getData());
                if (m.matches()) {
                    String href = m.group(1);

                    URI uri = URI.create(href);
                    uri = idFactory.absolutize(uri);

                    ImmutableList<URI> listOrNull = external.get(figureFactory.getStylesheetsKey());
                    List<URI> stylesheets = listOrNull == null ? new ArrayList<>() : new ArrayList<>(listOrNull.asList());
                    stylesheets.add(uri);
                    external.set(figureFactory.getStylesheetsKey(), ImmutableLists.ofCollection(stylesheets));
                }
            }
        }
    }

    public void setDoAddNotifyAndUpdateCss(boolean doAddNotifyAndUpdateCss) {
        this.doAddNotifyAndUpdateCss = doAddNotifyAndUpdateCss;
    }

    public void setFigureFactory(FigureFactory figureFactory) {
        this.figureFactory = figureFactory;
    }

    public IdFactory getIdFactory() {
        return idFactory;
    }

    public void setNamespaceURI(String namespaceURI) {
        this.namespaceURI = namespaceURI;
    }

    public Supplier<Layer> getLayerFactory() {
        return layerFactory;
    }

    public void setLayerFactory(Supplier<Layer> layerFactory) {
        this.layerFactory = layerFactory;
    }
}
