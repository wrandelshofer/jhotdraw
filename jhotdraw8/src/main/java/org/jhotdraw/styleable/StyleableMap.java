/* @(#)StyleableMap.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.styleable;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javafx.beans.InvalidationListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.css.StyleOrigin;
import org.jhotdraw.beans.ListenerSupport;
import org.jhotdraw.collection.Key;

/**
 * {@code StyleableMap} is a map which stores separate values for each
 * {@code StyleOrigin}.
 *
 * @author Werner Randelshofer
 */
public class StyleableMap<K, V> implements ObservableMap<K, V> {

    private static class StyledValue {

        /**
         * Magic value object to indicate that we have not stored a value.
         */
        private final static Object NO_VALUE = new Object();
        private final static StyleOrigin[] ORIGINS = StyleOrigin.values();
        public StyleOrigin origin;
        /**
         * Contains a slot for each of the four possible origins. The ordinal
         * number of StyleOrigin is used as an index.
         */
        private final Object[] values = {NO_VALUE, NO_VALUE, NO_VALUE, NO_VALUE};

        public <T> T removeValue(StyleOrigin origin) {
            if (hasValue(origin)) {
                int i = origin.ordinal();
                @SuppressWarnings("unchecked")
                T oldValue = (T) values[i];
                values[i] = NO_VALUE;
                if (this.origin == origin) {
                    this.origin = null;
                    for (int j = origin.ordinal() - 1; j >= 0; j--) {
                        if (values[j] != NO_VALUE) {
                            this.origin = ORIGINS[j];
                            break;
                        }
                    }
                }
                return oldValue;
            } else {
                return null;
            }
        }

        public <T> T setValue(StyleOrigin origin, T newValue) {
            int i = origin.ordinal();
            @SuppressWarnings("unchecked")
            T oldValue = (T) values[i];
            values[i] = newValue;
            if (this.origin == null || origin.ordinal() > this.origin.ordinal()) {
                this.origin = origin;
            }
            return oldValue;
        }

        public boolean hasValue(StyleOrigin origin) {
            return values[origin.ordinal()] != NO_VALUE;
        }

        public boolean isEmpty() {
            return origin == null;
        }

        private <T> T getValue(StyleOrigin styleOrigin) {
            @SuppressWarnings("unchecked")
            T ret = (T) values[styleOrigin.ordinal()];
            return ret==NO_VALUE?null:ret;
        }
        private <T> T getValue(StyleOrigin styleOrigin, T defaultValue) {
            @SuppressWarnings("unchecked")
            T ret = (T) values[styleOrigin==null?0:styleOrigin.ordinal()];
            return ret==NO_VALUE?defaultValue:ret;
        }

        private StyleOrigin getOrigin() {
            return origin;
        }
    }
    private ObservableEntrySet entrySet;
    private ObservableKeySet keySet;
    private ObservableValues values;

    private ListenerSupport<MapChangeListener<? super K, ? super V>> changeListenerSupport;
    private ListenerSupport<InvalidationListener> invalidationListenerSupport;
    private final Map<K, StyledValue> backingMap;

    public StyleableMap() {
        this.backingMap = new HashMap<>();
    }

    private class SimpleChange extends MapChangeListener.Change<K, V> {

        private final K key;
        private final V old;
        private final V added;
        private final boolean wasAdded;
        private final boolean wasRemoved;

        public SimpleChange(K key, V old, V added, boolean wasAdded, boolean wasRemoved) {
            super(StyleableMap.this);
            assert (wasAdded || wasRemoved);
            this.key = key;
            this.old = old;
            this.added = added;
            this.wasAdded = wasAdded;
            this.wasRemoved = wasRemoved;
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

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            if (wasAdded) {
                if (wasRemoved) {
                    builder.append("replaced ").append(old).append("by ").append(added);
                } else {
                    builder.append("added ").append(added);
                }
            } else {
                builder.append("removed ").append(old);
            }
            builder.append(" at key ").append(key);
            return builder.toString();
        }

    }

    protected void callObservers(MapChangeListener.Change<K, V> change) {
        if (changeListenerSupport != null) {
            changeListenerSupport.fire(l -> l.onChanged(change));
        }
        if (invalidationListenerSupport != null) {
            invalidationListenerSupport.fire(l -> l.invalidated(this));
        }
    }

