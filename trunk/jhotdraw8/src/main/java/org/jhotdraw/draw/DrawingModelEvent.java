/* @(#)DrawingModelEvent.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw;

import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import org.jhotdraw.collection.Key;

/**
 * DrawingModelEvent.
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DrawingModelEvent {

    enum EventType {

        FIGURE_ADDED,
        FIGURE_REMOVED,
        PROPERTY_CHANGED,
        FIGURE_INVALIDATED
    }
    private final Figure figure;
    private final Key<?> key;
    private final Object oldValue;
    private final Object newValue;

    private final Figure child;
    private final int index;
    private final EventType eventType;
    private final SimpleDrawingModel source;

    public <T> DrawingModelEvent(SimpleDrawingModel source, Figure figure, Key<T> key, T oldValue, T newValue) {
        if (figure==null) {
            throw new NullPointerException("figure is null");
        }
        eventType = EventType.PROPERTY_CHANGED;
        this.source = source;
        this.figure = figure;
        this.key = key;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.child = null;
        this.index = -1;
    }

    public <T> DrawingModelEvent(SimpleDrawingModel source, boolean wasAdded, Figure parent, Figure child, int index) {
        if (parent==null) {
            throw new NullPointerException("parent is null");
        }
        eventType = wasAdded ? EventType.FIGURE_ADDED : EventType.FIGURE_REMOVED;
        this.source = source;
        this.child = child;
        this.key = null;
        this.oldValue = null;
        this.newValue = null;
        this.figure = parent;
        this.index = index;
    }
    public <T> DrawingModelEvent(SimpleDrawingModel source, Figure invalidatedFigure) {
        if (invalidatedFigure==null) {
            throw new NullPointerException("figure is null");
        }
        eventType = EventType.FIGURE_INVALIDATED;
        this.source = source;
        this.figure = invalidatedFigure;
        this.key = null;
        this.oldValue = null;
        this.newValue = null;
        this.child = null;
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
        return figure;
    }
    /** If a child was added or removed, returns the child. */
    public Figure getChild() {
        return child;
    }

    /** If the figure was added or removed, returns the child index. */
    public int getIndex() {
        return index;
    }

    public EventType getEventType() {
        return eventType;
    }

    public SimpleDrawingModel getSource() {
        return source;
    }
    
}
