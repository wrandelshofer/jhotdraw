/* @(#)StyleableMap.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.styleable;

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
import javafx.css.StyleOrigin;
import static org.jhotdraw8.draw.figure.FontableFigure.TEXT_VPOS;
import static org.jhotdraw8.draw.figure.StrokeableFigure.STROKE;

/**
 * A map which stores its values in an array, and which can share its keys with
 * other maps.
 * <p>
 * This map stores one distinct value for each StyleOrigin.
 *
 * @author Werner Randelshofer
 * @param <K> key type
 * @param <V> value type
 */
public class SimpleStyleableMap<K, V> extends AbstractMap<K, V> implements StyleableMap<K, V> {

    private final static Object EMPTY = new Object();
    private final static int numOrigins = 4;
    private CopyOnWriteArrayList<MapChangeListener<? super K, ? super V>> changeListenerList;

    private CopyOnWriteArrayList<InvalidationListener> invalidationListenerList;
    private final Map<K, Integer> keyMap;
    private final StyleOrigin origin;
    private final int originOrdinal;
    private final int[] sizes;
    private final ArrayList<Object> values;

    /**
     * Creates a new instance.
     */
    public SimpleStyleableMap() {
        this(new HashMap<>());
    }

    /**
     * Creates a new instance.
     *
     * @param keyMap a map which maps from keys to indices. The indices must be
     * in the range {@code [0,keyMap.size()-1]}. This map will add new keys to
     * the keyMap if necessary, and assign {@code keyMap.size()} to each new
     * key.
     */
    public SimpleStyleableMap(Map<K, Integer> keyMap) {
        this.keyMap = keyMap;
        this.values = new ArrayList<>(keyMap.size() * numOrigins);
        this.origin = StyleOrigin.USER;
        this.originOrdinal = origin.ordinal();
        this.sizes = new int[numOrigins];
    }

