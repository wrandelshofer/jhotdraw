/*
 * @(#)SimpleXmlIO.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.io;

import javafx.css.StyleOrigin;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.beans.AbstractPropertyBean;
import org.jhotdraw8.collection.CompositeMapAccessor;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.concurrent.WorkState;
import org.jhotdraw8.draw.figure.Clipping;
import org.jhotdraw8.draw.figure.ClippingFigure;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.Layer;
import org.jhotdraw8.draw.figure.LayerFigure;
import org.jhotdraw8.draw.figure.StyleableFigure;
import org.jhotdraw8.draw.input.ClipboardInputFormat;
import org.jhotdraw8.draw.input.ClipboardOutputFormat;
import org.jhotdraw8.draw.key.NullableObjectKey;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.draw.render.SimpleRenderContext;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.io.UriResolver;
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
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 */
public class SimpleXmlIO extends AbstractPropertyBean implements InputFormat, OutputFormat, XmlOutputFormatMixin, ClipboardOutputFormat, ClipboardInputFormat {
    /**
     * Holds the current options.
     */
    @NonNull
    private Map<? super Key<?>, Object> options = Collections.emptyMap();

    @Override
    public void putAll(@Nullable Map<Key<?>, Object> options) {
        this.options = (options == null) ? Collections.emptyMap() : new LinkedHashMap<>(options);
    }

    /**
     * Comments which appear inside an XML element, that can not be associated
     * to as a head comment.
     */
    public final static NullableObjectKey<List<String>> XML_BODY_COMMENT_KEY = new NullableObjectKey<>("xmlHeadComment", List.class, new Class<?>[]{String.class}, Collections.emptyList());
    /**
     * Comments which can not be associated to a figure, or which appear in the
     * epilog of an XML file, are associated to the drawing.
     */
    public final static NullableObjectKey<List<String>> XML_EPILOG_COMMENT_KEY = new NullableObjectKey<>("xmlTailComment", List.class, new Class<?>[]{String.class}, Collections.emptyList());
    /**
     * Comments which appear before an XML element of a figure are associated to
     * the figure as a comment.
     */
    public final static NullableObjectKey<List<String>> XML_HEAD_COMMENT_KEY = new NullableObjectKey<>("xmlHeadComment", List.class, new Class<?>[]{String.class}, Collections.emptyList());
    private final static Pattern hrefPattern = Pattern.compile("(?:^|.* )href=\"([^\"]*)\".*");

    protected List<String> comments;
    protected FigureFactory figureFactory;
    /**
     * Performance: This does not need to be a linked hash map, because we iterate in parallel over it.
     */
    @NonNull
    protected Map<Figure, Element> figureToElementMap = new ConcurrentHashMap<>();
    protected IdFactory idFactory;
    protected String namespaceQualifier;
    protected String namespaceURI;
    @NonNull
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

    protected Element createElement(@NonNull Document doc, String unqualifiedName) throws IOException {
        if (namespaceURI == null) {
            return doc.createElement(unqualifiedName);
        }
        if (namespaceQualifier == null) {
            return doc.createElementNS(namespaceURI, unqualifiedName);
        } else {
            return doc.createElementNS(namespaceURI, namespaceQualifier + ":" + unqualifiedName);
        }
    }

