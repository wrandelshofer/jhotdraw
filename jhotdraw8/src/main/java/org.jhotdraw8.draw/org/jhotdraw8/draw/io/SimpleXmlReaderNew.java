/*
 * @(#)SimpleXmlReaderNew.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.draw.io;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.input.Clipboard;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.concurrent.WorkState;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.StyleableFigure;
import org.jhotdraw8.draw.input.ClipboardInputFormat;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.io.UriResolver;

import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleXmlReaderNew implements InputFormat, ClipboardInputFormat {
    @NonNull
    private final ObservableMap<Key<?>, Object> properties = FXCollections.observableHashMap();
    @NonNull
    private final IdFactory idFactory;
    @Nullable
    private String namespaceURI;
    @NonNull
    private FigureFactory figureFactory = new DefaultFigureFactory();

    private String idAttribute = "id";

    public SimpleXmlReaderNew(@NonNull IdFactory idFactory) {
        this.idFactory = idFactory;
    }

    @Override
    public @NonNull ObservableMap<Key<?>, Object> getProperties() {
        return properties;
    }

    @Override
    public @Nullable Figure read(@NonNull InputStream in, Drawing drawing, URI documentHome, @NonNull WorkState workState) throws IOException {
        XMLInputFactory dbf = XMLInputFactory.newInstance();

        // We do not want that the reader creates a socket connection,
        // even if it would benefit the result!
        dbf.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        dbf.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, false);
        dbf.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        dbf.setXMLResolver((publicID,
                            systemID,
                            baseURI,
                            namespace) -> null
        );
        Deque<Figure> stack = new ArrayDeque<>();
        List<Runnable> secondPass = new ArrayList<>();
        try {
            for (XMLStreamReader r = dbf.createXMLStreamReader(in); r.hasNext(); ) {
                readNode(r, r.next(), stack, secondPass);
            }

        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
        if (stack.size() > 1) {
            throw new IOException("Illegal stack size! " + stack);
        }

        try {
            secondPass.parallelStream().forEach(Runnable::run);
            /*for (Runnable pass : secondPass) {
                pass.run();
            }*/
        } catch (UncheckedIOException e) {
            throw e.getCause();
        } catch (Exception e) {
            throw new IOException(e);
        }


        return stack.isEmpty() ? null : stack.getFirst();
    }

    private void readNode(XMLStreamReader r, int next, @NonNull Deque<Figure> stack, @NonNull List<Runnable> secondPass) throws IOException {
        switch (next) {
        case XMLStreamReader.START_ELEMENT:
            readStartElement(r, stack, secondPass);
            break;
        case XMLStreamReader.END_ELEMENT:
            readEndElement(r, stack);
            break;
        case XMLStreamReader.PROCESSING_INSTRUCTION:
            readProcessingInstruction(r, stack, secondPass);
            break;
        case XMLStreamReader.CHARACTERS:
            break;
        case XMLStreamReader.COMMENT:
            break;
        case XMLStreamReader.SPACE:
            break;
        case XMLStreamReader.START_DOCUMENT:
            break;
        case XMLStreamReader.END_DOCUMENT:
            break;
        case XMLStreamReader.ENTITY_REFERENCE:
            break;
        case XMLStreamReader.ATTRIBUTE:
            break;
        case XMLStreamReader.DTD:
            break;
        case XMLStreamReader.CDATA:
            break;
        case XMLStreamReader.NAMESPACE:
            break;
        case XMLStreamReader.NOTATION_DECLARATION:
            break;
        case XMLStreamReader.ENTITY_DECLARATION:
            break;
        default:
            throw new IOException("unsupported XMLStream event: " + next);
        }
    }

    private final static Pattern hrefPattern = Pattern.compile("(?:^|.* )href=\"([^\"]*)\".*");
    @NonNull
    private Function<URI, URI> uriResolver = new UriResolver(null, null);

    private void readProcessingInstruction(XMLStreamReader r, @NonNull Deque<Figure> stack, List<Runnable> secondPass) {
        if (figureFactory.getStylesheetsKey() != null) {
            String piTarget = r.getPITarget();
            String piData = r.getPIData();

            if ("xml-stylesheet".equals(piTarget) && piData != null) {
                secondPass.add(() -> {
                    Matcher m = hrefPattern.matcher(piData);
                    if (m.matches()) {
                        String href = m.group(1);

                        URI uri = URI.create(href);
                        uri = uriResolver.apply(uri);
                        Figure drawing = stack.getFirst();
                        ImmutableList<URI> listOrNull = drawing.get(figureFactory.getStylesheetsKey());
                        List<URI> stylesheets = listOrNull == null ? new ArrayList<>() : new ArrayList<>(listOrNull.asList());
                        stylesheets.add(uri);
                        drawing.set(figureFactory.getStylesheetsKey(), ImmutableLists.ofCollection(stylesheets));
                    }
                });

            }
        }
    }

    private void readEndElement(@NonNull XMLStreamReader r, @NonNull Deque<Figure> stack) {
        stack.removeFirst();
    }

    private void readStartElement(@NonNull XMLStreamReader r, @NonNull Deque<Figure> stack, @NonNull List<Runnable> secondPass) throws IOException {
        if (namespaceURI != null && !namespaceURI.equals(r.getNamespaceURI())) {
            return;
        }

        Figure figure = createFigure(r, stack);
        readAttributes(r, figure, secondPass);
    }

    @NonNull
    private Figure createFigure(@NonNull XMLStreamReader r, @NonNull Deque<Figure> stack) throws IOException {
        Figure figure = figureFactory.createFigureByElementName(r.getLocalName());
        if (figure == null) {
            throw new IOException("Cannot create figure in element <" + r.getLocalName() + "> at line " + r.getLocation().getLineNumber() + ", col " + r.getLocation().getColumnNumber());
        }
        if (!stack.isEmpty()) {
            Figure parent = stack.peek();
            if (!figure.isSuitableParent(parent) || !parent.isSuitableChild(figure)) {
                throw new IOException("Cannot add figure to parent in element <" + r.getLocalName() + "> at line " + r.getLocation().getLineNumber() + ", col " + r.getLocation().getColumnNumber());
            }
            parent.getChildren().add(figure);
        } else {
            stack.addFirst(figure);// add twice, so that it will remain after we finish the file
        }
        stack.addFirst(figure);
        return figure;
    }

    private void readAttributes(@NonNull XMLStreamReader r, @NonNull Figure figure, @NonNull List<Runnable> secondPass) throws IOException {
        for (int i = 0, n = r.getAttributeCount(); i < n; i++) {
            String ns = r.getAttributeNamespace(i);
            if (namespaceURI != null && ns != null && !namespaceURI.equals(ns)) {
                continue;
            }
            String attributeLocalName = r.getAttributeLocalName(i);
            String attributeValue = r.getAttributeValue(i);
            Location location = r.getLocation();
            if (idAttribute.equals(attributeLocalName)) {
                Object anotherObjWithSameId = idFactory.putIdToObject(attributeValue, figure);
                if (anotherObjWithSameId != null) {
                    throw new IOException("Duplicate id " + attributeValue + " at line " + location.getLineNumber() + ", col " + location.getColumnNumber());
                }
                setId(figure, attributeValue);
            } else {
                secondPass.add(() -> {
                    try {
                        @SuppressWarnings("unchecked")
                        MapAccessor<Object> key =
                                (MapAccessor<Object>) figureFactory.getKeyByAttributeName(figure, attributeLocalName);
                        if (key != null) {
                            figure.set(key, figureFactory.stringToValue(key, attributeValue));
                        } else {
                            throw new UncheckedIOException(new IOException("Unsupported attribute " + attributeLocalName + " at line " + location.getLineNumber() + ", col " + location.getColumnNumber()));
                        }
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
            }
        }
    }

    protected void setId(@NonNull Figure figure, String id) {
        figure.set(StyleableFigure.ID, id);
    }

    @Override
    public Set<Figure> read(Clipboard clipboard, DrawingModel model, Drawing drawing, @Nullable Figure parent) throws IOException {
        return null;
    }


    public void setNamespaceURI(@Nullable String namespaceURI) {
        this.namespaceURI = namespaceURI;
    }

    public void setFigureFactory(@NonNull FigureFactory figureFactory) {
        this.figureFactory = figureFactory;
    }

    protected void setUriResolver(@NonNull Function<URI, URI> uriResolver) {
        this.uriResolver = uriResolver;
    }

}
