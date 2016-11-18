/* @(#)SimpleSelectAreaTracker.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.tool;

import javafx.geometry.Bounds;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.util.Resources;
import static java.lang.Math.*;
import java.util.List;
import org.jhotdraw8.draw.figure.Figure;
import javafx.scene.input.KeyEvent;

/**
 * {@code SimpleSelectAreaTracker} implements interactions with the background
 * area of a {@code Drawing}.
 * <p>
 * The {@code DefaultSelectAreaTracker} handles one of the three states of the
 * {@code SelectionTool}. It comes into action, when the user presses the mouse
 * button over the background of a {@code Drawing}.
 * <p>
 * This tool draws a {@code Rectangle} with style class "tool-rubberband".
 * <p>
 * Design pattern:<br>
 * Name: Chain of Responsibility.<br>
 * Role: Handler.<br>
 * Partners: {@link SelectionTool} as Handler, {@link DragTracker} as Handler,
 * {@link HandleTracker} as Handler.
 * <p>
 * Design pattern:<br>
 * Name: State.<br>
 * Role: State.<br>
 * Partners: {@link SelectionTool} as Context, {@link DragTracker} as State,
 * {@link HandleTracker} as State.
 *
 * @see SelectionTool
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleSelectAreaTracker extends AbstractTracker implements SelectAreaTracker {

    /**
     * This tool draws a JavaFX {@code Rectangle} with style class
     * "tool-rubberband".
     */
    public final static String STYLECLASS_TOOL_RUBBERBAND = "tool-rubberband";

    private static final long serialVersionUID = 1L;
    /**
     * The rubberband.
     */
    private Rectangle rubberband = new Rectangle();

    double x;
    double y;

    public SimpleSelectAreaTracker() {
        this("tool.selectArea", Resources.getResources("org.jhotdraw8.draw.Labels"));
    }

    public SimpleSelectAreaTracker(String name, Resources rsrc) {
        //super(name, rsrc);

        // Add the rubberband to the node with absolute positioning
        node.getChildren().add(rubberband);
        rubberband.setVisible(false);
        initNode(rubberband);
    }

    protected void initNode(Rectangle r) {
        r.setFill(null);
        r.setStroke(Color.BLACK);
        rubberband.getStyleClass().add(STYLECLASS_TOOL_RUBBERBAND);
    }

    @Override
    public void trackMousePressed(MouseEvent event, DrawingView dv) {
        Bounds b = getNode().getBoundsInParent();
        x = event.getX();
        y = event.getY();
        rubberband.setVisible(true);
        rubberband.setX(round(x) - 0.5);
        rubberband.setY(round(y) - 0.5);
        rubberband.setWidth(0);
        rubberband.setHeight(0);
    }

    @Override
    public void trackMouseReleased(MouseEvent event, DrawingView dv) {
        rubberband.setVisible(false);

        double w = x - event.getX();
        double h = y - event.getY();
        List<Figure> f = dv.findFiguresInside(min(x, event.getX()), min(y, event.getY()), abs(w), abs(h),false);
        if (!event.isShiftDown()) {
            dv.selectedFiguresProperty().clear();
        }
        dv.selectedFiguresProperty().addAll(f);
        //fireToolDone();
    }

    @Override
    public void trackMouseDragged(MouseEvent event, DrawingView dv) {
        double w = x - event.getX();
        double h = y - event.getY();
        rubberband.setX(round(min(x, event.getX())) - 0.5);
        rubberband.setY(round(min(y, event.getY())) - 0.5);
        rubberband.setWidth(round(abs(w)));
        rubberband.setHeight(round(abs(h)));
    }
    @Override
    public void trackKeyPressed(KeyEvent event, DrawingView view) {
    }

    @Override
    public void trackKeyReleased(KeyEvent event, DrawingView view) {
    }

    @Override
    public void trackKeyTyped(KeyEvent event, DrawingView view) {
    }

}
