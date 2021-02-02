/*
 * @(#)StyleableMap.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.styleable;

import javafx.collections.ObservableMap;
import javafx.css.StyleOrigin;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import java.util.Map;
import java.util.Set;

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
    <T extends K> boolean containsKey(@NonNull StyleOrigin origin, @NonNull T key);

    @Nullable V get(@NonNull StyleOrigin origin, @NonNull K key);

    @NonNull Map<K, V> getMap(@NonNull StyleOrigin origin);

    @Nullable StyleOrigin getStyleOrigin(@NonNull K key);

    /**
     * Removes the specified key from the specified style origin
     * and puts the provided defaulting method for the key in place.
     *
     * @param origin the style origin
     * @param key    the key
     */
    V removeKey(@NonNull StyleOrigin origin, @NonNull K key);

    @NonNull Map<K, V> getStyledMap();

    @Nullable V put(@NonNull StyleOrigin styleOrigin, @NonNull K key, @Nullable V value);

    void removeAll(@NonNull StyleOrigin origin);

    void resetStyledValues();

    Set<Entry<K, V>> entrySet(@NonNull StyleOrigin origin);
}
