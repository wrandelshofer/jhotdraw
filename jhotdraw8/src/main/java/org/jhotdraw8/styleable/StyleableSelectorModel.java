/* @(#)StyleableSelectorModel.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.styleable;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.css.CssMetaData;
import javafx.css.ParsedValue;
import javafx.css.PseudoClass;
import javafx.css.StyleConverter;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jhotdraw8.collection.ReadableList;
import org.jhotdraw8.css.StreamCssTokenizer;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.SelectorModel;
import org.w3c.dom.Element;

/**
 * StyleableSelectorModel.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class StyleableSelectorModel implements SelectorModel<Styleable> {

    private final MapProperty<String, Set<Element>> additionalPseudoClassStates = new SimpleMapProperty<>();

    @Nonnull
    public MapProperty<String, Set<Element>> additionalPseudoClassStatesProperty() {
        return additionalPseudoClassStates;
    }

    @Nullable
    @Override
    public String getAttributeAsString(@Nonnull Styleable element, StyleOrigin origin, @Nonnull String name) {
        if (origin == StyleOrigin.USER) {
            String attribute = getAttributeAsString(element, name);
            return attribute == null ? "" : attribute;
        } else {
            return null;
        }
    }

    @Override
    public boolean hasId(@Nonnull Styleable element, @Nonnull String id) {
        return id.equals(element.getId());
    }

    @Override
    public String getId(@Nonnull Styleable element) {
        return element.getId();
    }

    @Override
    public boolean hasType(@Nonnull Styleable element, @Nonnull String type) {
        return type.equals(element.getTypeSelector());
    }

    @Override
    public String getType(@Nonnull Styleable element) {
        return element.getTypeSelector();
    }

    @Override
    public boolean hasStyleClass(@Nonnull Styleable element, @Nonnull String clazz) {
        return element.getStyleClass().contains(clazz);
    }

    @Nonnull
    @Override
    public Set<String> getStyleClasses(@Nonnull Styleable element) {
        return new HashSet<>(element.getStyleClass());
    }

    @Override
    public boolean hasPseudoClass(@Nonnull Styleable element, @Nonnull String pseudoClass) {
        return element.getPseudoClassStates().contains(PseudoClass.getPseudoClass(pseudoClass));
    }

    @Override
    public Styleable getParent(@Nonnull Styleable element) {
        return element.getStyleableParent();
    }

    @Nullable
    @Override
    public Styleable getPreviousSibling(@Nonnull Styleable element) {
        return null;
    }

    @Override
    public boolean hasAttribute(@Nonnull Styleable element, @Nonnull String attributeName) {
        // XXX linear time!
        List<CssMetaData<? extends Styleable, ?>> list = element.getCssMetaData();
        for (CssMetaData<? extends Styleable, ?> item : list) {
            if (attributeName.equals(item.getProperty())) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    public List<CssToken> getAttribute(@Nonnull Styleable element, StyleOrigin origin, @Nonnull String attributeName) {
        List<CssMetaData<? extends Styleable, ?>> list = element.getCssMetaData();
        // XXX linear time!
        for (CssMetaData<? extends Styleable, ?> i : list) {
            @SuppressWarnings("unchecked")
            CssMetaData<Styleable, ?> item = (CssMetaData<Styleable, ?>) i;
            if (attributeName.equals(item.getProperty())) {
                Object value = item.getStyleableProperty(element).getValue();
                try {
                    return value == null ? null : new StreamCssTokenizer(value.toString()).toTokenList();
                } catch (IOException e) {
                    throw new RuntimeException("unexpected io exception", e);
                }
            }
        }
        return null;
    }

    private Set<String> getWordListAttribute(Styleable element, @Nonnull String attributeName) {
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
                    Set<String> slist = new HashSet<>();
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
    public boolean attributeValueEquals(@Nonnull Styleable element, @Nonnull String attributeName, @Nonnull String attributeValue) {
        String actualValue = getAttributeAsString(element, attributeName);
        return actualValue != null && actualValue.equals(attributeValue);
    }

    @Override
    public boolean attributeValueStartsWith(@Nonnull Styleable element, @Nonnull String attributeName, @Nonnull String substring) {
        String actualValue = getAttributeAsString(element, attributeName);
        return actualValue != null && actualValue.startsWith(substring);
    }

    @Override
    public boolean attributeValueContainsWord(@Nonnull Styleable element, @Nonnull String attributeName, @Nonnull String word) {
        Set<String> value = getWordListAttribute(element, attributeName);

        return value != null && value.contains(word);
    }

    @Override
    public boolean attributeValueEndsWith(@Nonnull Styleable element, @Nonnull String attributeName, @Nonnull String substring) {
        String actualValue = getAttributeAsString(element, attributeName);
        return actualValue != null && actualValue.endsWith(substring);
    }

    @Nonnull
    @Override
    public Set<String> getAttributeNames(@Nonnull Styleable element) {
        Set<String> attr = new HashSet<>();
        for (CssMetaData<? extends Styleable, ?> item : element.getCssMetaData()) {
            attr.add(item.getProperty());
        }
        return attr;
    }

    @Nonnull
    @Override
    public Set<String> getComposedAttributeNames(@Nonnull Styleable element) {
        // FIXME we actually can do this!
        return getAttributeNames(element);
    }

    @Nonnull
    @Override
    public Set<String> getDecomposedAttributeNames(@Nonnull Styleable element) {
        // FIXME we actually can do this!
        return getAttributeNames(element);
    }

    @Override
    public void setAttribute(@Nonnull Styleable elem, @Nonnull StyleOrigin origin, @Nonnull String name, ReadableList<CssToken> value) {
        if (value == null) {
            setAttributeAsString(elem, origin, name, null);
        } else {
            setAttributeAsString(elem, origin, name, value.stream().map(CssToken::fromToken).collect(Collectors.joining()));
        }
    }

    @Override
    public void setAttributeAsString(@Nonnull Styleable elem, @Nonnull StyleOrigin origin, @Nonnull String name, String value) {
        List<CssMetaData<? extends Styleable, ?>> metaList = elem.getCssMetaData();
        HashMap<String, CssMetaData<? extends Styleable, ?>> metaMap = new HashMap<>();
        for (CssMetaData<? extends Styleable, ?> m : metaList) {
            metaMap.put(m.getProperty(), m);
        }
        @SuppressWarnings("unchecked")
        CssMetaData<Styleable, ?> m = (CssMetaData<Styleable, ?>) metaMap.get(name);
        if (m != null && m.isSettable(elem)) {
            @SuppressWarnings("unchecked")
            StyleConverter<Object, Object> converter = (StyleConverter<Object, Object>) m.getConverter();
            ParsedValueImpl<Object, Object> parsedValue = new ParsedValueImpl<>(value, null);

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
