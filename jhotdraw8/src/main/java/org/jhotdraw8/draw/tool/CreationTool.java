/* @(#)CreationTool.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.tool;

import java.util.function.Supplier;

import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;

import javax.annotation.Nonnull;

import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;

import static java.lang.Math.*;

import javafx.scene.Cursor;
import org.jhotdraw8.draw.DrawingEditor;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.draw.figure.SimpleLayer;
import org.jhotdraw8.draw.constrain.Constrainer;
import org.jhotdraw8.util.Resources;
import org.jhotdraw8.draw.figure.Layer;
import org.jhotdraw8.draw.figure.AnchorableFigure;
import org.jhotdraw8.geom.Geom;

/**
 * CreationTool.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @design.pattern CreationTool AbstractFactory, Client. Creation tools use
 * abstract factories (Supplier) for creating new {@link Figure}s.
 */
public class CreationTool extends AbstractCreationTool<Figure> {


    private double defaultWidth = 10;
    private double defaultHeight = 10;
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
        super(name, rsrc, figureFactory, layerFactory);
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
    protected void handleMousePressed(@Nonnull MouseEvent event, @Nonnull DrawingView view) {
        x1 = event.getX();
        y1 = event.getY();
        x2 = x1;
        y2 = y1;
        createdFigure = createFigure();

        double anchorX = Geom.clamp(createdFigure.getNonnull(AnchorableFigure.ANCHOR_X), 0, 1);
        double anchorY = Geom.clamp(createdFigure.getNonnull(AnchorableFigure.ANCHOR_Y), 0, 1);


        CssPoint2D c = view.getConstrainer().constrainPoint(createdFigure, new CssPoint2D(view.viewToWorld(new Point2D(x1, y1))));
        createdFigure.reshapeInLocal(
                anchorX==0?c.getX():c.getX().subtract( new CssSize(defaultWidth).multiply( anchorX)),
                anchorY==0?c.getY():c.getY().subtract(new CssSize( defaultHeight).multiply( anchorY)),
                new CssSize(defaultWidth), new CssSize(defaultHeight));
        DrawingModel dm = view.getModel();
        Drawing drawing = dm.getDrawing();

        Layer layer = getOrCreateLayer(view, createdFigure);
        view.setActiveLayer(layer);

        dm.addChildTo(createdFigure, layer);
        event.consume();
    }

    @Override
    protected void handleMouseReleased(@Nonnull MouseEvent event, @Nonnull DrawingView dv) {
        if (createdFigure != null) {
            if (abs(x2 - x1) < minSize && abs(y2 - y1) < minSize) {
                CssPoint2D c1 = dv.getConstrainer().constrainPoint(createdFigure, new CssPoint2D(dv.viewToWorld(x1, y1)));
                CssPoint2D c2 = dv.getConstrainer().translatePoint(createdFigure, new CssPoint2D(dv.viewToWorld(x1
                        + minSize, y1 + minSize)), Constrainer.DIRECTION_NEAREST);
                if (c2.equals(c1)) {
                    c2 = dv.getConstrainer().constrainPoint(createdFigure, new CssPoint2D(c1.getX().getConvertedValue() + defaultWidth, c1.getY().getConvertedValue() + defaultHeight));
                }
                DrawingModel dm = dv.getModel();
                dm.reshapeInLocal(createdFigure, c1.getX(), c1.getY(),
                        c2.getX().subtract(c1.getX()),
                        c2.getY().subtract(c1.getY()));
            }
            dv.selectedFiguresProperty().clear();
            dv.selectedFiguresProperty().add(createdFigure);
            createdFigure = null;
        }
        event.consume();
        fireToolDone();
    }

    @Override
    protected void handleMouseDragged(@Nonnull MouseEvent event, @Nonnull DrawingView dv) {
        if (createdFigure != null) {
            x2 = event.getX();
            y2 = event.getY();
            CssPoint2D c1 = dv.getConstrainer().constrainPoint(createdFigure, new CssPoint2D(dv.viewToWorld(x1, y1)));
            CssPoint2D c2 = dv.getConstrainer().constrainPoint(createdFigure, new CssPoint2D(dv.viewToWorld(x2, y2)));
            CssSize newWidth = c2.getX().subtract( c1.getX());
            CssSize newHeight = c2.getY() .subtract( c1.getY());
            // shift keeps the aspect ratio
            boolean keepAspect = event.isShiftDown();
            if (keepAspect) {
                double preferredAspectRatio = createdFigure.getPreferredAspectRatio();
                double newRatio = newHeight.getConvertedValue() / newWidth.getConvertedValue();
                if (newRatio > preferredAspectRatio) {
                    newHeight = new CssSize(newWidth.getConvertedValue() * preferredAspectRatio);
                } else {
                    newWidth =new CssSize( newHeight.getConvertedValue() / preferredAspectRatio);
                }
            }

            DrawingModel dm = dv.getModel();
            dm.reshapeInLocal(createdFigure, c1.getX(), c1.getY(), newWidth, newHeight);
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
    public void activate(DrawingEditor editor) {
        requestFocus();
    }

}
