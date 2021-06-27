/*
 * @(#)FigureSelectorModel.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.css;

import javafx.css.StyleOrigin;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.CompositeMapAccessor;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.collection.ReadOnlyList;
import org.jhotdraw8.collection.ReadOnlySet;
import org.jhotdraw8.css.AbstractSelectorModel;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.ListCssTokenizer;
import org.jhotdraw8.css.QualifiedName;
import org.jhotdraw8.css.StreamCssTokenizer;
import org.jhotdraw8.css.text.CssConverter;
import org.jhotdraw8.css.text.CssStringConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.ReadOnlyStyleableMapAccessor;
import org.jhotdraw8.styleable.WritableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * FigureSelectorModel.
 *
 * @author Werner Randelshofer
 */
public class FigureSelectorModel extends AbstractSelectorModel<Figure> {
    public static final String JAVA_CLASS_NAMESPACE = "http://java.net";

    private static final Logger LOGGER = Logger.getLogger(FigureSelectorModel.class.getName());
    /**
     * Maps an attribute name to a key.
     */
    private @NonNull Map<Class<?>, Map<QualifiedName, WritableStyleableMapAccessor<?>>> nameToKeyMap = new ConcurrentHashMap<>();
    private @NonNull Map<Class<?>, Map<QualifiedName, ReadOnlyStyleableMapAccessor<?>>> nameToReadableKeyMap = new ConcurrentHashMap<>();
    /**
     * Maps a key to an attribute name.
     */
    private @NonNull ConcurrentHashMap<WritableStyleableMapAccessor<?>, QualifiedName> keyToNameMap = new ConcurrentHashMap<>();
    private @NonNull ConcurrentHashMap<Class<? extends Figure>, Map<QualifiedName, List<WritableStyleableMapAccessor<Object>>>> figureToMetaMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Class<? extends Figure>, Map<QualifiedName, List<ReadOnlyStyleableMapAccessor<Object>>>> figureToReadOnlyMetaMap = new ConcurrentHashMap<>();


    @Override
    public boolean hasId(@NonNull Figure element, @NonNull String id) {
        return id.equals(element.getId());
    }

    @Override
    public String getId(@NonNull Figure element) {
        return element.getId();
    }

    @Override
    public boolean hasType(@NonNull Figure element, @Nullable String namespace, @NonNull String type) {
        if (namespace == null) {
            return type.equals(element.getTypeSelector());
        }
        if (JAVA_CLASS_NAMESPACE.equals(namespace)) {
            return element.getClass().getSimpleName().equals(type);
        }
        return false;
    }

    @Override
    public String getType(@NonNull Figure element) {
        return element.getTypeSelector();
    }

    @Override
    public boolean hasStyleClass(@NonNull Figure element, @NonNull String clazz) {
        return element.getStyleClasses().contains(clazz);
    }

    @Override
    public ReadOnlySet<String> getStyleClasses(@NonNull Figure element) {
        return element.getStyleClasses();
    }

    private WritableStyleableMapAccessor<?> findKey(@NonNull Figure element, @Nullable String namespace, String attributeName) {
        Map<QualifiedName, WritableStyleableMapAccessor<?>> mm = nameToKeyMap.computeIfAbsent(element.getClass(), k -> {
            Map<QualifiedName, WritableStyleableMapAccessor<?>> m = new LinkedHashMap<>();
            for (MapAccessor<?> kk : element.getSupportedKeys()) {
                if (kk instanceof WritableStyleableMapAccessor) {
                    WritableStyleableMapAccessor<?> sk = (WritableStyleableMapAccessor<?>) kk;
                    m.put(new QualifiedName(sk.getCssNamespace(), element.getClass() + "$" + sk.getCssName()), sk);
                    if (sk.getCssNamespace() != null) {
                        m.put(new QualifiedName(null, element.getClass() + "$" + sk.getCssName()), sk);
                    }
                }
            }
            return m;
        });
        return mm.get(new QualifiedName(namespace, element.getClass() + "$" + attributeName));
    }

