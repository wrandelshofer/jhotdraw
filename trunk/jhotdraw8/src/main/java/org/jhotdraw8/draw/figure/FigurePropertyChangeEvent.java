/* @(#)FigurePropertyChangeEvent.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw8.draw.figure;

import org.jhotdraw8.collection.Key;
import org.jhotdraw8.event.Event;


/**
 * FigurePropertyChangeEvent.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class FigurePropertyChangeEvent extends Event<Figure> {
    private final static long serialVersionUID = 1L;
    private final Key<?> key;
    private final Object oldValue;
    private final Object newValue;
    public enum EventType {
        WILL_CHANGE,
        CHANGED
    }
    private final EventType type;

    public <T> FigurePropertyChangeEvent(Figure source, EventType type, Key<T> key, T oldValue, T newValue) {
        super(source);
        this.key=key;
        this.oldValue=oldValue;
        this.newValue=newValue;
        this.type = type;
    }

    public Key<?> getKey() {
        return key;
    }

    public Object getOldValue() {
        return oldValue;
    }

    public Object getNewValue() {
        return newValue;
    }

    public EventType getType() {
        return type;
    }
}
