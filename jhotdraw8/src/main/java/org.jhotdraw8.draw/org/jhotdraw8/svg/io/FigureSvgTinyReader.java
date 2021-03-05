/*
 * @(#)FigureSvgTinyReaderNew.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.svg.io;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.collection.ImmutableMaps;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.collection.NonNullKey;
import org.jhotdraw8.concurrent.CheckedRunnable;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.CssDefaultableValue;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.NamedCssColor;
import org.jhotdraw8.css.Paintable;
import org.jhotdraw8.css.UnitConverter;
import org.jhotdraw8.css.text.CssColorConverter;
import org.jhotdraw8.css.text.CssDefaultableValueConverter;
import org.jhotdraw8.css.text.CssSizeConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.StyleableFigure;
import org.jhotdraw8.draw.render.SimpleRenderContext;
import org.jhotdraw8.io.SimpleIdFactory;
import org.jhotdraw8.reflect.TypeToken;
import org.jhotdraw8.styleable.ReadOnlyStyleableMapAccessor;
import org.jhotdraw8.svg.css.SvgDefaultablePaint;
import org.jhotdraw8.svg.css.text.SvgDefaultablePaintConverter;
import org.jhotdraw8.svg.figure.SvgCircleFigure;
import org.jhotdraw8.svg.figure.SvgDefsFigure;
import org.jhotdraw8.svg.figure.SvgDrawing;
import org.jhotdraw8.svg.figure.SvgElementFigure;
import org.jhotdraw8.svg.figure.SvgEllipseFigure;
import org.jhotdraw8.svg.figure.SvgGFigure;
import org.jhotdraw8.svg.figure.SvgLineFigure;
import org.jhotdraw8.svg.figure.SvgLinearGradientFigure;
import org.jhotdraw8.svg.figure.SvgPathFigure;
import org.jhotdraw8.svg.figure.SvgPolygonFigure;
import org.jhotdraw8.svg.figure.SvgPolylineFigure;
import org.jhotdraw8.svg.figure.SvgRadialGradientFigure;
import org.jhotdraw8.svg.figure.SvgRectFigure;
import org.jhotdraw8.svg.figure.SvgStop;
import org.jhotdraw8.svg.figure.SvgTextFigure;
import org.jhotdraw8.svg.text.SvgXmlPaintableConverter;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.xml.text.XmlStringConverter;

import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Reads an SVG "Tiny" 1.2 file and creates Figure objects from it.
 * <p>
 * References:
 * <dl>
 *     <dt>SVG 1.2 Tiny</dt>
 *     <dd><a href="https://www.w3.org/TR/SVGTiny12/index.html">w3.org</a></dd>
 *
 *     <dt>SVG Strokes</dt>
 *     <dd><a href="https://www.w3.org/TR/svg-strokes/">w3.org</a></dd>
 * </dl>
 */
public class FigureSvgTinyReader {
    public static final String SVG_NAMESPACE = "http://www.w3.org/2000/svg";
    public static final String XML_NAMESPACE = "http://www.w3.org/XML/1998/namespace";
    /**
     * Maps from an attribute name to an accessor.
     */
    private final Map<String, Map<String, MapAccessor<?>>> accessorMap = new LinkedHashMap<>();
    /**
     * Maps from an element name to a figure factory.
     */
    private final Map<QName, Supplier<Figure>> figureMap = new LinkedHashMap<>();

    /**
     * Converts a CSS size string into a CssSize value.
     * <p>
     * FIXME we must use XmlSizeConverter and not CssSizeConverter!
     */
    private final CssSizeConverter sizeConverter = new CssSizeConverter(true);
    private final CssDefaultableValueConverter<CssSize> defaultableSizeConverter = new CssDefaultableValueConverter<>(new CssSizeConverter(false));

    /**
     * Converts a CSS color string into a CssColor value.
     * <p>
     * FIXME we must use XmlColorConverter and not CssColorConverter!
     */
    private final SvgDefaultablePaintConverter<CssColor> colorConverter = new SvgDefaultablePaintConverter<>(new CssColorConverter(true));
    /**
     * Maps from a type to a converter.
     */
    private final Map<Type, Converter<?>> converterMap = new LinkedHashMap<>();

