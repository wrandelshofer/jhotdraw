/* @(#)AbstractAction.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.tool;

import org.jhotdraw.app.action.*;
import java.util.HashMap;
import java.util.Optional;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import org.jhotdraw.app.AbstractDisableable;
import org.jhotdraw.beans.OptionalProperty;
import org.jhotdraw.collection.Key;
import org.jhotdraw.draw.DrawingView;

/**
 * AbstractAction.
 * @author Werner Randelshofer
 */
public abstract class AbstractTool extends AbstractDisableable implements Tool {

    /** The properties. */
    private MapProperty<Key<?>, Object> properties;
    /** The active view. */
    private final OptionalProperty<DrawingView> drawingView = new OptionalProperty<>(this, DRAWING_VIEW_PROPERTY);

    {
        drawingView.addListener((ObservableValue<? extends Optional<DrawingView>> observable, Optional<DrawingView> oldValue, Optional<DrawingView> newValue) -> {
            stopEditing();
        });
    }
    protected final Pane node = new Pane();
    {
        node.addEventHandler(MouseEvent.ANY, (MouseEvent event) -> {
            if (drawingView.get().isPresent()) {
                DrawingView dv = drawingView.get().get();
                EventType<? extends MouseEvent> type = event.getEventType();
                if (type == MouseEvent.MOUSE_MOVED) {
                    onMouseMoved(event,dv);
                } else if (type == MouseEvent.MOUSE_DRAGGED) {
                    onMouseDragged(event,dv);
                } else if (type == MouseEvent.MOUSE_EXITED) {
                    onMouseExited(event,dv);
                } else if (type == MouseEvent.MOUSE_ENTERED) {
                    onMouseEntered(event,dv);
                } else if (type == MouseEvent.MOUSE_RELEASED) {
                    onMouseReleased(event,dv);
                } else if (type == MouseEvent.MOUSE_PRESSED) {
                    onMousePressed(event,dv);
                } else if (type == MouseEvent.MOUSE_CLICKED) {
                    onMouseClicked(event,dv);
                }
                event.consume();
            }
        });
    }


    /** Creates a new instance.
     * Binds {@code disabled} to {@code disable}.
     */
    public AbstractTool() {
        this(null);

    }

    /** Creates a new instance.
     * Binds {@code disabled} to {@code disable}.
     * @param name the id of the tool
     */
    public AbstractTool(String name) {
        set(NAME, name);

    }

    @Override
    public final MapProperty<Key<?>, Object> properties() {
        if (properties == null) {
            properties = new SimpleMapProperty<>(FXCollections.observableMap(new HashMap<Key<?>, Object>()));
        }
        return properties;
    }

    @Override
    public OptionalProperty<DrawingView> drawingView() {
        return drawingView;
    }

    @Override
    public Node getNode() {
        return node;
    }

    protected abstract void stopEditing();

    protected void onMouseMoved(MouseEvent event, DrawingView dv) {
    }

    protected void onMouseDragged(MouseEvent event, DrawingView dv) {
    }

    protected void onMouseExited(MouseEvent event, DrawingView dv) {
    }

    protected void onMouseEntered(MouseEvent event, DrawingView dv) {
    }

    protected void onMouseReleased(MouseEvent event, DrawingView dv) {
    }

    protected void onMousePressed(MouseEvent event, DrawingView dv) {
    }

    protected void onMouseClicked(MouseEvent event, DrawingView dv) {
    }
}
