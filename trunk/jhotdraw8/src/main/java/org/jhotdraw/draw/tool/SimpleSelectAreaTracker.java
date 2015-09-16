/* @(#)SimpleSelectAreaTracker.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.tool;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.util.Resources;
import static java.lang.Math.*;
import java.util.List;
import org.jhotdraw.draw.Figure;

/**
 * {@code SimpleSelectAreaTracker} implements interactions with the background
 * area of a {@code Drawing}.
 * <p>
 * The {@code DefaultSelectAreaTracker} handles one of the three states of the
 * {@code SelectionTool}. It comes into action, when the user presses
 * the mouse button over the background of a {@code Drawing}.
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
 * Partners: {@link SelectionTool} as Context, {@link DragTracker} as 
 * State, {@link HandleTracker} as State. 
 *
 * @see SelectionTool
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleSelectAreaTracker extends AbstractTool implements SelectAreaTracker {

    private static final long serialVersionUID = 1L;
    /**
     * The rubberband. 
     */
    private Rectangle rubberband = new Rectangle();

    double x;
    double y;

    public SimpleSelectAreaTracker() {
        this("selectAreaTool", Resources.getBundle("org.jhotdraw.draw.Labels"));
    }

    public SimpleSelectAreaTracker(String name, Resources rsrc) {
        super(name, rsrc);

        // Add the rubberband to the node with absolute positioning
        node.getChildren().add(rubberband);
        rubberband.setVisible(false);
        configureRubberband(rubberband);
    }

    private void configureRubberband(Rectangle r) {
        r.setFill(null);
        r.setStroke(Color.WHITE);
        r.setBlendMode(BlendMode.DIFFERENCE);
    }

    @Override
    public void trackMousePressed(MouseEvent event, DrawingView dv) {
        Bounds b = getNode().getBoundsInParent();
        x = event.getX();
        y = event.getY();
        rubberband.setVisible(true);
        rubberband.setX(x);
        rubberband.setY(y);
        rubberband.setWidth(0);
        rubberband.setHeight(0);
    }

    @Override
    public void trackMouseReleased(MouseEvent event, DrawingView dv) {
        rubberband.setVisible(false);

        double w = x - event.getX();
        double h = y - event.getY();
        List<Figure> f = dv.findFiguresInside(min(x, event.getX()), min(y, event.getY()), abs(w), abs(h));
        if (!event.isShiftDown()) {
            dv.selectionProperty().clear();
        }
        dv.selectionProperty().addAll(f);
        fireToolDone();
    }

    @Override
    public void trackMouseDragged(MouseEvent event, DrawingView dv) {
        double w = x - event.getX();
        double h = y - event.getY();
        rubberband.setX(min(x - 0.5, event.getX()));
        rubberband.setY(min(y - 0.5, event.getY()));
        rubberband.setWidth(abs(w));
        rubberband.setHeight(abs(h));
    }

}