    {
        for (Map.Entry<String, ? extends Class<? extends Figure>> e : Arrays.asList(
                ImmutableMaps.entry("svg", SvgDrawing.class),
                ImmutableMaps.entry("g", SvgGFigure.class),
                ImmutableMaps.entry("rect", SvgRectFigure.class),
                ImmutableMaps.entry("defs", SvgDefsFigure.class),
                ImmutableMaps.entry("circle", SvgCircleFigure.class),
                ImmutableMaps.entry("ellipse", SvgEllipseFigure.class),
                ImmutableMaps.entry("line", SvgLineFigure.class),
                ImmutableMaps.entry("path", SvgPathFigure.class),
                ImmutableMaps.entry("polygon", SvgPolygonFigure.class),
                ImmutableMaps.entry("polyline", SvgPolylineFigure.class),
                ImmutableMaps.entry("text", SvgTextFigure.class),
                ImmutableMaps.entry("linearGradient", SvgLinearGradientFigure.class),
                ImmutableMaps.entry("radialGradient", SvgRadialGradientFigure.class)
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

    private final Key<String> textKey = SvgTextFigure.TEXT;

    /**
     * @see #isBestEffort()
     */
    private boolean bestEffort;


    /**
     * Returns true if the reader runs in best effort mode.
     * <p>
     * If best effort mode is true, the reader attempts to perform
     * error processing as specified in
     * <a href="https://www.w3.org/TR/2008/WD-SVGMobile12-20080915/implnote.html#ErrorProcessing">
     * SVG 1.2 Error processing
     * </a>.
     * <p>
     * If best effort mode is false, the reader throws an exception
     * at every element or value that it does not understand.
     * <p>
     * The default value is false.
     *
     * @return whether the reader runs in best effort mode.
     */
    public boolean isBestEffort() {
        return bestEffort;
    }

    public void setBestEffort(boolean bestEffort) {
        this.bestEffort = bestEffort;
    }


    private String toLocationString(@Nullable Location location) {
        return location == null ? "" :
                location.getSystemId()
                        + " at [row,col]:[" + location.getLineNumber() + "," + location.getColumnNumber() + "]";
    }

    /**
     * Performs error processing.
     *
     * @param r       the reader
     * @param message the error message
     * @see #isBestEffort()
     */
    private void handleError(XMLStreamReader r, String message) throws XMLStreamException {
        handleError(r.getLocation(), message);
    }

    /**
     * Performs error processing.
     *
     * @param r       the reader
     * @param message the error message
     * @see #isBestEffort()
     */
    private void handleError(@Nullable Location r, @NonNull String message) throws XMLStreamException {
        handleError(r, message, null);
    }

    /**
     * Performs error processing.
     *
     * @param r       the reader
     * @param message the error message
     * @param cause   the cause of the error
     * @see #isBestEffort()
     */
    private void handleError(@Nullable Location r, @NonNull String message, @Nullable Throwable cause) throws XMLStreamException {
        if (bestEffort) {
            errors.add(message + " " + toLocationString(r));
        } else {
            if (r == null) {
                throw new XMLStreamException(message, cause);
            } else {
                throw new XMLStreamException(message, r, cause);
            }
        }
    }

    public Figure read(@NonNull Source in) throws IOException {
        try {

            XMLInputFactory dbf = XMLInputFactory.newInstance();

            // We do not want that the reader creates a socket connection,
            // even if we would get a better result!
            dbf.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
            dbf.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, false);
            dbf.setProperty(XMLInputFactory.SUPPORT_DTD, false);
            dbf.setXMLResolver((publicID, systemID, baseURI, namespace) -> null);

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
                    handleError(r, "Expected an <svg> element. Found: " + r.getEventType());
                }
            }
            if (root == null) {
                handleError(r, "Could not find an <svg> element in the file.");
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
            ((SvgDrawing) root).set(SvgDrawing.BACKGROUND, NamedCssColor.TRANSPARENT);
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
            String localName = r.getAttributeLocalName(i);
            String value = r.getAttributeValue(i);

            if ("id".equals(localName)
                    && (namespace == null
                    || SVG_NAMESPACE.equals(namespace)
                    || XML_NAMESPACE.equals(namespace))) {
                ctx.idFactory.putIdAndObject(value, node);
                node.set(StyleableFigure.ID, value);
            } else if (namespace == null || SVG_NAMESPACE.equals(namespace)) {
                Location location = r.getLocation();
                if (m != null) {
                    ctx.secondPass.add(() -> {
                        @SuppressWarnings("unchecked") MapAccessor<Object> mapAccessor = (MapAccessor<Object>) m.get(localName);
                        if (mapAccessor instanceof ReadOnlyStyleableMapAccessor<?>) {
                            ReadOnlyStyleableMapAccessor<?> rosma = (ReadOnlyStyleableMapAccessor<?>) mapAccessor;
                            Converter<?> converter = converterMap.get(rosma.getValueType());
                            if (converter == null) {
                                handleError(location, "No converter for attribute \"" + localName + "\".");
                            } else {
                                try {
                                    node.set(mapAccessor, converter.fromString(value, ctx.idFactory));
                                } catch (ParseException | IOException e) {
                                    handleError(location, "Could not read attribute \"" + localName + "\".", e);
                                }
                            }
                        } else {
                            handleError(location, "Unsupported attribute " + localName + "=\"" + value + "\".");
                        }
                    });
                } else {
                    handleError(r, "Skipping SVG attribute " + localName + "=\"" + value + "\".");
                }
            } else {
                handleError(r, "Skipping foreign attribute {" + namespace + "}" + localName + "=\"" + value + "\".");
            }
        }
    }

    /**
     * Reads the children of the current element.
     * <p>
     * Precondition: the current event is START_ELEMENT.
     * <p>
     * Postcondition: the current event is the corresponding END_ELEMENT.
     *
     * @param r      the reader
     * @param parent the parent element
     * @param ctx    the context
     * @throws XMLStreamException
     */
    private void readChildElements(XMLStreamReader r, Figure parent, Context ctx) throws XMLStreamException {
        ctx.stringBuilder.setLength(0);
        boolean collectTextForTextFigure = SVG_NAMESPACE.equals(r.getNamespaceURI()) && "text".equals(r.getLocalName());
        Loop:
        while (true) {
            switch (r.next()) {
            case XMLStreamReader.END_DOCUMENT:
            case XMLStreamReader.END_ELEMENT:
                break Loop;
            case XMLStreamReader.START_ELEMENT:
                collectTextForTextFigure = false;
                readElement(r, parent, ctx);
                break;
            case XMLStreamConstants.DTD:
            case XMLStreamConstants.COMMENT:
            case XMLStreamConstants.PROCESSING_INSTRUCTION:
                break;
            case XMLStreamConstants.CHARACTERS:
                ctx.stringBuilder.append(r.getTextCharacters(), r.getTextStart(), r.getTextLength());
                break;
            default:
                handleError(r, "Expected an element. Found: " + r.getEventType());
            }
        }
        if (collectTextForTextFigure && ctx.stringBuilder.length() > 0) {
            parent.set(textKey, ctx.stringBuilder.toString());
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
                switch (localName == null ? "" : localName) {
                case "title":
                    readTitle(r, parent, ctx);
                    return parent;
                case "desc":
                    readDesc(r, parent, ctx);
                    return parent;
                case "style":
                    readStyle(r, parent, ctx);
                    return parent;
                case "stop":
                    readStop(r, parent, ctx);
                    return parent;
                default:
                    handleError(r, "Don't understand SVG element: " + localName + ".");
                    readChildElements(r, parent, ctx);
                }
            }
        } else {
            handleError(r, "Skipping foreign element: " + r.getName() + ".");
            skipElement(r, ctx);
        }
        return null;
    }

