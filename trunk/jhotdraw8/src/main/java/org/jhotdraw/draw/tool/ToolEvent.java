/* @(#)ToolEvent.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.tool;

import org.jhotdraw.draw.handle.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.collection.Key;
import org.jhotdraw.event.Event;

/**
 * ToolEvent.
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ToolEvent extends Event<Tool> {

    enum EventType {

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

    @Override
    public String toString() {
        return "ToolEvent{" + "type=" + eventType + " tool=" + getSource()
                + '}';
    }

}
