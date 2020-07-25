/*
 * @(#)DrawingEditor.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.collections.ObservableSet;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.beans.NonNullProperty;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.draw.handle.HandleType;
import org.jhotdraw8.draw.tool.Tool;

/**
 * The DrawingEditor can be used to edit multiple {@code DrawingView}s with a
 * single {@code Tool}.
 * <p>
 * The DrawingEditor invokes {@code activate()} and {@code deactivate()} methods
 * on the {@code Tool} if it becomes the active tool or loses this status.
 *
 * @author Werner Randelshofer
 * @design.pattern org.jhotdraw8.draw.figure.Drawing Framework, KeyAbstraction.
 * @design.pattern DrawingEditor Mediator, Mediator. The DrawingEditor allows to
 * use the same {@code Tool} with multiple {@code DrawingView}s.
 */
public interface DrawingEditor {

    // ----
    // property names
    // ----
    /**
     * The name of the helpText property.
     */
    String HELP_TEXT_PROPERTY = "helpText";
    /**
     * The name of the handle type property for single selection.
     */
    String HANDLE_TYPE_PROPERTY = "handleType";
    /**
     * The name of the handle type property for multiple selection.
     */
    String MULTI_HANDLE_TYPE_PROPERTY = "multiHandleType";

    String HANDLE_SIZE_PROPERTY = "handleSize";

    String HANDLE_STROKE_WDITH_PROPERTY = "handleStrokeWidth";

    String HANDLE_COLOR_PROPERTY = "handleColor";
    /**
     * The name of the drawingViews property.
     */
    String DRAWING_VIEWS_PROPERTY = "drawingViews";
    /**
     * The name of the activeDrawingView property.
     */
    String ACTIVE_DRAWING_VIEW_PROPERTY = "activeDrawingView";
    /**
     * The name of the activeTool property.
     */
    String ACTIVE_TOOL_PROPERTY = "activeTool";
    /**
     * The name of the defaultTool property.
     */
    String DEFAULT_TOOL_PROPERTY = "defaultTool";

    // ---
    // properties
    // ---

    /**
     * The drawing views associated with this editor.
     *
     * @return the property
     */
    @NonNull ReadOnlySetProperty<DrawingView> drawingViewsProperty();

    /**
     * The currently active drawing view.
     *
     * @return the property
     */
    @NonNull ObjectProperty<DrawingView> activeDrawingViewProperty();

    /**
     * The currently active tool.
     *
     * @return the property
     */
    @NonNull ObjectProperty<Tool> activeToolProperty();

    /**
     * The default tool. When the value is not null, the default tool is made
     * the active tool whenever another tool fires {@code ToolEvent.TOOL_DONE}.
     *
     * @return the property
     */
    @NonNull ObjectProperty<Tool> defaultToolProperty();

    // ---
    // convenience methods
    // ---

    /**
     * Adds a drawing view to this editor.
     *
     * @param drawingView the drawing view
     */
    default void addDrawingView(DrawingView drawingView) {
        drawingViewsProperty().add(drawingView);
    }

    /**
     * Removes a drawing view to this editor.
     *
     * @param drawingView the drawing view
     */
    default void removeDrawingView(DrawingView drawingView) {
        drawingViewsProperty().remove(drawingView);
    }

    /**
     * Gets the active drawing view.
     *
     * @return the active drawing view or empty
     */
    @Nullable
    default DrawingView getActiveDrawingView() {
        return activeDrawingViewProperty().get();
    }

    /**
     * Gets the drawing views.
     *
     * @return the active drawing view or empty
     */
    default ObservableSet<DrawingView> getDrawingViews() {
        return drawingViewsProperty().get();
    }

    /**
     * Sets the active drawing view.
     *
     * @param drawingView the drawing view or null if none is active
     */
    default void setActiveDrawingView(@Nullable DrawingView drawingView) {
        activeDrawingViewProperty().set(drawingView);
    }

