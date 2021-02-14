/*
 * @(#)ToolEvent.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.tool;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.event.Event;

/**
 * ToolEvent.
 *
 * @author Werner Randelshofer
 */
public class ToolEvent extends Event<Tool> {

    private static final long serialVersionUID = 1L;

    /**
     * Defines the event type.
     */
    public enum EventType {

        TOOL_STARTED,
        TOOL_DONE
    }

    private final EventType eventType;

    public <T> ToolEvent(@NonNull Tool source, EventType type) {
        super(source);
        this.eventType = type;
    }

    public EventType getEventType() {
        return eventType;
    }

    @Override
    public @NonNull String toString() {
        return "ToolEvent{" + "type=" + eventType + " tool=" + getSource()
                + '}';
    }

}
