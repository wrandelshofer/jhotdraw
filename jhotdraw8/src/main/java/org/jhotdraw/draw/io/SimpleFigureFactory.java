/* @(#)SimpleFigureFactory.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.io;

import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.jhotdraw.collection.Key;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.text.Converter;

/**
 * SimpleFigureFactory.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleFigureFactory implements FigureFactory {

    private final Map<Class<? extends Figure>, HashMap<String, Key<?>>> attrToKey = new HashMap<>();
    private final Map<Class<? extends Figure>, HashMap<Key<?>, String>> keyToAttr = new HashMap<>();
    private final Map<String, Class<? extends Figure>> elemToFigure = new HashMap<>();
    private final Map<Class<? extends Figure>, String> figureToElem = new HashMap<>();
    private final Map<String, Converter> valueToXML = new HashMap<>();
    private final Map<String, Converter> valueFromXML = new HashMap<>();
    private final Map<Class<? extends Figure>, HashSet<Key<?>>> figureKeys = new HashMap<>();
    private final Set<Class<? extends Figure>> skipFigures = new HashSet<>();
    private final Set<String> skipElements = new HashSet<>();

    public SimpleFigureFactory() {
    }

    /**
     * Adds the provided keys to the figure.
     *
     * @param f the figure
     * @param keys the keys
     */
    public void addFigureKeys(Class<? extends Figure> f, Collection<Key<?>> keys) {
        if (figureKeys.containsKey(f)) {
            figureKeys.get(f).addAll(keys);
        } else {
            figureKeys.put(f, new HashSet<>(keys));
        }
    }

    public void addFigureKeysAndNames(Class<? extends Figure> f, Collection<Key<?>> keys) {
        addFigureKeys(f, keys);
        for (Key<?> key : keys) {
            addKey(f, key.getName(), key);
        }
    }

    /**
     * Adds the provided mapping of XML attribute names from/to {@code Key}s.
     * <p>
     * The same key can be added more than once.
     *
     * @param figure the figure
     * @param name The attribute name
     * @param key The key
     */
    public void addKey(Class<? extends Figure> figure, String name, Key<?> key) {
        if (!attrToKey.containsKey(figure)) {
            attrToKey.put(figure, new HashMap<>());
        }
        HashMap<String, Key<?>> strToKey = attrToKey.get(figure);
        if (!strToKey.containsKey(name)) {
            strToKey.put(name, key);
        }

        if (!keyToAttr.containsKey(figure)) {
            keyToAttr.put(figure, new HashMap<>());
        }
        HashMap<Key<?>, String> keyToStr = keyToAttr.get(figure);
        if (!keyToStr.containsKey(key)) {
            keyToStr.put(key, name);
        }
    }

    /**
     * Adds the provided mapping of XML attribute names from/to {@code Key}s.
     * <p>
     * The same key can be added more than once.
     *
     * @param f The figure
     * @param keys The mapping from attribute names to keys
     */
    public void addKeys(Class<? extends Figure> f, HashMap<String, Key<?>> keys) {
        for (Map.Entry<String, Key<?>> entry : keys.entrySet()) {
            addKey(f, entry.getKey(), entry.getValue());
        }
    }

    /**
     * Clears the mapping of XML attributes from/to {@code Key}s.
     */
    public void clearAttributeMap() {
        attrToKey.clear();
        keyToAttr.clear();
    }

    /**
     * Adds the provided mappings of XML attribute names from/to
     * {@code Figure}s.
     *
     * @param name The element name
     * @param figure The figure class
     */
    public void addFigure(String name, Class<? extends Figure> figure) {
        if (!elemToFigure.containsKey(name)) {
            elemToFigure.put(name, figure);
        }
        if (!figureToElem.containsKey(figure)) {
            figureToElem.put(figure, name);
        }
    }

    /**
     * Adds a figure class to the list of {@code Figure}s which will be skipped
     * when writing the DOM.
     *
     * @param figure The figure class
     */
    public void addSkipFigure(Class<? extends Figure> figure) {
        skipFigures.add(figure);
    }

    /**
     * Adds an element to the list of elements which will be skipped when
     * reading the DOM.
     *
     * @param figure The figure class
     */
    public void addSkipElement(String elementName) {
        skipElements.add(elementName);
    }

    /**
     * Clears the mapping of XML attributes from/to {@code Key}s.
     */
    public void clearElementMap() {
        attrToKey.clear();
        keyToAttr.clear();
    }

    @Override
    public String figureToName(Figure f) throws IOException {
        if (!figureToElem.containsKey(f.getClass())) {
            if (skipFigures.contains(f.getClass())) {
                return null;
            }
            throw new IOException("no mapping for figure " + f.getClass());
        }
        return figureToElem.get(f.getClass());
    }

    @Override
    public Figure nameToFigure(String elementName) throws IOException {
        if (!elemToFigure.containsKey(elementName)) {
            if (skipElements.contains(elementName)) {
                return null;
            }
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
    public String keyToName(Figure f, Key<?> key) throws IOException {
        if (!keyToAttr.containsKey(f.getClass())) {
            throw new IOException("no mapping for figure " + f.getClass());
        }
        HashMap<Key<?>, String> keyToStr = keyToAttr.get(f.getClass());
        if (!keyToStr.containsKey(key)) {
            throw new IOException("no mapping for key " + key + " in figure "
                    + f.getClass());
        }
        return keyToStr.get(key);
    }

    @Override
    public Key<?> nameToKey(Figure f, String attributeName) throws IOException {
        if (!attrToKey.containsKey(f.getClass())) {
            throw new IOException("no mapping for figure " + f.getClass());
        }
        HashMap<String, Key<?>> strToKey = attrToKey.get(f.getClass());
        if (!strToKey.containsKey(attributeName)) {
            throw new IOException("no mapping for attribute " + attributeName
                    + " in figure " + f.getClass());
        }
        return strToKey.get(attributeName);
    }

    /**
     * Adds a converter.
     *
     * @param valueType A value type returned by {@code Key.getValueType();}.
     * @param converter the converter
     */
    public void addConverter(Class<?> valueType, Converter<?> converter) {
        addConverter(valueType.getName(), converter);

    }

    /**
     * Adds a converter.
     *
     * @param fullValueType A value type returned by
     * {@code Key.getFullValueType();}.
     * @param converter the converter
     */
    public void addConverter(String fullValueType, Converter<?> converter) {
        valueToXML.put(fullValueType, converter);
        valueFromXML.put(fullValueType, converter);
    }

    @Override
    public String valueToString(Key<?> key, Object value) throws IOException {
        Converter converter = valueToXML.get(key.getFullValueType());
        if (converter == null) {
            throw new IOException("no converter for attribute type "
                    + key.getFullValueType());
        }
        return converter.toString(value);
    }

    @Override
    public Object stringToValue(Key<?> key, String string) throws IOException {
        try {
            Converter converter = valueFromXML.get(key.getFullValueType());
            if (converter == null) {
                throw new IOException("no converter for attribute type "
                        + key.getClass());
            }
            return converter.fromString(string);
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
