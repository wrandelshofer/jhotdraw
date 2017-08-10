/* @(#)CreationTool.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.tool;

import java.util.function.Supplier;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import static java.lang.Math.*;
import javafx.scene.Cursor;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.draw.figure.SimpleLayer;
import org.jhotdraw8.draw.constrain.Constrainer;
import org.jhotdraw8.util.Resources;
import org.jhotdraw8.draw.figure.Layer;
import org.jhotdraw8.draw.SimpleDrawingEditor;
import org.jhotdraw8.draw.figure.AnchorableFigure;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.util.ReversedList;

/**
 * CreationTool.
 *
 * @design.pattern CreationTool AbstractFactory, Client. Creation tools use
 * abstract factories (Supplier) for creating new {@link Figure}s.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CreationTool extends AbstractCreationTool<Figure> {


private double defaultWidth=10;
private double defaultHeight=10;
    /**
     * The rubber band.
     */
    private double x1, y1, x2, y2;

    /**
     * The minimum size of a created figure (in view coordinates.
     */
    private double minSize = 2;

    public CreationTool(String name, Resources rsrc, Supplier<Figure> factory) {
        this(name, rsrc, factory, SimpleLayer::new);
    }

    public CreationTool(String name, Resources rsrc, Supplier<Figure> figureFactory, Supplier<Layer> layerFactory) {
        super(name, rsrc,figureFactory,layerFactory);
        node.setCursor(Cursor.CROSSHAIR);
    }

    public double getDefaultHeight() {
        return defaultHeight;
    }

    public void setDefaultHeight(double defaultHeight) {
        this.defaultHeight = defaultHeight;
    }

    public double getDefaultWidth() {
        return defaultWidth;
    }

    public void setDefaultWidth(double defaultWidth) {
        this.defaultWidth = defaultWidth;
    }

    @Override
    protected void stopEditing() {
        createdFigure = null;
    }

    @Override
    protected void handleMousePressed(MouseEvent event, DrawingView view) {
        x1 = event.getX();
        y1 = event.getY();
        x2 = x1;
        y2 = y1;
        createdFigure = createFigure();
        
        double anchorX=Geom.clamp(createdFigure.get(AnchorableFigure.ANCHOR_X),0,1);
        double anchorY=Geom.clamp(createdFigure.get(AnchorableFigure.ANCHOR_Y),0,1);
        
        
        Point2D c = view.getConstrainer().constrainPoint(createdFigure, view.viewToWorld(new Point2D(x1, y1)));
        createdFigure.reshapeInLocal(c.getX()-defaultWidth*anchorX, c.getY()-defaultHeight*anchorY, defaultWidth, defaultHeight);
        DrawingModel dm = view.getModel();
        Drawing drawing = dm.getDrawing();

        Layer layer = getOrCreateLayer(view, createdFigure);
        view.setActiveLayer(layer);

        dm.addChildTo(createdFigure, layer);
        event.consume();
    }

    @Override
    protected void handleMouseReleased(MouseEvent event, DrawingView dv) {
        if (createdFigure != null) {
            if (abs(x2 - x1) < minSize && abs(y2 - y1) < minSize) {
                Point2D c1 = dv.getConstrainer().constrainPoint(createdFigure, dv.viewToWorld(x1, y1));
                Point2D c2 = dv.getConstrainer().translatePoint(createdFigure, dv.viewToWorld(x1
                        + minSize, y1 + minSize), Constrainer.DIRECTION_NEAREST);
                if (c2.equals(c1)) {
                    c2 = new Point2D(c1.getX() + defaultWidth, c1.getY() + defaultHeight);
                }
                DrawingModel dm = dv.getModel();
                dm.reshape(createdFigure, c1.getX(), c1.getY(), c2.getX() - c1.getX(), c2.getY()
                        - c1.getY());
            }
            dv.selectedFiguresProperty().clear();
            dv.selectedFiguresProperty().add(createdFigure);
            createdFigure = null;
        }
        event.consume();
        fireToolDone();
    }

    @Override
    protected void handleMouseDragged(MouseEvent event, DrawingView dv) {
        if (createdFigure != null) {
            x2 = event.getX();
            y2 = event.getY();
            Point2D c1 = dv.getConstrainer().constrainPoint(createdFigure, dv.viewToWorld(x1, y1));
            Point2D c2 = dv.getConstrainer().constrainPoint(createdFigure, dv.viewToWorld(x2, y2));
            double newWidth = c2.getX() - c1.getX();
            double newHeight = c2.getY() - c1.getY();
            // shift keeps the aspect ratio
            boolean keepAspect = event.isShiftDown();
            if (keepAspect) {
                double preferredAspectRatio = createdFigure.getPreferredAspectRatio();
                double newRatio = newHeight / newWidth;
                if (newRatio > preferredAspectRatio) {
                    newHeight = newWidth * preferredAspectRatio;
                } else {
                    newWidth = newHeight / preferredAspectRatio;
                }
            }

            DrawingModel dm = dv.getModel();
            dm.reshape(createdFigure, c1.getX(), c1.getY(), newWidth, newHeight);
        }
        event.consume();
    }

    @Override
    protected void handleMouseClicked(MouseEvent event, DrawingView dv) {
    }



    /**
     * This implementation is empty.
     */
    @Override
    public void activate(SimpleDrawingEditor editor) {
        requestFocus();
    }

}
