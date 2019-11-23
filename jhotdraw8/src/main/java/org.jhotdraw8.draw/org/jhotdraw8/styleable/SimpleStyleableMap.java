/*
 * @(#)SimpleStyleableMap.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.styleable;

import javafx.beans.InvalidationListener;
import javafx.collections.MapChangeListener;
import javafx.css.StyleOrigin;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

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

    private final static Object NULL_VALUE = new Object();
    private final static int numOrigins = 4;
    private CopyOnWriteArrayList<MapChangeListener<? super K, ? super V>> changeListenerList;

    private CopyOnWriteArrayList<InvalidationListener> invalidationListenerList;
    private final Map<K, Integer> keyMap;
    @Nullable
    private final StyleOrigin origin;
    private final int originOrdinal;
    private final int[] sizes;
    private final ArrayList<Object> values;
    @NonNull
    private final SimpleStyleableMap<K, V> originalMap;
    private final static int AUTO_ORIGIN = -1;

    /**
     * Creates a new instance.
     */
    public SimpleStyleableMap() {
        this(Collections.synchronizedMap(new LinkedHashMap<>()));
    }

    /**
     * Creates a new instance.
     *
     * @param keyMap a map which maps from keys to indices. The indices must be
     *               in the range {@code [0,keyMap.size()-1]}. This map will add new keys to
     *               the keyMap if necessary, and assign {@code keyMap.size()} to each new
     *               key. Keys may be added to this map, but may never be removed.
     */
    public SimpleStyleableMap(@NonNull Map<K, Integer> keyMap) {
        this.keyMap = keyMap;
        this.values = new ArrayList<>(keyMap.size() * numOrigins);
        this.origin = StyleOrigin.USER;
        this.originOrdinal = origin.ordinal();
        this.sizes = new int[numOrigins];
        this.originalMap = this;
    }

    private SimpleStyleableMap(@NonNull SimpleStyleableMap<K, V> that, @Nullable StyleOrigin styleOrigin) {
        this.keyMap = that.keyMap;
        this.values = that.values;
        this.origin = styleOrigin;
        this.originOrdinal = (styleOrigin == null) ? AUTO_ORIGIN : styleOrigin.ordinal();
        this.sizes = that.sizes;
        this.originalMap = that;
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

    @Override
    public void clearAuthorAndInlineValues() {
        int author = StyleOrigin.AUTHOR.ordinal();
        int inline = StyleOrigin.INLINE.ordinal();
        for (Iterator<Entry<K, Integer>> i = keyMap.entrySet().iterator(); i.hasNext(); ) {
            Entry<K, Integer> e = i.next();
            Integer index = e.getValue();
            if (index < values.size()) {
                removeValue(author, index, e.getKey());
                removeValue(inline, index, e.getKey());
            }
        }
    }

    @Override
    public boolean containsKey(Object key) {
        return containsKey(origin, key);
    }

    public boolean containsKey(@Nullable StyleOrigin origin, Object key) {
        Integer index = keyMap.get(key);

        if (origin == null) {
            return getStyleOrigin(key) != null;
        }

        boolean result = index != null
                && index * numOrigins + origin.ordinal() < values.size()
                && values.get(index * numOrigins + origin.ordinal()) != null;
        return result;
    }

    @Override
    public boolean containsValue(Object value) {
        return containsValue(origin, value);
    }

    public boolean containsValue(StyleOrigin origin, @Nullable Object value) {
        if (value == null) {
            value = NULL_VALUE;
        }
        for (int i = origin.ordinal(), n = values.size(); i < n; i += numOrigins) {
            if (Objects.equals(values.get(i), value)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private int ensureCapacity(K key) {
        final int indexIfAbsent = keyMap.size();
        Integer indexIfPresent = keyMap.putIfAbsent(key, indexIfAbsent);
        int index = indexIfPresent == null ? indexIfAbsent : indexIfPresent;
        int n = n = (1 + index) * numOrigins;
        values.ensureCapacity(n);
        for (int i = values.size(); i < n; i++) {
            values.add(null);
        }
        return index;
    }

    private int indexIfPresent(K key) {
        Integer index = keyMap.get(key);
        return index == null ? -1 : index;
    }

    @NonNull
    @Override
    public Set<Entry<K, V>> entrySet() {
        return new EntrySet();
    }

    @Nullable
    @Override
    public V get(Object key) {
        return get(originOrdinal, key, null);
    }

    @Nullable
    @Override
    public V getOrDefault(Object key, V defaultValue) {
        return get(originOrdinal, key, defaultValue);
    }

    @Nullable
    public V get(@NonNull StyleOrigin origin, K key) {
        return get(origin.ordinal(), key, null);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private V get(int originOrdinal, Object key, V defaultValue) {
        Integer index = keyMap.get(key);
        return index == null ? defaultValue : getValue(originOrdinal, index, (K) key, defaultValue);
    }

    @NonNull
    public Map<K, V> getMap(StyleOrigin origin) {
        return (origin == this.origin) ? this : new SimpleStyleableMap<>(this, origin);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public StyleOrigin getStyleOrigin(Object key) {
        int index = ensureCapacity((K) key);
        for (int i = numOrigins - 1; i >= 0; i--) {
            final int arrayIndex = index * numOrigins + i;
            Object value = arrayIndex < values.size() ? values.get(arrayIndex) : null;
            if (value != null) {
                return StyleOrigin.values()[i];
            }
        }
        return null;
    }

    @Nullable
    public Map<K, V> getStyledMap() {
        return new SimpleStyleableMap<>(this, null);
    }

    @Nullable
    private V getValue(int index, K key) {
        return getValue(originOrdinal, index, key, null);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private V getValue(int ordinal, int index, K key, V defaultValue) {
        Object value;
        if (ordinal == -1) {
            value = null;
            if ((index + 1) * numOrigins <= values.size()) {
                for (int i = numOrigins - 1; i >= 0; i--) {
                    final int arrayIndex = index * numOrigins + i;
                    value = values.get(arrayIndex);
                    if (value != null) {
                        break;
                    }
                }
            }
        } else {
            final int arrayIndex = index * numOrigins + ordinal;
            value = arrayIndex < values.size() ? values.get(arrayIndex) : null;
        }
        return value == null ? defaultValue : (V) (value == NULL_VALUE ? null : value);
    }

    private boolean hasValue(int index) {
        return hasValue(originOrdinal, index);
    }

    private boolean hasValue(int ordinal, int index) {
        if (ordinal == AUTO_ORIGIN) {
            for (int i = 0; i < numOrigins; i++) {
                final int arrayIndex = index + i;
                if (values.get(arrayIndex) != null) {
                    return true;
                }
            }
            return false;
        }
        final int arrayIndex = index * numOrigins + ordinal;
        return arrayIndex < values.size() && values.get(arrayIndex) != null;
    }

    @Override
    public boolean isEmpty() {
        return sizes[originOrdinal] == 0;
    }

    @NonNull
    @Override
    public Set<K> keySet() {
        return new KeySet();
    }

    @Override
    public V put(K key, V value) {
        int index = ensureCapacity(key);
        return setValue(originOrdinal, index, key, value);
    }

    @Nullable
    public V put(@NonNull StyleOrigin styleOrigin, K key, V value) {
        int index = ensureCapacity(key);
        return setValue(styleOrigin.ordinal(), index, key, value);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public V remove(Object key) {
        int index = indexIfPresent((K) key);
        return index == -1 ? null : removeValue(originOrdinal, index, (K) key);
    }

    public void removeAll(@NonNull StyleOrigin origin) {
        int ordinal = origin.ordinal();
        for (Iterator<Entry<K, Integer>> i = keyMap.entrySet().iterator(); i.hasNext(); ) {
            Entry<K, Integer> e = i.next();
            Integer index = e.getValue();
            if (index < values.size()) {
                removeValue(ordinal, index, e.getKey());
            }
        }
    }

    @Override
    public void resetStyledValues() {
        final int userOrdinal = StyleOrigin.USER.ordinal();
        for (int i = 0, n = values.size(); i < n; i++) {
            if (i % numOrigins != userOrdinal) {
                values.set(i, null);
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

    @Nullable
    @SuppressWarnings("unchecked")
    private V removeValue(int ordinal, int index, K key) {
        if (ordinal == -1) {
            throw new UnsupportedOperationException("can not remove styled value");
        }
        final int arrayIndex = index * numOrigins + ordinal;

        Object oldValue = arrayIndex < values.size() ? values.get(arrayIndex) : null;
        if (oldValue == null) {
            return null;
        } else {
            values.set(index * numOrigins + ordinal, null);
            sizes[ordinal]--;
            V returnValue = oldValue == NULL_VALUE ? null : (V) oldValue;
            if (origin == StyleOrigin.USER) {
                @SuppressWarnings("unchecked")
                ChangeEvent change = new ChangeEvent(key, returnValue, null, false, true);
                callObservers(this.origin, change);
            }

            return returnValue;
        }

    }

    @Nullable
    @SuppressWarnings("unchecked")
    private V setValue(int ordinal, int index, K key, @Nullable V newValue) {
        if (ordinal == -1) {
            throw new UnsupportedOperationException("can not set styled value");
        }
        V oldValue = (V) values.get(index * numOrigins + ordinal);
        if (oldValue == null) {
            sizes[ordinal]++;
        }
        values.set(index * numOrigins + ordinal, newValue == null ? NULL_VALUE : newValue);

        V returnValue = oldValue == NULL_VALUE ? null : oldValue;
        if (!Objects.equals(returnValue, newValue)) {
            if (origin == StyleOrigin.USER) {
                ChangeEvent change = new ChangeEvent(key, returnValue, newValue, true, oldValue != null);
                callObservers(this.origin, change);
            }
        }

        return returnValue;
    }

    public int getIdentityHash() {
        return System.identityHashCode(values);
    }

    @Override
    public int size() {
        return sizes[originOrdinal];
    }

    @NonNull
    @Override
    public Collection<V> values() {
        return new ValueCollection();
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

        @NonNull
        @Override
        public String toString() {
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

        @Override
        public void clear() {
            SimpleStyleableMap.this.clear();
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
        @SuppressWarnings("unchecked")
        public boolean contains(Object o) {
            if (!(o instanceof Entry)) {
                return false;
            }
            Entry<K, V> e = (Entry<K, V>) o;
            return SimpleStyleableMap.this.containsKey(e.getKey())
                    && Objects.equals(SimpleStyleableMap.this.get(e.getKey()), e.getValue());
        }

        @NonNull
        @Override
        public Iterator<Entry<K, V>> iterator() {
            return new Iterator<Entry<K, V>>() {
                @NonNull
                private Iterator<Entry<K, Integer>> entryIt = SimpleStyleableMap.this.keyMap.entrySet().iterator();
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
                        if (hasValue(entry.getValue())) {
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

                @NonNull
                @Override
                public Entry<K, V> next() {
                    lastKey = nextKey;
                    lastValue = nextValue;
                    advance();
                    return new MapEntry(lastKey, lastValue);
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

        @NonNull
        @Override
        public Iterator<K> iterator() {
            return new Iterator<K>() {
                @NonNull
                private Iterator<Entry<K, Integer>> entryIt = SimpleStyleableMap.this.keyMap.entrySet().iterator();
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

        public MapEntry(K key, int index) {
            this.key = key;
            this.index = index;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Nullable
        @Override
        public V getValue() {
            return SimpleStyleableMap.this.getValue(SimpleStyleableMap.this.originOrdinal, index, key, null);
        }

        @Nullable
        @Override
        public V setValue(V value) {
            V oldValue = SimpleStyleableMap.this.getValue(SimpleStyleableMap.this.originOrdinal, index, key, null);
            SimpleStyleableMap.this.setValue(SimpleStyleableMap.this.originOrdinal, index, key, value);
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

        @Nullable
        @Override
        public final String toString() {
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

        @NonNull
        @Override
        public Iterator<V> iterator() {
            return new Iterator<V>() {

                @NonNull
                private Iterator<Entry<K, Integer>> entryIt = keyMap.entrySet().iterator();
                private boolean hasNext;
                private K nextKey;
                private K lastKey;
                @Nullable
                private V nextValue;
                @Nullable
                private V lastValue;

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

                @Nullable
                @Override
                public V next() {
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
