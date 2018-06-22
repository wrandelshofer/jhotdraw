/* @(#)StyleableMap.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.beans.InvalidationListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * An observable map which stores its values in an array, and which can share
 * its keys with other SharedKeysMaps.
 *
 * @author Werner Randelshofer
 * @param <K> key type
 * @param <V> value type
 */
public class SharedKeysMap<K, V> extends AbstractMap<K, V> implements ObservableMap<K, V> {

    private final static Object NULL_VALUE = new Object();
    private CopyOnWriteArrayList<MapChangeListener<? super K, ? super V>> changeListenerList;
    private CopyOnWriteArrayList<InvalidationListener> invalidationListenerList;
    @NonNull
    private final Map<K, Integer> keyMap;
    private int size;
    @NonNull
    private final ArrayList<Object> values;

    /**
     * Creates a new instance.
     */
    public SharedKeysMap() {
        this(new HashMap<>());
    }

    /**
     * Creates a new instance.
     *
     * @param keyMap a map which maps from keys to indices. The indices must be
     * in the range {@code [0,keyMap.size()-1]}. This map will add new keys to
     * the keyMap if necessary, and assign {@code keyMap.size()} to each new
     * key. Keys may be added to this map, but may never be removed.
     */
    public SharedKeysMap(Map<K, Integer> keyMap) {
        this.keyMap = keyMap;
        this.values = new ArrayList<>(keyMap.size());
    }

    @Override
    public void addListener(InvalidationListener listener) {
        if (invalidationListenerList == null) {
            invalidationListenerList = new CopyOnWriteArrayList<>();
        }
        invalidationListenerList.add(listener);
    }

    @Override
    public void addListener(MapChangeListener<? super K, ? super V> observer) {
        if (changeListenerList == null) {
            changeListenerList = new CopyOnWriteArrayList<>();
        }
        changeListenerList.add(observer);
    }

    protected void callObservers(MapChangeListener.Change<K, V> change) {
        if (changeListenerList != null) {
            for (MapChangeListener<? super K, ? super V> l : changeListenerList) {
                l.onChanged(change);
            }
        }
        if (invalidationListenerList != null) {
            for (InvalidationListener l : invalidationListenerList) {
                l.invalidated(this);
            }
        }
    }

    public void clear() {
        values.clear();
        size=0;
    }

    @Override
    public boolean containsKey(Object key) {
        Integer index = keyMap.get(key);

        boolean result = index != null
                && index < values.size()
                && values.get(index) != null;
        return result;
    }

    @Override
    public boolean containsValue(Object value) {
        for (int i = 0, n = values.size(); i < n; i++) {
            if (Objects.equals(values.get(i), value)) {
                return true;
            }
        }
        return false;
    }

    private void createListenerLists() {
        if (invalidationListenerList == null) {
            invalidationListenerList = new CopyOnWriteArrayList<>();
        }
        if (changeListenerList == null) {
            changeListenerList = new CopyOnWriteArrayList<>();
        }
    }

    @SuppressWarnings("unchecked")
    private int ensureCapacity(K key) {
        final int indexIfAbsent = keyMap.size();
        Integer indexIfPresent = keyMap.putIfAbsent(key, indexIfAbsent);
        int index = indexIfPresent == null ? indexIfAbsent : indexIfPresent;
        for (int i = values.size(), n = (1 + index); i < n; i++) {
            values.add(null);
        }
        return index;
    }

