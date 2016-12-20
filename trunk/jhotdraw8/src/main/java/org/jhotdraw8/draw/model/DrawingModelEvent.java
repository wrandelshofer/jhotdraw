/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhotdraw8.draw.model;

import org.jhotdraw8.collection.Key;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.event.Event;

/**
 * DrawingModelEvent.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DrawingModelEvent extends Event<DrawingModel> {

    private final static long serialVersionUID = 1L;

    public enum EventType {
        /**
         * The root of the model changed.
         */
        ROOT_CHANGED,
        /**
         * All JavaFX Nodes in a subtree of the figures have been invalidated.
         */
        SUBTREE_NODES_CHANGED,
        /**
         * A single figure has been added to a parent.
         */
        FIGURE_ADDED_TO_PARENT,
        /**
         * A single figure has been removed from its parent.
         */
        FIGURE_REMOVED_FROM_PARENT,
        /**
         * A subtree of figures has been added to the drawing.
         */
        SUBTREE_ADDED_TO_DRAWING,
        /**
         * A subtree of figures has been removed from the drawing.
         */
        SUBTREE_REMOVED_FROM_DRAWING,
        /**
         * The JavaFX Node of a single figure has been invalidated.
         */
        NODE_CHANGED,
        /**
         * A property value has been changed.
         */
        PROPERTY_VALUE_CHANGED,
        /**
         * The layout of a single figure has changed.
         */
        LAYOUT_CHANGED,
        /**
         * The style of a single figure has changed.
         */
        STYLE_CHANGED,
        /**
         * The connection of a figure has changed.
         */
        DEPENDENCY_CHANGED,
        /**
         * The transform of a figure has changed.
         */
        TRANSFORM_CHANGED,
    }
    private final Figure figure;
    private final Key<?> key;
    private final Object oldValue;
    private final Object newValue;

    private final Figure parent;
    private final Drawing drawing;
    private final int index;
    private final DrawingModelEvent.EventType eventType;

    private DrawingModelEvent(DrawingModel source, EventType eventType, Figure figure, Figure parent, Drawing drawing, int index, Key<?> key, Object oldValue, Object newValue) {
        super(source);
        this.figure = figure;
        this.key = key;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.parent = parent;
        this.drawing = drawing;
        this.index = index;
        this.eventType = eventType;
    }

    public static DrawingModelEvent subtreeNodesInvalidated(DrawingModel source, Figure subtreeRot) {
        return new DrawingModelEvent(source, EventType.SUBTREE_NODES_CHANGED, subtreeRot, null, null, -1, null, null, null);
    }

    public static DrawingModelEvent figureAddedToParent(DrawingModel source, Figure child, Figure parent, int index) {
        return new DrawingModelEvent(source, EventType.FIGURE_ADDED_TO_PARENT, child, parent, null, index, null, null, null);
    }

    public static DrawingModelEvent figureRemovedFromParent(DrawingModel source, Figure child, Figure parent, int index) {
        return new DrawingModelEvent(source, EventType.FIGURE_REMOVED_FROM_PARENT, child, parent, null, index, null, null, null);
    }

    public static DrawingModelEvent figureAddedToDrawing(DrawingModel source, Drawing drawing, Figure figure) {
        return new DrawingModelEvent(source, EventType.SUBTREE_ADDED_TO_DRAWING, figure, null, drawing, -1, null, null, null);
    }

    public static DrawingModelEvent figureRemovedFromDrawing(DrawingModel source, Drawing drawing, Figure figure) {
        return new DrawingModelEvent(source, EventType.SUBTREE_REMOVED_FROM_DRAWING, figure, null, drawing, -1, null, null, null);
    }

    public static DrawingModelEvent nodeInvalidated(DrawingModel source, Figure figure) {
        return new DrawingModelEvent(source, EventType.NODE_CHANGED, figure, null, null, -1, null, null, null);
    }

    public static <T> DrawingModelEvent propertyValueChanged(DrawingModel source, Figure figure, Key<T> key, T oldValue, T newValue) {
        return new DrawingModelEvent(source, EventType.PROPERTY_VALUE_CHANGED, figure, null, null, -1, key, oldValue, newValue);
    }

    public static <T> DrawingModelEvent dependencyChanged(DrawingModel source, Figure figure) {
        return new DrawingModelEvent(source, EventType.DEPENDENCY_CHANGED, figure, null, null, -1, null, null, null);
    }

    public static <T> DrawingModelEvent transformChanged(DrawingModel source, Figure figure) {
        return new DrawingModelEvent(source, EventType.TRANSFORM_CHANGED, figure, null, null, -1, null, null, null);
    }

    public static <T> DrawingModelEvent layoutChanged(DrawingModel source, Figure figure) {
        return new DrawingModelEvent(source, EventType.LAYOUT_CHANGED, figure, null, null, -1, null, null, null);
    }

    public static <T> DrawingModelEvent styleInvalidated(DrawingModel source, Figure figure) {
        return new DrawingModelEvent(source, EventType.STYLE_CHANGED, figure, null, null, -1, null, null, null);
    }

    public static <T> DrawingModelEvent rootChanged(DrawingModel source, Drawing figure) {
        return new DrawingModelEvent(source, EventType.ROOT_CHANGED, figure, null, null, -1, null, null, null);
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
        @SuppressWarnings("unchecked")
        Key<T> tmp = (Key<T>) key;
        return tmp;
    }

    /**
     * If the figure was changed, returns the old property value.
     *
     * @param <T> the value type
     * @return the old value
     */
    public <T> T getOldValue() {
        @SuppressWarnings("unchecked")
        T temp = (T) oldValue;
        return temp;
    }

    /**
     * If the figure was changed, returns the new property value.
     *
     * @param <T> the value type
     * @return the new value
     */
    public <T> T getNewValue() {
        @SuppressWarnings("unchecked")
        T temp = (T) newValue;
        return temp;
    }

    /**
     * If a child was added or removed from a parent, returns the parent.
     *
     * @return the parent
     */
    public Figure getParent() {
        return parent;
    }

    /**
     * If a child was added or removed from a drawing, returns the drawing.
     *
     * @return the parent
     */
    public Drawing getDrawing() {
        return drawing;
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

    /**
     * Returns the event type.
     *
     * @return the event type
     */
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