    @Override
    public void addListener(InvalidationListener listener) {
        if (invalidationListenerSupport == null) {
            invalidationListenerSupport = new ListenerSupport<>();
        }
        invalidationListenerSupport.add(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        if (invalidationListenerSupport != null) {
            invalidationListenerSupport.remove(listener);
        }
    }

    @Override
    public void addListener(MapChangeListener<? super K, ? super V> observer) {
        if (changeListenerSupport == null) {
            changeListenerSupport = new ListenerSupport<>();
        }
        changeListenerSupport.add(observer);
    }

    @Override
    public void removeListener(MapChangeListener<? super K, ? super V> observer) {
        if (changeListenerSupport != null) {
            changeListenerSupport.remove(observer);
        }
    }

    @Override
    public int size() {
        return size(StyleOrigin.USER);
    }

    @Override
    public boolean isEmpty() {
        return isEmpty(StyleOrigin.USER);
    }

    public int size(StyleOrigin o) {
        int count = 0;
        for (Map.Entry<K, StyledValue> e : backingMap.entrySet()) {
            if (e.getValue().hasValue(o)) {
                count++;
            }
        }
        return count;
    }

    public boolean isEmpty(StyleOrigin o) {
        for (Map.Entry<K, StyledValue> e : backingMap.entrySet()) {
            if (e.getValue().hasValue(o)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsKey(Object key) {
        return containsKey(StyleOrigin.USER, key);
    }

    public boolean containsKey(StyleOrigin o, Object key) {
        StyledValue sv = backingMap.get(key);
        return sv == null ? false : sv.hasValue(o);
    }

    public <T> boolean containsStyledKey(Key<T> key) {
        return backingMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return containsValue(StyleOrigin.USER, value);
    }

    public boolean containsValue(StyleOrigin o, Object value) {
        for (Map.Entry<K, StyledValue> e : backingMap.entrySet()) {
            if (e.getValue().hasValue(o)) {
                Object v = e.getValue().getValue(o);
                if (v == value
                        || (value != null && value.equals(v))) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public V get(Object key) {
        @SuppressWarnings("unchecked")
        V ret = get(StyleOrigin.USER, (K) key);
        return ret;
    }

    public V get(StyleOrigin o, K key) {
        StyledValue sv = backingMap.get(key);
        @SuppressWarnings("unchecked")
        V ret = sv == null ? null : (V) sv.getValue(o);
        return ret;
    }

    public V getStyled(K key) {
        StyledValue sv = backingMap.get(key);
        @SuppressWarnings("unchecked")
        V ret = sv == null ? null : (V) sv.getValue(sv.getOrigin());
        return ret;
    }
    public V getStyled(K key, V defaultValue) {
        StyledValue sv = backingMap.get(key);
        @SuppressWarnings("unchecked")
        V ret = (sv == null) ? defaultValue : sv.getValue(sv.getOrigin(),defaultValue);
        return ret;
    }

    public StyleOrigin getStyleOrigin(Object key) {
        StyledValue sv = backingMap.get(key);
        return sv == null ? null : sv.getOrigin();
    }

    @Override
    public V put(K key, V value) {
        return put(StyleOrigin.USER, key, value);
    }

    public V put(StyleOrigin o, K key, V value) {
        StyledValue sv = backingMap.get(key);
        if (sv == null) {
            sv = new StyledValue();
            backingMap.put(key, sv);
        }

        boolean hadValue = sv.hasValue(o);
        V ret = sv.setValue(o, value);
        if (o == StyleOrigin.USER) {
            if (ret == null && value != null || ret != null && !ret.equals(value)) {
                callObservers(new SimpleChange(key, ret, value, true, hadValue));
            }
        }
        return ret;
    }

    @Override
    @SuppressWarnings("unchecked")
    public V remove(Object key) {
        return remove(StyleOrigin.USER, (K) key);
    }

    public V remove(StyleOrigin o, K key) {
        StyledValue sv = backingMap.get(key);
        V ret = null;
        boolean hadValue;
        if (sv != null && sv.hasValue(o)) {
            hadValue = true;
            ret = sv.removeValue(o);
            // We do not remove empty values because the values will be
            // probably set right again after the map was cleared.
            /*if (sv.isEmpty()) {
             backingMap.remove(key);
             }*/
        } else {
            hadValue = false;
            ret = null;
        }
        if (hadValue && o == StyleOrigin.USER) {
            callObservers(new SimpleChange(key, ret, null, false, true));
        }
        return ret;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    public void putAll(StyleOrigin o, Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
            put(o, e.getKey(), e.getValue());
        }
    }

    @Override
    public void clear() {
        clear(StyleOrigin.USER);
    }

    public void removeAll(StyleOrigin o) {
        clear(o);
    }

    public void clear(StyleOrigin o) {
        for (Iterator<Entry<K, StyledValue>> i = backingMap.entrySet().iterator(); i.hasNext();) {
            Entry<K, StyledValue> e = i.next();
            K key = e.getKey();
            StyledValue sv = e.getValue();
            @SuppressWarnings("unchecked")
            V val = (V) sv.removeValue(o);
            // We do not remove empty values because the values will be
            // probably set right again after the map was cleared.
            /*if (sv.isEmpty()) {
             i.remove();
             }*/
            if (o == StyleOrigin.USER) {
                callObservers(new SimpleChange(key, val, null, false, true));
            }
        }
    }

    public void clearNonUserValues() {
        for (Iterator<Entry<K, StyledValue>> i = backingMap.entrySet().iterator(); i.hasNext();) {
            Entry<K, StyledValue> e = i.next();
            K key = e.getKey();
            StyledValue sv = e.getValue();
            sv.removeValue(StyleOrigin.INLINE);
            sv.removeValue(StyleOrigin.AUTHOR);
            sv.removeValue(StyleOrigin.USER_AGENT);
             // We do not remove empty values because the values will be
            // probably set right again after the map was cleared.
            /*if (sv.isEmpty()) {
             i.remove();
             }*/
        }
    }

    @Override
    public Set<K> keySet() {
        if (keySet == null) {
            keySet = new ObservableKeySet(StyleOrigin.USER);
        }
        return keySet;
    }

    public Set<K> keySet(StyleOrigin o) {
        return new ObservableKeySet(o);
    }

    @Override
    public Collection<V> values() {
        if (values == null) {
            values = new ObservableValues(StyleOrigin.USER);
        }
        return values;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        if (entrySet == null) {
            entrySet = new ObservableEntrySet(StyleOrigin.USER);
        }
        return entrySet;
    }

    @Override
    public String toString() {
        return backingMap.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return backingMap.equals(obj);
    }

    @Override
    public int hashCode() {
        return backingMap.hashCode();
    }

    private class ObservableKeySet implements Set<K> {

        private final StyleOrigin ksetOrigin;

        public ObservableKeySet(StyleOrigin o) {
            this.ksetOrigin = o;
        }

        @Override
        public int size() {
            return StyleableMap.this.size(ksetOrigin);
        }

        @Override
        public boolean isEmpty() {
            return StyleableMap.this.isEmpty(ksetOrigin);
        }

        @Override
        public boolean contains(Object o) {
            return StyleableMap.this.containsKey(ksetOrigin, o);
        }

        @Override
        public Iterator<K> iterator() {
            return new Iterator<K>() {

                private Iterator<Entry<K, StyledValue>> entryIt = backingMap.entrySet().iterator();
                private boolean hasNext;
                private K nextKey;
                private K lastKey;

                {
                    advance();
                }

                private void advance() {
                    while (entryIt.hasNext()) {
                        Entry<K, StyledValue> entry = entryIt.next();
                        if (entry.getValue().hasValue(ksetOrigin)) {
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
                    StyleableMap.this.remove(ksetOrigin, lastKey);
                }

            };
        }

        @Override
        public Object[] toArray() {
            Object[] a = new Object[size()];
            int i = 0;
            for (Iterator<K> it = iterator(); it.hasNext();) {
                a[i++] = it.next();
            }
            return a;
        }

        @Override
        public <T> T[] toArray(T[] a) {
            int size = size();
            @SuppressWarnings("unchecked")
            T[] r = a.length >= size ? a
                    : (T[]) java.lang.reflect.Array
                    .newInstance(a.getClass().getComponentType(), size);
            a = r;
            int i = 0;
            for (Iterator<K> it = iterator(); it.hasNext();) {
                @SuppressWarnings("unchecked")
                T tmp = (T) it.next();
                a[i++] = tmp;
            }
            return a;
        }

        @Override
        public boolean add(K e) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public boolean remove(Object o) {
            return StyleableMap.this.remove(o) != null;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            for (Object k : c) {
                if (!containsKey(ksetOrigin, k)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean addAll(Collection<? extends K> c) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return removeRetain(c, false);
        }

        private boolean removeRetain(Collection<?> c, boolean remove) {
            boolean removed = false;
            for (Iterator<Entry<K, StyledValue>> i = backingMap.entrySet().iterator(); i.hasNext();) {
                Entry<K, StyledValue> e = i.next();
                if (remove == c.contains(e.getKey())) {
                    K key = e.getKey();
                    StyledValue sv = e.getValue();
                    if (sv.hasValue(ksetOrigin)) {
                        removed = true;
                        V value = sv.removeValue(ksetOrigin);
                        // We do not remove empty values because the values will be
                        // probably set right again after the map was cleared.
                        /*if (sv.isEmpty()) {
                            i.remove();
                        }*/
                        callObservers(new SimpleChange(key, value, null, false, true));
                    }
                }
            }
            return removed;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return removeRetain(c, true);
        }

        @Override
        public void clear() {
            StyleableMap.this.clear(ksetOrigin);
        }

        @Override
        public String toString() {
            Iterator<K> it = iterator();
            if (!it.hasNext()) {
                return "[]";
            }

            StringBuilder sb = new StringBuilder();
            sb.append('[');
            for (;;) {
                K e = it.next();
                sb.append(e == this ? "(this Collection)" : e);
                if (!it.hasNext()) {
                    return sb.append(']').toString();
                }
                sb.append(',').append(' ');
            }
        }

        @Override
        public boolean equals(Object obj) {
            return backingMap.keySet().equals(obj);
        }

        @Override
        public int hashCode() {
            return backingMap.keySet().hashCode();
        }

    }

    private class ObservableValues implements Collection<V> {

        private final StyleOrigin ksetOrigin;

        public ObservableValues(StyleOrigin o) {
            this.ksetOrigin = o;
        }

        @Override
        public int size() {
            return StyleableMap.this.size(ksetOrigin);
        }

        @Override
        public boolean isEmpty() {
            return StyleableMap.this.isEmpty(ksetOrigin);
        }

        @Override
        public boolean contains(Object o) {
            return StyleableMap.this.containsValue(ksetOrigin, o);
        }

        @Override
        public Iterator<V> iterator() {
            return new Iterator<V>() {

                private Iterator<Entry<K, StyledValue>> entryIt = backingMap.entrySet().iterator();
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
                        Entry<K, StyledValue> entry = entryIt.next();
                        if (entry.getValue().hasValue(ksetOrigin)) {
                            nextKey = entry.getKey();
                            nextValue = entry.getValue().getValue(ksetOrigin);
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
                    StyleableMap.this.remove(ksetOrigin, lastKey);
                }

            };
        }

        @Override
        public Object[] toArray() {
            Object[] a = new Object[size()];
            int i = 0;
            for (Iterator<V> it = iterator(); it.hasNext();) {
                a[i++] = it.next();
            }
            return a;
        }

        @Override
        public <T> T[] toArray(T[] a) {
            int size = size();
            @SuppressWarnings("unchecked")
            T[] r = a.length >= size ? a
                    : (T[]) java.lang.reflect.Array
                    .newInstance(a.getClass().getComponentType(), size);
            a = r;
            int i = 0;
            for (Iterator<V> it = iterator(); it.hasNext();) {
                @SuppressWarnings("unchecked")
                T tmp = (T) it.next();
                a[i++] = tmp;
            }
            return a;
        }

        @Override
        public boolean add(V e) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public boolean remove(Object o) {
            for (Iterator<V> i = iterator(); i.hasNext();) {
                if (i.next().equals(o)) {
                    i.remove();
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            for (Object k : c) {
                if (!containsValue(ksetOrigin, k)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean addAll(Collection<? extends V> c) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return removeRetain(c, true);
        }

        private boolean removeRetain(Collection<?> c, boolean remove) {
            boolean removed = false;
            for (Iterator<Entry<K, StyledValue>> i = backingMap.entrySet().iterator(); i.hasNext();) {
                Entry<K, StyledValue> e = i.next();
                if (remove == c.contains(e.getValue())) {
                    K key = e.getKey();
                    StyledValue sv = e.getValue();
                    if (sv.hasValue(ksetOrigin)) {
                        removed = true;
                        V value = sv.removeValue(ksetOrigin);
                        if (sv.isEmpty()) {
                            i.remove();
                        }
                        callObservers(new SimpleChange(key, value, null, false, true));
                    }
                }
            }
            return removed;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return removeRetain(c, false);
        }

        @Override
        public void clear() {
            StyleableMap.this.clear(ksetOrigin);
        }

        @Override
        public String toString() {
            Iterator<V> it = iterator();
            if (!it.hasNext()) {
                return "[]";
            }

            StringBuilder sb = new StringBuilder();
            sb.append('[');
            for (;;) {
                V e = it.next();
                sb.append(e == this ? "(this Collection)" : e);
                if (!it.hasNext()) {
                    return sb.append(']').toString();
                }
                sb.append(',').append(' ');
            }
        }

        @Override
        public boolean equals(Object obj) {
            return backingMap.values().equals(obj);
        }

        @Override
        public int hashCode() {
            return backingMap.values().hashCode();
        }

    }

    private class ObservableEntry implements Entry<K, V> {

        private final StyleOrigin oeOrigin;
        private final Entry<K, StyledValue> backingEntry;

        public ObservableEntry(StyleOrigin o, Entry<K, StyledValue> backingEntry) {
            this.oeOrigin = o;
            this.backingEntry = backingEntry;
        }

        @Override
        public K getKey() {
            return backingEntry.getKey();
        }

        @Override
        public V getValue() {
            return backingEntry.getValue().getValue(oeOrigin);
        }

        @Override
        public V setValue(V value) {
            V oldValue = backingEntry.getValue().setValue(oeOrigin, value);
            callObservers(new SimpleChange(getKey(), oldValue, value, true, true));
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

    private class ObservableEntrySet implements Set<Entry<K, V>> {

        private final StyleOrigin oesOrigin;

        public ObservableEntrySet(StyleOrigin oesOrigin) {
            this.oesOrigin = oesOrigin;
        }

        @Override
        public int size() {
            return StyleableMap.this.size(oesOrigin);
        }

        @Override
        public boolean isEmpty() {
            return StyleableMap.this.isEmpty(oesOrigin);
        }

        @Override
        public boolean contains(Object o) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Iterator<Entry<K, V>> iterator() {
            return new Iterator<Entry<K, V>>() {

                private Iterator<Entry<K, StyledValue>> entryIt = backingMap.entrySet().iterator();
                private boolean hasNext;
                private Entry<K, StyledValue> nextEntry;
                private Entry<K, StyledValue> lastEntry;

                {
                    advance();
                }

                private void advance() {
                    while (entryIt.hasNext()) {
                        Entry<K, StyledValue> entry = entryIt.next();
                        if (entry.getValue().hasValue(oesOrigin)) {
                            nextEntry = entry;
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
                    lastEntry = nextEntry;
                    advance();
                    return new ObservableEntry(oesOrigin, lastEntry);
                }

                @Override
                public void remove() {
                    StyleableMap.this.remove(oesOrigin, lastEntry.getKey());
                }

            };
        }

        @Override
        @SuppressWarnings("unchecked")
        public Object[] toArray() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T[] toArray(T[] a) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public boolean add(Entry<K, V> e) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean remove(Object o) {
            Entry<K, V> entry = (Entry<K, V>) o;
            return StyleableMap.this.remove(oesOrigin, entry.getKey()) != null;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public boolean addAll(Collection<? extends Entry<K, V>> c) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return removeRetain(c, false);
        }

        private boolean removeRetain(Collection<?> c, boolean remove) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return removeRetain(c, true);
        }

        @Override
        public void clear() {
            StyleableMap.this.clear(oesOrigin);
        }

        @Override
        public String toString() {
            return backingMap.entrySet().toString();
        }

        @Override
        public boolean equals(Object obj) {
            return backingMap.entrySet().equals(obj);
        }

        @Override
        public int hashCode() {
            return backingMap.entrySet().hashCode();
        }

    }

}
