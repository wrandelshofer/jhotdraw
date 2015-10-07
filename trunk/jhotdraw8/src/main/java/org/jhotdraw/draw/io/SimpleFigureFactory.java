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
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private final Map<String, Supplier<Figure>> nameToFigure = new HashMap<>();
    private final Map<Class<? extends Figure>, String> figureToName = new HashMap<>();
    private final Map<String, Converter<?>> valueToXML = new HashMap<>();
    private final Map<String, Converter<?>> valueFromXML = new HashMap<>();
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
     * <p>
     * {@code figureClass.newInstance()} is used to instantiate a figure from a
     * name.</p>
     *
     * @param name The element name
     * @param figureClass The figure class is used both for instantiation of a
     * new figure and for determining the name of a figure.
     */
    public void addFigure(String name, Class<? extends Figure> figureClass) {
        if (!nameToFigure.containsKey(name)) {
            nameToFigure.put(name, () -> {
                try {
                    return figureClass.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new InternalError("Couldn't instantiate " + figureClass, e);
                }
            });
        }
        if (!figureToName.containsKey(figureClass)) {
            figureToName.put(figureClass, name);
        }
    }

    /**
     * Adds the provided mappings of XML attribute names from/to
     * {@code Figure}s.
     * <p>
     * The provided {@code figureSupplier} is used to instantiate a figure from
     * a name.</p>
     *
     * @param name The element name
     * @param figureSupplier The figure supplier is used for instantiating a
     * figure from a name.
     * @param figureClass The figure class is used for determining the name of a
     * figure.
     */
    public void addFigure(String name, Supplier<Figure> figureSupplier, Class<? extends Figure> figureClass) {
        if (!nameToFigure.containsKey(name)) {
            nameToFigure.put(name, figureSupplier);
        }
        if (!figureToName.containsKey(figureClass)) {
            figureToName.put(figureClass, name);
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
     * @param elementName the element name
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
        if (!figureToName.containsKey(f.getClass())) {
            if (skipFigures.contains(f.getClass())) {
                return null;
            }
            throw new IOException("no mapping for figure " + f.getClass());
        }
        return figureToName.get(f.getClass());
    }

    @Override
    public Figure nameToFigure(String elementName) throws IOException {
        if (!nameToFigure.containsKey(elementName)) {
            if (skipElements.contains(elementName)) {
                return null;
            }
            throw new IOException("no mapping for element " + elementName);
        }
        Supplier<Figure> supplier = nameToFigure.get(elementName);
        return supplier.get();
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
        @SuppressWarnings("unchecked")
        Converter<Object> converter = (Converter<Object>) valueToXML.get(key.getFullValueType());
        if (converter == null) {
            throw new IOException("no converter for attribute type "
                    + key.getFullValueType());
        }
        @SuppressWarnings("unchecked")
        String string = converter.toString(value);
        return string;
    }

    @Override
    public Object stringToValue(Key<?> key, String string) throws IOException {
        try {
            Converter<?> converter = valueFromXML.get(key.getFullValueType());
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
