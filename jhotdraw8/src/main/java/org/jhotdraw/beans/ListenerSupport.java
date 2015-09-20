/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhotdraw.beans;

import java.util.ArrayList;
import java.util.function.Consumer;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakListener;

/**
 * This is a utility class for handling listeners of any kind.
 * <p>
 * This class properly handles {@code javafx.beans.WeakListener}s.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @param <L> the listener type
 */
public class ListenerSupport<L> {

    /** The listener list. Nullable. */
    private ArrayList<L> listeners;

    /** Adds a listener to the list.
     *
     * @param listener the listener */
    public void add(L listener) {
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        listeners.add(listener);
    }

    /** Removes a listener from the list.
     *
     * @param listener the listener */
    public void remove(L listener) {
        if (listeners != null) {
            int index = listeners.lastIndexOf(listener);
            if (index != -1) {
                listeners.remove(index);
                if (listeners.isEmpty()) {
                    listeners = null;
                }
            }
        }
    }

    /** Invokes the specified consumer for each registered listener.
     * <p>
     * This method removes garbage collected {@code WeakListener}s from the
     * listener list.
     * <p>
     * This method makes a copy of the listener list before invoking the
     * consumer.
     * Thus it is safe to add or remove listeners by the consumer.
     *
     * @param consumer The consumer is invoked for each listener */
    public void fire(Consumer<L> consumer) {
        if (listeners != null) {
            // We copy the list into an array here. This way, listeners can remove themselves
            // from the list during event handling.
            for (L l : new ArrayList<>(listeners)) {
                if ((l instanceof WeakListener)
                        && ((WeakListener) l).wasGarbageCollected()) {
                    remove(l);
                }
                consumer.accept(l);
            }
        }
    }
}
