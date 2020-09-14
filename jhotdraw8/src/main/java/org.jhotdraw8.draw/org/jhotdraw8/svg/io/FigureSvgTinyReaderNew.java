/*
 * @(#)FxSvgTinyReaderNew.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.svg.io;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.collection.ImmutableMaps;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.concurrent.CheckedRunnable;
import org.jhotdraw8.css.CssDefaultableValue;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.Paintable;
import org.jhotdraw8.css.UnitConverter;
import org.jhotdraw8.css.text.CssDefaultableValueConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.StyleableFigure;
import org.jhotdraw8.draw.render.SimpleRenderContext;
import org.jhotdraw8.io.SimpleIdFactory;
import org.jhotdraw8.reflect.TypeToken;
import org.jhotdraw8.styleable.ReadOnlyStyleableMapAccessor;
import org.jhotdraw8.svg.figure.SvgCircleFigure;
import org.jhotdraw8.svg.figure.SvgDrawing;
import org.jhotdraw8.svg.figure.SvgElementFigure;
import org.jhotdraw8.svg.figure.SvgEllipseFigure;
import org.jhotdraw8.svg.figure.SvgGFigure;
import org.jhotdraw8.svg.figure.SvgLineFigure;
import org.jhotdraw8.svg.figure.SvgPathFigure;
import org.jhotdraw8.svg.figure.SvgPolygonFigure;
import org.jhotdraw8.svg.figure.SvgPolylineFigure;
import org.jhotdraw8.svg.figure.SvgRectFigure;
import org.jhotdraw8.svg.text.SvgXmlPaintableConverter;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.xml.text.XmlStringConverter;

import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.net.URI;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Reads an SVG "Tiny" 1.2 file and creates Figure objects from it.
 * <p>
 * References:<br>
 * <dl>
 *     <dt>SVG 1.2 Tiny</dt>
 *     <dd><a href="https://www.w3.org/TR/SVGTiny12/index.html">link</a></dd>
 * </dl>
 * <dl>
 *     <dt>SVG Strokes</dt>
 *     <dd><a href="https://www.w3.org/TR/svg-strokes/">link</a></dd>
 * </dl>
 */
public class FigureSvgTinyReaderNew {
    public static final String SVG_NAMESPACE = "http://www.w3.org/2000/svg";
    private static final Logger LOGGER = Logger.getLogger(FigureSvgTinyReaderNew.class.getName());
    /**
     * Maps from an attribute name to an accessor.
     */
    private final Map<String, Map<String, MapAccessor<?>>> accessorMap = new LinkedHashMap<>();
    /**
     * Maps from an element name to a figure factory.
     */
    private final Map<QName, Supplier<Figure>> figureMap = new LinkedHashMap<>();
    /**
     * Maps from a type to a converter.
     */
    private final Map<Type, Converter<?>> converterMap = new LinkedHashMap<>();

    {
        for (Map.Entry<String, ? extends Class<? extends Figure>> e : Arrays.asList(
                ImmutableMaps.entry("svg", SvgDrawing.class),
                ImmutableMaps.entry("g", SvgGFigure.class),
                ImmutableMaps.entry("rect", SvgRectFigure.class),
                ImmutableMaps.entry("circle", SvgCircleFigure.class),
                ImmutableMaps.entry("ellipse", SvgEllipseFigure.class),
                ImmutableMaps.entry("line", SvgLineFigure.class),
                ImmutableMaps.entry("path", SvgPathFigure.class),
                ImmutableMaps.entry("polygon", SvgPolygonFigure.class),
                ImmutableMaps.entry("polyline", SvgPolylineFigure.class)
                //ImmutableMaps.entry("text", SvgTextFigure.class)
        )) {
            String elem = e.getKey();
            Class<? extends Figure> figureClass = e.getValue();
            Map<String, MapAccessor<?>> m = Figure.getDeclaredAndInheritedMapAccessors(figureClass).stream()
                    .collect(Collectors.toMap(MapAccessor::getName, Function.identity()
                    ));
            accessorMap.put(elem, m);
            for (MapAccessor<?> acc : m.values()) {
                if (acc instanceof ReadOnlyStyleableMapAccessor) {
                    ReadOnlyStyleableMapAccessor<?> rosma = (ReadOnlyStyleableMapAccessor<?>) acc;
                    converterMap.put(acc.getValueType(), rosma.getCssConverter());
                }
            }

            figureMap.put(new QName(SVG_NAMESPACE, e.getKey()), () -> {
                try {
                    return figureClass.getConstructor().newInstance();
                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
                    throw new RuntimeException(ex);
                }
            });
        }

        // Override converters that have different representations in CSS and XML
        converterMap.put(String.class, new XmlStringConverter());
        converterMap.put(new TypeToken<CssDefaultableValue<Paintable>>() {
                }.getType(),
                new CssDefaultableValueConverter<>(new SvgXmlPaintableConverter()));
    }

