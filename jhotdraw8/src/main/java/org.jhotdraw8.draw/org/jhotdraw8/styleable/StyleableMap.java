/* @(#)StyleableMap.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.styleable;

import javafx.collections.ObservableMap;
import javafx.css.StyleOrigin;

import java.util.Map;

/**
 * A map which stores its values in an array, and which can share its keys with
 * other maps.
 * <p>
 * This map can store multiple values for each key.
 *
 * @param <K> key type
 * @param <V> value type
 * @author Werner Randelshofer
 */
public interface StyleableMap<K, V> extends ObservableMap<K, V> {

    void clearAuthorAndInlineValues();

    boolean containsKey(StyleOrigin origin, K key);

    V get(StyleOrigin origin, K key);

    default int getIdentityHash() {
        return System.identityHashCode(this);
    }

    Map<K, V> getMap(StyleOrigin origin);

    StyleOrigin getStyleOrigin(Object key);

    Map<K, V> getStyledMap();

    V put(StyleOrigin styleOrigin, K key, V value);

    void removeAll(StyleOrigin origin);

}
