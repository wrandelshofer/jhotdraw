/* @(#)FigureSelectorModel.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.css;

import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.css.PseudoClass;
import javafx.css.StyleOrigin;
import org.jhotdraw8.collection.CompositeMapAccessor;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.css.SelectorModel;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.ReadOnlyStyleableMapAccessor;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.CssStringConverter;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;

/**
 * FigureSelectorModel.
 *
 * @author Werner Randelshofer
 */
public class FigureSelectorModel implements SelectorModel<Figure> {

    private HashSet<Class<?>> mappedFigureClasses = new HashSet<>();
    /**
     * Maps an attribute name to a key.
     */
    private HashMap<String, WriteableStyleableMapAccessor<?>> nameToKeyMap = new HashMap<>();
    private HashMap<String, ReadOnlyStyleableMapAccessor<?>> nameToReadOnlyKeyMap = new HashMap<>();
    /**
     * Maps a key to an attribute name.
     */
    private HashMap<WriteableStyleableMapAccessor<?>, String> keyToNameMap = new HashMap<>();

    private final MapProperty<String, Set<Figure>> additionalPseudoClassStates = new SimpleMapProperty<>(FXCollections.observableHashMap());

    public MapProperty<String, Set<Figure>> additionalPseudoClassStatesProperty() {
        return additionalPseudoClassStates;
    }

    @Override
    public boolean hasId(Figure element, String id) {
        return id.equals(element.getId());
    }

    @Override
    public String getId(Figure element) {
        return element.getId();
    }

    @Override
    public boolean hasType(Figure element, String type) {
        return type.equals(element.getTypeSelector());
    }

    @Override
    public String getType(Figure element) {
        return element.getTypeSelector();
    }

    @Override
    public boolean hasStyleClass(Figure element, String clazz) {
        return element.getStyleClass().contains(clazz);
    }

    @Override
    public Set<String> getStyleClasses(Figure element) {
        Collection<String> styleClasses = element.getStyleClass();
        return (styleClasses == null) ? Collections.emptySet() : new HashSet<String>(element.getStyleClass());
    }

    private void mapFigureClass(Figure element) {
        for (MapAccessor<?> k : element.getSupportedKeys()) {
            if (k instanceof WriteableStyleableMapAccessor) {
                WriteableStyleableMapAccessor<?> sk = (WriteableStyleableMapAccessor<?>) k;
                nameToKeyMap.put(element.getClass() + "$" + sk.getCssName(), sk);
            }
            if (k instanceof ReadOnlyStyleableMapAccessor) {
                ReadOnlyStyleableMapAccessor<?> sk = (ReadOnlyStyleableMapAccessor<?>) k;
                nameToReadOnlyKeyMap.put(element.getClass() + "$" + sk.getCssName(), sk);
            }
        }
    }

    private WriteableStyleableMapAccessor<?> findKey(Figure element, String attributeName) {
        if (mappedFigureClasses.add(element.getClass())) {
            mapFigureClass(element);
        }
        WriteableStyleableMapAccessor<?> result = nameToKeyMap.get(element.getClass() + "$" + attributeName);
        return result;
    }

    private ReadOnlyStyleableMapAccessor<?> findReadOnlyKey(Figure element, String attributeName) {
        if (mappedFigureClasses.add(element.getClass())) {
            mapFigureClass(element);
        }
        return nameToReadOnlyKeyMap.get(element.getClass() + "$" + attributeName);
    }