    private XMLStreamException createException(XMLStreamReader r, String s) {
        return new XMLStreamException(s + " " + getLocation(r));
    }

    private XMLStreamException createException(XMLStreamReader r, String s, Throwable cause) {
        return new XMLStreamException(s + " " + getLocation(r), cause);
    }

    private XMLStreamException createException(Location r, String s, Throwable cause) {
        return new XMLStreamException(s + " " + getLocation(r), cause);
    }

    private XMLStreamException createException(Location r, String s) {
        return new XMLStreamException(s + " " + getLocation(r));
    }

    private String getLocation(XMLStreamReader r) {
        Location location = r.getLocation();
        return location == null ? "" : "at [row,col]:[" + location.getLineNumber() + "," + location.getColumnNumber() + "]";
    }

    private String getLocation(Location location) {
        return location == null ? "" : "at [row,col]:[" + location.getLineNumber() + "," + location.getColumnNumber() + "]";
    }

    private void logWarning(XMLStreamReader r, String message) {
        LOGGER.warning(message + " " + getLocation(r));
    }

    public Figure read(@NonNull java.nio.file.Path p) throws IOException {
        try (InputStream in = new BufferedInputStream(Files.newInputStream(p))) {
            return read(in, p.toUri());
        } catch (IOException e) {
            throw new IOException("Error reading file: \"" + p.toAbsolutePath() + "\".", e);
        }
    }

