/*
 * @(#)AbstractFigureFactory.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.io;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.text.Converter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.CharBuffer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * AbstractFigureFactory.
 *
 * @author Werner Randelshofer
 */
public abstract class AbstractFigureFactory implements FigureFactory {
    private final static Logger LOGGER = Logger.getLogger(AbstractFigureFactory.class.getName());
    private final Map<Class<? extends Figure>, HashMap<String, MapAccessor<?>>> attrToKey = new HashMap<>();
    private final Map<FigureAccessorKey<?>, Object> defaultValueMap = new HashMap<>();
    private final Map<Class<? extends Figure>, HashMap<String, MapAccessor<?>>> elemToKey = new HashMap<>();
    private final Map<Class<? extends Figure>, HashSet<MapAccessor<?>>> figureAttributeKeys = new HashMap<>();
    private final Map<Class<? extends Figure>, HashSet<MapAccessor<?>>> figureNodeListKeys = new HashMap<>();
    private final Map<Class<? extends Figure>, String> figureToName = new HashMap<>();
    private final Map<Class<? extends Figure>, HashMap<MapAccessor<?>, String>> keyToAttr = new HashMap<>();
    private final Map<Class<? extends Figure>, HashMap<MapAccessor<?>, String>> keyToElem = new HashMap<>();
    private final Map<MapAccessor<?>, Converter<?>> keyValueFromXML = new HashMap<>();
    private final Map<MapAccessor<?>, Converter<?>> keyValueToXML = new HashMap<>();
    private final Map<String, Supplier<Figure>> nameToFigure = new HashMap<>();
    private String objectIdAttribute = "id";
    private final Map<String, HashSet<Class<? extends Figure>>> skipAttributes = new HashMap<>();
    private final Set<String> skipElements = new HashSet<>();
    private final Set<Class<? extends Figure>> skipFigures = new HashSet<>();
    private final Map<String, Converter<?>> valueFromXML = new HashMap<>();

    private final Map<String, Converter<?>> valueToXML = new HashMap<>();
    @Nullable
    private IdFactory idFactory;

    public AbstractFigureFactory() {
        this(new SimpleFigureIdFactory());
    }

    public AbstractFigureFactory(@Nullable IdFactory idFactory) {
        Objects.requireNonNull(idFactory, "idFactory is null");
        this.idFactory = idFactory;
    }

    /**
     * Adds a converter for the specified key.
     *
     * @param <T>       the type of the value
     * @param key       the key
     * @param converter the converter
     */
    public <T> void addConverter(MapAccessor<T> key, Converter<T> converter) {
        keyValueToXML.put(key, converter);
        keyValueFromXML.put(key, converter);
    }

    /**
     * Adds a converter.
     *
     * @param <T>       the value type
     * @param valueType A value type returned by
     *                  {@code MapAccessor.getValueType();}.
     * @param converter the converter
     */
    public <T> void addConverterForType(@NonNull Class<? extends T> valueType, Converter<T> converter) {
        addConverterForType(valueType, converter, false);
    }

    public <T> void addConverterForType(@NonNull Class<? extends T> valueType, Converter<T> converter, boolean force) {
        addConverterForType(valueType.getName(), converter, force);

    }

    /**
     * Adds a converter.
     *
     * @param fullValueType A value type returned by
     *                      {@code MapAccessor.getFullValueType();}.
     * @param converter     the converter
     */
    public void addConverterForType(String fullValueType, Converter<?> converter) {
        addConverterForType(fullValueType, converter, false);
    }

    public void addConverterForType(String fullValueType, Converter<?> converter, boolean force) {
        if (!force && valueToXML.containsKey(fullValueType)) {
            throw new IllegalStateException("you already added " + fullValueType);
        }

        valueToXML.put(fullValueType, converter);
        valueFromXML.put(fullValueType, converter);
    }

    public <T> void addDefaultValue(Class<? extends Figure> figure, MapAccessor<T> acc, T value) {
        defaultValueMap.put(new FigureAccessorKey<>(figure, acc), value);
    }

