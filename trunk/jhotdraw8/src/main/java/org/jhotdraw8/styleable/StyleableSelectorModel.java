/* @(#)StyleableSelectorModel.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.styleable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.css.CssMetaData;
import javafx.css.ParsedValue;
import javafx.css.PseudoClass;
import javafx.css.StyleConverter;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import org.jhotdraw8.css.SelectorModel;
import org.jhotdraw8.draw.figure.Figure;
import org.w3c.dom.Element;

/**
 * StyleableSelectorModel.
 *
 * @author Werner Randelshofer
 * @version $Id: StyleableSelectorModel.java 1120 2016-01-15 17:37:49Z rawcoder
 * $
 */
public class StyleableSelectorModel implements SelectorModel<Styleable> {

    private final MapProperty<String, Set<Element>> additionalPseudoClassStates = new SimpleMapProperty<>();

    public MapProperty<String, Set<Element>> additionalPseudoClassStatesProperty() {
        return additionalPseudoClassStates;
    }

    @Override
    public boolean hasId(Styleable element, String id) {
        return id.equals(element.getId());
    }

    @Override
    public String getId(Styleable element) {
        return element.getId();
    }

    @Override
    public boolean hasType(Styleable element, String type) {
        return type.equals(element.getTypeSelector());
    }

    @Override
    public String getType(Styleable element) {
        return element.getTypeSelector();
    }

    @Override
    public boolean hasStyleClass(Styleable element, String clazz) {
        return element.getStyleClass().contains(clazz);
    }

    @Override
    public Set<String> getStyleClasses(Styleable element) {
        return new HashSet<String>(element.getStyleClass());
    }

    @Override
    public boolean hasPseudoClass(Styleable element, String pseudoClass) {
        return element.getPseudoClassStates().contains(PseudoClass.getPseudoClass(pseudoClass));
    }

    @Override
    public Styleable getParent(Styleable element) {
        return element.getStyleableParent();
    }

    @Override
    public Styleable getPreviousSibling(Styleable element) {
        return null;
    }

    @Override
    public boolean hasAttribute(Styleable element, String attributeName) {
        // XXX linear time!
        List<CssMetaData<? extends Styleable, ?>> list = element.getCssMetaData();
        for (CssMetaData<? extends Styleable, ?> item : list) {
            if (attributeName.equals(item.getProperty())) {
                return true;
            }
        }
        return false;
    }

    public String getAttribute(Styleable element, String attributeName) {
        List<CssMetaData<? extends Styleable, ?>> list = element.getCssMetaData();
        // XXX linear time!
        for (CssMetaData<? extends Styleable, ?> i : list) {
            @SuppressWarnings("unchecked")
            CssMetaData<Styleable, ?> item = (CssMetaData<Styleable, ?>) i;
            if (attributeName.equals(item.getProperty())) {
                Object value = item.getStyleableProperty(element).getValue();

                // FIXME this is wrong. we should be able to use the converter to 
                // convert the value from the object type to a CSS String.
                return value == null ? "" : value.toString();
            }
        }
        return null;
    }

    private Set<String> getWordListAttribute(Styleable element, String attributeName) {
        List<CssMetaData<? extends Styleable, ?>> list = element.getCssMetaData();
        // XXX linear time!
        for (CssMetaData<? extends Styleable, ?> i : list) {
            @SuppressWarnings("unchecked")
            CssMetaData<Styleable, ?> item = (CssMetaData<Styleable, ?>) i;
            if (attributeName.equals(item.getProperty())) {
                Object value = item.getStyleableProperty(element).getValue();

                if (value instanceof Collection) {
                    @SuppressWarnings("unchecked")
                    Collection<Object> olist = (Collection<Object>) value;
                    Set<String> slist = new HashSet<String>();
                    for (Object o : olist) {
                        slist.add(o.toString());
                    }
                    return slist;
                } else {
                    Set<String> slist = new HashSet<>();
                    if (value != null) {
                        String[] words = value.toString().split("\\s+");
                        for (String word : words) {
                            slist.add(word);
                        }
                    }

                    return slist;
                }
            }
        }
        return null;
    }

    @Override
    public boolean attributeValueEquals(Styleable element, String attributeName, String attributeValue) {
        String actualValue = getAttribute(element, attributeName);
        return actualValue != null && actualValue.equals(attributeValue);
    }

    @Override
    public boolean attributeValueStartsWith(Styleable element, String attributeName, String substring) {
        String actualValue = getAttribute(element, attributeName);
        return actualValue != null && actualValue.startsWith(substring);
    }

    @Override
    public boolean attributeValueContainsWord(Styleable element, String attributeName, String word) {
        Set<String> value = getWordListAttribute(element, attributeName);

        return value != null && value.contains(word);
    }

    @Override
    public boolean attributeValueEndsWith(Styleable element, String attributeName, String substring) {
        String actualValue = getAttribute(element, attributeName);
        return actualValue != null && actualValue.endsWith(substring);
    }

    @Override
    public boolean attributeValueContains(Styleable element, String attributeName, String substring) {
        String actualValue = getAttribute(element, attributeName);
        return actualValue != null && actualValue.contains(substring);
    }

    @Override
    public Set<String> getAttributeNames(Styleable element) {
        Set<String> attr = new HashSet<>();
        for (CssMetaData<? extends Styleable, ?> item : element.getCssMetaData()) {
            attr.add(item.getProperty());
        }
        return attr;
    }

    @Override
    public Set<String> getComposedAttributeNames(Styleable element) {
        // FIXME we actually can do this!
        return getAttributeNames(element);
    }
    @Override
    public Set<String> getDecomposedAttributeNames(Styleable element) {
        // FIXME we actually can do this!
        return getAttributeNames(element);
    }


    @Override
    public void setAttribute(Styleable elem, StyleOrigin origin, String name, String value) {
        List<CssMetaData<? extends Styleable, ?>> metaList = elem.getCssMetaData();
        HashMap<String, CssMetaData<? extends Styleable, ?>> metaMap = new HashMap<>();
        for (CssMetaData<? extends Styleable, ?> m : metaList) {
            metaMap.put(m.getProperty(), m);
        }
        @SuppressWarnings("unchecked")
        CssMetaData<Styleable, ?> m = (CssMetaData<Styleable, ?>) metaMap.get(name);
        if (m != null && m.isSettable(elem)) {
            @SuppressWarnings("unchecked")
            StyleConverter<String, Object> converter = (StyleConverter<String, Object>) m.getConverter();
            ParsedValueImpl<String, Object> parsedValue = new ParsedValueImpl<>(value, null);

            Object convertedValue = converter.convert(parsedValue, null);
            @SuppressWarnings("unchecked")
            StyleableProperty<Object> styleableProperty = (StyleableProperty<Object>) m.getStyleableProperty(elem);
            styleableProperty.applyStyle(origin, convertedValue);
        }

    }

    private static class ParsedValueImpl<V, T> extends ParsedValue<V, T> {

        public ParsedValueImpl(V value, StyleConverter<V, T> converter) {
            super(value, converter);
        }

    }

}