    @Nullable
    public Figure fromDocument(@NonNull Document doc, @Nullable Drawing oldDrawing, URI documentHome) throws IOException {
        setUriResolver(new UriResolver(documentHome, null));
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

    @Nullable
    private String getAttribute(@NonNull Element elem, String unqualifiedName) {
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

    @NonNull
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

    @Nullable
    private Function<URI, URI> getUriResolver() {
        return uriResolver;
    }

    protected void setUriResolver(Function<URI, URI> uriResolver) {
        this.uriResolver = uriResolver;
    }

    protected boolean isClipping(Element elem) throws IOException {
        Figure probe = readNode(elem);
        return probe instanceof Clipping;

    }

    public boolean isNamespaceAware() {
        return namespaceURI != null;
    }

    @NonNull
    @Override
    public Figure read(@NonNull Path file, Drawing drawing, @NonNull WorkState workState) throws IOException {
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

    @Nullable
    public Figure read(InputStream in, Drawing drawing, URI documentHome, WorkState workState) throws IOException {
        Document doc = XmlUtil.readWithLocations(new InputSource(in), isNamespaceAware());
        return read(doc, drawing, documentHome);
    }

    @Nullable
    public Figure read(InputSource in, Drawing drawing, URI documentHome) throws IOException {
        Document doc = XmlUtil.readWithLocations(in, isNamespaceAware());
        return read(doc, drawing, documentHome);
    }

    @Nullable
    public Figure read(@NonNull Document in, Drawing drawing, URI documentHome) throws IOException {
        return fromDocument(in, drawing, documentHome);
    }

    @Nullable
    protected Figure read(@NonNull String string, Drawing drawing, URI documentHome) throws IOException {
        try (StringReader in = new StringReader(string)) {
            return read(in, drawing, documentHome);
        }
    }

    @Nullable
    protected Figure read(Reader in, Drawing drawing, URI documentHome) throws IOException {
        Document doc = XmlUtil.read(in, isNamespaceAware());
        return read(doc, drawing, documentHome);
    }

    @NonNull
    @Override
    public Set<Figure> read(@NonNull Clipboard clipboard, @NonNull DrawingModel model, @NonNull Drawing drawing, @Nullable Figure layer) throws IOException {
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
                    layer = new LayerFigure();
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
    @Nullable
    protected Figure readDrawingOrClipping(@NonNull Element drawingElement, Drawing oldDrawing) throws IOException {

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
    @Nullable
    protected Figure readDrawingOrClippingFromDocument(@NonNull Document doc, Drawing oldDrawing, URI documentHome) throws IOException {

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
            external.set(XML_EPILOG_COMMENT_KEY, comments);
        }
        try {
            figureToElementMap.entrySet().stream()
                    .parallel()
                    .forEach((Map.Entry<Figure, Element> entry) -> {
                        try {
                            readElementAttributes(entry.getKey(), entry.getValue());
                            readElementNodeList(entry.getKey(), entry.getValue());
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
        } finally {
            figureToElementMap.clear();
        }
        comments = null;
        if (external != null) {
            Drawing internal = figureFactory.fromExternalDrawing(external);
            if (doAddNotifyAndUpdateCss) {
                internal.preorderIterable().forEach(figure -> figure.addNotify(internal));
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
     * @throws java.io.IOException in case of failure
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
            MapAccessor<Object> key = (MapAccessor<Object>) figureFactory.nameToKey(figure, attr.getLocalName());
            if (key != null && figureFactory.figureAttributeKeys(figure).contains(key)) {
                Object value = null;

                if (keyValueTypeIsFigure.computeIfAbsent(key, k -> Figure.class.isAssignableFrom(k.getValueType()))) {
                    value = getFigure(attr.getValue());
                } else {
                    try {
                        value = figureFactory.stringToValue(key, attr.getValue());
                    } catch (IOException e) {
                        throw createIOException(elem, e);
                    }
                }

                if (value instanceof URI) {
                    value = uriResolver.apply((URI) value);
                }

                figure.set(key, value);
            }
        }
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

    /**
     * This is a cache which checks if Figure.class is assignable from the value
     * type of a map accessor.
     */
    @NonNull
    Map<MapAccessor<Object>, Boolean> keyValueTypeIsFigure = new ConcurrentHashMap<>();

    /**
     * Reads the children of the specified element as a node list.
     *
     * @param figure the figure to which the node list will be applied
     * @param elem   the element
     * @throws java.io.IOException in case of failure
     */
    protected void readElementNodeList(@NonNull Figure figure, @NonNull Element elem) throws IOException {
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
     * Creates a figure but does not process the properties.
     *
     * @param node the node, which defines the figure
     * @return the created figure
     * @throws java.io.IOException in case of failure
     */
    @Nullable
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
     * Creates a figure but does not process the properties.
     *
     * @param node a node
     * @return a figure
     * @throws java.io.IOException in case of failure
     */
    @Nullable
    protected Figure readNodesRecursively(@NonNull Node node) throws IOException {
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
                if (child != null) {
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

    protected void readProcessingInstruction(Document doc, @NonNull ProcessingInstruction pi, @NonNull Drawing external) {
        if (figureFactory.getStylesheetsKey() != null) {
            if ("xml-stylesheet".equals(pi.getNodeName()) && pi.getData() != null) {
                Matcher m = hrefPattern.matcher(pi.getData());
                if (m.matches()) {
                    String href = m.group(1);

                    URI uri = URI.create(href);
                    uri = uriResolver.apply(uri);

                    ImmutableList<URI> listOrNull = external.get(figureFactory.getStylesheetsKey());
                    List<URI> stylesheets = listOrNull == null ? new ArrayList<>() : new ArrayList<>(listOrNull.asList());
                    stylesheets.add(uri);
                    external.set(figureFactory.getStylesheetsKey(), ImmutableLists.ofCollection(stylesheets));
                }
            }
        }
    }

    protected void setAttribute(@NonNull Element elem, String unqualifiedName, String value) throws IOException {
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

    public Document toDocument(URI documentHome, @NonNull Drawing internal, @NonNull Collection<Figure> selection) throws IOException {
        setUriResolver(new UriResolver(null, documentHome));
        if (selection.isEmpty() || selection.contains(internal)) {
            return toDocument(documentHome, internal);
        }

        // bring selection in z-order
        Set<Figure> s = new HashSet<>(selection);
        ArrayList<Figure> ordered = new ArrayList<>(selection.size());
        for (Figure f : internal.preorderIterable()) {
            if (s.contains(f)) {
                ordered.add(f);
            }
        }

        Clipping external = new ClippingFigure();

        idFactory.reset();
        final String docElemName = figureFactory.figureToName(external);
        Document doc = XmlUtil.createDocument(namespaceURI, namespaceQualifier, docElemName);

        Element docElement = doc.getDocumentElement();
        for (Figure child : ordered) {
            writeNodeRecursively(doc, docElement, child);
        }
        return doc;
    }

    public Document toDocument(URI documentHome, Drawing internal) throws IOException {
        setUriResolver(new UriResolver(null, documentHome));
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
    public void write(@NonNull Map<DataFormat, Object> out, Drawing drawing, Collection<Figure> selection) throws IOException {
        StringWriter sw = new StringWriter();
        write(null, sw, drawing, selection);
        out.put(getDataFormat(), sw.toString());
    }

    protected void writeElementAttributes(@NonNull Element elem, @NonNull Figure figure) throws IOException {
        String id = idFactory.createId(figure);
        setAttribute(elem, figureFactory.getObjectIdAttribute(), id);
        final Set<MapAccessor<?>> keys = figureFactory.figureAttributeKeys(figure);
        Set<MapAccessor<?>> done = new HashSet<>(keys.size());

        // First write all non-transient composite attributes, then write the remaining non-transient non-composite attributes
        for (MapAccessor<?> k : keys) {
            if (k instanceof CompositeMapAccessor) {
                done.add(k);
                if (!k.isTransient()) {
                    @SuppressWarnings("unchecked") CompositeMapAccessor<Object> cmap = (CompositeMapAccessor<Object>) k;
                    done.addAll(cmap.getSubAccessors());
                    writeElementAttribute(elem, figure, cmap);
                }
            }
        }
        for (MapAccessor<?> k : keys) {
            if (!k.isTransient() && !done.contains(k)) {
                @SuppressWarnings("unchecked") MapAccessor<Object> cmap = (MapAccessor<Object>) k;
                writeElementAttribute(elem, figure, cmap);
            }
        }
    }

    private void writeElementAttribute(@NonNull Element elem, @NonNull Figure figure, MapAccessor<Object> k) throws IOException {
        @SuppressWarnings("unchecked")
        MapAccessor<Object> key = k;
        Object value = figure.get(key);

        if (value instanceof URI) {
            try {
                value = uriResolver.apply((URI) value);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
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

    private void writeElementNodeList(Document document, @NonNull Element elem, @NonNull Figure figure) throws IOException {
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

    protected void writeNodeRecursively(@NonNull Document doc, @NonNull Element parent, @NonNull Figure figure) throws IOException {
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
    private void writeProcessingInstructions(@NonNull Document doc, @NonNull Drawing external) {
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
