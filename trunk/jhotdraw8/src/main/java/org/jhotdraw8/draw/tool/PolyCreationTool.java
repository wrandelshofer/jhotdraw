/* @(#)CreationTool.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.tool;

import java.util.function.Supplier;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import java.util.ArrayList;
import java.util.Objects;
import javafx.scene.Cursor;
import org.jhotdraw8.collection.ImmutableObservableList;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.draw.figure.SimpleLayer;
import org.jhotdraw8.util.Resources;
import org.jhotdraw8.draw.figure.Layer;
import org.jhotdraw8.draw.SimpleDrawingEditor;
import org.jhotdraw8.draw.handle.HandleType;
import org.jhotdraw8.draw.key.Point2DListStyleableFigureKey;
import org.jhotdraw8.util.ReversedList;

/**
 * CreationTool for polyline figures.
 *
 * @design.pattern CreationTool AbstractFactory, Client. Creation tools use
 * abstract factories (Supplier) for creating new {@link Figure}s.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class PolyCreationTool extends AbstractTool {

    private Supplier<Figure> figureFactory;
    private Supplier<Layer> layerFactory;

    /**
     * The created figure.
     */
    private Figure figure;

    /**
     * The rubber band.
     */
    private ArrayList<Point2D> points;

    private final Point2DListStyleableFigureKey key;

    public PolyCreationTool(String name, Resources rsrc, Point2DListStyleableFigureKey key, Supplier<Figure> factory) {
        this(name, rsrc, key, factory, SimpleLayer::new);
    }

    public PolyCreationTool(String name, Resources rsrc, Point2DListStyleableFigureKey key, Supplier<Figure> figureFactory, Supplier<Layer> layerFactory) {
        super(name, rsrc);
        this.key = key;
        this.figureFactory = figureFactory;
        this.layerFactory = layerFactory;
        node.setCursor(Cursor.CROSSHAIR);
    }

    public void setFigureFactory(Supplier<Figure> factory) {
        this.figureFactory = factory;
    }

    public void setLayerFactory(Supplier<Layer> factory) {
        this.layerFactory = factory;
    }

    @Override
    protected void stopEditing() {
        if (figure!=null) {
        figure = null;
        points = null;
        }
    }

    @Override
    protected void handleMousePressed(MouseEvent event, DrawingView view) {
        if (event.getClickCount() != 1) {
            return;
        }
        double x1 = event.getX();
        double y1 = event.getY();

        DrawingModel dm = view.getModel();
        Point2D c = view.getConstrainer().constrainPoint(figure, view.viewToDrawing(new Point2D(x1, y1)));
        if (figure == null) {
            figure = createFigure();
            points = new ArrayList<>();
            points.add(c);
            points.add(c);
            Layer layer = getOrCreateLayer(view, figure);
            view.setActiveLayer(layer);

            dm.addChildTo(figure, layer);
        } else  {
            points.add(c);
        }
        dm.set(figure, key, new ImmutableObservableList<>(points));

        event.consume();
    }

    @Override
    protected void handleMouseReleased(MouseEvent event, DrawingView dv) {

    }

    @Override
    protected void handleMouseMoved(MouseEvent event, DrawingView dv) {
        if (figure != null) {
            handleMouseDragged(event, dv);
        }
    }

    @Override
    protected void handleMouseDragged(MouseEvent event, DrawingView dv) {
        if (figure != null) {
            double x2 = event.getX();
            double y2 = event.getY();
            Point2D c2 = dv.getConstrainer().constrainPoint(figure, dv.viewToDrawing(x2, y2));
            DrawingModel dm = dv.getModel();
            points.set(points.size() - 1, c2);
            dm.set(figure, key, new ImmutableObservableList<>(points));
        }
        event.consume();
    }

    @Override
    protected void handleMouseClicked(MouseEvent event, DrawingView dv) {
        if (event.getClickCount() > 1) {
            if (figure != null) {
                for (int i=points.size()-1;i>0;i--) {
                    if (Objects.equals(points.get(i),points.get(i-1))) {
                        points.remove(i);
                    }
                }
            DrawingModel dm = dv.getModel();
            if (points.size()<2) {
dm.removeFromParent(figure);
            }else{
            dm.set(figure, key, new ImmutableObservableList<>(points));
                dv.getSelectedFigures().clear();
                dv.setHandleType(HandleType.POINT);
                dv.getSelectedFigures().add(figure);
            }
                figure = null;
                points = null;
                fireToolDone();
            }
        }
    }

    protected Figure createFigure() {
        return figureFactory.get();
    }

    /**
     * Finds a layer for the specified figure. Creates a new layer if no
     * suitable layer can be found.
     *
     * @param dv the drawing view
     * @param newFigure the figure
     * @return a suitable layer for the figure
     */
    protected Layer getOrCreateLayer(DrawingView dv, Figure newFigure) {
        // try to use the active layer
        Layer activeLayer = dv.getActiveLayer();
        if (activeLayer != null && activeLayer.isEditable() && activeLayer.isAllowsChildren()) {
            return activeLayer;
        }
        // search for a suitable layer front to back
        Layer layer = null;
        for (Figure candidate : new ReversedList<>(dv.getDrawing().getChildren())) {
            if (candidate.isEditable() && candidate.isAllowsChildren()) {
                layer = (Layer) candidate;
                break;
            }
        }
        // create a new layer if necessary
        if (layer == null) {
            layer = layerFactory.get();
            dv.getModel().addChildTo(layer, dv.getDrawing());
        }
        return layer;
    }

    /**
     * This implementation is empty.
     */
    @Override
    public void activate(SimpleDrawingEditor editor) {
        requestFocus();
        figure = null;
    }

}
