/*
 * @(#)ToolEvent.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.tool;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.event.Event;

/**
 * ToolEvent.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ToolEvent extends Event<Tool> {

    private final static long serialVersionUID = 1L;

    /**
     * Defines the event type.
     */
    public enum EventType {

        TOOL_STARTED,
        TOOL_DONE
    }

    private final EventType eventType;

    public <T> ToolEvent(Tool source, EventType type) {
        super(source);
        this.eventType = type;
    }

    public EventType getEventType() {
        return eventType;
    }

    @Nonnull
    @Override
    public String toString() {
        return "ToolEvent{" + "type=" + eventType + " tool=" + getSource()
                + '}';
    }

}
