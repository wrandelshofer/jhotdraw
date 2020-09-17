/*
 * @(#)SimpleXmlIO.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.io;

import javafx.css.StyleOrigin;
import javafx.scene.input.DataFormat;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.beans.AbstractPropertyBean;
import org.jhotdraw8.collection.CompositeMapAccessor;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.concurrent.WorkState;
import org.jhotdraw8.draw.figure.Clipping;
import org.jhotdraw8.draw.figure.ClippingFigure;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.input.ClipboardOutputFormat;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.io.UriResolver;
import org.jhotdraw8.xml.IndentingXMLStreamWriter;
import org.jhotdraw8.xml.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.dom.DOMResult;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * SimpleXmlWriter.
 * <p>
 * Represents each Figure by an element, and each figure property by an
 * attribute.
 * <p>
 * All attribute values are treated as value types, except if an attribute type
 * is an instance of Figure.
 * <p>
 * This writer only works for drawings which can be described entirely by
 * the properties of its figures.
 * <p>
 * Attempts to preserve comments in the XML file, by associating
 * them to the figures and to the drawing.
 * <p>
 * Does not preserve whitespace in the XML file.
 *
 * @author Werner Randelshofer
 */
public class SimpleXmlWriter extends AbstractPropertyBean implements OutputFormat, ClipboardOutputFormat {
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
    /**
     * This is a cache which checks if Figure.class is assignable from the value
     * type of a map accessor.
     */
    @NonNull
    Map<MapAccessor<Object>, Boolean> keyValueTypeIsFigure = new ConcurrentHashMap<>();
    /**
     * Holds the current options.
     */
    @NonNull
    private Map<? super Key<?>, Object> options = Collections.emptyMap();
    @NonNull
    private Function<URI, URI> uriResolver = new UriResolver(null, null);

    public SimpleXmlWriter(FigureFactory factory, IdFactory idFactory) {
        this(factory, idFactory, null, null);
    }

    public SimpleXmlWriter(FigureFactory factory, IdFactory idFactory, String namespaceURI, String namespaceQualifier) {
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


    private DataFormat getDataFormat() {
        String mimeType = "application/xml";
        DataFormat df = DataFormat.lookupMimeType(mimeType);
        if (df == null) {
            df = new DataFormat(mimeType);
        }
        return df;
    }

    @NonNull
    private Function<URI, URI> getUriResolver() {
        return uriResolver;
    }

    protected void setUriResolver(@NonNull Function<URI, URI> uriResolver) {
        this.uriResolver = uriResolver;
    }

    public boolean isNamespaceAware() {
        return namespaceURI != null;
    }

    @Override
    public void putAll(@Nullable Map<Key<?>, Object> options) {
        this.options = (options == null) ? Collections.emptyMap() : new LinkedHashMap<>(options);
    }

    public void setFigureFactory(FigureFactory figureFactory) {
        this.figureFactory = figureFactory;
    }

    public void setNamespaceURI(String namespaceURI) {
        this.namespaceURI = namespaceURI;
    }

    public Document toDocument(@Nullable URI documentHome, @NonNull Drawing internal, @NonNull Collection<Figure> selection) throws IOException {
        if (selection.isEmpty() || selection.contains(internal)) {
            return toDocument(documentHome, internal);
        }

        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            builderFactory.setNamespaceAware(true);
            DocumentBuilder builder = null;
            builder = builderFactory.newDocumentBuilder();
            // We do not want that the builder creates a socket connection!
            builder.setEntityResolver((publicId, systemId) -> new InputSource(new StringReader("")));
            Document doc = builder.newDocument();
            DOMResult result = new DOMResult(doc);
            XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
            XMLStreamWriter w = xmlOutputFactory.createXMLStreamWriter(result);

            setUriResolver(new UriResolver(null, documentHome));
            writeClipping(w, internal, selection);

            w.close();
            return doc;
        } catch (ParserConfigurationException | XMLStreamException e) {
            throw new IOException("Could not create document builder.", e);
        }

    }

    public Document toDocument(@Nullable URI documentHome, @NonNull Drawing internal) throws IOException {
        try {
            setUriResolver(new UriResolver(null, documentHome));
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            builderFactory.setNamespaceAware(true);
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            // We do not want that the builder creates a socket connection!
            builder.setEntityResolver((publicId, systemId) -> new InputSource(new StringReader("")));
            Document doc = builder.newDocument();
            DOMResult result = new DOMResult(doc);
            XMLStreamWriter w = XMLOutputFactory.newInstance().createXMLStreamWriter(result);
            writeDocument(w, documentHome, internal);
            w.close();
            return doc;
        } catch (XMLStreamException | ParserConfigurationException e) {
            throw new IOException("Error writing to DOM.", e);
        }
    }

