/*
 * @(#)DrawingModelEvent.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.model;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.event.Event;

/**
 * DrawingModelEvent.
 *
 * @author Werner Randelshofer
 */
public class DrawingModelEvent extends Event<DrawingModel> {

    private final static long serialVersionUID = 1L;

    public enum EventType {

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

    private DrawingModelEvent(@NonNull DrawingModel source, EventType eventType, Figure figure, Figure parent, Drawing drawing, int index, Key<?> key, Object oldValue, Object newValue) {
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

    @NonNull
    public static <T> DrawingModelEvent propertyValueChanged(@NonNull DrawingModel source, Figure figure, Key<T> key, T oldValue, T newValue) {
        return new DrawingModelEvent(source, EventType.PROPERTY_VALUE_CHANGED, figure, null, null, -1, key, oldValue, newValue);
    }

    @NonNull
    public static <T> DrawingModelEvent transformChanged(@NonNull DrawingModel source, Figure figure) {
        return new DrawingModelEvent(source, EventType.TRANSFORM_CHANGED, figure, null, null, -1, null, null, null);
    }

    @NonNull
    public static <T> DrawingModelEvent layoutChanged(@NonNull DrawingModel source, Figure figure) {
        return new DrawingModelEvent(source, EventType.LAYOUT_CHANGED, figure, null, null, -1, null, null, null);
    }

    @NonNull
    public static <T> DrawingModelEvent styleInvalidated(@NonNull DrawingModel source, Figure figure) {
        return new DrawingModelEvent(source, EventType.STYLE_CHANGED, figure, null, null, -1, null, null, null);
    }

    /**
     * The figure which was added, removed or of which a property changed.
     *
     * @return the figure
     */
    public Figure getNode() {
        return figure;
    }

    /**
     * If the figure was changed, returns the property key.
     *
     * @param <T> the value type
     * @return the key
     */
    @NonNull
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
    @Nullable
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
    @Nullable
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
     * @return the drawing
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

    @NonNull
    @Override
    public String toString() {
        return "DrawingModelEvent{"
                + (figure == null ? null : figure.getTypeSelector() + "@" + Integer.toHexString(System.identityHashCode(figure)))
                + ", key=" + key
                + ", oldValue=" + oldValue + ", newValue=" + newValue
                + ", parent=" + (parent == null ? null : parent.getTypeSelector() + "@" + Integer.toHexString(System.identityHashCode(parent)))
                + ", index=" + index + ", eventType="
                + eventType + ", source=" + source + '}';
    }

}