    /**
     * Adds the provided mappings of XML attribute names from/to
     * {@code Figure}s.
     * <p>
     * {@code figureClass.newInstance()} is used to instantiate a figure from a
     * name.</p>
     * <p>
     * If a figure with this name has already been added, it will be replaced by this figure.
     *
     * @param name        The element name
     * @param figureClass The figure class is used both for instantiation of a
     *                    new figure and for determining the name of a figure.
     */
    public void addFigure(String name, @NonNull Class<? extends Figure> figureClass) {
        nameToFigure.put(name, () -> {
            try {
                return figureClass.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                throw new InternalError("Couldn't instantiate " + figureClass, e);
            }
        });

        figureToName.put(figureClass, name);
    }


    /**
     * Adds the provided keys to the figure.
     *
     * @param f    the figure
     * @param keys the keys
     */
    public void addFigureAttributeKeys(Class<? extends Figure> f, @NonNull Collection<MapAccessor<?>> keys) {
        for (MapAccessor<?> key : keys) {
            addKey(f, key.getName(), key);
        }
    }

    public void addFigureKeysAndNames(String figureName, @NonNull Class<? extends Figure> f) {
        addFigureKeysAndNames(figureName, f, Figure.getDeclaredAndInheritedMapAccessors(f));
    }

    public void addFigureKeysAndNames(String figureName, @NonNull Class<? extends Figure> f, @NonNull Collection<MapAccessor<?>> keys) {
        addFigure(figureName, f);
        addFigureAttributeKeys(f, keys);
        for (MapAccessor<?> key : keys) {
            addKey(f, key.getName(), key);
        }
    }

    public void addFigureKeysAndNames(Class<? extends Figure> f, @NonNull Collection<MapAccessor<?>> keys) {
        addFigureAttributeKeys(f, keys);
        for (MapAccessor<?> key : keys) {
            addKey(f, key.getName(), key);
        }
    }

    /**
     * Adds the provided mapping of XML attribute names from/to
     * {@code MapAccessor}s.
     * <p>
     * The same key can be added more than once.
     *
     * @param figure the figure
     * @param name   The attribute name
     * @param key    The key
     */
    public void addKey(Class<? extends Figure> figure, String name, MapAccessor<?> key) {
        figureAttributeKeys.computeIfAbsent(figure, k -> new HashSet<>()).add(key);

        HashMap<String, MapAccessor<?>> strToKey = attrToKey.computeIfAbsent(figure, k -> new HashMap<>());
        strToKey.putIfAbsent(name, key);

        HashMap<MapAccessor<?>, String> keyToStr = keyToAttr.computeIfAbsent(figure, k -> new HashMap<>());
        keyToStr.putIfAbsent(key, name);
    }

    /**
     * Adds the provided mapping of XML attribute names from/to
     * {@code MapAccessor}s.
     * <p>
     * The same key can be added more than once.
     *
     * @param f    The figure
     * @param keys The mapping from attribute names to keys
     */
    public void addKeys(Class<? extends Figure> f, @NonNull HashMap<String, MapAccessor<?>> keys) {
        for (Map.Entry<String, MapAccessor<?>> entry : keys.entrySet()) {
            addKey(f, entry.getKey(), entry.getValue());
        }
    }

    /**
     * Adds the provided keys to the figure.
     *
     * @param figure the figure
     * @param name   the element name
     * @param key    the keys
     */
    public void addNodeListKey(Class<? extends Figure> figure, String name, MapAccessor<?> key) {
        if (figureNodeListKeys.containsKey(figure)) {
            figureNodeListKeys.get(figure).add(key);
        } else {
            HashSet<MapAccessor<?>> hset = new HashSet<>();
            hset.add(key);
            figureNodeListKeys.put(figure, hset);
        }

        HashMap<String, MapAccessor<?>> strToKey = elemToKey.computeIfAbsent(figure, k -> new HashMap<>());
        if (!strToKey.containsKey(name)) {
            strToKey.put(name, key);
        }

        HashMap<MapAccessor<?>, String> keyToStr = keyToElem.computeIfAbsent(figure, k -> new HashMap<>());
        if (!keyToStr.containsKey(key)) {
            keyToStr.put(key, name);
        }
    }

