/*
 * @(#)SimpleStyleableMap.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.styleable;

import javafx.beans.InvalidationListener;
import javafx.collections.MapChangeListener;
import javafx.css.StyleOrigin;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.util.Preconditions;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.lang.Integer.highestOneBit;
import static java.lang.Integer.max;

/**
 * A map which stores its values in an array, and which can share its keys with
 * other SimpleStyleableMaps.
 * <p>
 * This map stores one distinct value for each StyleOrigin.
 *
 * @param <K> key type
 * @param <V> value type
 * @author Werner Randelshofer
 */
public class SimpleStyleableMap<K, V> extends AbstractMap<K, V> implements StyleableMap<K, V> {
    public Set<Entry<K, V>> entrySet(@Nullable StyleOrigin origin) {
        return new EntrySet(origin);
    }


    private static final Object NULL_VALUE = new Object();
    private static final Object NO_VALUE = null;
    private static final int numOrigins = 4;
    private static final int numOriginsMask = 3;
    private @Nullable CopyOnWriteArrayList<MapChangeListener<? super K, ? super V>> changeListenerList;

    private @Nullable CopyOnWriteArrayList<InvalidationListener> invalidationListenerList;
    private  @NonNull Map<K, Integer> keyMap;
    private final @NonNull StyleOrigin origin;
    private final int originOrdinal;
    private final @NonNull int[] sizes;

    protected void setKeyMap(Map<K, Integer> keyMap) {
        this.keyMap=keyMap;
        this.values = new Object[keyMap.size() * numOrigins];
    }

    private Object[] values;
    private final @NonNull SimpleStyleableMap<K, V> originalMap;
    static final int AUTO_ORIGIN = -StyleOrigin.INLINE.ordinal();

    /**
     * Creates a new instance which supports insertion of
     * new keys.
     */
    public SimpleStyleableMap() {
        this(new HashMap<K, Integer>() {
            private static final long serialVersionUID = 0L;

            @SuppressWarnings("unchecked")
            @Override
            public Integer get(Object key) {
                return super.computeIfAbsent((K) key, k1 -> size());
            }
        });
    }

    /**
     * Creates a new instance which uses the provided key map.
     * <p>
     * The key map can be shared with other instances, provided that
     * either the key map is immutable, or the key map is mutable and
     * the only allowed mutation is the insertion of new entries.
     * <p>
     * All entries in the key map must contain distinct integer
     * values in the range [0, keyMap.size() - 1].
     * <p>
     * A shared mutable implementation of {@code keyMap} could be implemented
     * as follows:
     * <pre>
     * Map<K>, V> keyMap = new ConcurrentHashMap<K>, V>() {
     *
     *     final AtomicInteger nextIndex = new AtomicInteger();
     *
     *     public Integer get(Object key) {
     *         return super.computeIfAbsent((K) key, k -> nextIndex.getAndIncrement());
     *     }
     * };
     * </pre>
     * </p>
     *
     * @param keyMap a map which maps from keys to indices. The indices must be
     *               in the range {@code [0,keyMap.size()-1]}.
     */
    public SimpleStyleableMap(@NonNull Map<K, Integer> keyMap) {
        this.keyMap = keyMap;
        this.values = new Object[keyMap.size() * numOrigins];
        this.origin = StyleOrigin.USER;
        this.originOrdinal = origin.ordinal();
        this.sizes = new int[numOrigins];
        this.originalMap = this;
    }

    @Override
    public void addListener(InvalidationListener listener) {
        if (originalMap.invalidationListenerList == null) {
            originalMap.invalidationListenerList = new CopyOnWriteArrayList<>();
        }
        originalMap.invalidationListenerList.add(listener);
    }

    @Override
    public void addListener(MapChangeListener<? super K, ? super V> observer) {
        if (originalMap.changeListenerList == null) {
            originalMap.changeListenerList = new CopyOnWriteArrayList<>();
        }
        originalMap.changeListenerList.add(observer);
    }

