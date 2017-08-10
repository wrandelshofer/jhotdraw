/* @(#)ModifiableObservableSet.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

/**
 * ModifiableObservableSet.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ModifiableObservableSet<E> extends AbstractSet<E> implements ObservableSet<E> {

    private final Set<E> backingSet;
    private List<SetChangeListener<? super E>> changeListeners;
    private List<InvalidationListener> invalidationListeners;
    private InvalidationListener itemHandler = this::itemInvalidated;

    public ModifiableObservableSet(Collection<E> copyMe) {
        backingSet = new LinkedHashSet<>(copyMe);
    }

    public ModifiableObservableSet() {
        backingSet = new LinkedHashSet<>();
    }

    @Override
    public boolean add(E e) {
        boolean modified = backingSet.add(e);
        if (modified) {
            fireAdded(e);
            fireInvalidated();
        }
        return modified;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean modified = false;
        for (E e : c) {
            boolean added = backingSet.add(e);
            if (added) {
                fireAdded(e);
                modified = true;
            }
        }
        if (modified) {
            fireInvalidated();
        }
        return modified;
    }

    @Override
    public void addListener(InvalidationListener listener) {
        if (invalidationListeners == null) {
            invalidationListeners = new CopyOnWriteArrayList<>();
        }
        invalidationListeners.add(listener);
    }

    @Override
    public void addListener(SetChangeListener<? super E> listener) {
        if (changeListeners == null) {
            changeListeners = new CopyOnWriteArrayList<>();
        }
        changeListeners.add(listener);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void clear() {
        Object[] values = backingSet.toArray();
        backingSet.clear();
        for (Object v : values) {
            fireRemoved((E) v);
        }
        if (values.length > 0) {
            fireInvalidated();
        }
    }

    private void fireAdded(E e) {
        if (e instanceof Observable) {
            ((Observable) e).addListener(itemHandler);
        }
        if (changeListeners != null) {
            SetChangeListener.Change<E> change = new SetChangeListener.Change<E>(this) {
                @Override
                public E getElementAdded() {
                    return e;
                }

                @Override
                public E getElementRemoved() {
                    return null;
                }

                @Override
                public boolean wasAdded() {
                    return true;
                }

                @Override
                public boolean wasRemoved() {
                    return false;
                }
            };
            for (SetChangeListener<? super E> listener : changeListeners) {
                listener.onChanged(change);
            }
        }
    }

    /**
     * The method {@code invalidated()} can be overridden to receive
     * invalidation notifications. This is the preferred option in
     * {@code Objects} defining the property, because it requires less memory.
     *
     * The default implementation is empty.
     */
    protected void invalidated() {
    }

    private void itemInvalidated(Observable o) {
        fireInvalidated();
    }

    private void fireInvalidated() {
        invalidated();
        if (invalidationListeners != null) {
            for (InvalidationListener l : invalidationListeners) {
                l.invalidated(this);
            }
        }
    }

    private void fireRemoved(E e) {
        if (e instanceof Observable) {
            ((Observable) e).removeListener(itemHandler);
        }
        if (changeListeners != null) {
            SetChangeListener.Change<E> change = new SetChangeListener.Change<E>(this) {
                @Override
                public E getElementAdded() {
                    return null;
                }

                @Override
                public E getElementRemoved() {
                    return e;
                }

                @Override
                public boolean wasAdded() {
                    return false;
                }

                @Override
                public boolean wasRemoved() {
                    return true;
                }
            };
            for (SetChangeListener<? super E> listener : changeListeners) {
                listener.onChanged(change);
            }
        }
    }

    public void fireUpdated(E e) {
        fireRemoved(e);
        fireAdded(e);
        fireInvalidated();
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private final Iterator<? extends E> i = backingSet.iterator();
            private E current;

            @Override
            public boolean hasNext() {
                return i.hasNext();
            }

            @Override
            public E next() {
                return current = i.next();
            }

            @Override
            public void remove() {
                i.remove();
                fireRemoved(current);
                fireInvalidated();
            }
        };
    }

    @Override
    public boolean remove(Object o) {
        boolean modified = backingSet.remove(o);
        if (modified) {
            @SuppressWarnings("unchecked")
            final E e = (E) o;
            fireRemoved(e);
            fireInvalidated();
        }
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        for (Object o : c) {
            @SuppressWarnings("unchecked")
            final E e = (E) o;
            boolean removed = backingSet.remove(e);
            if (removed) {
                fireRemoved(e);
                modified = true;
            }
        }
        if (modified) {
            fireInvalidated();
        }
        return modified;
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        if (invalidationListeners != null) {
            invalidationListeners.remove(listener);
            if (invalidationListeners.isEmpty()) {
                invalidationListeners = null;
            }
        }
    }

    @Override
    public void removeListener(SetChangeListener<? super E> listener) {
        if (changeListeners != null) {
            changeListeners.remove(listener);
            if (changeListeners.isEmpty()) {
                changeListeners = null;
            }
        }
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        Iterator<E> it = backingSet.iterator();
        while (it.hasNext()) {
            E e = it.next();
            if (!c.contains(e)) {
                it.remove();
                fireRemoved(e);
                modified = true;
            }
        }
        if (modified) {
            fireInvalidated();
        }
        return modified;
    }

    @Override
    public int size() {
        return backingSet.size();
    }

}
