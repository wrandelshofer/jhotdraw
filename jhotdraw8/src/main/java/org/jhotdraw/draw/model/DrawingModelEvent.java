/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhotdraw.draw.model;

import org.jhotdraw.collection.Key;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.event.Event;

/**
 * DrawingModelEvent.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DrawingModelEvent extends Event<DrawingModel> {

    /**
     *
     */
    public final static Key<Drawing> ROOT_KEY = new Key<>("root", Drawing.class, null);

    public enum EventType {
        /** The root of the model changed. */
        ROOT_CHANGED,
        /** The structure
         * in a subtree of the figures changed. */
        SUBTREE_STRUCTURE_CHANGED,
        /** All
         * JavaFX Nodes in a subtree of the figures have been invalidated. */
        SUBTREE_NODES_INVALIDATED,
        /** A single figure has been
         * added. */
        FIGURE_ADDED,
        /** A single figure has been
         * removed. */
        FIGURE_REMOVED,
        /** The
         * JavaFX Node of a single figure has been invalidated. */
        NODE_INVALIDATED,
        /** The
         * layout of a single figure has been invalidated. */
        LAYOUT_INVALIDATED
    }
    private final Figure figure;
    private final Key<?> key;
    private final Object oldValue;
    private final Object newValue;

    private final Figure parent;
    private final int index;
    private final DrawingModelEvent.EventType eventType;

    private DrawingModelEvent(DrawingModel source, EventType eventType, Figure figure, Figure parent, int index, Key<?> key, Object oldValue, Object newValue) {
        super(source);
        this.figure = figure;
        this.key = key;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.parent = parent;
        this.index = index;
        this.eventType = eventType;
    }

    public static DrawingModelEvent subtreeStructureChanged(DrawingModel source, Figure root) {
        return new DrawingModelEvent(source, EventType.SUBTREE_STRUCTURE_CHANGED, root, null, -1, null, null, null);
    }

    public static DrawingModelEvent subtreeNodesInvalidated(DrawingModel source, Figure root) {
        return new DrawingModelEvent(source, EventType.SUBTREE_NODES_INVALIDATED, root, null, -1, null, null, null);
    }

    public static DrawingModelEvent figureAdded(DrawingModel source, Figure parent, Figure child, int index) {
        return new DrawingModelEvent(source, EventType.FIGURE_ADDED, child, parent, index, null, null, null);
    }

    public static DrawingModelEvent figureRemoved(DrawingModel source, Figure parent, Figure child, int index) {
        return new DrawingModelEvent(source, EventType.FIGURE_REMOVED, child, parent, index, null, null, null);
    }

    public static <T> DrawingModelEvent nodeInvalidated(DrawingModel source, Figure figure) {
        return new DrawingModelEvent(source, EventType.NODE_INVALIDATED, figure, null, -1, null, null, null);
    }

    public static <T> DrawingModelEvent layoutInvalidated(DrawingModel source, Figure figure) {
        return new DrawingModelEvent(source, EventType.LAYOUT_INVALIDATED, figure, null, -1, null, null, null);
    }

    public static <T> DrawingModelEvent rootChanged(DrawingModel source, Drawing figure) {
        return new DrawingModelEvent(source, EventType.ROOT_CHANGED, figure, null, -1, null, null, null);
    }

    /**
     * The figure which was added, removed or of which a property changed.
     *
     * @return the figure
     */
    public Figure getFigure() {
        return figure;
    }

    /**
     * If the figure was changed, returns the property key.
     *
     * @param <T> the value type
     * @return the key
     */
    public <T> Key<T> getKey() {
        return (Key<T>) key;
    }

    /**
     * If the figure was changed, returns the old property value.
     *
     * @param <T> the value type
     * @return the old value
     */
    public <T> T getOldValue() {
        return (T) oldValue;
    }

    /**
     * If the figure was changed, returns the new property value.
     *
     * @param <T> the value type
     * @return the new value
     */
    public <T> T getNewValue() {
        return (T) newValue;
    }

    /**
     * If a child was added or removed, returns the parent.
     *
     * @return the parent
     */
    public Figure getParent() {
        return parent;
    }

    /**
     * If a child was added or removed, returns the child.
     *
     * @return the child
     */
    public Figure getChild() {
        return figure;
    }

    /**
     * If the figure was added or removed, returns the child index.
     *
     * @return an index. Returns -1 if the figure was neither added or removed.
     */
    public int getIndex() {
        return index;
    }

    /** Returns the event type.
     *
     * @return the event type */
    public DrawingModelEvent.EventType getEventType() {
        return eventType;
    }

    @Override
    public String toString() {
        return "DrawingModelEvent{" + "figure=" + figure + ", key=" + key
                + ", oldValue=" + oldValue + ", newValue=" + newValue
                + ", parent=" + parent + ", index=" + index + ", eventType="
                + eventType + ", source=" + source + '}';
    }

}