    private SimpleStyleableMap(SimpleStyleableMap<K, V> that, StyleOrigin styleOrigin) {
        this.keyMap = that.keyMap;
        this.values = that.values;
        this.origin = styleOrigin;
        this.originOrdinal = (styleOrigin == null) ? -1 : styleOrigin.ordinal();
        this.sizes = that.sizes;
        that.createListenerLists();
        this.changeListenerList = that.changeListenerList;
        this.invalidationListenerList = that.invalidationListenerList;
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

    protected void callObservers(StyleOrigin origin, MapChangeListener.Change<K, V> change) {

        if (origin == StyleOrigin.USER) {
            if (changeListenerList != null) {
                for (MapChangeListener<? super K, ? super V> l : changeListenerList) {
                    l.onChanged(change);
                }
            }
        }
        if (invalidationListenerList != null) {
            for (InvalidationListener l : invalidationListenerList) {
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
        for (Iterator<Entry<K, Integer>> i = keyMap.entrySet().iterator(); i.hasNext();) {
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

    public boolean containsKey(StyleOrigin origin, Object key) {
        Integer index = keyMap.get(key);

        if (origin == null) {
            return getStyleOrigin(key) != null;
        }

        boolean result = index != null
                && index * numOrigins < values.size()
                && values.get(index * numOrigins + origin.ordinal()) != EMPTY;
        return result;
    }

    @Override
    public boolean containsValue(Object value) {
        return containsValue(origin, value);
    }

    public boolean containsValue(StyleOrigin origin, Object value) {
        for (int i = originOrdinal, n = values.size(); i < n; i += numOrigins) {
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
        for (int i = values.size(), n = (1 + index) * numOrigins; i < n; i++) {
            values.add(EMPTY);
        }
        return index;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return new EntrySet();
    }

    @Override
    public V get(Object key) {
        return get(originOrdinal, key);
    }

    public V get(StyleOrigin origin, K key) {
        return get(origin.ordinal(), key);
    }

    private V get(int originOrdinal, Object key) {
        Integer index = keyMap.get(key);
        return index == null ? null : getValue(originOrdinal, index, (K) key);
    }

    public Map<K, V> getMap(StyleOrigin origin) {
        return (origin == this.origin) ? this : new SimpleStyleableMap(this, origin);
    }

    public StyleOrigin getStyleOrigin(Object key) {
        int index = ensureCapacity((K) key);
        for (int i = numOrigins - 1; i >= 0; i--) {
            V value = (V) values.get(index * numOrigins + i);
            if (value != EMPTY) {
                return StyleOrigin.values()[i];
            }
        }
        return null;
    }
    private transient SimpleStyleableMap<K, V> styledMap;

    public Map<K, V> getStyledMap() {
        createListenerLists();
        if (styledMap == null) {
            styledMap = new SimpleStyleableMap<K, V>(this, null);
        }
        return styledMap;
    }

    private V getValue(int index, K key) {
        return getValue(originOrdinal, index, key);
    }

    @SuppressWarnings("unchecked")
    private V getValue(int ordinal, int index, K key) {
        Object value;
        if (ordinal == -1) {
            value = (V) EMPTY;
            if (index * numOrigins < values.size()) {
                for (int i = numOrigins - 1; i >= 0; i--) {
                    value = (V) values.get(index * numOrigins + i);
                    if (value != EMPTY) {
                        break;
                    }
                }
            }
        } else {
            final int arrayIndex = index * numOrigins + ordinal;
            value = values.size() < arrayIndex ? EMPTY : values.get(arrayIndex);
        }
        return value == EMPTY ? null : (V) value;
    }

    private boolean hasValue(int index) {
        return hasValue(originOrdinal, index);
    }

    private boolean hasValue(int ordinal, int index) {
        final int arrayIndex = index * numOrigins + ordinal;
        return arrayIndex<values.size()&&values.get(arrayIndex) != EMPTY;
    }

    @Override
    public boolean isEmpty() {
        return sizes[originOrdinal] == 0;
    }

    @Override
    public Set<K> keySet() {
        return new KeySet();
    }

    @Override
    public V put(K key, V value) {
        int index = ensureCapacity(key);
        return setValue(originOrdinal, index, key, value);
    }

    public V put(StyleOrigin styleOrigin, K key, V value) {
        int index = ensureCapacity(key);
        return setValue(styleOrigin.ordinal(), index, key, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public V remove(Object key) {
        int index = ensureCapacity((K) key);
        return removeValue(originOrdinal, index, (K) key);
    }

    public void removeAll(StyleOrigin origin) {
        int ordinal = origin.ordinal();
        for (Iterator<Entry<K, Integer>> i = keyMap.entrySet().iterator(); i.hasNext();) {
            Entry<K, Integer> e = i.next();
            Integer index = e.getValue();
            if (index < values.size()) {
                removeValue(ordinal, index, e.getKey());
            }
        }
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

    private V removeValue(int ordinal, int index, K key) {
        if (ordinal == -1) {
            throw new UnsupportedOperationException("can not remove styled value");
        }
        final int arrayIndex = index * numOrigins + ordinal;

        Object oldValue = arrayIndex < values.size() ? values.get(arrayIndex) : EMPTY;
        if (oldValue == EMPTY) {
            return null;
        } else {
            values.set(index * numOrigins + ordinal, EMPTY);
            sizes[ordinal]--;
            if (origin == StyleOrigin.USER) {
                @SuppressWarnings("unchecked")
                ChangeEvent change = new ChangeEvent(key, (V) oldValue, null, false, true);
                callObservers(this.origin, change);
            }

            return (V) oldValue;
        }

    }

    private V setValue(int ordinal, int index, K key, V newValue) {
        if (ordinal == -1) {
            throw new UnsupportedOperationException("can not set styled value");
        }
        V oldValue = (V) values.get(index * numOrigins + ordinal);
        if (oldValue == EMPTY) {
            sizes[ordinal]++;
        }
        values.set(index * numOrigins + ordinal, newValue);
        
        V returnValue = oldValue == EMPTY ? null : oldValue;
        if (!Objects.equals(oldValue, newValue)) {
            if (origin == StyleOrigin.USER) {
                ChangeEvent change = new ChangeEvent(key, returnValue, newValue, true, oldValue != EMPTY);
                callObservers(this.origin, change);
            }
        }

        return returnValue;
    }
    public int getIdentityHash() {return System.identityHashCode(values);}

    @Override
    public int size() {
        return sizes[originOrdinal];
    }

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
        public boolean contains(Object o) {
            if (!(o instanceof Entry)) {
                return false;
            }
            Entry<K, V> e = (Entry<K, V>) o;
            return SimpleStyleableMap.this.containsKey(e.getKey())
                    && Objects.equals(SimpleStyleableMap.this.get(e.getKey()), e.getValue());
        }

        @Override
        public Iterator<Entry<K, V>> iterator() {
            return new Iterator<Entry<K, V>>() {
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
        public boolean add(Entry<K, V> e) {
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
        public Iterator<K> iterator() {
            return new Iterator<K>() {
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

        @Override
        public V getValue() {
            return SimpleStyleableMap.this.getValue(SimpleStyleableMap.this.originOrdinal, index, key);
        }

        @Override
        public V setValue(V value) {
            V oldValue = SimpleStyleableMap.this.getValue(SimpleStyleableMap.this.originOrdinal, index, key);
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
        public Iterator<V> iterator() {
            return new Iterator<V>() {

                private Iterator<Entry<K, Integer>> entryIt = keyMap.entrySet().iterator();
                private boolean hasNext;
                private K nextKey;
                private K lastKey;
                private V nextValue;
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
