/*
 * @(#)AbstractDrawingEditor.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.SetChangeListener;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.beans.NonnullProperty;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.draw.handle.HandleType;
import org.jhotdraw8.draw.tool.Tool;
import org.jhotdraw8.draw.tool.ToolEvent;
import org.jhotdraw8.event.Listener;

import java.util.HashSet;
import java.util.prefs.Preferences;

public abstract class AbstractDrawingEditor implements DrawingEditor {
    @Nonnull
    private ObjectProperty<String> helpText = new SimpleObjectProperty<String>(this, HELP_TEXT_PROPERTY);
    private IntegerProperty handleSize = new SimpleIntegerProperty(
            this, HANDLE_SIZE_PROPERTY,
            Preferences.userNodeForPackage(AbstractDrawingView.class).getInt(HANDLE_SIZE_PROPERTY, 5)) {
        @Override
        public void set(int newValue) {
            super.set(newValue);
            Preferences.userNodeForPackage(AbstractDrawingView.class).putInt(HANDLE_SIZE_PROPERTY, newValue);
            recreateHandles();
        }
    };
    private IntegerProperty handleStrokeWidth = new SimpleIntegerProperty(
            this, HANDLE_STROKE_WDITH_PROPERTY,
            Preferences.userNodeForPackage(AbstractDrawingView.class).getInt(HANDLE_STROKE_WDITH_PROPERTY, 1)) {
        @Override
        public void set(int newValue) {
            super.set(newValue);
            Preferences.userNodeForPackage(AbstractDrawingView.class).putInt(HANDLE_STROKE_WDITH_PROPERTY, newValue);
            recreateHandles();
        }
    };
    private NonnullProperty<CssColor> handleColor = new NonnullProperty<CssColor>(this, HANDLE_COLOR_PROPERTY,
            CssColor.valueOf(Preferences.userNodeForPackage(AbstractDrawingView.class).get(HANDLE_COLOR_PROPERTY, "blue"))) {
        @Override
        public void set(CssColor newValue) {
            super.set(newValue);
            Preferences.userNodeForPackage(AbstractDrawingView.class).put(HANDLE_COLOR_PROPERTY, newValue.getName());
            recreateHandles();
        }
    };
    private final NonnullProperty<HandleType> handleType = new NonnullProperty<>(this, HANDLE_TYPE_PROPERTY, HandleType.RESIZE);
    private final ObjectProperty<HandleType> leadHandleType = new SimpleObjectProperty<>(this, HANDLE_TYPE_PROPERTY, HandleType.RESIZE);

    private final ObjectProperty<HandleType> anchorHandleType = new SimpleObjectProperty<>(this, HANDLE_TYPE_PROPERTY, HandleType.RESIZE);

    private final NonnullProperty<HandleType> multiHandleType = new NonnullProperty<>(this, MULTI_HANDLE_TYPE_PROPERTY, HandleType.SELECT);

    {
        ChangeListener<Object> recreateHandles = (observable, oldValue, newValue) -> {
            recreateHandles();
        };
        multiHandleType.addListener(recreateHandles);
        handleType.addListener(recreateHandles);
    }

    private final SetProperty<DrawingView> drawingViews = new SimpleSetProperty<>(this, DRAWING_VIEWS_PROPERTY, FXCollections.observableSet(new HashSet<>()));

    private final ChangeListener<Boolean> focusListener = (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
        if (newValue) {
            setActiveDrawingView((DrawingView) ((ReadOnlyProperty) observable).getBean());
        }
    };

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
            } else if (change.wasAdded()) {
                DrawingView added = change.getElementAdded();
                added.focusedProperty().addListener(focusListener);
                if (added.getEditor() != null) {
                    added.getEditor().removeDrawingView(added);
                }
                added.setEditor(this);
                if (drawingViews.size() == 1) {
                    setActiveDrawingView(added);
                }
            }

        });
    }

    @Override
    public ObjectProperty<String> helpTextProperty() {
        return helpText;
    }

    @Override
    public IntegerProperty handleSizeProperty() {
        return handleSize;
    }

    @Override
    public IntegerProperty handleStrokeWidthProperty() {
        return handleStrokeWidth;
    }

    @Override
    public NonnullProperty<CssColor> handleColorProperty() {
        return handleColor;
    }

    @Nonnull
    @Override
    public NonnullProperty<HandleType> handleTypeProperty() {
        return handleType;
    }

    @Nonnull
    @Override
    public ObjectProperty<HandleType> leadHandleTypeProperty() {
        return leadHandleType;
    }

    @Nonnull
    @Override
    public ObjectProperty<HandleType> anchorHandleTypeProperty() {
        return anchorHandleType;
    }

    @Nonnull
    @Override
    public NonnullProperty<HandleType> multiHandleTypeProperty() {
        return multiHandleType;
    }


    @Nonnull
    @Override
    public SetProperty<DrawingView> drawingViewsProperty() {
        return drawingViews;
    }


    @Nullable
    private final Listener<ToolEvent> defaultToolActivator = (event) -> {
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

    {
        activeTool.addListener((o, oldValue, newValue) -> {

            if (getActiveDrawingView() != null) {
                getActiveDrawingView().setTool(newValue);
            }
            if (oldValue != null) {
                oldValue.deactivate(this);
            }
            if (newValue != null) {
                newValue.setDrawingEditor(this);
                newValue.activate(this);
            }
        });
    }

    {
        activeTool.addListener((o, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.removeToolListener(defaultToolActivator);
            }
            if (newValue != null) {
                newValue.addToolListener(defaultToolActivator);
            }
        });
    }

    private final ObjectProperty<Tool> defaultTool = new SimpleObjectProperty<Tool>(this, DEFAULT_TOOL_PROPERTY) {

    };


    @Nonnull
    @Override
    public ObjectProperty<DrawingView> activeDrawingViewProperty() {
        return activeDrawingView;
    }

    @Nonnull
    @Override
    public ObjectProperty<Tool> activeToolProperty() {
        return activeTool;
    }

    @Nonnull
    @Override
    public ObjectProperty<Tool> defaultToolProperty() {
        return defaultTool;
    }
}
