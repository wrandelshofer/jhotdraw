/*
 * @(#)StyleableMap.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.styleable;

import javafx.collections.ObservableMap;
import javafx.css.StyleOrigin;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

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

    @Nullable V get(StyleOrigin origin, K key);

    default int getIdentityHash() {
        return System.identityHashCode(this);
    }

    @NonNull Map<K, V> getMap(StyleOrigin origin);

    @Nullable StyleOrigin getStyleOrigin(Object key);

    @Nullable Map<K, V> getStyledMap();

    @Nullable V put(StyleOrigin styleOrigin, K key, V value);

    void removeAll(StyleOrigin origin);

    void resetStyledValues();
}
