/*
 * @(#)StyleableSelectorModel.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.styleable;

import javafx.css.CssMetaData;
import javafx.css.ParsedValue;
import javafx.css.PseudoClass;
import javafx.css.StyleConverter;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ReadOnlyList;
import org.jhotdraw8.css.AbstractSelectorModel;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.QualifiedName;
import org.jhotdraw8.css.StreamCssTokenizer;

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
 */
public class StyleableSelectorModel extends AbstractSelectorModel<Styleable> {


    @Nullable
    @Override
    public String getAttributeAsString(@NonNull Styleable element, StyleOrigin origin, @Nullable String namespace, @NonNull String name) {
        if (origin == StyleOrigin.USER) {
            String attribute = getAttributeAsString(element, namespace, name);
            return attribute == null ? "" : attribute;
        } else {
            return null;
        }
    }

    @Override
    public boolean hasId(@NonNull Styleable element, @NonNull String id) {
        return id.equals(element.getId());
    }

    @Override
    public String getId(@NonNull Styleable element) {
        return element.getId();
    }

    @Override
    public boolean hasType(@NonNull Styleable element, @Nullable String namespace, @NonNull String type) {
        return type.equals(element.getTypeSelector());
    }

    @Override
    public void reset(Styleable elem) {
        // do nothing
    }

    @Override
    public String getType(@NonNull Styleable element) {
        return element.getTypeSelector();
    }

    @Override
    public boolean hasStyleClass(@NonNull Styleable element, @NonNull String clazz) {
        return element.getStyleClass().contains(clazz);
    }

    @NonNull
    @Override
    public Set<String> getStyleClasses(@NonNull Styleable element) {
        return new HashSet<>(element.getStyleClass());
    }

    @Override
    public boolean hasPseudoClass(@NonNull Styleable element, @NonNull String pseudoClass) {
        return element.getPseudoClassStates().contains(PseudoClass.getPseudoClass(pseudoClass));
    }

    @Override
    public Styleable getParent(@NonNull Styleable element) {
        return element.getStyleableParent();
    }

    @Nullable
    @Override
    public Styleable getPreviousSibling(@NonNull Styleable element) {
        return null;
    }

    @Override
    public boolean hasAttribute(@NonNull Styleable element, @Nullable String namespace, @NonNull String attributeName) {
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
    public List<CssToken> getAttribute(@NonNull Styleable element, StyleOrigin origin, @Nullable String namespace, @NonNull String attributeName) {
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

    @Nullable
    private Set<String> getWordListAttribute(@NonNull Styleable element, @Nullable String namespace, @NonNull String attributeName) {
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
    public boolean attributeValueEquals(@NonNull Styleable element, @Nullable String namespace, @NonNull String attributeName, @NonNull String attributeValue) {
        String actualValue = getAttributeAsString(element, namespace, attributeName);
        return actualValue != null && actualValue.equals(attributeValue);
    }

    @Override
    public boolean attributeValueStartsWith(@NonNull Styleable element, @Nullable String namespace, @NonNull String attributeName, @NonNull String substring) {
        String actualValue = getAttributeAsString(element, namespace, attributeName);
        return actualValue != null && actualValue.startsWith(substring);
    }

    @Override
    public boolean attributeValueContainsWord(@NonNull Styleable element, @Nullable String namespace, @NonNull String attributeName, @NonNull String word) {
        Set<String> value = getWordListAttribute(element, namespace, attributeName);

        return value != null && value.contains(word);
    }

    @Override
    public boolean attributeValueEndsWith(@NonNull Styleable element, @Nullable String namespace, @NonNull String attributeName, @NonNull String substring) {
        String actualValue = getAttributeAsString(element, namespace, attributeName);
        return actualValue != null && actualValue.endsWith(substring);
    }

    @NonNull
    @Override
    public Set<QualifiedName> getAttributeNames(@NonNull Styleable element) {
        Set<QualifiedName> attr = new HashSet<>();
        for (CssMetaData<? extends Styleable, ?> item : element.getCssMetaData()) {
            attr.add(new QualifiedName(null, item.getProperty()));
        }
        return attr;
    }

    @NonNull
    @Override
    public Set<QualifiedName> getComposedAttributeNames(@NonNull Styleable element) {
        // FIXME we actually can do this!
        return getAttributeNames(element);
    }

    @NonNull
    @Override
    public Set<QualifiedName> getDecomposedAttributeNames(@NonNull Styleable element) {
        // FIXME we actually can do this!
        return getAttributeNames(element);
    }

    @Override
    public void setAttribute(@NonNull Styleable elem, @NonNull StyleOrigin origin, @Nullable String namespace, @NonNull String name, @Nullable ReadOnlyList<CssToken> valueAsTokens) {
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
