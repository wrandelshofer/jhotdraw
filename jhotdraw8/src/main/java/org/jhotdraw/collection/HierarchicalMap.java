/* @(#)HierarchicalMap.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.collection;

import java.util.HashMap;
import javax.annotation.Nullable;

/**
 * HierarchicalMap.
 * @author Werner Randelshofer
 */
public class HierarchicalMap<K,V> extends HashMap<K, V> {

    @Nullable
    private HierarchicalMap<K,V> parent;

    public void setParent(@Nullable HierarchicalMap<K,V> newValue) {
        parent = newValue;
    }

    public @Nullable
    HierarchicalMap<K,V> getParent() {
        return parent;
    }

    /** Returns the action from this map or from its parent.
     * @param key the key
     * @return the value or null */
    public @Nullable
    V getOrParent(K key) {
        if (containsKey(key)) {
            return get(key);
        } else {
            return (parent == null) ? null : parent.getOrParent(key);
        }
    }
}
