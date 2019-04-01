/* @(#)StyleableSelectorModel.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.styleable;

import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.css.CssMetaData;
import javafx.css.ParsedValue;
import javafx.css.PseudoClass;
import javafx.css.StyleConverter;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ReadOnlyList;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.QualifiedName;
import org.jhotdraw8.css.SelectorModel;
import org.jhotdraw8.css.StreamCssTokenizer;
import org.w3c.dom.Element;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    public String getAttributeAsString(@Nonnull Styleable element, StyleOrigin origin, @Nullable String namespace, @Nonnull String name) {
        if (origin == StyleOrigin.USER) {
            String attribute = getAttributeAsString(element, namespace, name);
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
    public boolean hasType(@Nonnull Styleable element, @Nullable String namespace, @Nonnull String type) {
        return type.equals(element.getTypeSelector());
    }

    @Override
    public void reset(Styleable elem) {
        // do nothing
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
    public boolean hasAttribute(@Nonnull Styleable element, @Nullable String namespace, @Nonnull String attributeName) {
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
    public List<CssToken> getAttribute(@Nonnull Styleable element, StyleOrigin origin, @Nullable String namespace, @Nonnull String attributeName) {
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

    private Set<String> getWordListAttribute(Styleable element, @Nullable String namespace, @Nonnull String attributeName) {
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
    public boolean attributeValueEquals(@Nonnull Styleable element, @Nullable String namespace, @Nonnull String attributeName, @Nonnull String attributeValue) {
        String actualValue = getAttributeAsString(element, namespace, attributeName);
        return actualValue != null && actualValue.equals(attributeValue);
    }

    @Override
    public boolean attributeValueStartsWith(@Nonnull Styleable element, @Nullable String namespace, @Nonnull String attributeName, @Nonnull String substring) {
        String actualValue = getAttributeAsString(element, namespace, attributeName);
        return actualValue != null && actualValue.startsWith(substring);
    }

    @Override
    public boolean attributeValueContainsWord(@Nonnull Styleable element, @Nullable String namespace, @Nonnull String attributeName, @Nonnull String word) {
        Set<String> value = getWordListAttribute(element, namespace, attributeName);

        return value != null && value.contains(word);
    }

    @Override
    public boolean attributeValueEndsWith(@Nonnull Styleable element, @Nullable String namespace, @Nonnull String attributeName, @Nonnull String substring) {
        String actualValue = getAttributeAsString(element, namespace, attributeName);
        return actualValue != null && actualValue.endsWith(substring);
    }

    @Nonnull
    @Override
    public Set<QualifiedName> getAttributeNames(@Nonnull Styleable element) {
        Set<QualifiedName> attr = new HashSet<>();
        for (CssMetaData<? extends Styleable, ?> item : element.getCssMetaData()) {
            attr.add(new QualifiedName(null, item.getProperty()));
        }
        return attr;
    }

    @Nonnull
    @Override
    public Set<QualifiedName> getComposedAttributeNames(@Nonnull Styleable element) {
        // FIXME we actually can do this!
        return getAttributeNames(element);
    }

    @Nonnull
    @Override
    public Set<QualifiedName> getDecomposedAttributeNames(@Nonnull Styleable element) {
        // FIXME we actually can do this!
        return getAttributeNames(element);
    }

    @Override
    public void setAttribute(@Nonnull Styleable elem, @Nonnull StyleOrigin origin, @Nullable String namespace, @Nonnull String name, ReadOnlyList<CssToken> valueAsTokens) {
        String value;
        if (valueAsTokens == null) {
            value = null;
        } else {
            value = valueAsTokens.stream().map(CssToken::fromToken).collect(Collectors.joining());
        }

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
