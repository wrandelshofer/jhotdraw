/* @(#)SimpleFigureFactory.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.io;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jhotdraw.collection.Key;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.text.Converter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * SimpleFigureFactory.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleFigureFactory extends SimpleIdFactory implements FigureFactory {

    private final Map<Class<? extends Figure>, HashMap<String, Key<?>>> attrToKey = new HashMap<>();
    private final Map<Class<? extends Figure>, HashMap<Key<?>, String>> keyToAttr = new HashMap<>();
    private final Map<Class<? extends Figure>, HashMap<String, Key<?>>> elemToKey = new HashMap<>();
    private final Map<Class<? extends Figure>, HashMap<Key<?>, String>> keyToElem = new HashMap<>();
    private final Map<String, Supplier<Figure>> nameToFigure = new HashMap<>();
    private final Map<Class<? extends Figure>, String> figureToName = new HashMap<>();
    private final Map<String, Converter<?>> valueToXML = new HashMap<>();
    private final Map<String, Converter<?>> valueFromXML = new HashMap<>();
    private final Map<Key<?>, Converter<?>> keyValueToXML = new HashMap<>();
    private final Map<Key<?>, Converter<?>> keyValueFromXML = new HashMap<>();
    private final Map<Class<? extends Figure>, HashSet<Key<?>>> figureAttributeKeys = new HashMap<>();
    private final Map<Class<? extends Figure>, HashSet<Key<?>>> figureNodeListKeys = new HashMap<>();
    private final Set<Class<? extends Figure>> skipFigures = new HashSet<>();
    private final Set<String> skipElements = new HashSet<>();
    private final Map<String, HashSet<Class<? extends Figure>>> skipAttributes = new HashMap<>();
    private String objectIdAttribute = "oid";

    public SimpleFigureFactory() {
    }

    /**
     * Adds the provided keys to the figure.
     *
     * @param f the figure
     * @param keys the keys
     */
    public void addFigureAttributeKeys(Class<? extends Figure> f, Collection<Key<?>> keys) {
        for (Key<?> key : keys) {
            addKey(f, key.getName(), key);
        }
    }

    /**
     * Adds the provided keys to the figure.
     *
     * @param figure the figure
     * @param name the element name
     * @param key the keys
     */
    public void addNodeListKey(Class<? extends Figure> figure, String name, Key<?> key) {
        if (figureNodeListKeys.containsKey(figure)) {
            figureNodeListKeys.get(figure).add(key);
        } else {
            HashSet<Key<?>> hset = new HashSet<>();
            hset.add(key);
            figureNodeListKeys.put(figure, hset);
        }

        if (!elemToKey.containsKey(figure)) {
            elemToKey.put(figure, new HashMap<>());
        }
        HashMap<String, Key<?>> strToKey = elemToKey.get(figure);
        if (!strToKey.containsKey(name)) {
            strToKey.put(name, key);
        }

        if (!keyToElem.containsKey(figure)) {
            keyToElem.put(figure, new HashMap<>());
        }
        HashMap<Key<?>, String> keyToStr = keyToElem.get(figure);
        if (!keyToStr.containsKey(key)) {
            keyToStr.put(key, name);
        }
    }

    public void addFigureKeysAndNames(String figureName, Class<? extends Figure> f, Collection<Key<?>> keys) {
        addFigure(figureName, f);
        addFigureAttributeKeys(f, keys);
        for (Key<?> key : keys) {
            addKey(f, key.getName(), key);
        }
    }

    public void addFigureKeysAndNames(Class<? extends Figure> f, Collection<Key<?>> keys) {
        addFigureAttributeKeys(f, keys);
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
        if (figureAttributeKeys.containsKey(figure)) {
            figureAttributeKeys.get(figure).add(key);
        } else {
            HashSet<Key<?>> hset = new HashSet<>();
            hset.add(key);
            figureAttributeKeys.put(figure, hset);
        }

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
    public void addFigure(String name, Class<? extends Figure> figureClass, Supplier<Figure> figureSupplier) {
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
     * Adds an attribute to the list of attributes which will be skipped when
     * reading the DOM.
     *
     * @param attributeName the attribute name
     */
    public void addSkipAttribute(Class<? extends Figure> figure, String attributeName) {
        HashSet<Class<? extends Figure>> set = skipAttributes.get(attributeName);
        if (set == null) {
            set = new HashSet<Class<? extends Figure>>();
            skipAttributes.put(attributeName, set);
        }
        set.add(figure);
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
        HashMap<Key<?>, String> keyToStr = null;
        if (keyToAttr.containsKey(f.getClass())) {
            keyToStr = keyToAttr.get(f.getClass());
        }
        if (keyToStr == null || !keyToStr.containsKey(key)) {
            throw new IOException("no mapping for key " + key + " in figure "
                    + f.getClass());
        }
        return keyToStr.get(key);
    }

    @Override
    public Key<?> nameToKey(Figure f, String attributeName) throws IOException {
        HashMap<String, Key<?>> strToKey = attrToKey.get(f.getClass());
        if (attrToKey.containsKey(f.getClass())) {
            strToKey = attrToKey.get(f.getClass());
        }
        if (!strToKey.containsKey(attributeName)) {
            Set<Class<? extends Figure>> set = (skipAttributes.get(attributeName));
            if (set == null || !set.contains(f.getClass())) {
                throw new IOException("no mapping for attribute " + attributeName
                        + " in figure " + f.getClass());
            }
        }
        return strToKey.get(attributeName);
    }

    @Override
    public String keyToElementName(Figure f, Key<?> key) throws IOException {
        HashMap<Key<?>, String> keyToStr = null;
        if (keyToElem.containsKey(f.getClass())) {
            keyToStr = keyToElem.get(f.getClass());
        }
        if (keyToStr == null || !keyToStr.containsKey(key)) {
            throw new IOException("no mapping for key " + key + " in figure "
                    + f.getClass());
        }
        return keyToStr.get(key);
    }

    @Override
    public Key<?> elementNameToKey(Figure f, String attributeName) throws IOException {
        HashMap<String, Key<?>> strToKey = elemToKey.get(f.getClass());
        if (elemToKey.containsKey(f.getClass())) {
            strToKey = elemToKey.get(f.getClass());
        }
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
    public void addConverterForType(Class<?> valueType, Converter<?> converter) {
        SimpleFigureFactory.this.addConverterForType(valueType.getName(), converter);

    }

    /**
     * Adds a converter for the specified key.
     *
     * @param valueType A value type returned by {@code Key.getValueType();}.
     * @param converter the converter
     */
    public <T> void addConverter(Key<T> key, Converter<T> converter) {
        keyValueToXML.put(key, converter);
        keyValueFromXML.put(key, converter);
    }

    /**
     * Adds a converter.
     *
     * @param fullValueType A value type returned by
     * {@code Key.getFullValueType();}.
     * @param converter the converter
     */
    public void addConverterForType(String fullValueType, Converter<?> converter) {
        valueToXML.put(fullValueType, converter);
        valueFromXML.put(fullValueType, converter);
    }

    @Override
    public <T> String valueToString(Key<T> key, T value) throws IOException {

        Converter<T> converter;
        if (keyValueToXML.containsKey(key)) {
            @SuppressWarnings("unchecked")
            Converter<T> suppress = converter = (Converter<T>) keyValueToXML.get(key);
        } else {
            @SuppressWarnings("unchecked")
            Converter<T> suppress = converter = (Converter<T>) valueToXML.get(key.getFullValueType());
        }
        if (converter == null) {
            throw new IOException("no converter for attribute type "
                    + key.getFullValueType());
        }
        StringBuilder builder = new StringBuilder();
        converter.toString(builder, this, value);
        return builder.toString();
    }

    @Override
    public <T> T stringToValue(Key<T> key, String string) throws IOException {
        try {
            Converter<T> converter;
            if (keyValueFromXML.containsKey(key)) {
                @SuppressWarnings("unchecked")
                Converter<T> suppress = converter = (Converter<T>) keyValueFromXML.get(key);
            } else {
                @SuppressWarnings("unchecked")
                Converter<T> suppress = converter = (Converter<T>) valueFromXML.get(key.getFullValueType());
            }
            if (converter == null) {
                throw new IOException("no converter for key \"" + key + "\" with attribute type "
                        + key.getFullValueType());
            }
            return converter.fromString(CharBuffer.wrap(string), this);
        } catch (ParseException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public Set<Key<?>> figureAttributeKeys(Figure f) {
        Set<Key<?>> keys = figureAttributeKeys.get(f.getClass());
        return keys == null ? Collections.emptySet() : keys;
    }

    @Override
    public <T> T getDefaultValue(Key<T> key) {
        return key.getDefaultValue();
    }

    /**
     * This implementation returns null.
     *
     * @return null
     */
    @Override
    public String createFileComment() {
        return null;
    }

    @Override
    public List<Node> valueToNodeList(Key<?> key, Object value, Document document) throws IOException {
        if (key.getValueType() == String.class) {
            Text node = document.createTextNode((String) value);
            List<Node> list = new ArrayList<>();
            list.add(node);
            return list;
        } else {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public <T> T nodeListToValue(Key<T> key, List<Node> nodeList) throws IOException {
        if (key.getValueType() == String.class) {
            StringBuilder buf = new StringBuilder();
            for (Node node : nodeList) {
                if (node.getNodeType() == Node.TEXT_NODE) {
                    buf.append(node.getNodeValue());
                }
            }
            @SuppressWarnings("unchecked")
            T temp = (T) buf.toString();
            return temp;
        } else {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public Set<Key<?>> figureNodeListKeys(Figure f) {
        Set<Key<?>> keys = figureNodeListKeys.get(f.getClass());
        return keys == null ? Collections.emptySet() : keys;

    }

    public void checkConverters() {
        for (HashMap<Key<?>, String> map : keyToAttr.values()) {
            for (Key<?> k : map.keySet()) {
                String fullValueType = k.getFullValueType();
                if (!keyValueToXML.containsKey(k) && !valueToXML.containsKey(fullValueType)) {
                    System.err.println(this + " WARNING can not convert " + fullValueType + " to XML for key " + k + ".");
                }
            }
        }
    }

    /**
     * Returns the name of the object id attribute. The object id attribute is
     * used for referencing other objects in the XML file.
     * <p>
     * The default value is "oid".
     *
     * @return name of the object id attribute
     */
    @Override
    public String getObjectIdAttribute() {
        return objectIdAttribute;
    }

    /**
     * Sets the name of the object id attribute. The object id attribute is used
     * for referencing other objects in the XML file.
     *
     * @param newValue name of the object id attribute
     */
    public void setObjectIdAttribute(String newValue) {
        objectIdAttribute = newValue;
    }

    /**
     * Globally removes the specified key.
     */
    public void removeKey(Key<?> key) {
        for (Map.Entry<Class<? extends Figure>, HashMap<String, Key<?>>> entry : attrToKey.entrySet()) {
            for (Map.Entry<String, Key<?>> e : new ArrayList<>(entry.getValue().entrySet())) {
                if (e.getValue() == key) {
                    entry.getValue().remove(e.getKey());
                }
            }
        }
        for (Map.Entry<Class<? extends Figure>, HashMap<Key<?>, String>> entry : keyToAttr.entrySet()) {
            entry.getValue().remove(key);
        }
        for (Map.Entry<Class<? extends Figure>, HashMap<String, Key<?>>> entry : elemToKey.entrySet()) {
            for (Map.Entry<String, Key<?>> e : new ArrayList<>(entry.getValue().entrySet())) {
                if (e.getValue() == key) {
                    entry.getValue().remove(e.getKey());
                }
            }
        }
        for (Map.Entry<Class<? extends Figure>, HashMap<Key<?>, String>> entry : keyToElem.entrySet()) {
            entry.getValue().remove(key);
        }
        for (Map.Entry<Class<? extends Figure>, HashSet<Key<?>>> entry : figureAttributeKeys.entrySet()) {
            entry.getValue().remove(key);

        }
        for (Map.Entry<Class<? extends Figure>, HashSet<Key<?>>> entry : figureNodeListKeys.entrySet()) {
            entry.getValue().remove(key);
        }
    }

}