    /**
     * Gets the active tool.
     *
     * @return the active tool or null
     */
    @Nullable
    default Tool getActiveTool() {
        return activeToolProperty().get();
    }

    /**
     * Sets the active tool.
     *
     * @param tool the active tool or null if none is active
     */
    default void setActiveTool(@Nullable Tool tool) {
        activeToolProperty().set(tool);
    }

    /**
     * Gets the default tool.
     *
     * @return the default tool or null
     */
    @Nullable
    default Tool getDefaultTool() {
        return defaultToolProperty().get();
    }

    /**
     * Sets the default tool.
     *
     * @param tool the default tool or null if no default tool is desired
     */
    default void setDefaultTool(@Nullable Tool tool) {
        defaultToolProperty().set(tool);
    }

    /**
     * Holds the curent help text associated with this drawing view.
     *
     * @return the help text property.
     */
    @NonNull
    ObjectProperty<String> helpTextProperty();

    default String getHelpText() {
        return helpTextProperty().get();
    }

    default void setHelpText(String newValue) {
        helpTextProperty().set(newValue);
    }

    /**
     * Holds the size (width and height) of a handle.
     *
     * @return size of handle
     */
    IntegerProperty handleSizeProperty();

    /**
     * Holds the stroke width of a handle.
     *
     * @return size of handle
     */
    IntegerProperty handleStrokeWidthProperty();

    default int getHandleSize() {
        return handleSizeProperty().get();
    }

    default void setHandleSize(int newValue) {
        handleSizeProperty().set(newValue);
    }

    /**
     * Holds the color of the handles.
     *
     * @return color of handle
     */
    NonNullProperty<CssColor> handleColorProperty();

    default CssColor getHandleColor() {
        return handleColorProperty().get();
    }

    default void setHandleColor(CssColor newValue) {
        handleColorProperty().set(newValue);
    }


    default int getHandleStrokeWidth() {
        return handleStrokeWidthProperty().get();
    }

    default void setHandleStrokeWidth(int newValue) {
        handleStrokeWidthProperty().set(newValue);
    }

    default void recreateHandles() {
        for (DrawingView drawingView : getDrawingViews()) {
            drawingView.recreateHandles();
        }
    }


    default void setHandleType(@Nullable HandleType newValue) {
        handleTypeProperty().set(newValue);
    }

    @Nullable
    default HandleType getHandleType() {
        return handleTypeProperty().get();
    }


    /**
     * The handle type used for marking the anchor of a selection.
     *
     * @return the handle key
     */
    @NonNull ObjectProperty<HandleType> anchorHandleTypeProperty();

    /**
     * The handle type used for marking the lead of a selection.
     *
     * @return the handle key
     */
    @NonNull ObjectProperty<HandleType> leadHandleTypeProperty();

    default void setAnchorHandleType(@Nullable HandleType newValue) {
        anchorHandleTypeProperty().set(newValue);
    }

    @Nullable
    default HandleType getAnchorHandleType() {
        return anchorHandleTypeProperty().get();
    }

    default void setLeadHandleType(@Nullable HandleType newValue) {
        leadHandleTypeProperty().set(newValue);
    }

    @Nullable
    default HandleType getLeadHandleType() {
        return leadHandleTypeProperty().get();
    }


    /**
     * The handle type for single selection.
     *
     * @return the handle key
     */
    @NonNull NonNullProperty<HandleType> handleTypeProperty();


    /**
     * Tolerance (radius) in view coordinates.
     *
     * @return the tolerance radius
     */
    default double getTolerance() {
        // handle size * 0.5 * sqrt(2).
        return getHandleSize();
    }

    /**
     * The handle type for multiple selection.
     *
     * @return the handle key
     */
    @NonNull NonNullProperty<HandleType> multiHandleTypeProperty();


    default void setMultiHandleType(@Nullable HandleType newValue) {
        multiHandleTypeProperty().set(newValue);
    }

    @Nullable
    default HandleType getMultiHandleType() {
        return multiHandleTypeProperty().get();
    }
}
