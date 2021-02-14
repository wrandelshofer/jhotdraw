/*
 * @(#)Event.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
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

    private static final long serialVersionUID = 1L;

    public Event(@NonNull E source) {
        super(source);
    }

    @Override
    public @NonNull E getSource() {
        @SuppressWarnings("unchecked")
        E temp = (E) super.getSource();
        return temp;
    }

}
