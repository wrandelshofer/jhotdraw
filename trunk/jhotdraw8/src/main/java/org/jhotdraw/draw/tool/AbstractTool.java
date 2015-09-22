/*
 * @(#)AbstractAction.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.tool;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import javafx.beans.property.MapProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import org.jhotdraw.app.AbstractDisableable;
import org.jhotdraw.collection.Key;
import org.jhotdraw.draw.DrawingModel;
import org.jhotdraw.draw.DrawingModelEvent;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.handle.HandleEvent;
import org.jhotdraw.event.Listener;
import org.jhotdraw.util.Resources;

/**
 * AbstractAction.
 *
 * @author Werner Randelshofer
 */
public abstract class AbstractTool extends AbstractDisableable implements Tool {

    // ---
    // Fields
    // ---
    /**
     * The properties.
     */
    private MapProperty<Key<?>, Object> properties;
    /**
     * The active view.
     */
    private final ObjectProperty<DrawingView> drawingView = new SimpleObjectProperty<>(this, DRAWING_VIEW_PROPERTY);

    {
        drawingView.addListener((ObservableValue<? extends DrawingView> observable, DrawingView oldValue, DrawingView newValue) -> {
            stopEditing();
        });
    }
    protected final BorderPane node = new BorderPane();
    /**
     * This is the set of figures which are out of sync with their layout.
     */
    private final HashSet<Figure> dirtyLayouts = new HashSet<>();

    /**
     * Listeners.
     */
    private final LinkedList<Listener<HandleEvent>> handleListeners = new LinkedList<>();

    {
        node.addEventHandler(MouseEvent.ANY, (MouseEvent event) -> {
            if (drawingView.get() != null) {
                DrawingView dv = drawingView.get();
                registerLayoutInvalidationListener(dv);
                EventType<? extends MouseEvent> type = event.getEventType();
                if (type == MouseEvent.MOUSE_MOVED) {
                    onMouseMoved(event, dv);
                } else if (type == MouseEvent.MOUSE_DRAGGED) {
                    onMouseDragged(event, dv);
                } else if (type == MouseEvent.MOUSE_EXITED) {
                    onMouseExited(event, dv);
                } else if (type == MouseEvent.MOUSE_ENTERED) {
                    onMouseEntered(event, dv);
                } else if (type == MouseEvent.MOUSE_RELEASED) {
                    onMouseReleased(event, dv);
                } else if (type == MouseEvent.MOUSE_PRESSED) {
                    onMousePressed(event, dv);
                } else if (type == MouseEvent.MOUSE_CLICKED) {
                    onMouseClicked(event, dv);
                }
                event.consume();
                unregisterLayoutInvalidationListener(dv);
                validateLayouts(dv);
            }
        });
        node.addEventHandler(KeyEvent.ANY, (KeyEvent event) -> {
            if (drawingView.get() != null) {
                DrawingView dv = drawingView.get();
                registerLayoutInvalidationListener(dv);
                EventType<? extends KeyEvent> type = event.getEventType();
                if (type == KeyEvent.KEY_PRESSED) {
                    onKeyPressed(event, dv);
                } else if (type == KeyEvent.KEY_RELEASED) {
                    onKeyReleased(event, dv);
                } else if (type == KeyEvent.KEY_TYPED) {
                    onKeyTyped(event, dv);
                }
                event.consume();
                unregisterLayoutInvalidationListener(dv);
                validateLayouts(dv);
            }
        });
    }
    /**
     * Listeners.
     */
    private final LinkedList<Listener<ToolEvent>> toolListeners = new LinkedList<>();
    /**
     * Handler for drawing model events.
     */
    private final Listener<DrawingModelEvent> layoutInvalidationListener = new Listener<DrawingModelEvent>() {

        @Override
        public void handle(DrawingModelEvent event) {
            switch (event.getEventType()) {
            case FIGURE_ADDED:
            case FIGURE_REMOVED:
            case NODE_INVALIDATED:
            case ROOT_CHANGED:
            case SUBTREE_NODES_INVALIDATED:
                // not my business
                break;
            case LAYOUT_INVALIDATED:
                invalidateLayout(event.getFigure());
                break;
            case SUBTREE_STRUCTURE_CHANGED:
                invalidateLayout(event.getFigure());
                break;
            default:
                throw new UnsupportedOperationException(event.getEventType()
                        + "not supported");
            }
        }
    };

