/* @(#)StyleablePropertyMap.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.css;

import java.util.HashMap;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlyMapWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.css.CssMetaData;
import javafx.css.StyleOrigin;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import static org.jhotdraw.beans.PropertyBean.PROPERTIES_PROPERTY;
import org.jhotdraw.collection.Key;

/**
 * Holds properties which can be styled from a CSS stylesheet.
 * <p>
 * {@code StyleablePropertyMap} consists internally of four input maps and one
 * outputReadonly map.
 * <ul>
 * <li>An input map is provided for each {@link StyleOrigin}.</li>
 * <li>The outputReadonly map contains the styled value. The style origins have
 * the precedence as defined in {@link StyleableProperty} which is
 * {@code INLINE, AUTHOR, USER, USER_AGENT}.</li>
 * </ul>
 *
 * @author werni
 */
public class StyleablePropertyMap {

    // ---
    // constant declarations
    // ---
    /**
     * The name of the "user" property.
     */
    public final String USER_PROPERTY = "user";
    /**
     * The name of the "user agent" property.
     */
    public final String USER_AGENT_PROPERTY = "userAgent";
    /**
     * The name of the "inline" property.
     */
    public final String INLINE_PROPERTY = "inline";
    /**
     * The name of the "author" property.
     */
    public final String AUTHOR_PROPERTY = "author";
    /**
     * The name of the "author" property.
     */
    public final String OUTPUT_PROPERTY = "output";
    // ---
    // field declarations
    // ---
    /**
     * Holds the user properties.
     */
    protected final ReadOnlyMapProperty<Key<?>, Object> user = new ReadOnlyMapWrapper<Key<?>, Object>(this, USER_PROPERTY, FXCollections.observableHashMap()).getReadOnlyProperty();
    /**
     * Holds the inline properties.
     */
    protected final ReadOnlyMapProperty<Key<?>, Object> inline = new ReadOnlyMapWrapper<Key<?>, Object>(this, INLINE_PROPERTY, FXCollections.observableHashMap()).getReadOnlyProperty();
    /**
     * Holds the author properties.
     */
    protected final ReadOnlyMapProperty<Key<?>, Object> author = new ReadOnlyMapWrapper<Key<?>, Object>(this, AUTHOR_PROPERTY, FXCollections.observableHashMap()).getReadOnlyProperty();
    /**
     * Holds the user agent properties.
     */
    protected final ReadOnlyMapProperty<Key<?>, Object> userAgent = new ReadOnlyMapWrapper<Key<?>, Object>(this, USER_AGENT_PROPERTY, FXCollections.observableHashMap()).getReadOnlyProperty();
    /**
     * Holds the outputReadonly properties.
     */
    protected final ObservableMap<Key<?>, Object> output = FXCollections.observableHashMap();
    /**
     * Read-only wrapper for the outputReadonly properties.
     */
    protected final ReadOnlyMapProperty<Key<?>, Object> outputReadonly = new ReadOnlyMapWrapper<Key<?>, Object>(this, OUTPUT_PROPERTY, FXCollections.unmodifiableObservableMap(output)).getReadOnlyProperty();

    /**
     * Holds the styleable properties.
     */
    protected final HashMap<Key<?>, StyleableProperty<?>> styleableProperties = new HashMap<>();

    private MapChangeListener<Key<?>, Object> inputHandler = new MapChangeListener<Key<?>, Object>() {

        @Override
        public void onChanged(MapChangeListener.Change<? extends Key<?>, ? extends Object> change) {
            updateOutput(change.getKey());
        }
    };
    private final Object bean;

    private boolean willUpdateLater;
    // ---
    // constructors
    // ---
    
    
    public StyleablePropertyMap() {
        this(null);
    }

    public StyleablePropertyMap(Object bean) {
        author.addListener(inputHandler);
        userAgent.addListener(inputHandler);
        inline.addListener(inputHandler);
        user.addListener(inputHandler);
        this.bean = bean;
    }

    // ---
    // property methods
    // ---
    /**
     * Returns an observable map of property keys and their values.
     *
     * @return the map
     */
    ReadOnlyMapProperty<Key<?>, Object> userProperties() {
        return user;
    }

