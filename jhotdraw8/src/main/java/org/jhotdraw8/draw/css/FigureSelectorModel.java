/* @(#)FigureSelectorModel.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.css;

import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.css.PseudoClass;
import javafx.css.StyleOrigin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jhotdraw8.collection.CompositeMapAccessor;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.collection.ReadableList;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.ListCssTokenizer;
import org.jhotdraw8.css.QualifiedName;
import org.jhotdraw8.css.StreamCssTokenizer;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.SelectorModel;
import org.jhotdraw8.css.text.CssConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.ReadableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.css.text.CssStringConverter;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;

/**
 * FigureSelectorModel.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class FigureSelectorModel implements SelectorModel<Figure> {

    private final static Logger LOGGER = Logger.getLogger(FigureSelectorModel.class.getName());
    private final MapProperty<String, Set<Figure>> additionalPseudoClassStates = new SimpleMapProperty<>(FXCollections.observableHashMap());
    @Nonnull
    private HashSet<Class<?>> mappedFigureClasses = new HashSet<>();
    /**
     * Maps an attribute name to a key.
     */
    @Nonnull
    private HashMap<QualifiedName, WriteableStyleableMapAccessor<?>> nameToKeyMap = new HashMap<>();
    @Nonnull
    private HashMap<QualifiedName, ReadableStyleableMapAccessor<?>> nameToReadOnlyKeyMap = new HashMap<>();
    /**
     * Maps a key to an attribute name.
     */
    @Nonnull
    private HashMap<WriteableStyleableMapAccessor<?>, QualifiedName> keyToNameMap = new HashMap<>();
    @Nonnull
    private Map<Class<? extends Figure>, Map<QualifiedName, WriteableStyleableMapAccessor<Object>>> figureToMetaMap = new HashMap<>();

    @Nonnull
    public MapProperty<String, Set<Figure>> additionalPseudoClassStatesProperty() {
        return additionalPseudoClassStates;
    }

    @Override
    public boolean hasId(@Nonnull Figure element, @Nonnull String id) {
        return id.equals(element.getId());
    }

    @Override
    public String getId(@Nonnull Figure element) {
        return element.getId();
    }

    @Override
    public boolean hasType(@Nonnull Figure element, @Nullable String namespace, @Nonnull String type) {
        return type.equals(element.getTypeSelector());
    }

    @Override
    public String getType(@Nonnull Figure element) {
        return element.getTypeSelector();
    }

    @Override
    public boolean hasStyleClass(@Nonnull Figure element, @Nonnull String clazz) {
        return element.getStyleClass().contains(clazz);
    }

    @Nonnull
    @Override
    public Set<String> getStyleClasses(@Nonnull Figure element) {
        Collection<String> styleClasses = element.getStyleClass();
        return (styleClasses == null) ? Collections.emptySet() : new HashSet<>(element.getStyleClass());
    }

    private void mapFigureClass(Figure element) {
        for (MapAccessor<?> k : element.getSupportedKeys()) {
            if (k instanceof WriteableStyleableMapAccessor) {
                WriteableStyleableMapAccessor<?> sk = (WriteableStyleableMapAccessor<?>) k;
                nameToKeyMap.put(new QualifiedName(sk.getCssNamespace(),element.getClass() + "$" + sk.getCssName()), sk);
            }
            if (k instanceof ReadableStyleableMapAccessor) {
                ReadableStyleableMapAccessor<?> sk = (ReadableStyleableMapAccessor<?>) k;
                nameToReadOnlyKeyMap.put(new QualifiedName(sk.getCssNamespace(),element.getClass() + "$" + sk.getCssName()), sk);
            }
        }
    }

    private WriteableStyleableMapAccessor<?> findKey(Figure element, @Nullable String namespace, String attributeName) {
        if (mappedFigureClasses.add(element.getClass())) {
            mapFigureClass(element);
        }
        WriteableStyleableMapAccessor<?> result = nameToKeyMap.get(new QualifiedName(namespace,element.getClass() + "$" + attributeName));
        return result;
    }

    private ReadableStyleableMapAccessor<?> findReadOnlyKey(Figure element, @Nullable String namespace, String attributeName) {
        if (mappedFigureClasses.add(element.getClass())) {
            mapFigureClass(element);
        }
        return nameToReadOnlyKeyMap.get(new QualifiedName(namespace,element.getClass() + "$" + attributeName));
    }

    @Override
    public boolean hasAttribute(@Nonnull Figure element, @Nullable String namespace, @Nonnull String attributeName) {
        for (MapAccessor<?> key : element.getSupportedKeys()) {
            if (key.getName().equals(attributeName) && (key instanceof WriteableStyleableMapAccessor)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean attributeValueEquals(@Nonnull Figure element, @Nullable String namespace, @Nonnull String attributeName, @Nonnull String requestedValue) {
        String stringValue = getReadOnlyAttributeValueAsString(element, namespace,attributeName);
        return Objects.equals(stringValue, requestedValue);
    }

    @Override
    public boolean attributeValueStartsWith(@Nonnull Figure element, @Nullable String namespace, @Nonnull String attributeName, @Nonnull String substring) {
        String stringValue = getReadOnlyAttributeValueAsString(element, namespace,attributeName);
        return stringValue != null && stringValue.startsWith(substring);
    }

    @Nullable
    protected ReadableStyleableMapAccessor<Object> getReadOnlyAttributeAccessor(@Nonnull Figure element, @Nullable String namespace, String attributeName) {
        @SuppressWarnings("unchecked")
        ReadableStyleableMapAccessor<Object> k = (ReadableStyleableMapAccessor<Object>) findReadOnlyKey(element,namespace, attributeName);
        return k;
    }

    @Nullable
    protected String getReadOnlyAttributeValueAsString(@Nonnull Figure element,@Nullable String namespace, String attributeName) {
        ReadableStyleableMapAccessor<Object> k = getReadOnlyAttributeAccessor(element, namespace,attributeName);
        if (k == null) {
            return null;
        }
        Object value = element.get(k);

        // FIXME get rid of special treatment for CssStringConverter
        @SuppressWarnings("unchecked")
        Converter<Object> c = k.getConverter();
        String stringValue = (((Converter<?>) c) instanceof CssStringConverter) ? (String) value : k.getConverter().toString(value);
        return stringValue;
    }

    @Override
    public boolean attributeValueEndsWith(@Nonnull Figure element, @Nullable String namespace, @Nonnull String attributeName, @Nonnull String substring) {
        String stringValue = getReadOnlyAttributeValueAsString(element, namespace,attributeName);
        return stringValue != null && stringValue.endsWith(substring);
    }

    @Override
    public boolean attributeValueContains(@Nonnull Figure element, @Nullable String namespace, @Nonnull String attributeName, @Nonnull String substring) {
        String stringValue = getReadOnlyAttributeValueAsString(element, namespace,attributeName);
        return stringValue != null && stringValue.contains(substring);
    }

    @Override
    public boolean attributeValueContainsWord(@Nonnull Figure element, @Nullable String namespace, @Nonnull String attributeName, @Nonnull String word) {
        ReadableStyleableMapAccessor<Object> k = getReadOnlyAttributeAccessor(element, namespace,attributeName);
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
        } else if (value instanceof String) {
            for (String s : ((String) value).split("\\s+")) {
                if (s.equals(word)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean hasPseudoClass(@Nonnull Figure element, @Nonnull String pseudoClass) {
        Set<Figure> fs = additionalPseudoClassStates.get(pseudoClass);
        if (fs != null && fs.contains(element)) {
            return true;
        }

        // XXX we unnecessarily create many pseudo class states!
        return element.getPseudoClassStates().contains(PseudoClass.getPseudoClass(pseudoClass));
    }

    @Override
    public Figure getParent(@Nonnull Figure element) {
        return element.getParent();
    }

    @Nullable
    @Override
    public Figure getPreviousSibling(@Nonnull Figure element) {
        if (element.getParent() == null) {
            return null;
        }
        int i = element.getParent().getChildren().indexOf(element);
        return i == 0 ? null : element.getParent().getChild(i - 1);
    }

    @Nonnull
    @Override
    public Set<QualifiedName> getAttributeNames(@Nonnull Figure element) {
        // FIXME use keyToName map
        return getMetaMap(element).keySet();
    }

    @Nonnull
    @Override
    public Set<QualifiedName> getComposedAttributeNames(@Nonnull Figure element) {
        // FIXME use keyToName map
        Set<QualifiedName> attr = new HashSet<>();
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
            attr.add(new QualifiedName(key.getCssNamespace(),key.getCssName()));
        }
        return attr;
    }

    @Nonnull
    @Override
    public Set<QualifiedName> getDecomposedAttributeNames(@Nonnull Figure element) {
        // FIXME use keyToName map
        Set<QualifiedName> attr = new HashSet<>();
        Set<WriteableStyleableMapAccessor<?>> attrk = new HashSet<>();
        for (MapAccessor<?> key : element.getSupportedKeys()) {
            if ((key instanceof WriteableStyleableMapAccessor) && !(key instanceof CompositeMapAccessor)) {
                WriteableStyleableMapAccessor<?> sk = (WriteableStyleableMapAccessor<?>) key;
                attrk.add(sk);
            }
        }
        for (WriteableStyleableMapAccessor<?> key : attrk) {
            attr.add(new QualifiedName(key.getCssNamespace(),key.getCssName()));
        }
        return attr;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public String getAttributeAsString(@Nonnull Figure element, @Nullable String namespace, @Nonnull String attributeName) {
        return getAttributeAsString(element, StyleOrigin.USER,namespace, attributeName);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public String getAttributeAsString(@Nonnull Figure element, @Nullable StyleOrigin origin, @Nullable String namespace, @Nonnull String attributeName) {
        WriteableStyleableMapAccessor<Object> key = (WriteableStyleableMapAccessor<Object>) findKey(element, namespace,attributeName);
        if (key == null) {
            return null;
        }
        boolean isInitialValue = origin != null && !element.containsKey(origin, key);
        if (isInitialValue) {
            if ((key instanceof CompositeMapAccessor)) {
                for (MapAccessor<Object> subkey : (Set<MapAccessor<Object>>) ((CompositeMapAccessor) key).getSubAccessors()) {
                    if (element.containsKey(origin, subkey)) {
                        isInitialValue = false;
                        break;
                    }
                }
            }
        }
        if (isInitialValue) {
            return null;
        }
        return key.getConverter().toString(element.getStyled(origin, key));
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public List<CssToken> getAttribute(@Nonnull Figure element, @Nullable StyleOrigin origin, @Nullable String namespace, @Nonnull String attributeName) {
        WriteableStyleableMapAccessor<Object> key = (WriteableStyleableMapAccessor<Object>) findKey(element, namespace, attributeName);
        if (key == null) {
            return null;
        }
        boolean isInitialValue = origin != null && !element.containsKey(origin, key);
        if (isInitialValue) {
            if ((key instanceof CompositeMapAccessor)) {
                for (MapAccessor<Object> subkey : (Set<MapAccessor<Object>>) ((CompositeMapAccessor) key).getSubAccessors()) {
                    if (element.containsKey(origin, subkey)) {
                        isInitialValue = false;
                        break;
                    }
                }
            }
        }
        if (isInitialValue) {
            return null;
        }
        Converter<Object> converter = key.getConverter();
        if (converter instanceof CssConverter) {
            return ((CssConverter<Object>) converter).toTokens(element.getStyled(origin, key), null);
        } else {
            try {
                CssTokenizer tt = new StreamCssTokenizer(converter.toString(element.getStyled(origin, key)));
                return tt.toTokenList();
            } catch (IOException e) {
                throw new RuntimeException("unexpected exception", e);
            }
        }
    }

    @Nullable
    public Converter<?> getConverter(@Nonnull Figure element, @Nullable String namespace, String attributeName) {
        @SuppressWarnings("unchecked")
        WriteableStyleableMapAccessor<Object> k = (WriteableStyleableMapAccessor<Object>) findKey(element, namespace,attributeName);
        return k == null ? null : k.getConverter();
    }

    private Map<QualifiedName, WriteableStyleableMapAccessor<Object>> getMetaMap(Figure elem) {

        Map<QualifiedName, WriteableStyleableMapAccessor<Object>> metaMap = figureToMetaMap.get(elem.getClass());
        if (metaMap == null) {
            metaMap = new HashMap<>();
            figureToMetaMap.put(elem.getClass(), metaMap);

            for (MapAccessor<?> k : elem.getSupportedKeys()) {
                if (k instanceof WriteableStyleableMapAccessor) {
                    @SuppressWarnings("unchecked")
                    WriteableStyleableMapAccessor<Object> sk = (WriteableStyleableMapAccessor<Object>) k;
                    metaMap.put(new QualifiedName(sk.getCssNamespace(),sk.getCssName()), sk);
                }
            }
        }
        return metaMap;
    }

    @Override
    public void setAttributeAsString(@Nonnull Figure elem, @Nonnull StyleOrigin origin, @Nullable String namespace, @Nonnull String name, @Nullable String value) {
        Map<QualifiedName, WriteableStyleableMapAccessor<Object>> metaMap = getMetaMap(elem);

        WriteableStyleableMapAccessor<Object> k = metaMap.get(new QualifiedName(namespace,name));
        if (k != null) {
            if (value == null) {
                elem.remove(origin, k);
            } else {
                @SuppressWarnings("unchecked")
                Converter<Object> converter = k.getConverter();
                Object convertedValue;
                try {
                    convertedValue = converter.fromString(value);
                    elem.setStyled(origin, k, convertedValue);
                } catch (@Nonnull ParseException | IOException ex) {
                    LOGGER.log(Level.WARNING, "error setting attribute " + name + " with tokens " + value.toString(), ex);
                }
            }
        }
    }

    @Override
    public void setAttribute(@Nonnull Figure elem, @Nonnull StyleOrigin origin, @Nullable String namespace, @Nonnull String name, @Nullable ReadableList<CssToken> value) {
        Map<QualifiedName, WriteableStyleableMapAccessor<Object>> metaMap = getMetaMap(elem);

        WriteableStyleableMapAccessor<Object> k = metaMap.get(new QualifiedName(namespace,name));
        if (k != null) {
            if (value == null) {
                elem.remove(origin, k);
            } else {
                // Ignore it tokens are bad or just whitespace and comments
                boolean ignore=true;
                for (CssToken t:value) {
                    if (t.getType()!= CssTokenType.TT_S&&t.getType()!=CssTokenType.TT_COMMENT) {
                        ignore=false;
                        break;
                    }
                }
                if (ignore||value.isEmpty())return;


                @SuppressWarnings("unchecked")
                Converter<Object> converter = k.getConverter();
                Object convertedValue;
                try {
                    if (converter instanceof CssConverter) {
                        convertedValue = ((CssConverter<Object>) converter).parse(new ListCssTokenizer(value), null);
                    } else {
                        convertedValue = converter.fromString(value.stream().map(CssToken::fromToken).collect(Collectors.joining()));
                    }
                    elem.setStyled(origin, k, convertedValue);
                } catch (@Nonnull ParseException | IOException ex) {
                    LOGGER.log(Level.WARNING, "error setting attribute " + name + " with tokens " + value.toString(), ex);
                }
            }
        }
    }

}
