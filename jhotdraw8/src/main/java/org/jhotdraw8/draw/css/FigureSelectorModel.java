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
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.css.PseudoClass;
import javafx.css.StyleOrigin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
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
 * @version $Id$
 */
public class FigureSelectorModel implements SelectorModel<Figure> {

    @NonNull
    private HashSet<Class<?>> mappedFigureClasses = new HashSet<>();
    /**
     * Maps an attribute name to a key.
     */
    @NonNull
    private HashMap<String, WriteableStyleableMapAccessor<?>> nameToKeyMap = new HashMap<>();
    @NonNull
    private HashMap<String, ReadOnlyStyleableMapAccessor<?>> nameToReadOnlyKeyMap = new HashMap<>();
    /**
     * Maps a key to an attribute name.
     */
    @NonNull
    private HashMap<WriteableStyleableMapAccessor<?>, String> keyToNameMap = new HashMap<>();

    private final MapProperty<String, Set<Figure>> additionalPseudoClassStates = new SimpleMapProperty<>(FXCollections.observableHashMap());

    @NonNull
    public MapProperty<String, Set<Figure>> additionalPseudoClassStatesProperty() {
        return additionalPseudoClassStates;
    }

    @Override
    public boolean hasId(@NonNull Figure element, @NonNull String id) {
        return id.equals(element.getId());
    }

    @Override
    public String getId(@NonNull Figure element) {
        return element.getId();
    }

    @Override
    public boolean hasType(@NonNull Figure element, @NonNull String type) {
        return type.equals(element.getTypeSelector());
    }

    @Override
    public String getType(@NonNull Figure element) {
        return element.getTypeSelector();
    }

    @Override
    public boolean hasStyleClass(@NonNull Figure element, String clazz) {
        return element.getStyleClass().contains(clazz);
    }