    private void readStop(XMLStreamReader r, Figure parent, Context ctx) throws XMLStreamException {
        SvgDefaultablePaint<CssColor> stopColor = null;
        CssSize offset = null;
        CssDefaultableValue<CssSize> stopOpacity = new CssDefaultableValue<>(CssSize.ONE);
        for (int i = 0, n = r.getAttributeCount(); i < n; i++) {
            String namespace = r.getAttributeNamespace(i);
            if (namespace == null || SVG_NAMESPACE.equals(namespace)) {
                String localName = r.getAttributeLocalName(i);
                String value = r.getAttributeValue(i);
                try {
                    switch (localName) {
                    case "stop-color":
                        stopColor = colorConverter.fromString(value);
                        break;
                    case "offset":
                        offset = sizeConverter.fromString(value);
                        break;
                    case "stop-opacity":
                        stopOpacity = defaultableSizeConverter.fromString(value);
                        break;
                    default:
                        handleError(r, "stop: Skipping SVG attribute " + localName + "=\"" + value + "\"W");
                        break;
                    }
                } catch (ParseException | IOException e) {
                    handleError(r, "stop: Could not parse attribute " + localName + "=\"" + value + "\"W");
                }
            } else {
                handleError(r, "stop: Skipping foreign attribute " + r.getAttributeName(i));
            }
        }
        if (offset != null) {
            if (!parent.getSupportedKeys().contains(stopsKey)) {
                handleError(r, "stop: Cannot add stop to parent element " + parent.getTypeSelector());
            } else {
                SvgStop stop = new SvgStop(offset.getConvertedValue(), stopColor, stopOpacity);
                parent.put(stopsKey, ImmutableLists.add(parent.get(stopsKey), stop));
            }
        }
        skipElement(r, ctx);
    }

    private NonNullKey<ImmutableList<SvgStop>> stopsKey = SvgLinearGradientFigure.STOPS;

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
                    handleError(r, "Skipping SVG attribute " + localName);
                    break;
                }
            } else {
                handleError(r, "Skipping foreign attribute " + r.getAttributeName(i));
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
                    handleError(r, "Skipping element " + r.getName() + ".");
                } else {
                    handleError(r, "Skipping element " + r.getName() + ".");
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

    /**
     * This list contains all collected error messages.
     * The list must be synchronized, because we do some processing in
     * a parallel stream.
     */
    private final List<String> errors = Collections.synchronizedList(new ArrayList<>());

    /**
     * Clears the errors list.
     */
    public void clearErrors() {
        errors.clear();
    }

    /**
     * Gets a copy of the errors list.
     *
     * @return a copy of the errors list
     */
    public List<String> getCopyOfErrors() {
        return new ArrayList<>(errors);
    }

    /**
     * Holds the current reading context.
     */
    private static class Context {
        SimpleIdFactory idFactory = new SimpleIdFactory();
        List<CheckedRunnable> secondPass = new ArrayList<>();
        List<String> stylesheets = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
    }
}