    protected void callObservers(StyleOrigin origin, MapChangeListener.Change<K, V> change) {
        if (origin == StyleOrigin.USER) {
            if (originalMap.changeListenerList != null) {
                for (MapChangeListener<? super K, ? super V> l : originalMap.changeListenerList) {
                    l.onChanged(change);
                }
            }
        }
        if (originalMap.invalidationListenerList != null) {
            for (InvalidationListener l : originalMap.invalidationListenerList) {
                l.invalidated(this);
            }
        }
    }

    /**
     * Clears the map.
     */
    public void clear() {
        removeAll(origin);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean containsKey(Object key) {
        return containsKey(origin, (K) key);
    }

    public <T extends K> boolean containsKey(@Nullable StyleOrigin origin, @NonNull T key) {
        Integer index = keyMap.get(key);

        if (origin == null) {
            return getStyleOrigin(key) != null;
        }

        if (index != null
                && index * numOrigins + origin.ordinal() < values.length) {
            Object rawValue = values[index * numOrigins + origin.ordinal()];
            return rawValue != NO_VALUE;
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return containsValue(origin, value);
    }

    public boolean containsValue(StyleOrigin origin, @Nullable Object value) {
        if (value == null) {
            value = NULL_VALUE;
        }
        for (int i = origin.ordinal(), n = values.length; i < n; i += numOrigins) {
            if (Objects.equals(values[i], value)) {
                return true;
            }
        }
        return false;
    }

    private int ensureCapacity(K key) {
        Integer indexNullable = keyMap.get(key);
        if (indexNullable == null) {
            throw new UnsupportedOperationException("Could not retrieve key " + key + " from keyMap: " + keyMap);
        }
        int index = indexNullable;
        int minCapacity = (1 + index) * numOrigins;
        if (values.length < minCapacity) {
            int newCapacity = nextPowerOfTwoUp(1 + index) * numOrigins;
            values = Arrays.copyOf(values, max(values.length, newCapacity));
        }

        return index;
    }

    /**
     * Returns the next power of two that is equal or greater than the specified
     * value.
     *
     * @param value a value in the range [0,1<<29].
     * @return nextUp with nextUp @gt;= value && nextUp == highestOneBit(nextUp).
     */
    private int nextPowerOfTwoUp(int value) {
        Preconditions.checkIndex(value, 1 << 29);
        int highestOneBit = highestOneBit(value);
        return (value == highestOneBit) ? value : highestOneBit << 1;
    }

    private int indexIfPresent(K key) {
        Integer index = keyMap.get(key);
        return index == null ? -1 : index;
    }

    @Override
    public @NonNull Set<Entry<K, V>> entrySet() {
        return new EntrySet(null);
    }

    @Override
    public @Nullable V get(Object key) {
        return getOrDefault(originOrdinal, key, null);
    }

    @Override
    public @Nullable V getOrDefault(@NonNull Object key, @Nullable V defaultValue) {
        return getOrDefault(originOrdinal, key, defaultValue);
    }

    public @Nullable V get(@NonNull StyleOrigin origin, @NonNull K key) {
        return getOrDefault(origin.ordinal(), key, null);
    }

    public @Nullable V getOrDefault(@NonNull StyleOrigin origin, @NonNull K key, @Nullable V defaultValue) {
        return getOrDefault(origin.ordinal(), key, null);
    }

    @SuppressWarnings("unchecked")
    protected @Nullable V getOrDefault(int originOrdinal, @NonNull Object key, @Nullable V defaultValue) {
        Integer index = keyMap.get(key);
        return index == null ? defaultValue : getValue(originOrdinal, index, (K) key, defaultValue);
    }

    public @NonNull Map<K, V> getMap(@NonNull StyleOrigin origin) {
        return (origin == this.origin) ? this : new SimpleStyleableMapProxy<>(this, origin);
    }

    @SuppressWarnings("unchecked")
    public @Nullable StyleOrigin getStyleOrigin(@NonNull K key) {
        Integer indexNullable = keyMap.get(key);
        if (indexNullable == null) {
            return null;
        }
        int index = indexNullable;
        for (int i = numOrigins - 1; i >= 0; i--) {
            final int arrayIndex = index * numOrigins + i;
            Object value = arrayIndex < values.length ? values[arrayIndex] : null;
            if (value != null) {
                return StyleOrigin.values()[i];
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public V removeKey(@NonNull StyleOrigin origin, @NonNull K key) {
        Object oldRawValue = setRawValue(origin.ordinal(), ensureCapacity(key), key, NO_VALUE);
        return rawValueToValue(oldRawValue);
    }

    public @NonNull Map<K, V> getStyledMap() {
        return new SimpleStyleableMapProxy<>(this, null);
    }

    private @Nullable V getValue(int index, K key) {
        return getValue(originOrdinal, index, key, null);
    }

    @SuppressWarnings("unchecked")
    private @Nullable V getValue(int ordinal, int index, K key, V defaultValue) {
        Object rawValue = getRawValue(ordinal, index);
        return rawValue == NO_VALUE ? defaultValue : rawValueToValue(rawValue);
    }

    /**
     * Gets a raw value from the specified ordinal StyleOrigin or (if the negative ordinal
     * is given) searches through StyleOrigins starting from the given negative ordinal
     * StyleOrigin.
     *
     * @param ordinal the ordinal of the StyleOrigin or the negative ordinal.
     * @param index   the index of the Key
     * @return rawValue: NO_VALUE means that no value is stored, NULL_VALUE means
     * that the null value is stored, all other values are stored values.
     */
    @SuppressWarnings("unchecked")
    private @Nullable Object getRawValue(int ordinal, int index) {
        Object value;
        if (ordinal < 0) {
            value = null;
            if (index * numOrigins < values.length) {
                for (int i = -ordinal; i >= 0; i--) {
                    final int valueIndex = index * numOrigins + i;
                    value = values[valueIndex];
                    if (value != NO_VALUE) {
                        break;
                    }
                }
            }
        } else {
            final int arrayIndex = index * numOrigins + ordinal;
            value = arrayIndex < values.length ? values[arrayIndex] : NO_VALUE;
        }
        return value;
    }


    private boolean hasValue(int index) {
        return hasValue(originOrdinal, index);
    }

    private boolean hasValue(int ordinal, int index) {
        return getRawValue(ordinal, index) != NO_VALUE;
    }

    @Override
    public boolean isEmpty() {
        return sizes[originOrdinal] == 0;
    }

    @Override
    public @NonNull Set<K> keySet() {
        return new KeySet();
    }

    @Override
    public V put(K key, V value) {
        int index = ensureCapacity(key);
        return setValue(originOrdinal, index, key, value);
    }

    public @Nullable V put(@NonNull StyleOrigin styleOrigin, @NonNull K key, V value) {
        return put(styleOrigin.ordinal(), key, value);
    }

    protected @Nullable V put(int originOrdinal, @NonNull K key, V value) {
        int index = ensureCapacity(key);
        return setValue(originOrdinal, index, key, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable V remove(Object key) {
        int index = indexIfPresent((K) key);
        return index == -1 ? null : removeValue(originOrdinal, index, (K) key);
    }

    public void removeAll(@NonNull StyleOrigin origin) {
        if (origin == StyleOrigin.USER) {
            // We have to fire change events, so we need the keys.
            int ordinal = origin.ordinal();
            for (Iterator<Entry<K, Integer>> i = keyMap.entrySet().iterator(); i.hasNext(); ) {
                Entry<K, Integer> e = i.next();
                Integer index = e.getValue();
                if (index < values.length) {
                    removeValue(ordinal, index, e.getKey());
                }
            }
        } else {
            // We do not fire change events
            int ordinal = origin.ordinal();
            for (int i = 0, n = values.length; i < n; i++) {
                if ((i & numOriginsMask) == ordinal) {
                    values[i] = NO_VALUE;
                }
            }
        }
    }

    @Override
    public void resetStyledValues() {
        // Performance: this method is called very often.
        final int userOrdinal = StyleOrigin.USER.ordinal();
        for (int i = 0, n = values.length; i < n; i++) {
            if ((i & numOriginsMask) != userOrdinal) {
                values[i] = NO_VALUE;
            }
        }
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        if (originalMap.invalidationListenerList != null) {
            originalMap.invalidationListenerList.remove(listener);
        }
    }

    @Override
    public void removeListener(MapChangeListener<? super K, ? super V> observer) {
        if (originalMap.changeListenerList != null) {
            originalMap.changeListenerList.remove(observer);
        }
    }

    @SuppressWarnings("unchecked")
    private @Nullable V removeValue(int ordinal, int index, K key) {
        if (ordinal == -1) {
            throw new UnsupportedOperationException("can not remove styled value");
        }
        final int arrayIndex = index * numOrigins + ordinal;

        Object oldRawValue = arrayIndex < values.length ? values[arrayIndex] : NO_VALUE;
        if (oldRawValue == NO_VALUE) {
            return null;
        } else {
            values[index * numOrigins + ordinal] = null;
            sizes[ordinal]--;
            V oldValue = rawValueToValue(oldRawValue);
            if (origin == StyleOrigin.USER) {
                ChangeEvent change = new ChangeEvent(key, oldValue, null, false, true);
                callObservers(this.origin, change);
            }

            return oldValue;
        }
    }

    private @Nullable V setValue(int ordinal, int index, K key, @Nullable V newValue) {
        Object oldRawValue = setRawValue(ordinal, index, key, newValue == null ? NULL_VALUE : newValue);
        return rawValueToValue(oldRawValue);
    }

    @SuppressWarnings("unchecked")
    private @Nullable V rawValueToValue(@Nullable Object rawValue) {
        return rawValue == NULL_VALUE || rawValue == NO_VALUE ? null : (V) rawValue;
    }

    private Object setRawValue(int ordinal, int keyIndex, K key, @Nullable Object newRawValue) {
        if (ordinal == -1) {
            throw new UnsupportedOperationException("can not set styled value");
        }
        int valueIndex = keyIndex * numOrigins + ordinal;
        Object oldRawValue = values[valueIndex];
        //noinspection ConstantConditions
        if (oldRawValue == NO_VALUE) {
            sizes[ordinal]++;
        }
        if (newRawValue == NO_VALUE) {
            sizes[ordinal]--;
        }
        values[valueIndex] = newRawValue;
        if (!Objects.equals(oldRawValue, newRawValue)) {
            if (ordinal == StyleOrigin.USER.ordinal()) {
                // Only StyleOrigin.USER may fire a property change event.
                // The other style origins can be update in parallel, and thus
                // firing an event is not a good idea!
                @SuppressWarnings("unchecked")
                V newValue = rawValueToValue(newRawValue);
                @SuppressWarnings("unchecked")
                V oldValue = rawValueToValue(oldRawValue);
                ChangeEvent change = new ChangeEvent(key, oldValue, newValue, newRawValue != NO_VALUE, oldRawValue != NO_VALUE);
                callObservers(this.origin, change);
            }
        }
        return oldRawValue;
    }

    public int getIdentityHash() {
        return System.identityHashCode(values);
    }

    @Override
    public int size() {
        return sizes[originOrdinal];
    }

    @Override
    public @NonNull Collection<V> values() {
        return new ValueCollection();
    }

    public int size(@Nullable StyleOrigin origin) {
        if (origin == null) {
            return sizes[originOrdinal];
        } else {
            return sizes[origin.ordinal()];
        }
    }

    private class ChangeEvent extends MapChangeListener.Change<K, V> {

        private final K key;
        private final V old;
        private final V added;
        private final boolean wasAdded;
        private final boolean wasRemoved;

        public ChangeEvent(K key, V old, V added, boolean wasAdded, boolean wasRemoved) {
            super(SimpleStyleableMap.this);
            assert (wasAdded || wasRemoved);
            this.key = key;
            this.old = old;
            this.added = added;
            this.wasAdded = wasAdded;
            this.wasRemoved = wasRemoved;
        }

        @Override
        public @NonNull String toString() {
            return "ChangeEvent{" + "key=" + key + ", old=" + old + ", added=" + added + ", wasAdded=" + wasAdded + ", wasRemoved=" + wasRemoved + '}';
        }

        @Override
        public boolean wasAdded() {
            return wasAdded;
        }

        @Override
        public boolean wasRemoved() {
            return wasRemoved;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValueAdded() {
            return added;
        }

        @Override
        public V getValueRemoved() {
            return old;
        }

    }

    private class EntrySet extends AbstractSet<Entry<K, V>> {
        private final @Nullable StyleOrigin origin;
        private final int originOrdinal;

        public EntrySet(@Nullable StyleOrigin origin) {
            this.origin = origin;
            this.originOrdinal = origin == null ? AUTO_ORIGIN : origin.ordinal();
        }

        @Override
        public void clear() {
            SimpleStyleableMap.this.clear();
        }

        @Override
        public int size() {
            return SimpleStyleableMap.this.size(origin);
        }

        @Override
        public boolean isEmpty() {
            return SimpleStyleableMap.this.isEmpty();
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean contains(Object o) {
            if (!(o instanceof Entry)) {
                return false;
            }
            Entry<K, V> e = (Entry<K, V>) o;
            return SimpleStyleableMap.this.containsKey(origin, e.getKey())
                    && Objects.equals(SimpleStyleableMap.this.get(origin, e.getKey()), e.getValue());
        }

        @Override
        public @NonNull Iterator<Entry<K, V>> iterator() {
            return new Iterator<Entry<K, V>>() {
                private @NonNull Iterator<Entry<K, Integer>> entryIt = SimpleStyleableMap.this.keyMap.entrySet().iterator();
                private boolean hasNext;
                private K nextKey;
                private K lastKey;
                private int nextValue;
                private int lastValue;

                {
                    advance();
                }

                private void advance() {
                    while (entryIt.hasNext()) {
                        Entry<K, Integer> entry = entryIt.next();
                        if (hasValue(originOrdinal, entry.getValue())) {
                            nextKey = entry.getKey();
                            nextValue = entry.getValue();
                            hasNext = true;
                            return;
                        }
                    }
                    hasNext = false;
                }

                @Override
                public boolean hasNext() {
                    return hasNext;
                }

                @Override
                public @NonNull Entry<K, V> next() {
                    lastKey = nextKey;
                    lastValue = nextValue;
                    advance();
                    return new MapEntry(lastKey, lastValue, SimpleStyleableMap.this.originOrdinal);
                }

                @Override
                public void remove() {
                    SimpleStyleableMap.this.removeValue(SimpleStyleableMap.this.originOrdinal, lastValue, lastKey);
                }

            };
        }

        @Override
        public boolean add(@NonNull Entry<K, V> e) {
            boolean added = !SimpleStyleableMap.this.containsKey(e.getKey())
                    || Objects.equals(SimpleStyleableMap.this.get(e.getKey()), e.getValue());
            if (added) {
                SimpleStyleableMap.this.put(e.getKey(), e.getValue());
            }
            return added;
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean remove(Object o) {
            if (!(o instanceof Entry)) {
                return false;
            }
            Entry<K, V> e = (Entry<K, V>) o;
            boolean removed = SimpleStyleableMap.this.containsKey(e.getKey())
                    && Objects.equals(SimpleStyleableMap.this.get(e.getKey()), e.getValue());
            if (removed) {
                SimpleStyleableMap.this.remove(e.getKey());
            }
            return removed;
        }

    }

    private class KeySet extends AbstractSet<K> {

        @Override
        public int size() {
            return SimpleStyleableMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return SimpleStyleableMap.this.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return SimpleStyleableMap.this.containsKey(o);
        }

        @Override
        public @NonNull Iterator<K> iterator() {
            return new Iterator<K>() {
                private @NonNull Iterator<Entry<K, Integer>> entryIt = SimpleStyleableMap.this.keyMap.entrySet().iterator();
                private boolean hasNext;
                private K nextKey;
                private K lastKey;

                {
                    advance();
                }

                private void advance() {
                    while (entryIt.hasNext()) {
                        Entry<K, Integer> entry = entryIt.next();
                        if (hasValue(entry.getValue())) {
                            nextKey = entry.getKey();
                            hasNext = true;
                            return;
                        }
                    }
                    hasNext = false;
                }

                @Override
                public boolean hasNext() {
                    return hasNext;
                }

                @Override
                public K next() {
                    lastKey = nextKey;
                    advance();
                    return lastKey;
                }

                @Override
                public void remove() {
                    SimpleStyleableMap.this.remove(lastKey);
                }

            };
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean remove(Object o) {
            boolean removed = SimpleStyleableMap.this.containsKey(o);
            if (removed) {
                SimpleStyleableMap.this.remove(o);
            }
            return removed;
        }

        @Override
        public void clear() {
            SimpleStyleableMap.this.clear();
        }
    }

    private class MapEntry implements Entry<K, V> {

        private final K key;
        private final int index;
        private final int originOrdinal;

        public MapEntry(K key, int index, int originOrdinal) {
            this.key = key;
            this.index = index;
            this.originOrdinal = originOrdinal;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public @Nullable V getValue() {
            return SimpleStyleableMap.this.getValue(originOrdinal, index, key, null);
        }

        @Override
        public @Nullable V setValue(V value) {
            V oldValue = SimpleStyleableMap.this.getValue(originOrdinal, index, key, null);
            SimpleStyleableMap.this.setValue(originOrdinal, index, key, value);
            return oldValue;
        }

        @Override
        public final boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            @SuppressWarnings("unchecked")
            Map.Entry<K, V> e = (Map.Entry<K, V>) o;
            Object k1 = getKey();
            Object k2 = e.getKey();
            if (k1 == k2 || (k1 != null && k1.equals(k2))) {
                Object v1 = getValue();
                Object v2 = e.getValue();
                return v1 == v2 || (v1 != null && v1.equals(v2));
            }
            return false;
        }

        @Override
        public final int hashCode() {
            return (getKey() == null ? 0 : getKey().hashCode())
                    ^ (getValue() == null ? 0 : getValue().hashCode());
        }

        @Override
        public final @Nullable String toString() {
            return getKey() + "=" + getValue();
        }

    }

    private class ValueCollection extends AbstractCollection<V> {

        public ValueCollection() {
        }

        @Override
        public int size() {
            return SimpleStyleableMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return SimpleStyleableMap.this.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return SimpleStyleableMap.this.containsValue(o);
        }

        @Override
        public @NonNull Iterator<V> iterator() {
            return new Iterator<V>() {

                private @NonNull Iterator<Entry<K, Integer>> entryIt = keyMap.entrySet().iterator();
                private boolean hasNext;
                private K nextKey;
                private K lastKey;
                private @Nullable V nextValue;
                private @Nullable V lastValue;

                {
                    advance();
                }

                private void advance() {
                    while (entryIt.hasNext()) {
                        Entry<K, Integer> entry = entryIt.next();
                        if (SimpleStyleableMap.this.hasValue(entry.getValue())) {
                            nextKey = entry.getKey();
                            nextValue = SimpleStyleableMap.this.getValue(entry.getValue(), nextKey);
                            hasNext = true;
                            return;
                        }
                    }
                    hasNext = false;
                }

                @Override
                public boolean hasNext() {
                    return hasNext;
                }

                @Override
                public @Nullable V next() {
                    lastKey = nextKey;
                    lastValue = nextValue;
                    advance();
                    return lastValue;
                }

                @Override
                public void remove() {
                    SimpleStyleableMap.this.remove(lastKey);
                }

            };
        }

        @Override
        public void clear() {
            SimpleStyleableMap.this.clear();
        }

    }

}