    public Figure read(@NonNull InputStream in, @NonNull URI documentHome) throws IOException {
        try {

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

            XMLStreamReader r = dbf.createXMLStreamReader(in);
            Context ctx = new Context();
            Figure root = null;
            Loop:
            while (true) {
                switch (r.next()) {
                case XMLStreamReader.END_DOCUMENT:
                    break Loop;
                case XMLStreamReader.START_ELEMENT:
                    if (SVG_NAMESPACE.equals(r.getNamespaceURI())
                            && "svg".equals(r.getLocalName())) {
                        root = readElement(r, null, ctx);
                    }
                    break Loop;
                case XMLStreamConstants.DTD:
                case XMLStreamConstants.COMMENT:
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                case XMLStreamConstants.CHARACTERS:
                    break;
                default:
                    throw new XMLStreamException("Expected an <svg> element. Found: " + r.getEventType());
                }
            }
            if (root == null) {
                throw new XMLStreamException("Could not find an <svg> element in the file.");
            }

            for (CheckedRunnable secondPass : ctx.secondPass) {
                secondPass.run();
            }

            root.set(SvgDrawing.INLINE_STYLESHEETS, ImmutableLists.ofCollection(ctx.stylesheets));


            if (!(root instanceof SvgDrawing)) {
                SvgDrawing svgDrawing = new SvgDrawing();
                svgDrawing.addChild(root);
                root = svgDrawing;
            }
            ((SvgDrawing) root).updateAllCss(new SimpleRenderContext());

            setSizeOfDrawing(root);

            return root;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    private void readAttributes(XMLStreamReader r, Figure node, Map<String, MapAccessor<?>> m, Context ctx) throws XMLStreamException {
        for (int i = 0, n = r.getAttributeCount(); i < n; i++) {
            String namespace = r.getAttributeNamespace(i);
            if (namespace == null || SVG_NAMESPACE.equals(namespace)) {
                String localName = r.getAttributeLocalName(i);
                String value = r.getAttributeValue(i);
                Location location = r.getLocation();

                if ("id".equals(localName)) {
                    ctx.idFactory.putId(value, node);
                    node.set(StyleableFigure.ID, value);
                } else {
                    if (m != null) {
                        ctx.secondPass.add(() -> {
                            @SuppressWarnings("unchecked") MapAccessor<Object> mapAccessor = (MapAccessor<Object>) m.get(localName);
                            if (mapAccessor instanceof ReadOnlyStyleableMapAccessor<?>) {
                                ReadOnlyStyleableMapAccessor<?> rosma = (ReadOnlyStyleableMapAccessor<?>) mapAccessor;
                                Converter<?> converter = converterMap.get(rosma.getValueType());
                                if (converter == null) {
                                    throw createException(location, "No converter for attribute \"" + localName + "\".");
                                } else {
                                    try {
                                        node.set(mapAccessor, converter.fromString(value, ctx.idFactory));
                                    } catch (ParseException e) {
                                        LOGGER.warning("Could not convert attribute \"" + localName + "\". " + getLocation(location)
                                                + "\n" + e.getMessage());
                                    } catch (IOException e) {
                                        throw createException(location, "Could not read attribute \"" + localName + "\".", e);
                                    }
                                }
                            }
                        });
                    } else {
                        logWarning(r, "Skipping SVG attribute \"" + localName + "\".");
                    }
                }
            }
        }
    }

    private void readChildElements(XMLStreamReader r, Figure parent, Context ctx) throws XMLStreamException {
        Loop:
        while (true) {
            switch (r.next()) {
            case XMLStreamReader.END_DOCUMENT:
            case XMLStreamReader.END_ELEMENT:
                break Loop;
            case XMLStreamReader.START_ELEMENT:
                readElement(r, parent, ctx);
                break;
            case XMLStreamConstants.DTD:
            case XMLStreamConstants.COMMENT:
            case XMLStreamConstants.PROCESSING_INSTRUCTION:
            case XMLStreamConstants.CHARACTERS:
                break;
            default:
                throw new XMLStreamException("Expected an element. Found: " + r.getEventType());
            }
        }
    }

    private void readDesc(XMLStreamReader r, Figure parent, Context ctx) throws XMLStreamException {
        parent.set(SvgElementFigure.DESC_KEY, readTextContent(r, parent, ctx));
    }

    private Figure readElement(XMLStreamReader r, Figure parent, Context ctx) throws XMLStreamException {
        String localName = r.getLocalName();

        if (SVG_NAMESPACE.equals(r.getNamespaceURI())) {
            Supplier<Figure> figureSupplier = figureMap.get(r.getName());
            if (figureSupplier != null) {
                Figure node = figureSupplier.get();
                readAttributes(r, node, accessorMap.get(localName), ctx);
                readChildElements(r, node == null ? parent : node, ctx);
                if (parent != null) {
                    parent.getChildren().add(node);
                }
                return node;
            } else {
                switch (localName) {
                case "title":
                    readTitle(r, parent, ctx);
                    return parent;
                case "desc":
                    readDesc(r, parent, ctx);
                    return parent;
                case "style":
                    readStyle(r, parent, ctx);
                    return parent;
                default:
                    logWarning(r, "Don't understand SVG element: " + localName + ".");
                    readChildElements(r, parent, ctx);
                }
            }
        } else {
            logWarning(r, "Skipping foreign element: " + r.getName() + ".");
            skipElement(r, ctx);
        }
        System.out.println("done element " + localName);
        return null;
    }

    private void readStyle(XMLStreamReader r, Figure parent, Context ctx) throws XMLStreamException {
        String id = null;
        String type = null;
        String media = null;
        for (int i = 0, n = r.getAttributeCount(); i < n; i++) {
            String namespace = r.getAttributeNamespace(i);
            if (namespace == null || SVG_NAMESPACE.equals(namespace)) {
                String localName = r.getAttributeLocalName(i);
                String value = r.getAttributeValue(i);
                switch (localName) {
                case "id":
                    id = value;
                    break;
                case "type":
                    type = value;
                    break;
                case "media":
                    media = value;
                    break;
                default:
                    logWarning(r, "Skipping SVG attribute " + localName);
                    break;
                }
            } else {
                logWarning(r, "Skipping foreign attribute " + r.getAttributeName(i));
            }
        }
        ctx.stylesheets.add(readTextContent(r, parent, ctx));
    }

    /**
     * Reads all text content until ELEMENT_END is encountered.
     */
    private @NonNull String readTextContent(XMLStreamReader r, Figure parent, Context ctx) throws XMLStreamException {
        StringBuilder buf = new StringBuilder();
        int depth = 1;
        Loop:
        for (int eventType = r.next(); eventType != XMLStreamConstants.END_DOCUMENT; eventType = r.next()) {
            switch (eventType) {
            case XMLStreamConstants.CHARACTERS:
            case XMLStreamConstants.CDATA:
            case XMLStreamConstants.SPACE:
                if (depth == 1) {
                    buf.append(r.getText());
                }
                break;
            case XMLStreamConstants.START_ELEMENT:
                readElement(r, parent, ctx);
                depth++;
                break;
            case XMLStreamConstants.END_ELEMENT:
                depth--;
                if (depth == 0) {
                    break Loop;
                }
                break;
            default:
                break;
            }
        }
        return buf.toString();
    }

    private void readTitle(XMLStreamReader r, Figure parent, Context ctx) throws XMLStreamException {
        parent.set(SvgElementFigure.TITLE_KEY, readTextContent(r, parent, ctx));
    }

    private void setSizeOfDrawing(Figure root) {
        CssSize w = root.getNonNull(SvgDrawing.WIDTH);
        CssSize h = root.getNonNull(SvgDrawing.HEIGHT);
        CssRectangle2D viewBox = root.get(SvgDrawing.VIEW_BOX);
        if (UnitConverter.PERCENTAGE.equals(w.getUnits())) {
            root.set(SvgDrawing.WIDTH,
                    viewBox == null || UnitConverter.PERCENTAGE.equals(viewBox.getWidth().getUnits()) ?
                            new CssSize(640) : viewBox.getWidth()
            );
        }
        if (UnitConverter.PERCENTAGE.equals(h.getUnits())) {
            root.set(SvgDrawing.HEIGHT,
                    viewBox == null || UnitConverter.PERCENTAGE.equals(viewBox.getHeight().getUnits()) ?
                            new CssSize(480) : viewBox.getHeight()
            );
        }
    }

    /**
     * Skips the current element and all its child elements.
     *
     * @param r   the reader
     * @param ctx the context
     * @throws XMLStreamException if a skipped element is in SVG namespace
     */
    private void skipElement(XMLStreamReader r, Context ctx) throws XMLStreamException {
        int depth = 1;// we are currently inside START_ELEMENT
        Loop:
        while (true) {
            switch (r.next()) {
            case XMLStreamReader.START_ELEMENT:
                if (SVG_NAMESPACE.equals(r.getNamespaceURI())) {
                    logWarning(r, "Skipping element " + r.getName() + ".");
                } else {
                    LOGGER.fine("Skipping element " + r.getName() + ".");
                }
                depth++;
                break;
            case XMLStreamReader.END_ELEMENT:
                depth--;
                if (depth == 0) {
                    break Loop;
                }
                break;
            default:
                break;
            }
        }

    }

    private static class Context {
        SimpleIdFactory idFactory = new SimpleIdFactory();
        List<CheckedRunnable> secondPass = new ArrayList<>();
        List<String> stylesheets = new ArrayList<>();

    }
}