    @Override
    public boolean hasAttribute(Figure element, String attributeName) {
        for (MapAccessor<?> key : element.getSupportedKeys()) {
            if (key.getName().equals(attributeName) && (key instanceof WriteableStyleableMapAccessor)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean attributeValueEquals(Figure element, String attributeName, String requestedValue) {
        @SuppressWarnings("unchecked")
        ReadOnlyStyleableMapAccessor<Object> k = (ReadOnlyStyleableMapAccessor<Object>) findReadOnlyKey(element, attributeName);
        if (k == null) {
            return false;
        }
        Object value = element.get(k);

        // FIXME get rid of special treatment for CssStringConverter
        @SuppressWarnings("unchecked")
        Converter<Object> c = k.getConverter();
        String stringValue = (((Converter<?>) c) instanceof CssStringConverter) ? (String) value : k.getConverter().toString(value);

        return requestedValue.equals(stringValue);
    }

    @Override
    public boolean attributeValueStartsWith(Figure element, String attributeName, String substring) {
        @SuppressWarnings("unchecked")
        ReadOnlyStyleableMapAccessor<Object> k = (ReadOnlyStyleableMapAccessor<Object>) findReadOnlyKey(element, attributeName);
        if (k == null) {
            return false;
        }
        Object value = element.get(k);
        @SuppressWarnings("unchecked")
        Converter<Object> c = k.getConverter();
        String stringValue = (((Converter<?>) c) instanceof CssStringConverter) ? (String) value : k.getConverter().toString(value);
        return stringValue.startsWith(substring);
    }

    @Override
    public boolean attributeValueEndsWith(Figure element, String attributeName, String substring) {
        @SuppressWarnings("unchecked")
        ReadOnlyStyleableMapAccessor<Object> k = (ReadOnlyStyleableMapAccessor<Object>) findReadOnlyKey(element, attributeName);
        if (k == null) {
            return false;
        }
        Object value = element.get(k);

        // FIXME get rid of special treatment for CssStringConverter
        @SuppressWarnings("unchecked")
        Converter<Object> c = k.getConverter();
        String stringValue = (((Converter<?>) c) instanceof CssStringConverter) ? (String) value : k.getConverter().toString(value);

        return stringValue.endsWith(substring);
    }

    @Override
    public boolean attributeValueContains(Figure element, String attributeName, String substring) {
        @SuppressWarnings("unchecked")
        ReadOnlyStyleableMapAccessor<Object> k = (ReadOnlyStyleableMapAccessor<Object>) findReadOnlyKey(element, attributeName);
        if (k == null) {
            return false;
        }
        Object value = element.get(k);

        // FIXME get rid of special treatment for CssStringConverter
        @SuppressWarnings("unchecked")
        Converter<Object> c = k.getConverter();
        String stringValue = (((Converter<?>) c) instanceof CssStringConverter) ? (String) value : k.getConverter().toString(value);

        return stringValue.contains(substring);
    }

    @Override
    public boolean attributeValueContainsWord(Figure element, String attributeName, String word) {
        @SuppressWarnings("unchecked")
        ReadOnlyStyleableMapAccessor<Object> k = (ReadOnlyStyleableMapAccessor<Object>) findReadOnlyKey(element, attributeName);
        if (k == null) {
            return false;
        }
        Object value = element.get(k);
        if (value instanceof Collection) {
            @SuppressWarnings("unchecked")
            Collection<Object> c = (Collection<Object>) value;
            if (k.getValueTypeParameters().equals("<String>")) {
                return c.contains(word);
            } else {
                for (Object o : c) {
                    if (o != null && word.equals(o.toString())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean hasPseudoClass(Figure element, String pseudoClass) {
        Set<Figure> fs = additionalPseudoClassStates.get(pseudoClass);
        if (fs != null && fs.contains(element)) {
            return true;
        }

        // XXX we unnecessarily create many pseudo class states!
        return element.getPseudoClassStates().contains(PseudoClass.getPseudoClass(pseudoClass));
    }

    @Override
    public Figure getParent(Figure element) {
        return element.getParent();
    }

    @Override
    public Figure getPreviousSibling(Figure element) {
        if (element.getParent() == null) {
            return null;
        }
        int i = element.getParent().getChildren().indexOf(element);
        return i == 0 ? null : element.getParent().getChild(i - 1);
    }

    @Override
    public Set<String> getAttributeNames(Figure element) {
        // FIXME use keyToName map
        return getMetaMap(element).keySet();
    }

    @Override
    public Set<String> getComposedAttributeNames(Figure element) {
        // FIXME use keyToName map
        Set<String> attr = new HashSet<>();
        Set<WriteableStyleableMapAccessor<?>> attrk = new HashSet<>();
        for (MapAccessor<?> key : element.getSupportedKeys()) {
            if (key instanceof WriteableStyleableMapAccessor) {
                WriteableStyleableMapAccessor<?> sk = (WriteableStyleableMapAccessor<?>) key;
                attrk.add(sk);
            }
        }
        for (MapAccessor<?> key : element.getSupportedKeys()) {
            if (key instanceof CompositeMapAccessor) {
                attrk.removeAll(((CompositeMapAccessor) key).getSubAccessors());
            }
        }
        for (WriteableStyleableMapAccessor<?> key : attrk) {
            attr.add(key.getCssName());
        }
        return attr;
    }

    @Override
    public Set<String> getDecomposedAttributeNames(Figure element) {
        // FIXME use keyToName map
        Set<String> attr = new HashSet<>();
        Set<WriteableStyleableMapAccessor<?>> attrk = new HashSet<>();
        for (MapAccessor<?> key : element.getSupportedKeys()) {
            if ((key instanceof WriteableStyleableMapAccessor) && !(key instanceof CompositeMapAccessor)) {
                WriteableStyleableMapAccessor<?> sk = (WriteableStyleableMapAccessor<?>) key;
                attrk.add(sk);
            }
        }
        for (WriteableStyleableMapAccessor<?> key : attrk) {
            attr.add(key.getCssName());
        }
        return attr;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getAttribute(Figure element, String attributeName) {
        WriteableStyleableMapAccessor<Object> key = (WriteableStyleableMapAccessor<Object>) findKey(element, attributeName);
        if (key == null) {
            return null;
        }
        boolean isInitialValue = !element.containsKey(StyleOrigin.USER, key);
        if ((key instanceof CompositeMapAccessor)) {
            for (MapAccessor<Object> subkey : (Set<MapAccessor<Object>>) ((CompositeMapAccessor) key).getSubAccessors()) {
                // FIXME should recurse here
                if (element.containsKey(StyleOrigin.USER, subkey)) {
                    isInitialValue = false;
                }
            }
        }
        if (isInitialValue) {
            return INITIAL_VALUE_KEYWORD;
        }
        return key.getConverter().toString(element.get(key));
    }

    public Converter<?> getConverter(Figure element, String attributeName) {
        @SuppressWarnings("unchecked")
        WriteableStyleableMapAccessor<Object> k = (WriteableStyleableMapAccessor<Object>) findKey(element, attributeName);
        return k == null ? null : k.getConverter();
    }

    private HashMap<String, WriteableStyleableMapAccessor<Object>> getMetaMap(Figure elem) {
        HashMap<String, WriteableStyleableMapAccessor<Object>> metaMap = new HashMap<>();
        for (MapAccessor<?> k : elem.getSupportedKeys()) {
            if (k instanceof WriteableStyleableMapAccessor) {
                @SuppressWarnings("unchecked")
                WriteableStyleableMapAccessor<Object> sk = (WriteableStyleableMapAccessor<Object>) k;
                metaMap.put(sk.getCssName(), sk);
            }
        }
        return metaMap;
    }

    @Override
    public void setAttribute(Figure elem, StyleOrigin origin, String name, String value) {
        HashMap<String, WriteableStyleableMapAccessor<Object>> metaMap = getMetaMap(elem);

        WriteableStyleableMapAccessor<Object> k = metaMap.get(name);
        if (k != null) {
            if (INITIAL_VALUE_KEYWORD.equals(value)) {
                elem.remove(origin, k);
            } else {
                @SuppressWarnings("unchecked")
                Converter<Object> converter = k.getConverter();
                Object convertedValue;
                try {
                    convertedValue = converter.fromString(value);
                    elem.setStyled(origin, k, convertedValue);
                } catch (ParseException | IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

}
