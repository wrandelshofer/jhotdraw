/* @(#)SimpleFigureFactory.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.io;

import org.jhotdraw.text.Point2DConverter;
import org.jhotdraw.text.Rectangle2DConverter;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import org.jhotdraw.collection.Key;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.FigureKeys;
import org.jhotdraw.draw.RectangleFigure;
import org.jhotdraw.draw.SimpleDrawing;
import org.jhotdraw.draw.TextFigure;
import org.jhotdraw.text.CDataConverter;
import org.jhotdraw.text.Converter;
import org.jhotdraw.text.DefaultConverter;

/**
 * SimpleFigureFactory.
 * <p>
 * This factory provides the following mappings.
 * <ul>
 * <li>Maps {@code Figure}s from/to XML element names.</li>
 * <li>Maps {@code Key}s from/to XML attribute names.</li>
 * </ul>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleFigureFactory implements FigureFactory {

    private final Map<String, Key<?>> attrToKey = new HashMap<>();
    private final Map<Key<?>, String> keyToAttr = new HashMap<>();
    private final Map<String, Class<? extends Figure>> elemToFigure = new HashMap<>();
    private final Map<Class<? extends Figure>, String> figureToElem = new HashMap<>();
    private final Map<String, Converter> valueToCData = new HashMap<>();
    private final Map<String, Converter> valueOfCData = new HashMap<>();
    private final Map<Class<? extends Figure>, HashSet<Key<?>>> figureKeys = new HashMap<>();

    public SimpleFigureFactory() {
    }

    /** Adds the provided keys to the figure. 
     *
     *  @param map the mapping 
     */
    public void addFigureKeys(Class<? extends Figure> f, Collection<Key<?>> keys) {
        if (figureKeys.containsKey(f)) {
            figureKeys.get(f).addAll(keys);
        } else {
            figureKeys.put(f, new HashSet<>(keys));
        }
    }

    /** Adds the provided mapping of XML attribute names from/to
     * {@code Key}s.
     *  <p>
     * The same key can be added more than once.
     *
     *  @param name The attribute name
     *  @param key The key
     */
    public void addKey(String name, Key<?> key) {
        if (!attrToKey.containsKey(name)) {
            attrToKey.put(name, key);
        }
        if (!keyToAttr.containsKey(key)) {
            keyToAttr.put(key, name);
        }
    }
    /** Adds the provided mapping of XML attribute names from/to
     * {@code Key}s.
     *  <p>
     * The same key can be added more than once.
     *
     *  @param name The attribute name
     *  @param key The key
     */
    public void addKeys(HashMap<String,Key<?>> keys) {
        for (Map.Entry<String,Key<?>> entry:keys.entrySet()) {
            addKey(entry.getKey(),entry.getValue());
        }
    }

    /** Clears the mapping of XML attributes from/to {@code Key}s.
     */
    public void clearAttributeMap() {
        attrToKey.clear();
        keyToAttr.clear();
    }

    /** Adds the provided mappings of XML attribute names from/to
     * {@code Figure}s.
     *  @param name The element name
     *  @param figure The figure class
     */
    public void addFigure(String name, Class<? extends Figure> figure) {
        if (!elemToFigure.containsKey(name)) {
            elemToFigure.put(name, figure);
        }
        if (!figureToElem.containsKey(figure)) {
            figureToElem.put(figure, name);
        }
    }

    /** Clears the mapping of XML attributes from/to {@code Key}s.
     */
    public void clearElementMap() {
        attrToKey.clear();
        keyToAttr.clear();
    }

    @Override
    public String figureToName(Figure f) throws IOException {
        if (!figureToElem.containsKey(f.getClass())) {
            throw new IOException("no mapping for figure " + f.getClass());
        }
        return figureToElem.get(f.getClass());
    }

    @Override
    public Figure nameToFigure(String elementName) throws IOException {
        if (!elemToFigure.containsKey(elementName)) {
            throw new IOException("no mapping for element " + elementName);
        }
        Class<? extends Figure> clazz = elemToFigure.get(elementName);
        try {
            return (Figure) clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new IOException("can not instantiate " + clazz);
        }
    }

    @Override
    public String keyToName(Key<?> key) throws IOException {
        if (!keyToAttr.containsKey(key)) {
            throw new IOException("no mapping for key " + key);
        }
        return keyToAttr.get(key);
    }

    @Override
    public Key<?> nameToKey(String attributeName) throws IOException {
        if (!attrToKey.containsKey(attributeName)) {
            throw new IOException("no mapping for attribute " + attributeName);
        }
        return attrToKey.get(attributeName);
    }

    /** Adds a converter.
     * @param valueType A value type returned by {@code Key.getValueType();}.
     * @param converter */
    public void addConverter(Class<?> valueType, Converter<?> converter) {
        addConverter(valueType.getName(), converter);

    }

    /** Adds a converter.
     * @param valueType A value type returned by {@code Key.getFullValueType();}.
     * @param converter
     */
    public void addConverter(String fullValueType, Converter<?> converter) {
        valueToCData.put(fullValueType, converter);
        valueOfCData.put(fullValueType, converter);
    }

    @Override
    public String valueToString(Key<?> key, Object value) throws IOException {
        Converter converter = valueToCData.get(key.getFullValueType());
        if (converter == null) {
            throw new IOException("no converter for attribute type " + key.getFullValueType());
        }
        return converter.toString(value);
    }

    @Override
    public Object stringToValue(Key<?> key, String string) throws IOException {
        try {
            Converter converter = valueOfCData.get(key.getFullValueType());
            if (converter == null) {
                throw new IOException("no converter for attribute type " + key.getClass());
            }
            return converter.toValue(string);
        } catch (ParseException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public Set<Key<?>> figureKeys(Figure f) {
        Set<Key<?>> keys = figureKeys.get(f.getClass());
        return keys == null ? Collections.emptySet() : keys;
    }

    @Override
    public <T> T getDefaultValue(Key<T> key) {
        return key.getDefaultValue();
    }
}
