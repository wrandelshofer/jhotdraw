/* @(#)ConnectionTool.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.tool;

import java.util.function.Supplier;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import java.util.List;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.model.DrawingModel;
import org.jhotdraw.draw.SimpleLayer;
import org.jhotdraw.util.Resources;
import org.jhotdraw.draw.Layer;
import org.jhotdraw.draw.figure.misc.LineConnectionFigure;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.util.ReversedList;

/**
 * ConnectionTool.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ConnectionTool extends AbstractTool {

    private Supplier<LineConnectionFigure> figureFactory;
    private Supplier<Layer> layerFactory;

    /**
     * The created figure.
     */
    private Figure figure;

    /**
     * The minimum size of a created figure (in view coordinates.
     */
    private double minSize = 2;

    public ConnectionTool(String name, Resources rsrc, Supplier<LineConnectionFigure> figureFactory) {
        this(name, rsrc, figureFactory, SimpleLayer::new);
    }
    public ConnectionTool(String name, Resources rsrc, Supplier<LineConnectionFigure> figureFactory,
            Supplier<Layer> layerFactory) {
        super(name, rsrc);
        this.figureFactory = figureFactory;
        this.layerFactory = layerFactory;
    }

    public void setFactory(Supplier<LineConnectionFigure> factory) {
        this.figureFactory = factory;
    }

    @Override
    protected void stopEditing() {
        figure = null;
    }

    @Override
    protected void handleMousePressed(MouseEvent event, DrawingView view) {
        requestFocus();
        figure = figureFactory.get();
        Point2D pointInViewCoordinates = new Point2D(event.getX(), event.getY());
        Point2D newPoint = view.viewToWorld(pointInViewCoordinates);
        Point2D constrainedPoint = view.getConstrainer().constrainPoint(figure, newPoint);
        figure.reshape(constrainedPoint.getX(), constrainedPoint.getY(), 1, 1);
        DrawingModel dm = view.getModel();
        Drawing drawing = dm.getRoot();

        Layer layer = getOrCreateLayer(view, figure);
        view.setActiveLayer(layer);

        Connector newConnector = null;
        Figure newConnectedFigure = null;
        if (!event.isMetaDown()) {
            List<Figure> list = view.findFigures(pointInViewCoordinates, true);
            for (Figure ff : list) {
                newConnector = ff.findConnector(newPoint, figure);
                if (newConnector != null) {
                    newConnectedFigure = ff;
                    break;
                }
            }
        }
        figure.set(LineConnectionFigure.START_CONNECTOR, newConnector);

        dm.addChildTo(figure, layer);
        event.consume();
    }

    @Override
    protected void handleMouseReleased(MouseEvent event, DrawingView view) {
        if (figure != null) {
            handleMouseDragged(event, view);
            view.getSelectedFigures().clear();
            view.getSelectedFigures().add(figure);
            figure = null;
        }
        fireToolDone();
    }

    @Override
    protected void handleMouseDragged(MouseEvent event, DrawingView view) {
        if (figure != null) {
        Point2D pointInViewCoordinates = new Point2D(event.getX(), event.getY());
        Point2D newPoint = view.viewToWorld(pointInViewCoordinates);

            Connector newConnector = null;
            if (!event.isMetaDown()) {
                List<Figure> list = view.findFigures(pointInViewCoordinates, true);
                for (Figure ff : list) {
                    newConnector = ff.findConnector(ff.worldToLocal(newPoint), figure);
                    if (newConnector != null) {
                        break;
                    }
                }
            }

            Point2D constrainedPoint;
            if (!event.isAltDown() && !event.isControlDown()) {
                // alt or control turns the constrainer off
                constrainedPoint = view.getConstrainer().constrainPoint(figure, newPoint);
            } else {
                constrainedPoint = newPoint;
            }

            DrawingModel model = view.getModel();
            model.set(figure, LineConnectionFigure.END, figure.worldToLocal(constrainedPoint));
            model.set(figure, LineConnectionFigure.END_CONNECTOR, newConnector);
        }
        event.consume();
    }

    @Override
    protected void handleMouseClicked(MouseEvent event, DrawingView dv) {
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

}