    @Override
    public void write(URI documentHome, OutputStream out, Drawing drawing, WorkState workState) throws IOException {
        write(documentHome, new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8)),
                drawing, workState);
    }

    protected void write(URI documentHome, Writer out, Drawing drawing, WorkState workState) throws IOException {
        IndentingXMLStreamWriter w = new IndentingXMLStreamWriter(out);
        try {
            writeDocument(w, documentHome, drawing);
            w.flush();
        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void write(@NonNull Map<DataFormat, Object> out, Drawing drawing, Collection<Figure> selection) throws IOException {
        StringWriter sw = new StringWriter();
        IndentingXMLStreamWriter w = new IndentingXMLStreamWriter(sw);
        try {
            if (selection == null || selection.isEmpty()) {
                writeDocument(w, null, drawing);
            } else {
                writeClipping(w, drawing, selection);
            }
        } catch (XMLStreamException e) {
            throw new IOException(e);
        }

        out.put(getDataFormat(), sw.toString());
    }

    protected void writeClipping(@NonNull XMLStreamWriter w, @NonNull Drawing internal, @NonNull Collection<Figure> selection) throws IOException, XMLStreamException {
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
        w.writeStartDocument();
        w.setDefaultNamespace(namespaceURI);
        w.writeStartElement(docElemName);
        for (Figure child : ordered) {
            writeNodeRecursively(w, child, 1);
        }
        w.writeEndElement();
        w.writeEndDocument();
    }

    protected void writeDocument(@NonNull XMLStreamWriter w, @Nullable URI documentHome, @NonNull Drawing internal) throws XMLStreamException {
        try {
            setUriResolver(new UriResolver(null, documentHome));
            Drawing external = figureFactory.toExternalDrawing(internal);
            idFactory.reset();
            final String docElemName = figureFactory.figureToName(external);
            w.writeStartDocument();
            w.setDefaultNamespace(namespaceURI);
            writeProcessingInstructions(w, external);
            w.writeStartElement(docElemName);
            w.writeDefaultNamespace(namespaceURI);
            writeElementAttributes(w, external);
            for (Figure child : external.getChildren()) {
                writeNodeRecursively(w, child, 1);
            }
            w.writeEndElement();
            w.writeEndDocument();
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    private void writeElementAttribute(@NonNull XMLStreamWriter w, @NonNull Figure figure, MapAccessor<Object> k) throws IOException, XMLStreamException {
        Object value = figure.get(k);
        if (value instanceof URI) {
            try {
                value = uriResolver.apply((URI) value);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        if (!k.isTransient() && !figureFactory.isDefaultValue(figure, k, value)) {
            String name = figureFactory.keyToName(figure, k);
            if (figureFactory.getObjectIdAttribute().equals(name)) {
                return;
            }
            if (Figure.class.isAssignableFrom(k.getRawValueType())) {
                w.writeAttribute(name, idFactory.createId(value));
            } else {
                w.writeAttribute(name, figureFactory.valueToString(k, value));
            }
        }
    }

    protected void writeElementAttributes(@NonNull XMLStreamWriter w, @NonNull Figure figure) throws IOException, XMLStreamException {
        String id = idFactory.createId(figure);
        String objectIdAttribute = figureFactory.getObjectIdAttribute();
        w.writeAttribute(objectIdAttribute, id);
        final Set<MapAccessor<?>> keys = figureFactory.figureAttributeKeys(figure);
        Set<MapAccessor<?>> done = new HashSet<>(keys.size());

        // First write all non-transient composite attributes, then write the remaining non-transient non-composite attributes
        for (MapAccessor<?> k : keys) {
            if (k instanceof CompositeMapAccessor) {
                done.add(k);
                if (!k.isTransient()) {
                    @SuppressWarnings("unchecked") CompositeMapAccessor<Object> cmap = (CompositeMapAccessor<Object>) k;
                    done.addAll(cmap.getSubAccessors());
                    writeElementAttribute(w, figure, cmap);
                }
            }
        }
        for (MapAccessor<?> k : keys) {
            if (!k.isTransient() && !done.contains(k)) {
                @SuppressWarnings("unchecked") MapAccessor<Object> cmap = (MapAccessor<Object>) k;
                writeElementAttribute(w, figure, cmap);
            }
        }
    }

    private void writeElementNodeList(@NonNull XMLStreamWriter w, @NonNull Figure figure) throws IOException, XMLStreamException {
        for (MapAccessor<?> k : figureFactory.figureNodeListKeys(figure)) {
            @SuppressWarnings("unchecked")
            MapAccessor<Object> key = (MapAccessor<Object>) k;
            Object value = figure.get(key);
            if (!key.isTransient() && figure.containsKey(StyleOrigin.USER, key) && !figureFactory.isDefaultValue(figure, key, value)) {
                figureFactory.valueToNodeList(key, value, w);
            }
        }
    }

    protected void writeNodeRecursively(@NonNull XMLStreamWriter w, @NonNull Figure figure, int depth) throws IOException {
        try {
            String elementName = figureFactory.figureToName(figure);
            if (elementName == null) {
                // => the figureFactory decided that we should skip the figure
                return;
            }
            w.writeStartElement(elementName);
            writeElementAttributes(w, figure);
            writeElementNodeList(w, figure);
            for (Figure child : figure.getChildren()) {
                if (figureFactory.figureToName(child) != null) {
                    writeNodeRecursively(w, child, depth + 1);
                }
            }
            w.writeEndElement();
        } catch (IOException | XMLStreamException e) {
            throw new IOException("Error writing figure " + figure, e);
        }
    }

    // XXX maybe this should not be in SimpleXmlIO?
    protected void writeProcessingInstructions(@NonNull XMLStreamWriter w, @NonNull Drawing external) throws XMLStreamException {
        if (figureFactory.getStylesheetsKey() != null) {
            ImmutableList<URI> stylesheets = external.get(figureFactory.getStylesheetsKey());
            if (stylesheets != null) {
                for (Object stylesheet : stylesheets) {
                    if (stylesheet instanceof URI) {
                        stylesheet = uriResolver.apply((URI) stylesheet);

                        String stylesheetString = stylesheet.toString();
                        String type = "text/" + stylesheetString.substring(stylesheetString.lastIndexOf('.') + 1);
                        if ("text/".equals(type)) {
                            type = "text/css";
                        }
                        w.writeProcessingInstruction("xml-stylesheet", //
                                "type=\"" + type + "\" href=\"" + stylesheet + "\"");
                    }
                }
            }
        }
    }
}
