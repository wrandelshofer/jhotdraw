/*
 * @(#)AbstractAction.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.tool;

import java.util.LinkedList;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlyMapWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.jhotdraw.app.AbstractDisableable;
import static org.jhotdraw.beans.PropertyBean.PROPERTIES_PROPERTY;
import org.jhotdraw.collection.Key;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.SimpleDrawingEditor;
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
     * The getProperties.
     */
    private ReadOnlyMapProperty<Key<?>, Object> properties;
    /**
     * The active view.
     */
    private final ObjectProperty<DrawingView> drawingView = new SimpleObjectProperty<>(this, DRAWING_VIEW_PROPERTY);

    {
        drawingView.addListener((ObservableValue<? extends DrawingView> observable, DrawingView oldValue, DrawingView newValue) -> {
            stopEditing();
        });
    }
    protected final BorderPane eventPane = new BorderPane();
    protected final BorderPane drawPane = new BorderPane();
    protected final StackPane node = new StackPane();

    /**
     * Listeners.
     */
    private final LinkedList<Listener<HandleEvent>> handleListeners = new LinkedList<>();

    {
        eventPane.addEventHandler(MouseEvent.ANY, (MouseEvent event) -> {
            if (drawingView.get() != null) {
                DrawingView dv = drawingView.get();
                EventType<? extends MouseEvent> type = event.getEventType();
                if (type == MouseEvent.MOUSE_MOVED) {
                    handleMouseMoved(event, dv);
                } else if (type == MouseEvent.MOUSE_DRAGGED) {
                    handleMouseDragged(event, dv);
                } else if (type == MouseEvent.MOUSE_EXITED) {
                    handleMouseExited(event, dv);
                } else if (type == MouseEvent.MOUSE_ENTERED) {
                    handleMouseEntered(event, dv);
                } else if (type == MouseEvent.MOUSE_RELEASED) {
                    handleMouseReleased(event, dv);
                } else if (type == MouseEvent.MOUSE_PRESSED) {
                    handleMousePressed(event, dv);
                } else if (type == MouseEvent.MOUSE_CLICKED) {
                    handleMouseClicked(event, dv);
                }
                event.consume();
            }
        });
        eventPane.addEventHandler(KeyEvent.ANY, (KeyEvent event) -> {
            if (drawingView.get() != null) {
                DrawingView dv = drawingView.get();
                EventType<? extends KeyEvent> type = event.getEventType();
                if (type == KeyEvent.KEY_PRESSED) {
                    handleKeyPressed(event, dv);
                } else if (type == KeyEvent.KEY_RELEASED) {
                    handleKeyReleased(event, dv);
                } else if (type == KeyEvent.KEY_TYPED) {
                    handleKeyTyped(event, dv);
                }
                event.consume();
            }
        });
    }
    /**
     * Listeners.
     */
    private final LinkedList<Listener<ToolEvent>> toolListeners = new LinkedList<>();

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
        
        node.getChildren().addAll(drawPane,eventPane);
    }

    // ---
    // Properties
    // ---
    @Override
    public final ReadOnlyMapProperty<Key<?>, Object> propertiesProperty() {
        if (properties == null) {
            properties
                    = new ReadOnlyMapWrapper<Key<?>, Object>(//
                            this, PROPERTIES_PROPERTY, //
                            FXCollections.observableHashMap()).getReadOnlyProperty();
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
            v.getDrawing().getChildren().removeAll(v.getSelectedFigures());
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
    protected void handleMouseMoved(MouseEvent event, DrawingView view) {
    }

    protected void handleMouseDragged(MouseEvent event, DrawingView view) {
    }

    protected void handleMouseExited(MouseEvent event, DrawingView view) {
    }

    protected void handleMouseEntered(MouseEvent event, DrawingView view) {
    }

    protected void handleMouseReleased(MouseEvent event, DrawingView view) {
    }

    protected void handleMousePressed(MouseEvent event, DrawingView view) {
    }

    protected void handleMouseClicked(MouseEvent event, DrawingView view) {
    }

    protected void handleKeyPressed(KeyEvent event, DrawingView view) {
    }

    protected void handleKeyReleased(KeyEvent event, DrawingView view) {
    }

    protected void handleKeyTyped(KeyEvent event, DrawingView view) {
    }

    /**
     * This implementation is empty.
     */
    @Override
    public void activate(SimpleDrawingEditor editor) {
    }

    /**
     * This implementation is empty.
     */
    @Override
    public void deactivate(SimpleDrawingEditor editor) {
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
    @Override
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
}
