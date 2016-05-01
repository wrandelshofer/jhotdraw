/* @(#)WeakListener.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw.event;

import java.lang.ref.WeakReference;
import java.util.EventObject;
import java.util.function.Consumer;

/**
 * WeakListener.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public final class WeakListener<E extends EventObject> implements Listener<E>, javafx.beans.WeakListener {

    private final WeakReference<Listener<E>> ref;
    private Consumer<Listener<E>> removeListener;

    public WeakListener(Listener<E> listener, Consumer<Listener<E>> removeListener) {
        if (listener == null) {
            throw new NullPointerException("Listener must be specified.");
        }
        this.ref = new WeakReference<Listener<E>>(listener);
        this.removeListener = removeListener;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean wasGarbageCollected() {
        return (ref.get() == null);
    }
    @Override
    public void handle(E event) {
        Listener<E> listener = ref.get();
        if (listener != null) {
            listener.handle(event);
        } else {
            // The weakly reference listener has been garbage collected,
            // so this WeakListener will now unhook itself from the
            // source bean
            removeListener.accept(this);
        }
    }

}
