/* @(#)DrawingModelEvent.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw;

import org.jhotdraw.collection.Key;
import org.jhotdraw.event.Event;

/**
 * DrawingModelEvent.
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DrawingModelEvent extends Event<DrawingModel> {

    enum EventType {

        FIGURE_ADDED,
        FIGURE_REMOVED,
        FIGURE_REQUEST_REMOVE,
        PROPERTY_CHANGED,
        FIGURE_INVALIDATED
    }
    private final Figure figure;
    private final Key<?> key;
    private final Object oldValue;
    private final Object newValue;

    private final Figure parent;
    private final int index;
    private final EventType eventType;

    public <T> DrawingModelEvent(DrawingModel source, Figure figure, Key<T> key, T oldValue, T newValue) {
        super(source);
        if (figure==null) {
            throw new NullPointerException("figure is null");
        }
        eventType = EventType.PROPERTY_CHANGED;
        this.figure = figure;
        this.key = key;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.parent = null;
        this.index = -1;
    }

    public <T> DrawingModelEvent(DrawingModel source, boolean wasAdded, Figure parent, Figure child, int index) {
        super(source);
        if (parent==null) {
            throw new NullPointerException("parent is null");
        }
        eventType = wasAdded ? EventType.FIGURE_ADDED : EventType.FIGURE_REMOVED;
        this.figure = child;
        this.key = null;
        this.oldValue = null;
        this.newValue = null;
        this.parent = parent;
        this.index = index;
    }
    public <T> DrawingModelEvent(DrawingModel source, Figure invalidatedFigure) {
        super(source);
        if (invalidatedFigure==null) {
            throw new NullPointerException("figure is null");
        }
        eventType = EventType.FIGURE_INVALIDATED;
        this.figure = invalidatedFigure;
        this.key = null;
        this.oldValue = null;
        this.newValue = null;
        this.parent = null;
        this.index = -1;
    }

    public boolean wasInvalidated() {
        return getEventType() == EventType.FIGURE_INVALIDATED;
    }
    
    public boolean wasAdded() {
        return getEventType() == EventType.FIGURE_ADDED;
    }

    public boolean wasRemoved() {
        return getEventType() == EventType.FIGURE_REMOVED;
    }

    public boolean wasChanged() {
        return getEventType() == EventType.PROPERTY_CHANGED;
    }

    /** The figure which was added, removed or of which a property changed. */
    public Figure getFigure() {
        return figure;
    }

    /** If the figure was changed, returns the property key. */
    public <T> Key<T> getKey() {
        return (Key<T>) key;
    }

    /** If the figure was changed, returns the old property value. */
    public <T> T getOldValue() {
        return (T) oldValue;
    }

    /** If the figure was changed, returns the new property value. */
    public <T> T getNewValue() {
        return (T) newValue;
    }

    /** If a child was added or removed, returns the parent. */
    public Figure getParent() {
        return parent;
    }
    /** If a child was added or removed, returns the child. */
    public Figure getChild() {
        return figure;
    }

    /** If the figure was added or removed, returns the child index. */
    public int getIndex() {
        return index;
    }

    public EventType getEventType() {
        return eventType;
    }

    @Override
    public String toString() {
        return "DrawingModelEvent{" + "figure=" + figure + ", key=" + key + ", oldValue=" + oldValue + ", newValue=" + newValue + ", parent=" + parent + ", index=" + index + ", eventType=" + eventType + ", source=" + source + '}';
    }
    
}
