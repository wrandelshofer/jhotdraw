/*
 * @(#)SetValueMapAccessor.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * SetValueMapAccessor.
 *
 * @author Werner Randelshofer
 */
public class SetValueMapAccessor<E> implements CompositeMapAccessor<Boolean> {
    private final static long serialVersionUID = 1L;
    @NonNull
    private final MapAccessor<ImmutableSet<E>> setAccessor;
    @Nullable
    private final E value;
    private boolean defaultValue;
    @NonNull
    private final String name;
    private final boolean isTransient;

    public SetValueMapAccessor(@NonNull String name, boolean isTransient, @NonNull MapAccessor<ImmutableSet<E>> setAccessor, @Nullable E value, boolean defaultValue) {
        Objects.requireNonNull(value, "value is null");
        this.setAccessor = setAccessor;
        this.value = value;
        this.defaultValue = defaultValue;
        this.name = name;
        this.isTransient = isTransient;
    }

    public SetValueMapAccessor(@NonNull String name, @NonNull MapAccessor<ImmutableSet<E>> setAccessor, E value) {
        this(name, false, setAccessor, value, false);
    }

    @Override
    public Boolean get(@NonNull Map<? super Key<?>, Object> a) {
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

    @NonNull
    @Override
    public Set<MapAccessor<?>> getSubAccessors() {
        return Collections.singleton(setAccessor);
    }

    @NonNull
    @Override
    public Class<Boolean> getValueType() {
        return Boolean.class;
    }

    @NonNull
    @Override
    public Class<?> getComponentValueType() {
        return Boolean.class;
    }

    @NonNull
    @Override
    public List<Class<?>> getValueTypeParameters() {
        return Collections.emptyList();
    }

    @Override
    public boolean isTransient() {
        return isTransient;
    }

    @Override
    public Boolean put(@NonNull Map<? super Key<?>, Object> a, @Nullable Boolean value) {
        ImmutableSet<E> set = setAccessor.get(a);
        assert set != null;
        boolean oldValue = set.contains(this.value);
        if (value != null && value) {
            set = ImmutableSets.add(set, this.value);
        } else {
            set = ImmutableSets.remove(set, this.value);
        }
        setAccessor.put(a, set);
        return oldValue;
    }

    @Override
    public Boolean remove(@NonNull Map<? super Key<?>, Object> a) {
        return put(a, false);
    }

}