    /**
     * Returns an observable map of property keys and their values.
     *
     * @return the map
     */
    ReadOnlyMapProperty<Key<?>, Object> userAgentProperties() {
        return userAgent;
    }

    /**
     * Returns an observable map of property keys and their values.
     *
     * @return the map
     */
    ReadOnlyMapProperty<Key<?>, Object> authorProperties() {
        return author;
    }

    /**
     * Returns an observable map of property keys and their values.
     *
     * @return the map
     */
    ReadOnlyMapProperty<Key<?>, Object> inlineProperties() {
        return inline;
    }

    /**
     * Returns an observable map of property keys and their values.
     * <p>
     * The map is unmodifiable.
     *
     * @return the map
     */
    ReadOnlyMapProperty<Key<?>, Object> outputProperties() {
        return outputReadonly;
    }

    // ---
    // behavior
    // ---
    
    /** Clears all properties except the user properties. */
    public void clearNonUserProperties() {
        userAgent.clear();
        inline.clear();
        author.clear();
    }
    
    private void updateOutput(Key<?> key) {
        StyleOrigin origin = getStyleOrigin(key);
        if (origin == null) {
            output.remove(key);
        } else {
            switch (origin) {
                case INLINE:
                    output.put(key, inline.get(key));
                    break;
                case AUTHOR:
System.out.println(this+" updating output k:"+key+" v:"+author.get(key));                    
                    output.put(key, author.get(key));
                    break;
                case USER:
                    output.put(key, user.get(key));
                    break;
                case USER_AGENT:
                    output.put(key, userAgent.get(key));
                    break;
                default:
                    throw new InternalError("unknown enum value " + origin);
            }
        }
    }

    /**
     * Returns the style origin of the specified value.
     */
    public StyleOrigin getStyleOrigin(Key<?> key) {
        if (inline.containsKey(key)) {
            return StyleOrigin.INLINE;
        } else if (author.containsKey(key)) {
            return StyleOrigin.AUTHOR;
        } else if (user.containsKey(key)) {
            return StyleOrigin.INLINE;
        } else if (userAgent.containsKey(key)) {
            return StyleOrigin.USER_AGENT;
        } else {
            return null;
        }
    }

    public Object getBean() {
        return bean;
    }

    public <T> StyleableProperty<T> getStyleableProperty(Key<T> key) {
        StyleableProperty<T> sp = (StyleableProperty<T>) styleableProperties.get(key);
        if (sp == null) {
            if (key instanceof StyleableKey) {
                sp = new MapStyleableProperty<>(key, ((StyleableKey) key).createCssMetaData());
            } else {
                sp = new MapStyleableProperty<>(key, null);
            }
            styleableProperties.put(key, sp);
        }
        return sp;
    }

    // ---
    // static inner classes
    // ---
    public class MapStyleableProperty<T> extends ObjectPropertyBase<T> implements StyleableProperty<T> {

        private final Key<T> key;
        private final CssMetaData metaData;

        public MapStyleableProperty(Key<T> key, CssMetaData metaData) {
            this.key = key;
            this.metaData = metaData;
        }

        @Override
        public Object getBean() {
            return StyleablePropertyMap.this.getBean();
        }

        @Override
        public String getName() {
            return key.getName();
        }

        @Override
        public CssMetaData getCssMetaData() {
            return metaData;
        }

        @Override
        public void applyStyle(StyleOrigin origin, T value) {
            if (!key.isAssignable(value)) {
                throw new ClassCastException("value is not assignable. key:"+key+" value:"+value);
            }
            if (origin == null) {
                throw new IllegalArgumentException("origin must not be null");
            } else {
                switch (origin) {
                    case INLINE:
                        inline.put(key, value);
                        break;
                    case AUTHOR:
                        author.put(key, value);
                        break;
                    case USER:
                        user.put(key, value);
                        break;
                    case USER_AGENT:
                        userAgent.put(key, value);
                        break;
                    default:
                        throw new InternalError("unknown enum value " + origin);
                }
            }
        }

        @Override
        public void set(T v) {
            applyStyle(StyleOrigin.USER, v);
        }

        @Override
        public StyleOrigin getStyleOrigin() {
            return StyleablePropertyMap.this.getStyleOrigin(key);
        }

    }
}