    /**
     * Adds an attribute to the list of attributes which will be skipped when
     * reading the DOM.
     *
     * @param figure        the figure class
     * @param attributeName the attribute name
     */
    public void addSkipAttribute(Class<? extends Figure> figure, String attributeName) {
        HashSet<Class<? extends Figure>> set = skipAttributes.get(attributeName);
        if (set == null) {
            set = new HashSet<>();
            skipAttributes.put(attributeName, set);
        }
        set.add(figure);
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
     * Adds a figure class to the list of {@code Figure}s which will be skipped
     * when writing the DOM.
     *
     * @param figure The figure class
     */
    public void addSkipFigure(Class<? extends Figure> figure) {
        skipFigures.add(figure);
    }

    public void checkConverters() {
        for (HashMap<MapAccessor<?>, String> map : keyToAttr.values()) {
            for (MapAccessor<?> k : map.keySet()) {
                String fullValueType = k.getFullValueType();
                if (!k.isTransient() && !keyValueToXML.containsKey(k) && !valueToXML.containsKey(fullValueType)) {
                    LOGGER.warning(getClass() + " can not convert " + fullValueType + " to XML for key " + k + ".");
                }
            }
        }
    }

    /**
     * Clears the mapping of XML attributes from/to {@code MapAccessor}s.
     */
    public void clearAttributeMap() {
        attrToKey.clear();
        keyToAttr.clear();
    }

    /**
     * Clears the mapping of XML attributes from/to {@code MapAccessor}s.
     */
    public void clearElementMap() {
        attrToKey.clear();
        keyToAttr.clear();
    }

    @Override
    public MapAccessor<?> elementNameToKey(@NonNull Figure f, String attributeName) throws IOException {
        HashMap<String, MapAccessor<?>> strToKey = elemToKey.get(f.getClass());
        if (elemToKey.containsKey(f.getClass())) {
            strToKey = elemToKey.get(f.getClass());
        }
        if (!strToKey.containsKey(attributeName)) {
            throw new IOException("no mapping for attribute " + attributeName
                    + " in figure " + f.getClass());
        }
        return strToKey.get(attributeName);
    }

    @NonNull
    @Override
    public Set<MapAccessor<?>> figureAttributeKeys(@NonNull Figure f) {
        Set<MapAccessor<?>> keys = figureAttributeKeys.get(f.getClass());
        return keys == null ? Collections.emptySet() : keys;
    }

    @NonNull
    @Override
    public Set<MapAccessor<?>> figureNodeListKeys(@NonNull Figure f) {
        Set<MapAccessor<?>> keys = figureNodeListKeys.get(f.getClass());
        return keys == null ? Collections.emptySet() : keys;

    }

    @Nullable
    @Override
    public String figureToName(@NonNull Figure f) throws IOException {
        if (!figureToName.containsKey(f.getClass())) {
            if (skipFigures.contains(f.getClass())) {
                return null;
            }
            throw new IOException("no mapping for figure " + f.getClass());
        }
        return figureToName.get(f.getClass());
    }

    @Override
    public <T> T getDefaultValue(@NonNull Figure f, @NonNull MapAccessor<T> key) {
        FigureAccessorKey<T> k = new FigureAccessorKey<>(f.getClass(), key);
        if (defaultValueMap.containsKey(k)) {
            @SuppressWarnings("unchecked")
            T defaultValue = (T) defaultValueMap.get(k);
            return defaultValue;
        } else {
            return key.getDefaultValue();
        }
    }

    @Nullable
    public IdFactory getIdFactory() {
        return idFactory;
    }

    public void setIdFactory(IdFactory idFactory) {
        this.idFactory = idFactory;
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

    @Override
    public <T> boolean isDefaultValue(@NonNull Figure f, @NonNull MapAccessor<T> key, @Nullable T value) {
        FigureAccessorKey<T> k = new FigureAccessorKey<>(f.getClass(), key);
        T defaultValue;
        if (defaultValueMap.containsKey(k)) {
            @SuppressWarnings("unchecked")
            T suppress = defaultValue = (T) defaultValueMap.get(k);
        } else {
            defaultValue = key.getDefaultValue();
        }

        return defaultValue == null ? value == null : (value != null && defaultValue.equals(value));
    }

    @Override
    public String keyToElementName(@NonNull Figure f, MapAccessor<?> key) throws IOException {
        HashMap<MapAccessor<?>, String> keyToStr = null;
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
    public String keyToName(@NonNull Figure f, MapAccessor<?> key) throws IOException {
        HashMap<MapAccessor<?>, String> keyToStr = null;
        if (keyToAttr.containsKey(f.getClass())) {
            keyToStr = keyToAttr.get(f.getClass());
        }
        if (keyToStr == null || !keyToStr.containsKey(key)) {
            throw new IOException("no mapping for key " + key + " in figure "
                    + f.getClass());
        }
        return keyToStr.get(key);
    }

    @Nullable
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

    @Nullable
    @Override
    public MapAccessor<?> nameToKey(@NonNull Figure f, String attributeName) throws IOException {
        HashMap<String, MapAccessor<?>> strToKey = attrToKey.get(f.getClass());
        if (attrToKey.containsKey(f.getClass())) {
            strToKey = attrToKey.get(f.getClass());
        }
        if (!strToKey.containsKey(attributeName)) {
            Set<Class<? extends Figure>> set = (skipAttributes.get(attributeName));
            if (set == null || !set.contains(f.getClass())) {
                LOGGER.warning("no mapping for attribute " + attributeName
                        + " in figure " + f.getClass());
                return null;
            }
        }
        return strToKey.get(attributeName);
    }

    @NonNull
    @Override
    public <T> T nodeListToValue(@NonNull MapAccessor<T> key, @NonNull List<Node> nodeList) throws IOException {
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

    /**
     * Globally removes the specified key.
     *
     * @param key the key
     */
    public void removeKey(MapAccessor<?> key) {
        for (Map.Entry<Class<? extends Figure>, HashMap<String, MapAccessor<?>>> entry : attrToKey.entrySet()) {
            for (Map.Entry<String, MapAccessor<?>> e : new ArrayList<>(entry.getValue().entrySet())) {
                if (e.getValue() == key) {
                    entry.getValue().remove(e.getKey());
                }
            }
        }
        for (Map.Entry<Class<? extends Figure>, HashMap<MapAccessor<?>, String>> entry : keyToAttr.entrySet()) {
            entry.getValue().remove(key);
        }
        for (Map.Entry<Class<? extends Figure>, HashMap<String, MapAccessor<?>>> entry : elemToKey.entrySet()) {
            for (Map.Entry<String, MapAccessor<?>> e : new ArrayList<>(entry.getValue().entrySet())) {
                if (e.getValue() == key) {
                    entry.getValue().remove(e.getKey());
                }
            }
        }
        for (Map.Entry<Class<? extends Figure>, HashMap<MapAccessor<?>, String>> entry : keyToElem.entrySet()) {
            entry.getValue().remove(key);
        }
        for (Map.Entry<Class<? extends Figure>, HashSet<MapAccessor<?>>> entry : figureAttributeKeys.entrySet()) {
            entry.getValue().remove(key);

        }
        for (Map.Entry<Class<? extends Figure>, HashSet<MapAccessor<?>>> entry : figureNodeListKeys.entrySet()) {
            entry.getValue().remove(key);
        }
    }

    @Override
    public <T> T stringToValue(@NonNull MapAccessor<T> key, @NonNull String string) throws IOException {
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
            return converter.fromString(CharBuffer.wrap(string), idFactory);
        } catch (ParseException ex) {
            throw new IOException(ex + "\nstring: \"" + string + "\"", ex);
        }
    }

    @NonNull
    @Override
    public List<Node> valueToNodeList(@NonNull MapAccessor<?> key, Object value, @NonNull Document document) throws IOException {
        if (key.getValueType() == String.class) {
            Text node = document.createTextNode((String) value);
            List<Node> list = new ArrayList<>();
            list.add(node);
            return list;
        } else {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @NonNull
    @Override
    public <T> String valueToString(@NonNull MapAccessor<T> key, T value) throws IOException {

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
        converter.toString(builder, idFactory, value);
        return builder.toString();
    }

    private static class FigureAccessorKey<T> {

        private final Class<? extends Figure> figure;
        private final MapAccessor<T> acc;

        public FigureAccessorKey(Class<? extends Figure> figure, MapAccessor<T> acc) {
            this.figure = figure;
            this.acc = acc;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            return hash;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }

            final FigureAccessorKey<?> other = (FigureAccessorKey<?>) obj;
            if (!Objects.equals(this.figure, other.figure)) {
                return false;
            }
            return Objects.equals(this.acc, other.acc);
        }

    }

}
