/* @(#)HierarchicalMap.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.collection;

import java.util.HashMap;
import java.util.Optional;

/**
 * HierarchicalMap.
 * @author Werner Randelshofer
 * @param <K>
 * @param <V>
 */
public class HierarchicalMap<K,V> extends HashMap<K, V> {

    private Optional<HierarchicalMap<K,V>> parent = Optional.empty();

    public void setParent(Optional<HierarchicalMap<K,V>> newValue) {
        if (newValue == null) {
            throw new IllegalArgumentException("newValue is null");
        }
        parent = newValue;
    }

    public Optional<
    HierarchicalMap<K,V>> getParent() {
        return parent;
    }

    /** Returns the action from this map or from its parent.
     * @param key the key
     * @return the value or null */
    public Optional<
    V> getOrParent(K key) {
        if (containsKey(key)) {
            return Optional.of(get(key));
        } else {
            return (parent.isPresent()) ? parent.get().getOrParent(key) : Optional.empty();
        }
    }
}
