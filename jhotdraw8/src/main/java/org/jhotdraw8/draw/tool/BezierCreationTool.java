/* @(#)BezierCreationTool.java
 * Copyright © 2017 by the authors and contributors ofCollection JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.draw.DrawingEditor;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.Layer;
import org.jhotdraw8.draw.figure.SimpleLayer;
import org.jhotdraw8.draw.handle.HandleType;
import org.jhotdraw8.draw.key.BezierNodeListStyleableFigureKey;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.geom.BezierFit;
import org.jhotdraw8.geom.BezierNode;
import org.jhotdraw8.geom.BezierNodePathBuilder;
import org.jhotdraw8.geom.Transforms;
import org.jhotdraw8.util.Resources;

/**
 * CreationTool for bezier figures.
 *
 * @design.pattern CreationTool AbstractFactory, Client. Creation tools use
 * abstract factories (Supplier) for creating new {@link Figure}s.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class BezierCreationTool extends AbstractCreationTool<Figure> {

    /**
     * Remembers the number ofCollection points that we had, when the user
     * started to drag the mouse.
     */
    private int dragStartIndex;

    private final BezierNodeListStyleableFigureKey key;
    /**
     * The bezier nodes being created.
     */
    @Nullable
    private ArrayList<BezierNode> points;
    /**
     * The rubber band shows where the next point will be added.
     */
    @NonNull
    private Line rubberBand = new Line();

    public BezierCreationTool(String name, Resources rsrc, BezierNodeListStyleableFigureKey key, Supplier<Figure> factory) {
        this(name, rsrc, key, factory, SimpleLayer::new);
    }

    public BezierCreationTool(String name, Resources rsrc, BezierNodeListStyleableFigureKey key, Supplier<Figure> figureFactory, Supplier<Layer> layerFactory) {
        super(name, rsrc, figureFactory, layerFactory);
        this.key = key;
        node.setCursor(Cursor.CROSSHAIR);
        rubberBand.setVisible(false);
        rubberBand.setMouseTransparent(true);
        rubberBand.getStrokeDashArray().setAll(2.0, 5.0);
        rubberBand.setManaged(false);
        node.getChildren().add(rubberBand);
    }

    /**
     * This implementation is empty.
     */
    @Override
    public void activate(DrawingEditor editor) {
        requestFocus();
        rubberBand.setVisible(false);
        createdFigure = null;
    }

    @Override
    protected void handleMouseClicked(@NonNull MouseEvent event, @NonNull DrawingView dv) {
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
                    dm.set(createdFigure, key, ImmutableList.ofCollection(points));
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
    protected void handleMouseDragged(@NonNull MouseEvent event, @NonNull DrawingView dv) {
        if (createdFigure != null) {
            double x2 = event.getX();
            double y2 = event.getY();
            Point2D c2 = dv.viewToWorld(x2, y2);
            DrawingModel dm = dv.getModel();
            if (dragStartIndex == -1) {
                points.add(new BezierNode(c2));
                dragStartIndex = points.size() - 1;
            } else {
                points.add(new BezierNode(c2));
            }
            dm.set(createdFigure, key, ImmutableList.ofCollection(points));
        }
        event.consume();
    }

    @Override
    protected void handleMouseMoved(@NonNull MouseEvent event, @NonNull DrawingView dv) {
        if (createdFigure != null) {
            /*
            dragStartIndex = -1;
            double x2 = event.getX();
            double y2 = event.getY();
            Point2D c2 = dv.getConstrainer().constrainPoint(createdFigure, dv.viewToWorld(x2, y2));
            DrawingModel dm = dv.getModel();
            points.set(points.size() - 1, new BezierNode(c2));
            dm.set(createdFigure, key, ImmutableList.ofCollection(points));
             */
            if (!points.isEmpty()) {
                BezierNode lastNode = points.get(points.size() - 1);
                Point2D start = Transforms.transform(Transforms.concat(dv.getWorldToView(), createdFigure.getLocalToWorld()), lastNode.getX0(), lastNode.getY0());
                rubberBand.setStartX(start.getX());
                rubberBand.setStartY(start.getY());
                rubberBand.setEndX(event.getX());
                rubberBand.setEndY(event.getY());
                rubberBand.setVisible(true);
            }
        }
        event.consume();
    }

    @Override
    protected void handleMousePressed(@NonNull MouseEvent event, @NonNull DrawingView view) {
        if (event.getClickCount() != 1) {
            return;
        }
        double x1 = event.getX();
        double y1 = event.getY();

        DrawingModel dm = view.getModel();
        Point2D c = view.getConstrainer().constrainPoint(createdFigure, view.viewToWorld(new Point2D(x1, y1)));
        if (createdFigure == null) {
            createdFigure = createFigure();
            points = new ArrayList<>();
            points.add(new BezierNode(c));
            Layer layer = getOrCreateLayer(view, createdFigure);
            view.setActiveLayer(layer);

            dm.addChildTo(createdFigure, layer);
        } else {
            points.add(new BezierNode(c));
        }
        dm.set(createdFigure, key, ImmutableList.ofCollection(points));

        rubberBand.setVisible(false);
        dragStartIndex = -1;
        event.consume();
    }

    @Override
    protected void handleMouseReleased(MouseEvent event, @NonNull DrawingView dv) {
        if (createdFigure == null) {
            return;
        }
        if (dragStartIndex != -1) {
            List<Point2D> digitized = new ArrayList<>(points.size() - dragStartIndex);
            List<java.awt.geom.Point2D.Double> digiti = new ArrayList<>();
            for (int i = dragStartIndex, n = points.size(); i < n; i++) {
                digitized.add(points.get(i).getC0());
                digiti.add(new java.awt.geom.Point2D.Double(points.get(i).getC0().getX(), points.get(i).getC0().getY()));
            }
            BezierNodePathBuilder builder = new BezierNodePathBuilder();
            double error = 5 / dv.getZoomFactor();
            BezierFit.fitBezierPath(builder, digitized, error);
            final ImmutableList<BezierNode> built = builder.build();
            ArrayList<BezierNode> newList = new ArrayList<>(dragStartIndex + built.size());
            for (int i = 0; i < dragStartIndex; i++) {
                newList.add(points.get(i));
            }

            for (int i = 0, n = built.size(); i < n; i++) {
                if (i == 0) {
                    newList.add(built.get(i).setMask(built.get(i).getMask() & (BezierNode.MOVE_MASK ^ -1)));
                } else {
                    newList.add(built.get(i));
                }
            }
            newList.add(points.get(points.size() - 1));
            points = newList;

            DrawingModel dm = dv.getModel();
            dm.set(createdFigure, key, ImmutableList.ofCollection(points));
            dragStartIndex = -1;
        }
    }

    @Override
    protected void stopEditing() {
        if (createdFigure != null) {
            rubberBand.setVisible(false);

            createdFigure = null;
            points = null;
        }
    }

}
