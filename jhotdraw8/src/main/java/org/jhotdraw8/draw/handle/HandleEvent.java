/* @(#)HandleEvent.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.handle;

import org.jhotdraw8.event.Event;

/**
 * HandleEvent.
 * @author Werner Randelshofer
 * @version $Id$
 */
public class HandleEvent extends Event<Handle> {

    private final static long serialVersionUID = 1L;

    enum EventType {

        FIGURE_ADDED,
        FIGURE_REMOVED,
        PROPERTY_CHANGED,
        FIGURE_INVALIDATED
    }
    private final EventType eventType;

    public <T> HandleEvent(Handle source, EventType type) {
        super(source);
        this.eventType = type;
    }

    public EventType getEventType() {
        return eventType;
    }

    @Override
    public String toString() {
        return "HandleEvent{" + "type=" + eventType + " handle=" + getSource()
                + '}';
    }

}
