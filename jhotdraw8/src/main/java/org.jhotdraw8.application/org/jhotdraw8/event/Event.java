/*
 * @(#)Event.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.event;

import org.jhotdraw8.annotation.NonNull;

import java.util.EventObject;

/**
 * Event.
 *
 * @author Werner Randelshofer
 */
public class Event<E> extends EventObject {

    private final static long serialVersionUID = 1L;

    public Event(@NonNull E source) {
        super(source);
    }

    @NonNull
    @Override
    public E getSource() {
        @SuppressWarnings("unchecked")
        E temp = (E) super.getSource();
        return temp;
    }

}
