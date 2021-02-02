/*
 * @(#)HandleEvent.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.handle;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.event.Event;

/**
 * HandleEvent.
 *
 * @author Werner Randelshofer
 */
public class HandleEvent extends Event<Handle> {

    private final static long serialVersionUID = 1L;

    public enum EventType {

        FIGURE_ADDED,
        FIGURE_REMOVED,
        PROPERTY_CHANGED,
        FIGURE_INVALIDATED
    }

    private final EventType eventType;

    public <T> HandleEvent(@NonNull Handle source, EventType type) {
        super(source);
        this.eventType = type;
    }

    public EventType getEventType() {
        return eventType;
    }

    @NonNull
    @Override
    public String toString() {
        return "HandleEvent{" + "type=" + eventType + " handle=" + getSource()
                + '}';
    }

}