    private ReadOnlyStyleableMapAccessor<?> findReadableKey(@NonNull Figure element, @Nullable String namespace, String attributeName) {
        Map<QualifiedName, ReadOnlyStyleableMapAccessor<?>> mm = nameToReadableKeyMap.computeIfAbsent(element.getClass(), k -> {
            Map<QualifiedName, ReadOnlyStyleableMapAccessor<?>> m = new LinkedHashMap<>();
            for (MapAccessor<?> kk : element.getSupportedKeys()) {
                if (kk instanceof ReadOnlyStyleableMapAccessor) {
                    ReadOnlyStyleableMapAccessor<?> sk = (ReadOnlyStyleableMapAccessor<?>) kk;
                    m.put(new QualifiedName(sk.getCssNamespace(), element.getClass() + "$" + sk.getCssName()), sk);
                    if (sk.getCssNamespace() != null) {
                        m.put(new QualifiedName(null, element.getClass() + "$" + sk.getCssName()), sk);
                    }
                }
            }
            return m;
        });
        return mm.get(new QualifiedName(namespace, element.getClass() + "$" + attributeName));
    }

    @Override
    public boolean hasAttribute(@NonNull Figure element, @Nullable String namespace, @NonNull String attributeName) {
        return getReadableMetaMap(element).containsKey(new QualifiedName(namespace, attributeName));
    }

    @Override
    public boolean attributeValueEquals(@NonNull Figure element, @Nullable String namespace, @NonNull String attributeName, @NonNull String requestedValue) {
        String stringValue = getReadOnlyAttributeValueAsString(element, namespace, attributeName);
        return Objects.equals(stringValue, requestedValue);
    }

    @Override
    public boolean attributeValueStartsWith(@NonNull Figure element, @Nullable String namespace, @NonNull String attributeName, @NonNull String substring) {
        String stringValue = getReadOnlyAttributeValueAsString(element, namespace, attributeName);
        return stringValue != null && stringValue.startsWith(substring);
    }

    protected @Nullable ReadOnlyStyleableMapAccessor<Object> getReadableAttributeAccessor(@NonNull Figure element, @Nullable String namespace, String attributeName) {
        @SuppressWarnings("unchecked")
        ReadOnlyStyleableMapAccessor<Object> k = (ReadOnlyStyleableMapAccessor<Object>) findReadableKey(element, namespace, attributeName);
        return k;
    }

    protected @Nullable String getReadOnlyAttributeValueAsString(@NonNull Figure element, @Nullable String namespace, String attributeName) {
        ReadOnlyStyleableMapAccessor<Object> k = getReadableAttributeAccessor(element, namespace, attributeName);
        if (k == null) {
            return null;
        }
        Object value = element.get(k);

        // FIXME get rid of special treatment for CssStringConverter
        Converter<Object> c = k.getCssConverter();
        String stringValue = (((Converter<?>) c) instanceof CssStringConverter) ? (String) value : k.getCssConverter().toString(value);
        return stringValue;
    }

    @Override
    public boolean attributeValueEndsWith(@NonNull Figure element, @Nullable String namespace, @NonNull String attributeName, @NonNull String substring) {
        String stringValue = getReadOnlyAttributeValueAsString(element, namespace, attributeName);
        return stringValue != null && stringValue.endsWith(substring);
    }

    @Override
    public boolean attributeValueContains(@NonNull Figure element, @Nullable String namespace, @NonNull String attributeName, @NonNull String substring) {
        String stringValue = getReadOnlyAttributeValueAsString(element, namespace, attributeName);
        return stringValue != null && stringValue.contains(substring);
    }

