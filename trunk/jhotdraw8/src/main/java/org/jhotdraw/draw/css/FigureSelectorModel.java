/* @(#)FigureSelectorModel.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.css;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import org.jhotdraw.collection.CompositeMapAccessor;
import org.jhotdraw.collection.Key;
import org.jhotdraw.collection.MapAccessor;
import org.jhotdraw.css.SelectorModel;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.text.Converter;
import org.jhotdraw.text.CssStringConverter;
import org.w3c.dom.Element;
import org.jhotdraw.styleable.StyleableMapAccessor;

/**
 * FigureSelectorModel.
 *
 * @author Werner Randelshofer
 */
public class FigureSelectorModel implements SelectorModel<Figure> {

    private HashSet<Class<?>> mappedFigureClasses = new HashSet<>();
    /** Maps an attribute name to a key. */
    private HashMap<String, StyleableMapAccessor<?>> nameToKeyMap = new HashMap<>();
    /** Maps a key to an attribute name. */
    private HashMap<StyleableMapAccessor<?>, String> keyToNameMap = new HashMap<>();

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
        return new HashSet<String>(element.getStyleClass());
    }

    private StyleableMapAccessor<?> findKey(Figure element, String attributeName) {
        if (!mappedFigureClasses.contains(element.getClass())) {

            for (MapAccessor<?> k : element.getSupportedKeys()) {
                if (k instanceof StyleableMapAccessor) {
                    StyleableMapAccessor<?> sk = (StyleableMapAccessor<?>) k;
                    nameToKeyMap.put(sk.getCssName(), sk);
                }
            }
        }
        return nameToKeyMap.get(attributeName);
    }

    @Override
    public boolean hasAttribute(Figure element, String attributeName) {
        for (MapAccessor<?> key : element.getSupportedKeys()) {
            if (key.getName().equals(attributeName) && (key instanceof StyleableMapAccessor)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean attributeValueEquals(Figure element, String attributeName, String attributeValue) {
        @SuppressWarnings("unchecked")
        StyleableMapAccessor<Object> k = (StyleableMapAccessor<Object>) findKey(element, attributeName);
        if (k == null) {
            return false;
        }
        Object value = element.get(k);
        
        // FIXME get rid of special treatment for CssStringConverter
        @SuppressWarnings("unchecked")
        Converter<Object> c = k.getConverter();
        String stringValue = (((Converter<?>)c) instanceof CssStringConverter) ? (String)value:k.getConverter().toString(value);
        
        return attributeValue.equals(stringValue);
    }

    @Override
    public boolean attributeValueStartsWith(Figure element, String attributeName, String substring) {
        @SuppressWarnings("unchecked")
        StyleableMapAccessor<Object> k = (StyleableMapAccessor<Object>) findKey(element, attributeName);
        if (k == null) {
            return false;
        }
        Object value = element.get(k);
        @SuppressWarnings("unchecked")
        Converter<Object> c = k.getConverter();
        String stringValue = (((Converter<?>)c) instanceof CssStringConverter) ? (String)value:k.getConverter().toString(value);
        return stringValue.startsWith(substring);
    }

    @Override
    public boolean attributeValueEndsWith(Figure element, String attributeName, String substring) {
        @SuppressWarnings("unchecked")
        StyleableMapAccessor<Object> k = (StyleableMapAccessor<Object>) findKey(element, attributeName);
        if (k == null) {
            return false;
        }
        Object value = element.get(k);
        
        // FIXME get rid of special treatment for CssStringConverter
        @SuppressWarnings("unchecked")
        Converter<Object> c = k.getConverter();
        String stringValue = (((Converter<?>)c) instanceof CssStringConverter) ? (String)value:k.getConverter().toString(value);
        
        return stringValue.endsWith(substring);
    }

    @Override
    public boolean attributeValueContains(Figure element, String attributeName, String substring) {
        @SuppressWarnings("unchecked")
        StyleableMapAccessor<Object> k = (StyleableMapAccessor<Object>) findKey(element, attributeName);
        if (k == null) {
            return false;
        }
        Object value = element.get(k);
        
        // FIXME get rid of special treatment for CssStringConverter
        @SuppressWarnings("unchecked")
        Converter<Object> c = k.getConverter();
        String stringValue = (((Converter<?>)c) instanceof CssStringConverter) ? (String)value:k.getConverter().toString(value);
        
        return stringValue.contains(substring);
    }

    @Override
    public boolean attributeValueContainsWord(Figure element, String attributeName, String word) {
        @SuppressWarnings("unchecked")
        StyleableMapAccessor<Object> k = (StyleableMapAccessor<Object>) findKey(element, attributeName);
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
        return element.getPseudoClassStates().contains(pseudoClass);
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
        Set<String> attr = new HashSet<>();
        for (MapAccessor<?> key : element.getSupportedKeys()) {
            if (key instanceof StyleableMapAccessor) {
                StyleableMapAccessor<?> sk = (StyleableMapAccessor<?>) key;
                attr.add(sk.getCssName());
            }
        }
        return attr;
    }
    @Override
    public Set<String> getNonDecomposedAttributeNames(Figure element) {
        // FIXME use keyToName map
        Set<String> attr = new HashSet<>();
        Set<StyleableMapAccessor<?>> attrk = new HashSet<>();
        for (MapAccessor<?> key : element.getSupportedKeys()) {
            if (key instanceof StyleableMapAccessor) {
                StyleableMapAccessor<?> sk = (StyleableMapAccessor<?>) key;
                attrk.add(sk);
            }
        }
        for (MapAccessor<?> key : element.getSupportedKeys()) {
            if (key instanceof CompositeMapAccessor) {
                attrk.removeAll( ((CompositeMapAccessor) key).getSubAccessors());
            }
        }
        for (StyleableMapAccessor<?> key : attrk) {
            attr.add(key.getCssName());
        }
        return attr;
    }

    @Override
    public String getAttributeValue(Figure element, String attributeName) {
        @SuppressWarnings("unchecked")
        StyleableMapAccessor<Object> k = (StyleableMapAccessor<Object>) findKey(element, attributeName);
        if (k == null) {
            return null;
        }
        return k.getConverter().toString(element.get(k));
    }

}
