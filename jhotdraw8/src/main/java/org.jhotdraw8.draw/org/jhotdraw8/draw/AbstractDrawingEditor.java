/*
 * @(#)AbstractDrawingEditor.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.SetChangeListener;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.beans.NonNullObjectProperty;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.draw.handle.HandleType;
import org.jhotdraw8.draw.tool.Tool;
import org.jhotdraw8.draw.tool.ToolEvent;
import org.jhotdraw8.event.Listener;

import java.util.HashSet;
import java.util.prefs.Preferences;

public abstract class AbstractDrawingEditor implements DrawingEditor {
    private final @NonNull ObjectProperty<String> helpText = new SimpleObjectProperty<String>(this, HELP_TEXT_PROPERTY);
    private final @NonNull DoubleProperty handleSize = new SimpleDoubleProperty(
            this, HANDLE_SIZE_PROPERTY,
            Preferences.userNodeForPackage(DrawingEditor.class).getDouble(HANDLE_SIZE_PROPERTY, 5.0)) {
        @Override
        public void set(double newValue) {
            super.set(newValue);
            Preferences.userNodeForPackage(DrawingEditor.class).putDouble(HANDLE_SIZE_PROPERTY, newValue);
            recreateHandles();
        }
    };
    private final @NonNull DoubleProperty tolerance = new SimpleDoubleProperty(
            this, TOLERANCE_PROPERTY,
            Preferences.userNodeForPackage(DrawingEditor.class).getDouble(TOLERANCE_PROPERTY, 5.0)) {
        @Override
        public void set(double newValue) {
            super.set(newValue);
            Preferences.userNodeForPackage(DrawingEditor.class).putDouble(TOLERANCE_PROPERTY, newValue);
            recreateHandles();
        }
    };
    private final @NonNull DoubleProperty handleStrokeWidth = new SimpleDoubleProperty(
            this, HANDLE_STROKE_WDITH_PROPERTY,
            Preferences.userNodeForPackage(DrawingEditor.class).getDouble(HANDLE_STROKE_WDITH_PROPERTY, 1.0)) {
        @Override
        public void set(double newValue) {
            super.set(newValue);
            Preferences.userNodeForPackage(DrawingEditor.class).putDouble(HANDLE_STROKE_WDITH_PROPERTY, newValue);
            recreateHandles();
        }
    };
    private final @NonNull NonNullObjectProperty<CssColor> handleColor = new NonNullObjectProperty<CssColor>(this, HANDLE_COLOR_PROPERTY,
            CssColor.valueOf(Preferences.userNodeForPackage(DrawingEditor.class).get(HANDLE_COLOR_PROPERTY, "blue"))) {
        @Override
        public void set(CssColor newValue) {
            super.set(newValue);
            Preferences.userNodeForPackage(DrawingEditor.class).put(HANDLE_COLOR_PROPERTY, newValue.getName());
            recreateHandles();
        }
    };
    private final NonNullObjectProperty<HandleType> handleType = new NonNullObjectProperty<>(this, HANDLE_TYPE_PROPERTY, HandleType.RESIZE);
    private final ObjectProperty<HandleType> leadHandleType = new SimpleObjectProperty<>(this, HANDLE_TYPE_PROPERTY, HandleType.RESIZE);

    private final ObjectProperty<HandleType> anchorHandleType = new SimpleObjectProperty<>(this, HANDLE_TYPE_PROPERTY, HandleType.RESIZE);

    private final NonNullObjectProperty<HandleType> multiHandleType = new NonNullObjectProperty<>(this, MULTI_HANDLE_TYPE_PROPERTY, HandleType.SELECT);
    private final SetProperty<DrawingView> drawingViews = new SimpleSetProperty<>(this, DRAWING_VIEWS_PROPERTY, FXCollections.observableSet(new HashSet<>()));
    private final ChangeListener<Boolean> focusListener = (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
        if (newValue) {
            setActiveDrawingView((DrawingView) ((ReadOnlyProperty<?>) observable).getBean());
        }
    };
    private final @Nullable Listener<ToolEvent> defaultToolActivator = (event) -> {
        switch (event.getEventType()) {
            case TOOL_DONE:
                if (getDefaultTool() != event.getSource() && getDefaultTool() != null) {
                    setActiveTool(getDefaultTool());
                }
                break;
            default:
                break;
        }
    };
    private final ObjectProperty<DrawingView> activeDrawingView = new SimpleObjectProperty<>(this, ACTIVE_DRAWING_VIEW_PROPERTY);
    private final ObjectProperty<Tool> activeTool = new SimpleObjectProperty<>(this, ACTIVE_TOOL_PROPERTY);
    private final ObjectProperty<Tool> defaultTool = new SimpleObjectProperty<>(this, DEFAULT_TOOL_PROPERTY);

    {
        ChangeListener<Object> recreateHandles = (observable, oldValue, newValue) -> {
            recreateHandles();
        };
        multiHandleType.addListener(recreateHandles);
        handleType.addListener(recreateHandles);
    }

    {
        drawingViews.addListener((SetChangeListener.Change<? extends DrawingView> change) -> {
            if (change.wasRemoved()) {
                DrawingView removed = change.getElementRemoved();
                removed.setEditor(null);
                removed.focusedProperty().removeListener(focusListener);
                if (getActiveDrawingView() != null) {
                    if (getActiveDrawingView() == removed) {
                        setActiveDrawingView(drawingViews.isEmpty() ? null : drawingViews.get().iterator().next());
                    }
                }
                removed.setTool(null);
            } else if (change.wasAdded()) {
                DrawingView added = change.getElementAdded();
                added.focusedProperty().addListener(focusListener);
                if (added.getEditor() != null) {
                    added.getEditor().removeDrawingView(added);
                }
                added.setEditor(this);
                final Tool theActiveTool = getActiveTool();
                added.setTool(theActiveTool);
                if (drawingViews.size() == 1) {
                    setActiveDrawingView(added);
                }
            }

        });
    }

    {
        activeTool.addListener((o, oldValue, newValue) -> {

            if (getActiveDrawingView() != null) {
                getActiveDrawingView().setTool(newValue);
            }
            if (oldValue != null) {
                oldValue.deactivate(this);
                oldValue.removeToolListener(defaultToolActivator);
            }
            if (newValue != null) {
                newValue.addToolListener(defaultToolActivator);
                newValue.setDrawingEditor(this);
                newValue.activate(this);
            }
        });
    }

    @Override
    public @NonNull ObjectProperty<DrawingView> activeDrawingViewProperty() {
        return activeDrawingView;
    }

    @Override
    public @NonNull ObjectProperty<Tool> activeToolProperty() {
        return activeTool;
    }

    @Override
    public @NonNull ObjectProperty<HandleType> anchorHandleTypeProperty() {
        return anchorHandleType;
    }

    @Override
    public @NonNull ObjectProperty<Tool> defaultToolProperty() {
        return defaultTool;
    }

    @Override
    public @NonNull SetProperty<DrawingView> drawingViewsProperty() {
        return drawingViews;
    }

    @Override
    public @NonNull NonNullObjectProperty<CssColor> handleColorProperty() {
        return handleColor;
    }

    @Override
    public @NonNull DoubleProperty handleSizeProperty() {
        return handleSize;
    }

    @Override
    public @NonNull DoubleProperty handleStrokeWidthProperty() {
        return handleStrokeWidth;
    }

    @Override
    public @NonNull NonNullObjectProperty<HandleType> handleTypeProperty() {
        return handleType;
    }

    @Override
    public @NonNull ObjectProperty<String> helpTextProperty() {
        return helpText;
    }

    @Override
    public @NonNull ObjectProperty<HandleType> leadHandleTypeProperty() {
        return leadHandleType;
    }

    @Override
    public @NonNull NonNullObjectProperty<HandleType> multiHandleTypeProperty() {
        return multiHandleType;
    }

    @Override
    public @NonNull DoubleProperty toleranceProperty() {
        return tolerance;
    }
}