    // ---
    // Constructors
    // ---
    /**
     * Creates a new instance.
     */
    public AbstractTool() {
        this(null, null);

    }

    /**
     * Creates a new instance.
     *
     * @param name the id of the tool
     * @param rsrc iff nonnull, the resource is applied to the tool
     */
    public AbstractTool(String name, Resources rsrc) {
        set(NAME, name);
        if (rsrc != null) {
            applyResources(rsrc);
        }

    }

    // ---
    // Properties
    // ---
    @Override
    public final MapProperty<Key<?>, Object> properties() {
        if (properties == null) {
            properties = new SimpleMapProperty<>(FXCollections.observableMap(new HashMap<Key<?>, Object>()));
        }
        return properties;
    }

    @Override
    public ObjectProperty<DrawingView> drawingViewProperty() {
        return drawingView;
    }

    // ---
    // Behaviors
    // ---
    protected void applyResources(Resources rsrc) {
        String name = get(NAME);
        set(LABEL, rsrc.getTextProperty(name));
        set(LARGE_ICON_KEY, rsrc.getLargeIconProperty(name, getClass()));
        set(SHORT_DESCRIPTION, rsrc.getToolTipTextProperty(name));
    }

    @Override
    public Node getNode() {
        return node;
    }

    protected void stopEditing() {
    }

    /**
     * Deletes the selection. Depending on the tool, this could be selected
     * figures, selected points or selected text.
     */
    @Override
    public void editDelete() {
        if (getDrawingView() != null) {
            DrawingView v = getDrawingView();
            v.getDrawing().children().removeAll(v.getSelectedFigures());
        }
    }

    /**
     * Cuts the selection into the clipboard. Depending on the tool, this could
     * be selected figures, selected points or selected text.
     */
    @Override
    public void editCut() {
    }

    /**
     * Copies the selection into the clipboard. Depending on the tool, this
     * could be selected figures, selected points or selected text.
     */
    @Override
    public void editCopy() {
    }

    /**
     * Duplicates the selection. Depending on the tool, this could be selected
     * figures, selected points or selected text.
     */
    @Override
    public void editDuplicate() {
    }

    /**
     * Pastes the contents of the clipboard. Depending on the tool, this could
     * be selected figures, selected points or selected text.
     */
    @Override
    public void editPaste() {
    }

    // ---
    // Event handlers
    // ----
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

    protected void onKeyPressed(KeyEvent event, DrawingView dv) {
    }

    protected void onKeyReleased(KeyEvent event, DrawingView dv) {
    }

    protected void onKeyTyped(KeyEvent event, DrawingView dv) {
    }

    // ---
    // Listeners
    // ---
    @Override
    public void addToolListener(Listener<ToolEvent> listener) {
        toolListeners.add(listener);
    }

    @Override
    public void removeToolListener(Listener<ToolEvent> listener) {
        toolListeners.remove(listener);
    }

    protected void fire(ToolEvent event) {
        for (Listener<ToolEvent> l : toolListeners) {
            l.handle(event);
        }
    }

    protected void fireToolStarted() {
        fire(new ToolEvent(this, ToolEvent.EventType.TOOL_STARTED));
    }

    protected void fireToolDone() {
        fire(new ToolEvent(this, ToolEvent.EventType.TOOL_DONE));
    }

    /**
     * Gets the active drawing view.
     */
    public DrawingView getDrawingView() {
        return drawingViewProperty().get();
    }

    /**
     * Sets the active drawing view.
     * <p>
     * This method is invoked by {@link DrawingView} when the tool is set or
     * unset on the drawing view.
     */
    public void setDrawingView(DrawingView drawingView) {
        drawingViewProperty().set(drawingView);
    }

    protected void registerLayoutInvalidationListener(DrawingView dv) {
        dv.getDrawingModel().addDrawingModelListener(layoutInvalidationListener);
    }

    protected void unregisterLayoutInvalidationListener(DrawingView dv) {
        dv.getDrawingModel().removeDrawingModelListener(layoutInvalidationListener);
    }

    protected void invalidateLayout(Figure figure) {
        dirtyLayouts.add(figure);
    }

    protected void validateLayouts(DrawingView dv) {
        DrawingModel dm = dv.getDrawingModel();
        LinkedList<Figure> fs = new LinkedList<>(dirtyLayouts);
        dirtyLayouts.clear();
        for (Figure f : fs) {
            dm.layout(f);
        }
    }

}
