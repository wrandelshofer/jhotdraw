/* @(#)LineConnectionTool.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.tool;

import java.util.function.Supplier;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import static java.lang.Math.*;
import java.util.List;
import javafx.application.Platform;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.model.DrawingModel;
import org.jhotdraw.draw.SimpleLayer;
import org.jhotdraw.draw.constrain.Constrainer;
import org.jhotdraw.util.Resources;
import org.jhotdraw.draw.Layer;
import org.jhotdraw.draw.LineConnectionFigure;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.model.DrawingModelEvent;
import org.jhotdraw.util.ReversedList;

/**
 * LineConnectionTool.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class LineConnectionTool extends AbstractTool {

    private Supplier<LineConnectionFigure> figureFactory;
    private Supplier<Layer> layerFactory;

    /**
     * The created figure.
     */
    private Figure figure;

    /**
     * The rubber band.
     */
    private double x1, y1, x2, y2;

    /**
     * The minimum size of a created figure (in view coordinates.
     */
    private double minSize = 2;

    public LineConnectionTool(String name, Resources rsrc, Supplier<LineConnectionFigure> factory) {
        super(name, rsrc);
        this.figureFactory = factory;
        this.layerFactory = SimpleLayer::new;
    }

    public void setFactory(Supplier<LineConnectionFigure> factory) {
        this.figureFactory = factory;
    }

    @Override
    protected void stopEditing() {
        figure = null;
    }

    @Override
    protected void onMousePressed(MouseEvent event, DrawingView view) {
        Platform.runLater(() -> view.getNode().requestFocus());
        x1 = event.getX();
        y1 = event.getY();
        x2 = x1;
        y2 = y1;
        figure = figureFactory.get();
        Point2D newPoint = view.getConstrainer().constrainPoint(figure, view.viewToDrawing(new Point2D(x1, y1)));
        figure.reshape(newPoint.getX(), newPoint.getY(), 1, 1);
        DrawingModel dm = view.getModel();
        Drawing drawing = dm.getRoot();

        Layer layer = getOrCreateLayer(view, figure);
        view.setActiveLayer(layer);

        Connector newConnector = null;
        Figure newConnectedFigure = null;
        if (!event.isMetaDown()) {
            List<Figure> list = view.findFigures(newPoint, true);
            for (Figure ff : list) {
                newConnector = ff.findConnector(newPoint, figure);
                if (newConnector != null) {
                    newConnectedFigure = ff;
                    break;
                }
            }
        }
        figure.set(LineConnectionFigure.START_FIGURE, newConnectedFigure);
        figure.set(LineConnectionFigure.START_CONNECTOR, newConnector);

        dm.addChildTo(figure, layer);
        event.consume();
    }

    @Override
    protected void onMouseReleased(MouseEvent event, DrawingView view) {
        if (figure != null) {
            onMouseDragged(event, view);
            view.getSelectedFigures().clear();
            view.getSelectedFigures().add(figure);
            figure=null;
        }
        fireToolDone();
    }

    @Override
    protected void onMouseDragged(MouseEvent event, DrawingView view) {
        if (figure != null) {
            Point2D newPoint = view.viewToDrawing(new Point2D(event.getX(), event.getY()));

            if (!event.isAltDown() && !event.isControlDown()) {
                // alt or control turns the constrainer off
                newPoint = view.getConstrainer().constrainPoint(figure, newPoint);
            }

            Connector newConnector = null;
            Figure newConnectedFigure = null;
            if (!event.isMetaDown()) {
                List<Figure> list = view.findFigures(newPoint, true);
                for (Figure ff : list) {
                    newConnector = ff.findConnector(newPoint, figure);
                    if (newConnector != null) {
                        newConnectedFigure = ff;
                        break;
                    }
                }
            }

            DrawingModel model = view.getModel();
            model.set(figure, LineConnectionFigure.END, figure.drawingToLocal(newPoint));
            Figure oldConnectedFigure = model.set(figure, LineConnectionFigure.END_FIGURE, newConnectedFigure);
            model.set(figure, LineConnectionFigure.END_CONNECTOR, newConnector);
            if (oldConnectedFigure != null) {
                model.fire(DrawingModelEvent.nodeInvalidated(model, oldConnectedFigure));
            }
            if (newConnectedFigure != null) {
                model.fire(DrawingModelEvent.nodeInvalidated(model, newConnectedFigure));
            }
            model.layout(figure);
        }
        event.consume();
    }

    @Override
    protected void onMouseClicked(MouseEvent event, DrawingView dv) {
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
        if (activeLayer != null && !activeLayer.get(Figure.DISABLED) && activeLayer.isAllowsChildren()) {
            return activeLayer;
        }
        // search for a suitable layer front to back
        Layer layer = null;
        for (Figure candidate : new ReversedList<>(dv.getDrawing().getChildren())) {
            if (!candidate.get(Figure.DISABLED) && candidate.isAllowsChildren()) {
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