    @Override
    public boolean attributeValueContainsWord(@NonNull Figure element, @Nullable String namespace, @NonNull String attributeName, @NonNull String word) {
        ReadOnlyStyleableMapAccessor<Object> k = getReadableAttributeAccessor(element, namespace, attributeName);
        if (k == null) {
            return false;
        }
        Object value = element.get(k);
        if (value instanceof Collection) {
            Collection<?> c = (Collection<?>) value;
            for (Object o : c) {
                if (o != null && word.equals(o.toString())) {
                    return true;
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
        Set<Figure> fs = additionalPseudoClassStatesProperty().get(pseudoClass);
        if (fs != null && fs.contains(element)) {
            return true;
        }

        // XXX Pseudo class is not thread safe!
        // XXX we unnecessarily create many pseudo class states!
        return element.getPseudoClassStates().contains(pseudoClass);
    }

    @Override
    public Figure getParent(@NonNull Figure element) {
        return element.getParent();
    }

    @Override
    public @Nullable Figure getPreviousSibling(@NonNull Figure element) {
        if (element.getParent() == null) {
            return null;
        }
        int i = element.getParent().getChildren().indexOf(element);
        return i == 0 ? null : element.getParent().getChild(i - 1);
    }

    @Override
    public @NonNull Set<QualifiedName> getAttributeNames(@NonNull Figure element) {
        return getWritableMetaMap(element).keySet();
    }

    @Override
    public @NonNull Set<QualifiedName> getComposedAttributeNames(@NonNull Figure element) {
        Set<QualifiedName> attr = new HashSet<>();
        Set<WritableStyleableMapAccessor<?>> attrk = new HashSet<>();
        for (MapAccessor<?> key : element.getSupportedKeys()) {
            if (key instanceof WritableStyleableMapAccessor) {
                WritableStyleableMapAccessor<?> sk = (WritableStyleableMapAccessor<?>) key;
                attrk.add(sk);
            }
        }
        for (MapAccessor<?> key : element.getSupportedKeys()) {
            if (key instanceof CompositeMapAccessor) {
                attrk.removeAll(((CompositeMapAccessor<?>) key).getSubAccessors());
            }
        }
        for (WritableStyleableMapAccessor<?> key : attrk) {
            attr.add(new QualifiedName(key.getCssNamespace(), key.getCssName()));
        }
        return attr;
    }

    @Override
    public @NonNull Set<QualifiedName> getDecomposedAttributeNames(@NonNull Figure element) {
        // FIXME use keyToName map
        Set<QualifiedName> attr = new HashSet<>();
        Set<WritableStyleableMapAccessor<?>> attrk = new HashSet<>();
        for (MapAccessor<?> key : element.getSupportedKeys()) {
            if ((key instanceof WritableStyleableMapAccessor) && !(key instanceof CompositeMapAccessor)) {
                WritableStyleableMapAccessor<?> sk = (WritableStyleableMapAccessor<?>) key;
                attrk.add(sk);
            }
        }
        for (WritableStyleableMapAccessor<?> key : attrk) {
            attr.add(new QualifiedName(key.getCssNamespace(), key.getCssName()));
        }
        return attr;
    }

    @Override
    public @Nullable String getAttributeAsString(@NonNull Figure element, @Nullable String namespace, @NonNull String attributeName) {
        return getAttributeAsString(element, StyleOrigin.USER, namespace, attributeName);
    }

    public @Nullable String getAttributeAsString(@NonNull Figure element, @Nullable StyleOrigin origin, @Nullable String namespace, @NonNull String attributeName) {
        ReadOnlyStyleableMapAccessor<?> key = findReadableKey(element, namespace, attributeName);
        if (key == null) {
            return null;
        }
        boolean isInitialValue = origin != null && !element.containsMapAccessor(origin, key);
        if (isInitialValue) {
            if ((key instanceof CompositeMapAccessor)) {
                for (MapAccessor<?> subkey : ((CompositeMapAccessor<?>) key).getSubAccessors()) {
                    if (element.containsMapAccessor(origin, subkey)) {
                        isInitialValue = false;
                        break;
                    }
                }
            }
        }
        if (isInitialValue) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        @SuppressWarnings("unchecked")
        Converter<Object> converter = (Converter<Object>) key.getCssConverter();
        if (converter instanceof CssConverter) {
            CssConverter<Object> c = (CssConverter<Object>) converter;
            try {
                List<CssToken> cssTokens = c.toTokens(element.getStyled(origin, key), null);
                if (cssTokens.size() == 1) {
                    // If the value is scalar, then append it without CSS syntax adornments.
                    for (CssToken t : cssTokens) {
                        switch (t.getType()) {
                        case CssTokenType.TT_NUMBER:
                            buf.append(t.getNumericValueNonNull().toString());
                            break;
                            case CssTokenType.TT_PERCENTAGE:
                                buf.append(t.getNumericValueNonNull().toString());
                                buf.append('%');
                                break;
                            case CssTokenType.TT_DIMENSION:
                                buf.append(t.getNumericValueNonNull().toString());
                                if (t.getStringValue() != null) {
                                    buf.append(t.getStringValue());
                                }
                                break;
                            default:
                                if (t.getStringValue() != null) {
                                    buf.append(t.getStringValue());
                                }
                                break;
                        }
                    }
                } else {
                    // If the value is non-scalar, then append it with all CSS syntax adornments.
                    for (CssToken t : cssTokens) {
                        buf.append(t.toString());
                    }
                }
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Could not produce tokens for key: " + key + " value: " + element.getStyled(origin, key), e);
            }
        } else {
            buf.append(converter.toString(element.getStyled(origin, key)));// XXX THIS IS WRONG!!)
        }

        return buf.toString();
    }

    public @Nullable List<CssToken> getAttribute(@NonNull Figure element, @Nullable StyleOrigin origin, @Nullable String namespace, @NonNull String attributeName) {
        ReadOnlyStyleableMapAccessor<?> key = findReadableKey(element, namespace, attributeName);
        if (key == null) {
            return null;
        }
        boolean isInitialValue = origin != null && !element.containsMapAccessor(origin, key);
        if (isInitialValue) {
            if ((key instanceof CompositeMapAccessor)) {
                for (MapAccessor<?> subkey : ((CompositeMapAccessor<?>) key).getSubAccessors()) {
                    if (element.containsMapAccessor(origin, subkey)) {
                        isInitialValue = false;
                        break;
                    }
                }
            }
        }
        if (isInitialValue) {
            return null;
        }
        @SuppressWarnings("unchecked")
        Converter<Object> converter = (Converter<Object>) key.getCssConverter();
        if (converter instanceof CssConverter) {
            try {
                return ((CssConverter<Object>) converter).toTokens(element.getStyled(origin, key), null);
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Could not produce tokens for key: " + key + " value: " + element.getStyled(origin, key), e);
                return null;
            }
        } else {
            try {
                CssTokenizer tt = new StreamCssTokenizer(converter.toString(element.getStyled(origin, key)));
                return tt.toTokenList();
            } catch (IOException e) {
                throw new RuntimeException("unexpected exception", e);
            }
        }
    }

    public @Nullable Converter<?> getConverter(@NonNull Figure element, @Nullable String namespace, String attributeName) {
        WritableStyleableMapAccessor<?> k = findKey(element, namespace, attributeName);
        return k == null ? null : k.getCssConverter();
    }

    public @Nullable WritableStyleableMapAccessor<?> getAccessor(@NonNull Figure element, @Nullable String namespace, String attributeName) {
        return findKey(element, namespace, attributeName);
    }

    private Map<QualifiedName, List<WritableStyleableMapAccessor<Object>>> getWritableMetaMap(@NonNull Figure elem) {
        return figureToMetaMap.computeIfAbsent(elem.getClass(), klass -> {
            Map<QualifiedName, List<WritableStyleableMapAccessor<Object>>> metaMap = new HashMap<>();

            Function<QualifiedName, List<WritableStyleableMapAccessor<Object>>> arrayListSupplier = key -> new ArrayList<>();
            for (MapAccessor<?> k : elem.getSupportedKeys()) {
                if (k instanceof WritableStyleableMapAccessor) {
                    @SuppressWarnings("unchecked")
                    WritableStyleableMapAccessor<Object> sk = (WritableStyleableMapAccessor<Object>) k;
                    metaMap.computeIfAbsent(new QualifiedName(sk.getCssNamespace(), sk.getCssName()), arrayListSupplier).add(sk);
                    if (sk.getCssNamespace() != null) {
                        // all names can be accessed without specificying a namespace
                        metaMap.computeIfAbsent(new QualifiedName(null, sk.getCssName()), arrayListSupplier).add(sk);
                    }
                }
            }

            return metaMap;
        });
    }

    private Map<QualifiedName, List<ReadOnlyStyleableMapAccessor<Object>>> getReadableMetaMap(@NonNull Figure elem) {
        return figureToReadOnlyMetaMap.computeIfAbsent(elem.getClass(), klass -> {
            Map<QualifiedName, List<ReadOnlyStyleableMapAccessor<Object>>> metaMap = new HashMap<>();

            Function<QualifiedName, List<ReadOnlyStyleableMapAccessor<Object>>> arrayListSupplier = key -> new ArrayList<>();
            for (MapAccessor<?> k : elem.getSupportedKeys()) {
                if (k instanceof ReadOnlyStyleableMapAccessor) {
                    @SuppressWarnings("unchecked")
                    ReadOnlyStyleableMapAccessor<Object> sk = (ReadOnlyStyleableMapAccessor<Object>) k;
                    metaMap.computeIfAbsent(new QualifiedName(sk.getCssNamespace(), sk.getCssName()), arrayListSupplier).add(sk);
                    if (sk.getCssNamespace() != null) {
                        // all names can be accessed without specificying a namespace
                        metaMap.computeIfAbsent(new QualifiedName(null, sk.getCssName()), arrayListSupplier).add(sk);
                    }
                }
            }

            return metaMap;
        });
    }

    @Override
    public void setAttribute(@NonNull Figure elem, @NonNull StyleOrigin origin, @Nullable String namespace, @NonNull String name, @Nullable ReadOnlyList<CssToken> value)
            throws ParseException {
        Map<QualifiedName, List<WritableStyleableMapAccessor<Object>>> metaMap = getWritableMetaMap(elem);

        List<WritableStyleableMapAccessor<Object>> ks = metaMap.get(new QualifiedName(namespace, name));
        if (ks != null) {
            for (WritableStyleableMapAccessor<Object> k : ks) {
                if (value == null || isInitial(value)) {
                    elem.remove(origin, k);
                } else {
                    @SuppressWarnings("unchecked")
                    Converter<Object> converter = k.getCssConverter();
                    Object convertedValue;
                    try {
                        if (converter instanceof CssConverter) {
                            convertedValue = ((CssConverter<Object>) converter).parse(new ListCssTokenizer(value), null);
                        } else {
                            convertedValue = converter.fromString(value.stream().map(CssToken::fromToken).collect(Collectors.joining()));
                        }
                        elem.setStyled(origin, k, intern(convertedValue));
                    } catch (Throwable ex) {
                        LOGGER.log(Level.WARNING, "error setting attribute " + name + " with tokens " + value.toString(), ex);
                    }
                }
            }
        }
    }

    private final Map<Object, Object> inlinedValues = new ConcurrentHashMap<>();

    private Object intern(Object convertedValue) {
        return convertedValue == null ? null : inlinedValues.computeIfAbsent(convertedValue, k -> convertedValue);
    }

    /**
     * XXX All selector models must treat the keywords "initial","inherit","revert","unset".
     *
     * @param value the token
     * @return true if the value is "initial".
     */
    private boolean isInitial(@Nullable ReadOnlyList<CssToken> value) {
        if (value != null) {
            boolean isInitial = false;
            Loop:
            for (CssToken token : value) {

                switch (token.getType()) {
                    case CssTokenType.TT_IDENT:
                        if ("initial".equals(token.getStringValue())) {
                            isInitial = true;
                        }
                        break;
                    case CssTokenType.TT_S:
                        break;
                    default:
                        isInitial = false;
                        break Loop;
                }
            }
            return isInitial;
        }
        return false;
    }

    @Override
    public void reset(@NonNull Figure elem) {
        elem.resetStyledValues();
    }

}
