/* @(#)SetValueMapAccessor.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * SetValueMapAccessor.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SetValueMapAccessor<E> implements CompositeMapAccessor<Boolean> {
private final static long serialVersionUID=1L;
    private final MapAccessor<ImmutableSet<E>> setAccessor;
    @Nullable
    private final E value;
    private boolean defaultValue;
    private final String name;
    private final boolean isTransient;

    public SetValueMapAccessor(String name, boolean isTransient, MapAccessor<ImmutableSet<E>> setAccessor, @Nullable E value, boolean defaultValue) {
        if (value == null) {
            throw new IllegalArgumentException("value is null");
        }
        this.setAccessor = setAccessor;
        this.value = value;
        this.defaultValue = defaultValue;
        this.name = name;
        this.isTransient = isTransient;
    }

    public SetValueMapAccessor(String name, MapAccessor<ImmutableSet<E>> setAccessor, E value) {
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

    @Nonnull
    @Override
    public Set<MapAccessor<?>> getSubAccessors() {
        return Collections.singleton(setAccessor);
    }

    @Nonnull
    @Override
    public Class<Boolean> getValueType() {
        return Boolean.class;
    }

    @Nonnull
    @Override
    public List<Class<?>> getValueTypeParameters() {
        return Collections.emptyList();
    }

    @Override
    public boolean isTransient() {
        return isTransient;
    }

    @Override
    public Boolean put(Map<? super Key<?>, Object> a, @Nullable Boolean value) {
        ImmutableSet<E> set = setAccessor.get(a);
        assert set != null;
        boolean oldValue = set.contains(this.value);
        if (value != null && value) {
            set = ImmutableSet.add(set, this.value);
        } else {
            set = ImmutableSet.remove(set, this.value);
        }
        setAccessor.put(a, set);
        return oldValue;
    }

    @Override
    public Boolean remove(Map<? super Key<?>, Object> a) {
        return put(a, false);
    }

}