    @NonNull
    @Override
    public Set<Entry<K, V>> entrySet() {
        return new EntrySet();
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public V get(Object key) {
        Integer index = keyMap.get(key);
        return index == null ? null : getValue(index, (K) key);
    }

    public int getIdentityHash() {
        return System.identityHashCode(values);
    }

    @SuppressWarnings("unchecked")
    private V getValue(int index, K key) {
        Object value;
        final int arrayIndex = index;
        value = arrayIndex < values.size() ? values.get(arrayIndex) : null;
        return value == NULL_VALUE ? null : (V) value;
    }

    private boolean hasValue(int index) {
        final int arrayIndex = index;
        return arrayIndex < values.size() && values.get(arrayIndex) != null;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @NonNull
    @Override
    public Set<K> keySet() {
        return new KeySet();
    }

    @Override
    public V put(K key, V value) {
        int index = ensureCapacity(key);
        return setValue(index, key, value);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public V remove(Object key) {
        int index = ensureCapacity((K) key);
        return removeValue(index, (K) key);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        if (invalidationListenerList != null) {
            invalidationListenerList.remove(listener);
        }
    }

    @Override
    public void removeListener(MapChangeListener<? super K, ? super V> observer) {
        if (changeListenerList != null) {
            changeListenerList.remove(observer);
        }
    }

    @SuppressWarnings("unchecked")
    private V removeValue(int index, K key) {
        final int arrayIndex = index;

        Object oldValue = arrayIndex < values.size() ? values.get(arrayIndex) : null;
        if (oldValue == null) {
            return null;
        } else {
            values.set(index, null);
            size--;
             V returnValue = (V)(oldValue==NULL_VALUE?null:oldValue);
            @SuppressWarnings("unchecked")
            ChangeEvent change = new ChangeEvent(key, returnValue, null, false, true);
            callObservers(change);

            return returnValue;
        }

    }

    @Nullable
    @SuppressWarnings("unchecked")
    private V setValue(int index, K key, V newValue) {
        V oldValue = (V) values.get(index);
        if (oldValue == null) {
            size++;
        }
        values.set(index, newValue);

        V returnValue = oldValue == NULL_VALUE ? null : oldValue;
        if (!Objects.equals(oldValue, newValue)) {
            ChangeEvent change = new ChangeEvent(key, returnValue, newValue, true, oldValue != null);
            callObservers(change);
        }

        return returnValue;
    }

    @Override
    public int size() {
        return size;
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
            super(SharedKeysMap.this);
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
            SharedKeysMap.this.clear();
        }

        @Override
        public int size() {
            return SharedKeysMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return SharedKeysMap.this.isEmpty();
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean contains(Object o) {
            if (!(o instanceof Entry)) {
                return false;
            }
            Entry<K, V> e = (Entry<K, V>) o;
            return SharedKeysMap.this.containsKey(e.getKey())
                    && Objects.equals(SharedKeysMap.this.get(e.getKey()), e.getValue());
        }

        @NonNull
        @Override
        public Iterator<Entry<K, V>> iterator() {
            return new Iterator<Entry<K, V>>() {
                @NonNull
                private Iterator<Entry<K, Integer>> entryIt = SharedKeysMap.this.keyMap.entrySet().iterator();
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
                    SharedKeysMap.this.removeValue(lastValue, lastKey);
                }

            };
        }

        @Override
        public boolean add(@NonNull Entry<K, V> e) {
            boolean added = !SharedKeysMap.this.containsKey(e.getKey())
                    || Objects.equals(SharedKeysMap.this.get(e.getKey()), e.getValue());
            if (added) {
                SharedKeysMap.this.put(e.getKey(), e.getValue());
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
            boolean removed = SharedKeysMap.this.containsKey(e.getKey())
                    && Objects.equals(SharedKeysMap.this.get(e.getKey()), e.getValue());
            if (removed) {
                SharedKeysMap.this.remove(e.getKey());
            }
            return removed;
        }

    }

    private class KeySet extends AbstractSet<K> {

        @Override
        public int size() {
            return SharedKeysMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return SharedKeysMap.this.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return SharedKeysMap.this.containsKey(o);
        }

        @NonNull
        @Override
        public Iterator<K> iterator() {
            return new Iterator<K>() {
                @NonNull
                private Iterator<Entry<K, Integer>> entryIt = SharedKeysMap.this.keyMap.entrySet().iterator();
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
                    SharedKeysMap.this.remove(lastKey);
                }

            };
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean remove(Object o) {
            boolean removed = SharedKeysMap.this.containsKey(o);
            if (removed) {
                SharedKeysMap.this.remove(o);
            }
            return removed;
        }

        @Override
        public void clear() {
            SharedKeysMap.this.clear();
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
            return SharedKeysMap.this.getValue(index, key);
        }

        @Nullable
        @Override
        public V setValue(V value) {
            V oldValue = SharedKeysMap.this.getValue(index, key);
            SharedKeysMap.this.setValue(index, key, value);
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
                if (v1 == v2 || (v1 != null && v1.equals(v2))) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public final int hashCode() {
            return (getKey() == null ? 0 : getKey().hashCode())
                    ^ (getValue() == null ? 0 : getValue().hashCode());
        }

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
            return SharedKeysMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return SharedKeysMap.this.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return SharedKeysMap.this.containsValue(o);
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
                        if (SharedKeysMap.this.hasValue(entry.getValue())) {
                            nextKey = entry.getKey();
                            nextValue = SharedKeysMap.this.getValue(entry.getValue(), nextKey);
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
                    SharedKeysMap.this.remove(lastKey);
                }

            };
        }

        @Override
        public void clear() {
            SharedKeysMap.this.clear();
        }

    }

}
