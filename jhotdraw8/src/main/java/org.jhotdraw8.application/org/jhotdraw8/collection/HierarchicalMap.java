/*
 * @(#)HierarchicalMap.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import org.jhotdraw8.annotation.Nullable;

import java.util.HashMap;

/**
 * HierarchicalMap.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author Werner Randelshofer
 */
public class HierarchicalMap<K, V> extends HashMap<K, V> {

    private static final long serialVersionUID = 1L;
    private @Nullable HierarchicalMap<K, V> parent = null;

    public void setParent(@Nullable HierarchicalMap<K, V> parent) {
        this.parent = parent;
    }

    public @Nullable HierarchicalMap<K, V> getParent() {
        return parent;
    }

    /**
     * Returns the value from this map or from its parent.
     *
     * @param key the key
     * @return the value or null
     */
    @Override
    public @Nullable V get(@Nullable Object key) {
        if (containsKey(key)) {
            return super.get(key);
        } else {
            return (parent == null) ? null : parent.get(key);
        }
    }
}
