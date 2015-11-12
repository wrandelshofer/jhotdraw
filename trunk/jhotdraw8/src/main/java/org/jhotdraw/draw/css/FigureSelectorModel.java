/* @(#)FigureSelectorModel.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.css;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import org.jhotdraw.collection.Key;
import org.jhotdraw.css.SelectorModel;
import org.jhotdraw.draw.Figure;
import static org.jhotdraw.draw.Figure.*;
import org.jhotdraw.styleable.SimpleParsedValue;
import org.jhotdraw.styleable.StyleableKey;

/**
 * FigureSelectorModel.
 *
 * @author Werner Randelshofer
 */
public class FigureSelectorModel implements SelectorModel<Figure> {

    private HashSet<Class<?>> mappedFigureClasses = new HashSet<>();
    private HashMap<String, StyleableKey<?>> nameToKeyMap = new HashMap<>();

    @Override
    public boolean hasId(Figure element, String id) {
        return id.equals(element.getId());
    }

    @Override
    public boolean hasType(Figure element, String type) {
        return type.equals(element.getTypeSelector());
    }

    @Override
    public boolean hasStyleClass(Figure element, String clazz) {
        return element.getStyleClass().contains(clazz);
    }

    private StyleableKey<?> findKey(Figure element, String attributeName) {
        if (!mappedFigureClasses.contains(element.getClass())) {

            for (Key<?> k : element.getSupportedKeys()) {
                if (k instanceof StyleableKey) {
                    StyleableKey<?> sk = (StyleableKey<?>) k;
                    nameToKeyMap.put(sk.getCssName(), sk);
                }
            }
        }
        return nameToKeyMap.get(attributeName);
    }

    @Override
    public boolean hasAttribute(Figure element, String attributeName) {
        StyleableKey<?> k = findKey(element, attributeName);
        return k != null && element.propertiesProperty().containsKey(k);
    }

    @Override
    public boolean attributeValueEquals(Figure element, String attributeName, String attributeValue) {
        @SuppressWarnings("unchecked")
        StyleableKey<Object> k = (StyleableKey<Object>) findKey(element, attributeName);
        if (k == null) {
            return false;
        }
        Object value = element.get(k);
        String stringValue = k.getConverter().toString(value);
        return attributeValue.equals(stringValue);
    }

    @Override
    public boolean attributeValueStartsWith(Figure element, String attributeName, String substring) {
        @SuppressWarnings("unchecked")
        StyleableKey<Object> k = (StyleableKey<Object>) findKey(element, attributeName);
        if (k == null) {
            return false;
        }
        Object value = element.get(k);
        String stringValue = k.getConverter().toString(value);
        return stringValue.startsWith(substring);
    }

    @Override
    public boolean attributeValueEndsWith(Figure element, String attributeName, String substring) {
        @SuppressWarnings("unchecked")
        StyleableKey<Object> k = (StyleableKey<Object>) findKey(element, attributeName);
        if (k == null) {
            return false;
        }
        Object value = element.get(k);
        String stringValue = k.getConverter().toString(value);
        return stringValue.endsWith(substring);
    }

    @Override
    public boolean attributeValueContains(Figure element, String attributeName, String substring) {
        @SuppressWarnings("unchecked")
        StyleableKey<Object> k = (StyleableKey<Object>) findKey(element, attributeName);
        if (k == null) {
            return false;
        }
        Object value = element.get(k);
        String stringValue = k.getConverter().toString(value);
        return stringValue.contains(substring);
    }

    @Override
    public boolean attributeValueContainsWord(Figure element, String attributeName, String word) {
        @SuppressWarnings("unchecked")
        StyleableKey<Object> k = (StyleableKey<Object>) findKey(element, attributeName);
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

}
