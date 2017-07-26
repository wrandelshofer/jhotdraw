/* @(#)BezierCreationTool.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import org.jhotdraw8.collection.ImmutableObservableList;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.SimpleDrawingEditor;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.Layer;
import org.jhotdraw8.draw.figure.SimpleLayer;
import org.jhotdraw8.draw.handle.HandleType;
import org.jhotdraw8.draw.key.BezierNodeListStyleableFigureKey;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.geom.BezierFit;
import org.jhotdraw8.geom.BezierNode;
import org.jhotdraw8.geom.BezierNodePathBuilder;
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
     * Remembers the number of points that we had, when the user started to drag
     * the mouse.
     */
    private int dragStartIndex;

    private final BezierNodeListStyleableFigureKey key;
    /**
     * The rubber band.
     */
    private ArrayList<BezierNode> points;

    public BezierCreationTool(String name, Resources rsrc, BezierNodeListStyleableFigureKey key, Supplier<Figure> factory) {
        this(name, rsrc, key, factory, SimpleLayer::new);
    }

    public BezierCreationTool(String name, Resources rsrc, BezierNodeListStyleableFigureKey key, Supplier<Figure> figureFactory, Supplier<Layer> layerFactory) {
        super(name, rsrc, figureFactory, layerFactory);
        this.key = key;
        node.setCursor(Cursor.CROSSHAIR);
    }

    /**
     * This implementation is empty.
     */
    @Override
    public void activate(SimpleDrawingEditor editor) {
        requestFocus();
        createdFigure = null;
    }

    @Override
    protected void handleMouseClicked(MouseEvent event, DrawingView dv) {
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
                    dm.set(createdFigure, key, new ImmutableObservableList<>(points));
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
    protected void handleMouseDragged(MouseEvent event, DrawingView dv) {
        if (createdFigure != null) {
            double x2 = event.getX();
            double y2 = event.getY();
            Point2D c2 = dv.viewToWorld(x2, y2);
            DrawingModel dm = dv.getModel();
            if (dragStartIndex == -1) {
                points.set(points.size() - 1, new BezierNode(c2));
                dragStartIndex = points.size() - 2;
            } else {
                points.add(new BezierNode(c2));
            }
            dm.set(createdFigure, key, new ImmutableObservableList<>(points));
        }
        event.consume();
    }

    @Override
    protected void handleMouseMoved(MouseEvent event, DrawingView dv) {
        if (createdFigure != null) {
            dragStartIndex = -1;
            double x2 = event.getX();
            double y2 = event.getY();
            Point2D c2 = dv.getConstrainer().constrainPoint(createdFigure, dv.viewToWorld(x2, y2));
            DrawingModel dm = dv.getModel();
            points.set(points.size() - 1, new BezierNode(c2));
            dm.set(createdFigure, key, new ImmutableObservableList<>(points));
        }
        event.consume();
    }

    @Override
    protected void handleMousePressed(MouseEvent event, DrawingView view) {
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
            points.add(new BezierNode(c));
            Layer layer = getOrCreateLayer(view, createdFigure);
            view.setActiveLayer(layer);

            dm.addChildTo(createdFigure, layer);
        } else {
            points.add(new BezierNode(c));
        }
        dm.set(createdFigure, key, new ImmutableObservableList<>(points));

        dragStartIndex = -1;
        event.consume();
    }

    @Override
    protected void handleMouseReleased(MouseEvent event, DrawingView dv) {
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
            final ImmutableObservableList<BezierNode> built = builder.getNodes();
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
            dm.set(createdFigure, key, new ImmutableObservableList<>(points));
        }
        dragStartIndex = -1;
    }

    @Override
    protected void stopEditing() {
        if (createdFigure != null) {
            createdFigure = null;
            points = null;
        }
    }

}
