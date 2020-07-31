/*
 * @(#)StyleableMap.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.styleable;

import javafx.collections.ObservableMap;
import javafx.css.StyleOrigin;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssDefaulting;

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
    boolean containsKey(@NonNull StyleOrigin origin, @NonNull K key);

    @Nullable V get(@NonNull StyleOrigin origin, @NonNull K key);

    @NonNull Map<K, V> getMap(@NonNull StyleOrigin origin);

    @Nullable StyleOrigin getStyleOrigin(@NonNull K key);

    /**
     * If the styleable map does not contain the specified key in
     * the specified origin, then the defaulting indicates how to obtain the
     * value.
     *
     * @param origin the style origin
     * @param key    the key
     * @return the defaulting method indicates how the value can be retrieved
     * when the key is absent, null indicates that the key is present. If the
     * key is not known to this map, then the value is {@link CssDefaulting#INHERIT}
     * for an {@link InheritableKey}, and {@link CssDefaulting#INHERIT} if not.
     */
    @Nullable CssDefaulting getDefaulting(@NonNull StyleOrigin origin, @NonNull K key);

    /**
     * Removes the specified key from the specified style origin
     * and puts the provided defaulting method for the key in place.
     *
     * @param origin   the style origin
     * @param key      the key
     * @param newValue the defaulting method to use when the key is absent
     */
    void remove(@NonNull StyleOrigin origin, @NonNull K key, @Nullable CssDefaulting newValue);

    @NonNull Map<K, V> getStyledMap();

    @Nullable V put(@NonNull StyleOrigin styleOrigin, @NonNull K key, @Nullable V value);

    void removeAll(@NonNull StyleOrigin origin);

    void resetStyledValues();
}