    @NonNull
    @Override
    public Set<String> getStyleClasses(@NonNull Figure element) {
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
    public boolean hasAttribute(@NonNull Figure element, String attributeName) {
        for (MapAccessor<?> key : element.getSupportedKeys()) {
            if (key.getName().equals(attributeName) && (key instanceof WriteableStyleableMapAccessor)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean attributeValueEquals(@NonNull Figure element, String attributeName, String requestedValue) {
        String stringValue = getReadOnlyAttributeValueAsString(element, attributeName);
        return Objects.equals(stringValue, requestedValue);
    }

    @Override
    public boolean attributeValueStartsWith(@NonNull Figure element, String attributeName, @NonNull String substring) {
        String stringValue = getReadOnlyAttributeValueAsString(element, attributeName);
        return stringValue != null && stringValue.startsWith(substring);
    }

    @NonNull
    protected ReadOnlyStyleableMapAccessor<Object> getReadOnlyAttributeAccessor(@NonNull Figure element, String attributeName) {
        @SuppressWarnings("unchecked")
        ReadOnlyStyleableMapAccessor<Object> k = (ReadOnlyStyleableMapAccessor<Object>) findReadOnlyKey(element, attributeName);
        return k;
    }

    @Nullable
    protected String getReadOnlyAttributeValueAsString(@NonNull Figure element, String attributeName) {
        ReadOnlyStyleableMapAccessor<Object> k = getReadOnlyAttributeAccessor(element, attributeName);
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
    public boolean attributeValueEndsWith(@NonNull Figure element, String attributeName, @NonNull String substring) {
        String stringValue = getReadOnlyAttributeValueAsString(element, attributeName);
        return stringValue != null && stringValue.endsWith(substring);
    }

    @Override
    public boolean attributeValueContains(@NonNull Figure element, String attributeName, @NonNull String substring) {
        String stringValue = getReadOnlyAttributeValueAsString(element, attributeName);
        return stringValue != null && stringValue.contains(substring);
    }

    @Override
    public boolean attributeValueContainsWord(@NonNull Figure element, String attributeName, @NonNull String word) {
        ReadOnlyStyleableMapAccessor<Object> k = getReadOnlyAttributeAccessor(element, attributeName);
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
    public boolean hasPseudoClass(@NonNull Figure element, @NonNull String pseudoClass) {
        Set<Figure> fs = additionalPseudoClassStates.get(pseudoClass);
        if (fs != null && fs.contains(element)) {
            return true;
        }

        // XXX we unnecessarily create many pseudo class states!
        return element.getPseudoClassStates().contains(PseudoClass.getPseudoClass(pseudoClass));
    }

    @Override
    public Figure getParent(@NonNull Figure element) {
        return element.getParent();
    }

    @Nullable
    @Override
    public Figure getPreviousSibling(@NonNull Figure element) {
        if (element.getParent() == null) {
            return null;
        }
        int i = element.getParent().getChildren().indexOf(element);
        return i == 0 ? null : element.getParent().getChild(i - 1);
    }

    @NonNull
    @Override
    public Set<String> getAttributeNames(@NonNull Figure element) {
        // FIXME use keyToName map
        return getMetaMap(element).keySet();
    }

    @NonNull
    @Override
    public Set<String> getComposedAttributeNames(@NonNull Figure element) {
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

    @NonNull
    @Override
    public Set<String> getDecomposedAttributeNames(@NonNull Figure element) {
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

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public String getAttribute(@NonNull Figure element, String attributeName) {
        return getAttribute(element, StyleOrigin.USER, attributeName);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public String getAttribute(@NonNull Figure element, @Nullable StyleOrigin origin, String attributeName) {
        WriteableStyleableMapAccessor<Object> key = (WriteableStyleableMapAccessor<Object>) findKey(element, attributeName);
        if (key == null) {
            return null;
        }
        boolean isInitialValue = origin != null && !element.containsKey(origin, key);
        if (isInitialValue) {
            if ((key instanceof CompositeMapAccessor)) {
                for (MapAccessor<Object> subkey : (Set<MapAccessor<Object>>) ((CompositeMapAccessor) key).getSubAccessors()) {
                    // FIXME should recurse here
                    if (element.containsKey(origin, subkey)) {
                        isInitialValue = false;
                        break;
                    }
                }
            }
        }
        if (isInitialValue) {
            return INITIAL_VALUE_KEYWORD;
        }
        return key.getConverter().toString(element.getStyled(origin, key));
    }

    @Nullable
    public Converter<?> getConverter(@NonNull Figure element, String attributeName) {
        @SuppressWarnings("unchecked")
        WriteableStyleableMapAccessor<Object> k = (WriteableStyleableMapAccessor<Object>) findKey(element, attributeName);
        return k == null ? null : k.getConverter();
    }
    @NonNull
    private Map<Class<? extends Figure>, Map<String, WriteableStyleableMapAccessor<Object>>> figureToMetaMap = new HashMap<>();

    private Map<String, WriteableStyleableMapAccessor<Object>> getMetaMap(Figure elem) {

        Map<String, WriteableStyleableMapAccessor<Object>> metaMap = figureToMetaMap.get(elem.getClass());
        if (metaMap == null) {
            metaMap = new HashMap<>();
            figureToMetaMap.put(elem.getClass(), metaMap);

            for (MapAccessor<?> k : elem.getSupportedKeys()) {
                if (k instanceof WriteableStyleableMapAccessor) {
                    @SuppressWarnings("unchecked")
                    WriteableStyleableMapAccessor<Object> sk = (WriteableStyleableMapAccessor<Object>) k;
                    metaMap.put(sk.getCssName(), sk);
                }
            }
        }
        return metaMap;
    }

    @Override
    public void setAttribute(@NonNull Figure elem, StyleOrigin origin, String name, String value) {
        Map<String, WriteableStyleableMapAccessor<Object>> metaMap = getMetaMap(elem);

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
                } catch (@NonNull ParseException | IOException ex) {
                    //FIXME we should mark this as an error somewhere in the GUI
                    //ex.printStackTrace();
                }
            }
        }
    }
}
