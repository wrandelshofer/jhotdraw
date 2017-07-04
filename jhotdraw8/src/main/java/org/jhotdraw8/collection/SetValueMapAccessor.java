/* @(#)SetValueMapAccessor.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.collection;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * SetValueMapAccessor.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 * @param <E> the element type of the set
 */
public class SetValueMapAccessor<E> implements CompositeMapAccessor<Boolean> {
private final static long serialVersionUID=1L;
    private final MapAccessor<ImmutableObservableSet<E>> setAccessor;
    private final E value;
    private boolean defaultValue;
    private final String name;
    private final boolean isTransient;

    public SetValueMapAccessor(String name, boolean isTransient, MapAccessor<ImmutableObservableSet<E>> setAccessor, E value, boolean defaultValue) {
        if (value == null) {
            throw new IllegalArgumentException("value is null");
        }
        this.setAccessor = setAccessor;
        this.value = value;
        this.defaultValue = defaultValue;
        this.name = name;
        this.isTransient = isTransient;
    }

    public SetValueMapAccessor(String name, MapAccessor<ImmutableObservableSet<E>> setAccessor, E value) {
        this(name, false, setAccessor, value, false);
    }

    @Override
    public Boolean get(Map<? super Key<?>, Object> a) {
        return setAccessor.get(a).contains(value);
    }

    @Override
    public Boolean getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String getFullValueType() {
        return Boolean.class.getName();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Set<MapAccessor<?>> getSubAccessors() {
        return Collections.singleton(setAccessor);
    }

    @Override
    public Class<Boolean> getValueType() {
        return Boolean.class;
    }

    @Override
    public List<Class<?>> getValueTypeParameters() {
        return Collections.emptyList();
    }

    @Override
    public boolean isTransient() {
        return isTransient;
    }

    @Override
    public Boolean put(Map<? super Key<?>, Object> a, Boolean value) {
        ImmutableObservableSet<E> set = setAccessor.get(a);
        boolean oldValue = set.contains(this.value);
        if (value != null && value.booleanValue()) {
            set = ImmutableObservableSet.add(set, this.value);
        } else {
            set = ImmutableObservableSet.remove(set, this.value);
        }
        setAccessor.put(a, set);
        return oldValue;
    }

    @Override
    public Boolean remove(Map<? super Key<?>, Object> a) {
        return put(a, false);
    }

}
