/* @(#)PolyCreationTool.java
 * Copyright © by the authors and contributors ofCollection JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.tool;

import java.util.function.Supplier;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;

import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import java.util.ArrayList;
import java.util.Objects;
import javafx.scene.Cursor;
import org.jhotdraw8.draw.DrawingEditor;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.draw.figure.SimpleLayer;
import org.jhotdraw8.util.Resources;
import org.jhotdraw8.draw.figure.Layer;
import org.jhotdraw8.draw.handle.HandleType;
import org.jhotdraw8.draw.key.Point2DListStyleableFigureKey;

/**
 * CreationTool for polyline figures.
 *
 * @design.pattern CreationTool AbstractFactory, Client. Creation tools use
 * abstract factories (Supplier) for creating new {@link Figure}s.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class PolyCreationTool extends AbstractCreationTool<Figure> {

    /**
     * The rubber band.
     */
    @Nullable
    private ArrayList<Point2D> points;

    private final Point2DListStyleableFigureKey key;

    public PolyCreationTool(String name, Resources rsrc, Point2DListStyleableFigureKey key, Supplier<Figure> factory) {
        this(name, rsrc, key, factory, SimpleLayer::new);
    }

    public PolyCreationTool(String name, Resources rsrc, Point2DListStyleableFigureKey key, Supplier<Figure> figureFactory, Supplier<Layer> layerFactory) {
        super(name, rsrc, figureFactory, layerFactory);
        this.key = key;
        node.setCursor(Cursor.CROSSHAIR);
    }

    @Override
    protected void stopEditing() {
        if (createdFigure != null) {
            createdFigure = null;
            points = null;
        }
    }

    @Override
    protected void handleMousePressed(@Nonnull MouseEvent event, @Nonnull DrawingView view) {
        if (event.getClickCount() != 1) {
            return;
        }
        double x1 = event.getX();
        double y1 = event.getY();

        DrawingModel dm = view.getModel();
        Point2D c = view.getConstrainer().constrainPoint(createdFigure, new CssPoint2D(view.viewToWorld(new Point2D(x1, y1)))).getConvertedValue();
        if (createdFigure == null) {
            createdFigure = createFigure();
            points = new ArrayList<>();
            points.add(c);
            points.add(c);
            Layer layer = getOrCreateLayer(view, createdFigure);
            view.setActiveLayer(layer);

            dm.addChildTo(createdFigure, layer);
        } else {
            points.add(c);
        }
        dm.set(createdFigure, key, ImmutableList.ofCollection(points));

        event.consume();
    }

    @Override
    protected void handleMouseReleased(MouseEvent event, DrawingView dv) {

    }

    @Override
    protected void handleMouseMoved(@Nonnull MouseEvent event, @Nonnull DrawingView dv) {
        if (createdFigure != null) {
            handleMouseDragged(event, dv);
        }
    }

    @Override
    protected void handleMouseDragged(@Nonnull MouseEvent event, @Nonnull DrawingView dv) {
        if (createdFigure != null) {
            double x2 = event.getX();
            double y2 = event.getY();
            Point2D c2 = dv.getConstrainer().constrainPoint(createdFigure, new CssPoint2D(dv.viewToWorld(x2, y2))).getConvertedValue();
            DrawingModel dm = dv.getModel();
            points.set(points.size() - 1, c2);
            dm.set(createdFigure, key,  ImmutableList.ofCollection(points));
        }
        event.consume();
    }

    @Override
    protected void handleMouseClicked(@Nonnull MouseEvent event, @Nonnull DrawingView dv) {
        if (event.getClickCount() > 1) {
            if (createdFigure != null) {
                for (int i = points.size() - 1; i > 0; i--) {
                    if (Objects.equals(points.get(i), points.get(i - 1))) {
                        points.remove(i);
                    }
                }
                DrawingModel dm = dv.getModel();
                if (points.size() < 2) {
                    dm.removeFromParent(createdFigure);
                } else {
                    dm.set(createdFigure, key,  ImmutableList.ofCollection(points));
                    dv.getSelectedFigures().clear();
                    dv.setHandleType(HandleType.POINT);
                    dv.getSelectedFigures().add(createdFigure);
                }
                createdFigure = null;
                points = null;
                fireToolDone();
            }
        }
    }


    @Override
    public void activate(DrawingEditor editor) {
        requestFocus();
        super.activate(editor);
        createdFigure = null;
    }

    @Override
    public String getHelpText() {
        return "PolyCreationTool"
                + "\n  Click on the drawing view. The tool will create a new polygon with a point at that location."
                + "\n  Continue clicking on the drawing view. The tool will add each clicked point to the created polygon."
                + "\n  Press enter or escape, when you are done.";
    }

}
