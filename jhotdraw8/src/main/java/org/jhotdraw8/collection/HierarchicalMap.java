/* @(#)HierarchicalMap.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import java.util.HashMap;

/**
 * HierarchicalMap.
 *
 * @author Werner Randelshofer
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 */
public class HierarchicalMap<K, V> extends HashMap<K, V> {

    private final static long serialVersionUID = 1L;

    private HierarchicalMap<K, V> parent = null;

    public void setParent(HierarchicalMap<K, V> newValue) {
        parent = newValue;
    }

    public HierarchicalMap<K, V> getParent() {
        return parent;
    }

    /**
     * Returns the value from this map or from its parent.
     *
     * @param key the key
     * @return the value or null
     */
    @Override
    public V get(Object key) {
        if (containsKey(key)) {
            return super.get(key);
        } else {
            return (parent != null) ? parent.get(key) : null;
        }
    }
}
