/* @(#)StyleableMap.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.styleable;

import java.util.Map;
import javafx.collections.ObservableMap;
import javafx.css.StyleOrigin;

/**
 * A map which stores its values in an array, and which can share its keys with
 * other maps.
 * <p>
 * This map can store multiple values for each key.
 *
 * @author Werner Randelshofer
 * @param <K> key type
 * @param <V> value type
 */
public interface StyleableMap<K, V> extends ObservableMap<K, V> {

    void clearAuthorAndInlineValues();

    boolean containsKey(StyleOrigin origin, K key);

    V get(StyleOrigin origin, K key);

    default int getIdentityHash() {return System.identityHashCode(this);}

    Map<K, V> getMap(StyleOrigin origin);

    StyleOrigin getStyleOrigin(Object key);

    Map<K, V> getStyledMap();

    V put(StyleOrigin styleOrigin, K key, V value);

    void removeAll(StyleOrigin origin);

}
