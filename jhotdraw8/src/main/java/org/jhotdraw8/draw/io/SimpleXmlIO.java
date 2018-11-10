/* @(#)SimpleXmlIO.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.css.StyleOrigin;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javax.annotation.Nullable;
import javax.annotation.Nonnull;

import org.jhotdraw8.collection.CompositeMapAccessor;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.draw.figure.Clipping;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.Layer;
import org.jhotdraw8.draw.figure.SimpleClipping;
import org.jhotdraw8.draw.figure.SimpleLayer;
import org.jhotdraw8.draw.figure.StyleableFigure;
import org.jhotdraw8.draw.input.ClipboardInputFormat;
import org.jhotdraw8.draw.input.ClipboardOutputFormat;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.SimpleFigureKey;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.io.UriResolver;
import org.jhotdraw8.xml.XmlUtil;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
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
 * SimpleXmlIO attempts to preserve comments in the XML file, by associating
 * them to the figures and to the drawing.
 * <p>
 * SimpleXmlIO does not preserve whitespace in the XML file.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleXmlIO implements InputFormat, OutputFormat, XmlOutputFormatMixin, ClipboardOutputFormat, ClipboardInputFormat {
    /**
     * Holds the current options.
     */
    @Nonnull
    private Map<? super Key<?>, Object> options = Collections.emptyMap();

    @Override
    public void setOptions(@Nullable Map<? super Key<?>, Object> options) {
        this.options = (options == null) ? Collections.emptyMap() : new LinkedHashMap<>(options);
    }

    /**
     * Comments which appear inside an XML element, that can not be associated
     * to as a head comment.
     */
    public final static SimpleFigureKey<List<String>> XML_BODY_COMMENT_KEY = new SimpleFigureKey<>("xmlHeadComment", List.class, new Class<?>[]{String.class}, DirtyMask.EMPTY, Collections.emptyList());
    /**
     * Comments which can not be associated to a figure, or which appear in the
     * epilog of an XML file, are associated to the drawing.
     */
    public final static SimpleFigureKey<List<String>> XML_EPILOG_COMMENT_KEY = new SimpleFigureKey<>("xmlTailComment", List.class, new Class<?>[]{String.class}, DirtyMask.EMPTY, Collections.emptyList());
    /**
     * Comments which appear before an XML element of a figure are associated to
     * the figure as a comment.
     */
    public final static SimpleFigureKey<List<String>> XML_HEAD_COMMENT_KEY = new SimpleFigureKey<>("xmlHeadComment", List.class, new Class<?>[]{String.class}, DirtyMask.EMPTY, Collections.emptyList());
    private final static Pattern hrefPattern = Pattern.compile("(?:^|.* )href=\"([^\"]*)\".*");
    @javax.annotation.Nullable
    protected List<String> comments;
    protected FigureFactory figureFactory;
    @Nonnull
    protected HashMap<Figure, Element> figureToElementMap = new HashMap<>();
    protected IdFactory idFactory;
    protected String namespaceQualifier;
    protected String namespaceURI;
    @javax.annotation.Nullable
    private Function<URI, URI> uriResolver = new UriResolver(null, null);
    private boolean doAddNotifyAndUpdateCss = true;

    public SimpleXmlIO(FigureFactory factory, IdFactory idFactory) {
        this(factory, idFactory, null, null);
    }

    public SimpleXmlIO(FigureFactory factory, IdFactory idFactory, String namespaceURI, String namespaceQualifier) {
        this.figureFactory = factory;
        this.idFactory = idFactory;
        this.namespaceURI = namespaceURI;
        this.namespaceQualifier = namespaceQualifier;
    }

    public void setDoAddNotifyAndUpdateCss(boolean doAddNotifyAndUpdateCss) {
        this.doAddNotifyAndUpdateCss = doAddNotifyAndUpdateCss;
    }

    protected Element createElement(@Nonnull Document doc, String unqualifiedName) throws IOException {
        if (namespaceURI == null || namespaceQualifier == null) {
            return doc.createElement(unqualifiedName);
        }
        if (namespaceQualifier == null) {
            return doc.createElementNS(namespaceURI, unqualifiedName);
        } else {
            return doc.createElementNS(namespaceURI, namespaceQualifier + ":" + unqualifiedName);
        }
    }

    @javax.annotation.Nullable
    public Figure fromDocument(@Nonnull Document doc, @javax.annotation.Nullable Drawing oldDrawing, URI documentHome) throws IOException {
        setUriResolver(new UriResolver(documentHome, documentHome));
        idFactory.reset();
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

    private String getAttribute(@Nonnull Element elem, String unqualifiedName) {
        if (namespaceURI == null) {
            return elem.getAttribute(unqualifiedName);
        } else if (elem.hasAttributeNS(namespaceURI, unqualifiedName)) {
            return elem.getAttributeNS(namespaceURI, unqualifiedName);
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

    @Nonnull
    public Figure getFigure(String id) throws IOException {
        Figure f = (Figure) idFactory.getObject(id);
        /*        if (f == null) {
        throw new IOException("no figure for id:" + id);
        }*/
        return f;
    }

    public void setFigureFactory(FigureFactory figureFactory) {
        this.figureFactory = figureFactory;
    }

    public void setNamespaceURI(String namespaceURI) {
        this.namespaceURI = namespaceURI;
    }

    @javax.annotation.Nullable
    private Function<URI, URI> getUriResolver() {
        return uriResolver;
    }

    private void setUriResolver(Function<URI, URI> uriResolver) {
        this.uriResolver = uriResolver;
    }

    protected boolean isClipping(Element elem) throws IOException {
        Figure probe = readNode(elem);
        return probe instanceof Clipping;

    }

    public boolean isNamespaceAware() {
        return namespaceURI != null;
    }

    @Nonnull
    @Override
    public Figure read(@Nonnull File file, Drawing drawing) throws IOException {
        try {
            URI documentHome = file.getParentFile() == null ? new File(System.getProperty("user.home")).toURI() : file.getParentFile().toURI();
            final Drawing newDrawing;
            try ( BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))) {
                newDrawing = (Drawing) read(in, drawing, documentHome);
            }
            return newDrawing;
        } catch (IOException e) {
            throw new IOException("Error reading " + file + ".", e);
        }
    }

    @javax.annotation.Nullable
    public Figure read(InputStream in, Drawing drawing, URI documentHome) throws IOException {
        Document doc = XmlUtil.read(in, isNamespaceAware());
        return read(doc, drawing, documentHome);
    }

    @javax.annotation.Nullable
    public Figure read(@Nonnull Document in, Drawing drawing, URI documentHome) throws IOException {
        return fromDocument(in, drawing, documentHome);
    }

    @javax.annotation.Nullable
    protected Figure read(@Nonnull String string, Drawing drawing, URI documentHome) throws IOException {
        try ( StringReader in = new StringReader(string)) {
            return read(in, drawing, documentHome);
        }
    }

    @javax.annotation.Nullable
    protected Figure read(Reader in, Drawing drawing, URI documentHome) throws IOException {
        Document doc = XmlUtil.read(in, isNamespaceAware());
        return read(doc, drawing, documentHome);
    }

    @Nonnull
    @Override
    public Set<Figure> read(@Nonnull Clipboard clipboard, @Nonnull DrawingModel model, @Nonnull Drawing drawing, @javax.annotation.Nullable Layer layer) throws IOException {
        setUriResolver(new UriResolver(null, drawing.get(Drawing.DOCUMENT_HOME)));
        Object content = clipboard.getContent(getDataFormat());
        if (content instanceof String) {
            Set<Figure> figures = new LinkedHashSet<>();
            Figure newDrawing = read((String) content, drawing, drawing.get(Drawing.DOCUMENT_HOME));
            idFactory.reset();
            for (Figure f : drawing.preorderIterable()) {
                idFactory.createId(f);
            }
            if (layer == null) {
                layer = (Layer) drawing.getLastChild();
                if (layer == null) {
                    layer = new SimpleLayer();
                    layer.set(StyleableFigure.ID, idFactory.createId(layer));
                    model.addChildTo(layer, drawing);
                }
            }
            for (Figure f : new ArrayList<>(newDrawing.getChildren())) {
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
     * @param oldDrawing a drawing or null
     * @return the figure
     * @throws IOException in case of failure
     */
    @javax.annotation.Nullable
    protected Figure readDrawingOrClipping(@Nonnull Element drawingElement, Drawing oldDrawing) throws IOException {

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
            external.set(XML_EPILOG_COMMENT_KEY, comments);
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
            internal.preorderIterable().forEach(figure -> figure.addNotify(internal));
            internal.preorderIterable().forEach(Figure::updateCss);
            return internal;
        } else {
            return clipping;
        }
    }

    /**
     * Reads drawing or clipping starting from the specified node. The idFactory
     * must have been initialized before this method is called.
     *
     * @param doc the document
     * @param oldDrawing the drawing
     * @param documentHome documentHome
     * @return a figure
     * @throws IOException in case of failure
     */
    @javax.annotation.Nullable
    protected Figure readDrawingOrClippingFromDocument(@Nonnull Document doc, Drawing oldDrawing, URI documentHome) throws IOException {

        figureToElementMap.clear();
        Drawing external = null;
        Clipping clipping = null;
        NodeList list = doc.getChildNodes();
        comments = new ArrayList<>();
        for (int i = 0, n = list.getLength(); i < n; i++) {
            Node node = list.item(i);
            switch (node.getNodeType()) {
            case Node.COMMENT_NODE:
                comments.add(((Comment) node).getTextContent());
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
            external.set(XML_EPILOG_COMMENT_KEY, comments);
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
            if (doAddNotifyAndUpdateCss) {
                internal.preorderIterable().forEach(figure -> figure.addNotify(internal));
                internal.preorderIterable().forEach(Figure::updateCss);
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
     * @param elem an element with attributes for the figure
     * @throws java.io.IOException in case of failure
     */
    protected void readElementAttributes(@Nonnull Figure figure, @Nonnull Element elem) throws IOException {
        NamedNodeMap attrs = elem.getAttributes();
        for (int i = 0, n = attrs.getLength(); i < n; i++) {
            Attr attr = (Attr) attrs.item(i);
            if (attr.getNamespaceURI() != null && !attr.getNamespaceURI().equals(namespaceURI)) {
                continue;
            }

            /*if (idFactory.getObjectIdAttribute().equals(attr.getLocalName())) {
                continue;
            }*/
            @SuppressWarnings("unchecked")
            MapAccessor<Object> key = (MapAccessor<Object>) figureFactory.nameToKey(figure, attr.getLocalName());
            if (key != null && figureFactory.figureAttributeKeys(figure).contains(key)) {
                Object value = null;

                if (keyValueTypeIsFigure.computeIfAbsent(key, k -> Figure.class.isAssignableFrom(k.getValueType()))) {
                    value = getFigure(attr.getValue());
                } else {
                    value = figureFactory.stringToValue(key, attr.getValue());
                }

                if (value instanceof URI) {
                    value = uriResolver.apply((URI) value);
                }

                figure.set(key, value);
            }
        }
    }

    /**
     * This is a cache which checks if Figure.class is assignable from the value
     * type of a map accessor.
     */
    @Nonnull
    Map<MapAccessor<Object>, Boolean> keyValueTypeIsFigure = new HashMap<>();

    /**
     * Reads the children of the specified element as a node list.
     *
     * @param figure the figure to which the node list will be applied
     * @param elem the element
     * @throws java.io.IOException in case of failure
     */
    protected void readElementNodeList(@Nonnull Figure figure, @Nonnull Element elem) throws IOException {
        Set<MapAccessor<?>> keys = figureFactory.figureNodeListKeys(figure);
        for (MapAccessor<?> ky : keys) {
            @SuppressWarnings("unchecked")
            MapAccessor<Object> key = (MapAccessor<Object>) ky;
            String name = figureFactory.keyToElementName(figure, key);
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
                                if (figureFactory.nameToFigure(childElem.getLocalName()) != null) {
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
     * Creates a figure but does not process the getProperties.
     *
     * @param node the node, which defines the figure
     * @return the created figure
     * @throws java.io.IOException in case of failure
     */
    @javax.annotation.Nullable
    protected Figure readNode(Node node) throws IOException {
        if (node instanceof Element) {
            Element elem = (Element) node;
            if (namespaceURI != null) {
                if (!namespaceURI.equals(elem.getNamespaceURI())) {
                    return null;
                }
            }
            Figure figure = figureFactory.nameToFigure(elem.getLocalName());
            if (figure == null) {
                return null;
            }
            figureToElementMap.put(figure, elem);
            String id = getAttribute(elem, figureFactory.getObjectIdAttribute());

            if (id != null && !id.isEmpty()) {
                if (idFactory.getObject(id) != null) {
                    System.err.println("SimpleXmlIO warning: duplicate id " + id + " in element " + elem.getTagName());
                    idFactory.putId(id, figure);
                } else {
                    idFactory.putId(id, figure);
                }
            }
            return figure;
        }
        return null;
    }

    /**
     * Creates a figure but does not process the getProperties.
     *
     * @param node a node
     * @return a figure
     * @throws java.io.IOException in case of failure
     */
    @javax.annotation.Nullable
    protected Figure readNodesRecursively(@Nonnull Node node) throws IOException {
        switch (node.getNodeType()) {
        case Node.ELEMENT_NODE:
            Figure figure = readNode(node);
            if (!comments.isEmpty()) {
                figure.set(XML_HEAD_COMMENT_KEY, comments);
                comments = new ArrayList<>();
            }
            NodeList list = node.getChildNodes();
            for (int i = 0; i < list.getLength(); i++) {
                Figure child = readNodesRecursively(list.item(i));
                if (child instanceof Figure) {
                    if (!child.isSuitableParent(figure)) {
                        throw new IOException(list.item(i).getNodeName() + " is not a suitable child for " + ((Element) node).getTagName() + ".");
                    }
                    figure.addChild(child);
                }
            }
            if (!comments.isEmpty()) {
                figure.set(XML_BODY_COMMENT_KEY, comments);
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

    protected void readProcessingInstruction(Document doc, @Nonnull ProcessingInstruction pi, @Nonnull Drawing external) {
        if (figureFactory.getStylesheetsKey() != null) {
            if ("xml-stylesheet".equals(pi.getNodeName()) && pi.getData() != null) {
                Matcher m = hrefPattern.matcher(pi.getData());
                if (m.matches()) {
                    String href = m.group(1);

                    URI uri = URI.create(href);
                    uri = uriResolver.apply(uri);

                    List<URI> listOrNull = external.get(figureFactory.getStylesheetsKey());
                    List<URI> stylesheets = listOrNull == null ? new ArrayList<>() : new ArrayList<>(listOrNull);
                    stylesheets.add(uri);
                    external.set(figureFactory.getStylesheetsKey(), stylesheets);
                }
            }
        }
    }

    protected void setAttribute(@Nonnull Element elem, String unqualifiedName, String value) throws IOException {
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

    public Document toDocument(@Nonnull Drawing internal, @Nonnull Collection<Figure> selection) throws IOException {
        if (selection.isEmpty() || selection.contains(internal)) {
            return toDocument(internal);
        }

        // bring selection in z-order
        Set<Figure> s = new HashSet<>(selection);
        ArrayList<Figure> ordered = new ArrayList<>(selection.size());
        for (Figure f : internal.preorderIterable()) {
            if (s.contains(f)) {
                ordered.add(f);
            }
        }

        Clipping external = new SimpleClipping();

        idFactory.reset();
        final String docElemName = figureFactory.figureToName(external);
        Document doc = XmlUtil.createDocument(namespaceURI, namespaceQualifier, docElemName);

        Element docElement = doc.getDocumentElement();
        for (Figure child : ordered) {
            writeNodeRecursively(doc, docElement, child);
        }
        return doc;
    }

    public Document toDocument(Drawing internal) throws IOException {
        Drawing external = figureFactory.toExternalDrawing(internal);

        idFactory.reset();
        final String docElemName = figureFactory.figureToName(external);
        Document doc = XmlUtil.createDocument(namespaceURI, namespaceQualifier, docElemName);

        Element docElement = doc.getDocumentElement();

        writeProcessingInstructions(doc, external);
        for (String string : external.get(XML_HEAD_COMMENT_KEY)) {
            doc.insertBefore(doc.createComment(string), docElement);
        }
        writeElementAttributes(docElement, external);
        String linebreak = "\n";
        for (Figure child : external.getChildren()) {
            writeNodeRecursively(doc, docElement, child);
        }
        for (String string : external.get(XML_BODY_COMMENT_KEY)) {
            docElement.appendChild(doc.createComment(string));
        }
        for (String string : external.get(XML_EPILOG_COMMENT_KEY)) {
            doc.appendChild(doc.createComment(string));
        }
        return doc;
    }

    @Override
    public void write(@Nonnull Map<DataFormat, Object> out, Drawing drawing, Collection<Figure> selection) throws IOException {
        StringWriter sw = new StringWriter();
        write(sw, drawing, selection);
        out.put(getDataFormat(), sw.toString());
    }

    protected void writeElementAttributes(@Nonnull Element elem, @Nonnull Figure figure) throws IOException {
        String id = idFactory.createId(figure);
        setAttribute(elem, figureFactory.getObjectIdAttribute(), id);


        Set<MapAccessor<?>> todo = new LinkedHashSet<>(figureFactory.figureAttributeKeys(figure));

        // First write all non-transient composite attributes, then write the remaining non-transient non-composite attributes
        for (MapAccessor<?> k : new ArrayList<>(todo)) {
            if (k instanceof CompositeMapAccessor) {
                todo.remove(k);
                if (! k.isTransient()) {
                    @SuppressWarnings("unchecked") CompositeMapAccessor<Object> cmap = (CompositeMapAccessor<Object>) k;
                    todo.removeAll(cmap.getSubAccessors());
                    writeElementAttribute(elem, figure, cmap);
                }
            }
        }
        for (MapAccessor<?> k : todo) {
            if (!k.isTransient()) {
                @SuppressWarnings("unchecked") MapAccessor<Object> cmap=(MapAccessor<Object>)k;
                writeElementAttribute(elem, figure, cmap);
            }
        }
    }

    private void writeElementAttribute(@Nonnull Element elem, @Nonnull Figure figure, MapAccessor<Object> k) throws IOException {
        @SuppressWarnings("unchecked")
        MapAccessor<Object> key = k;
        Object value = figure.get(key);

        if (value instanceof URI) {
            value = uriResolver.apply((URI) value);
        }

        if (!key.isTransient() /*&& figure.containsKey(StyleOrigin.USER, key)*/ && !figureFactory.isDefaultValue(figure, key, value)) {
            String name = figureFactory.keyToName(figure, key);
            if (Figure.class.isAssignableFrom(key.getValueType())) {
                setAttribute(elem, name, idFactory.createId(value));
            } else {
                setAttribute(elem, name, figureFactory.valueToString(key, value));
            }
        }
    }

    private void writeElementNodeList(Document document, @Nonnull Element elem, @Nonnull Figure figure) throws IOException {
        for (MapAccessor<?> k : figureFactory.figureNodeListKeys(figure)) {
            @SuppressWarnings("unchecked")
            MapAccessor<Object> key = (MapAccessor<Object>) k;
            Object value = figure.get(key);
            if (!key.isTransient() && figure.containsKey(StyleOrigin.USER, key) && !figureFactory.isDefaultValue(figure, key, value)) {
                for (Node node : figureFactory.valueToNodeList(key, value, document)) {
                    elem.appendChild(node);
                }
            }
        }
    }

    protected void writeNodeRecursively(@Nonnull Document doc, @Nonnull Element parent, @Nonnull Figure figure) throws IOException {
        try {
            String elementName = figureFactory.figureToName(figure);
            if (elementName == null) {
                // => the figureFactory decided that we should skip the figure
                return;
            }
            Element elem = createElement(doc, elementName);
            writeElementAttributes(elem, figure);
            writeElementNodeList(doc, elem, figure);

            for (String string : figure.get(XML_HEAD_COMMENT_KEY)) {

                parent.appendChild(doc.createComment(string));
            }
            for (Figure child : figure.getChildren()) {
                if (figureFactory.figureToName(child) != null) {
                    writeNodeRecursively(doc, elem, child);
                }
            }
            boolean hasNoElementNodes = figureFactory.figureNodeListKeys(figure).isEmpty();
            for (String string : figure.get(XML_BODY_COMMENT_KEY)) {
                elem.appendChild(doc.createComment(string));
            }
            parent.appendChild(elem);
        } catch (IOException e) {
            throw new IOException("Error writing figure " + figure, e);
        }
    }

    // XXX maybe this should not be in SimpleXmlIO?
    private void writeProcessingInstructions(Document doc, @Nonnull Drawing external) {
        Element docElement = doc.getDocumentElement();
        if (figureFactory.getStylesheetsKey() != null && external.get(figureFactory.getStylesheetsKey()) != null) {
            for (Object stylesheet : external.get(figureFactory.getStylesheetsKey())) {
                if (stylesheet instanceof URI) {
                    stylesheet = uriResolver.apply((URI) stylesheet);

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

}
