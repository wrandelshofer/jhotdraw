/* @(#)HierarchicalMap.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import java.util.HashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * HierarchicalMap.
 *
 * @author Werner Randelshofer
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 */
public class HierarchicalMap<K, V> extends HashMap<K, V> {

    private final static long serialVersionUID = 1L;
    @Nullable
    private HierarchicalMap<K, V> parent = null;

    public void setParent(@Nullable HierarchicalMap<K, V> parent) {
        this.parent = parent;
    }

    @Nullable
    public HierarchicalMap<K, V> getParent() {
        return parent;
    }

    /**
     * Returns the value from this map or from its parent.
     *
     * @param key the key
     * @return the value or null
     */
    @Override @Nullable
    public V get(@Nullable Object key) {
        if (containsKey(key)) {
            return super.get(key);
        } else {
            return (parent == null) ? null : parent.get(key);
        }
    }
}
