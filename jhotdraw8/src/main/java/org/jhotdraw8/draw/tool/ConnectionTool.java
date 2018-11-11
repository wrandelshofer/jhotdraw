/* @(#)ConnectionTool.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.tool;

import java.util.function.Supplier;

import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;

import java.util.List;

import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.draw.figure.SimpleLayer;
import org.jhotdraw8.util.Resources;
import org.jhotdraw8.draw.figure.Layer;
import org.jhotdraw8.draw.figure.SimpleLineConnectionFigure;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.figure.ConnectableFigure;
import org.jhotdraw8.draw.figure.ConnectingFigure;
import org.jhotdraw8.draw.handle.HandleType;
import org.jhotdraw8.util.ReversedList;

/**
 * ConnectionTool.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @design.pattern CreationTool AbstractFactory, Client.
 */
public class ConnectionTool extends AbstractTool {

    /**
     * The created figure.
     */
    @Nullable
    private ConnectingFigure figure;

    private Supplier<ConnectingFigure> figureFactory;
    private Supplier<Layer> layerFactory;

    /**
     * The minimum size of a created figure (in view coordinates.
     */
    private double minSize = 2;

    @Nullable
    private HandleType handleType = null;

    public ConnectionTool(String name, Resources rsrc, Supplier<ConnectingFigure> figureFactory) {
        this(name, rsrc, figureFactory, SimpleLayer::new);
    }

    public ConnectionTool(String name, Resources rsrc, Supplier<ConnectingFigure> figureFactory,
                          Supplier<Layer> layerFactory) {
        this(name, rsrc, null, figureFactory, layerFactory);

    }

    public ConnectionTool(String name, Resources rsrc, HandleType handleType, Supplier<ConnectingFigure> figureFactory,
                          Supplier<Layer> layerFactory) {
        super(name, rsrc);
        this.handleType = handleType;
        this.figureFactory = figureFactory;
        this.layerFactory = layerFactory;
    }

    public void setFactory(Supplier<ConnectingFigure> factory) {
        this.figureFactory = factory;
    }

    /**
     * Finds a layer for the specified figure. Creates a new layer if no
     * suitable layer can be found.
     *
     * @param dv        the drawing view
     * @param newFigure the figure
     * @return a suitable layer for the figure
     */
    @Nullable
    protected Layer getOrCreateLayer(@Nonnull DrawingView dv, Figure newFigure) {
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

    @Override
    protected void handleMouseClicked(MouseEvent event, DrawingView dv) {
    }

    @Override
    protected void handleMouseDragged(@Nonnull MouseEvent event, @Nonnull DrawingView view) {
        if (figure != null) {
            Point2D pointInViewCoordinates = new Point2D(event.getX(), event.getY());
            Point2D unconstrainedPoint = view.viewToWorld(pointInViewCoordinates);
            Point2D constrainedPoint;
            if (!event.isAltDown() && !event.isControlDown()) {
                // alt or control turns the constrainer off
                constrainedPoint = view.getConstrainer().constrainPoint(figure, new CssPoint2D(unconstrainedPoint)).getConvertedValue();
            } else {
                constrainedPoint = unconstrainedPoint;
            }
            Connector newConnector = null;
            Figure newConnectionTarget = null;
            DrawingModel model = view.getModel();
            // must clear end target, otherwise findConnector won't work as expected
            model.set(figure, SimpleLineConnectionFigure.END_TARGET, null);
            if (!event.isMetaDown()) {
                List<Figure> list = view.findFigures(pointInViewCoordinates, true);
                SearchLoop:
                for (Figure f1 : list) {
                    for (Figure ff : f1.breadthFirstIterable()) {
                        if (figure != ff && (ff instanceof ConnectableFigure)) {
                            ConnectableFigure cff = (ConnectableFigure) ff;
                            Point2D pointInLocal = cff.worldToLocal(unconstrainedPoint);
                            if (ff.getBoundsInLocal().contains(pointInLocal)) {
                                newConnector = cff.findConnector(cff.worldToLocal(constrainedPoint), figure);
                                if (newConnector != null && figure.canConnect(ff, newConnector)) {
                                    newConnectionTarget = ff;
                                    break SearchLoop;
                                }
                            }
                        }
                    }
                }
            }

            model.set(figure, SimpleLineConnectionFigure.END, new CssPoint2D(figure.worldToLocal(constrainedPoint)));
            model.set(figure, SimpleLineConnectionFigure.END_CONNECTOR, newConnector);
            model.set(figure, SimpleLineConnectionFigure.END_TARGET, newConnectionTarget);
        }
        event.consume();
    }

    @Override
    protected void handleMousePressed(@Nonnull MouseEvent event, @Nonnull DrawingView view) {
        requestFocus();
        figure = figureFactory.get();
        if (handleType != null) {
            view.setHandleType(handleType);
        }
        Point2D pointInViewCoordinates = new Point2D(event.getX(), event.getY());
        Point2D unconstrainedPoint = view.viewToWorld(pointInViewCoordinates);
        Point2D constrainedPoint = view.getConstrainer().constrainPoint(figure, new CssPoint2D(unconstrainedPoint)).getConvertedValue();
        figure.reshapeInLocal(constrainedPoint.getX(), constrainedPoint.getY(), 1, 1);
        DrawingModel dm = view.getModel();
        Drawing drawing = dm.getDrawing();

        Layer layer = getOrCreateLayer(view, figure);
        view.setActiveLayer(layer);

        Connector newConnector = null;
        Figure newConnectedFigure = null;
        if (!event.isMetaDown()) {
            List<Figure> list = view.findFigures(pointInViewCoordinates, true);

            SearchLoop:
            for (Figure f1 : list) {
                for (Figure ff : f1.breadthFirstIterable()) {
                    if (figure != ff && (ff instanceof ConnectableFigure)) {
                        ConnectableFigure cff = (ConnectableFigure) ff;
                        Point2D pointInLocal = cff.worldToLocal(unconstrainedPoint);
                        if (ff.getBoundsInLocal().contains(pointInLocal)) {
                            newConnector = cff.findConnector(cff.worldToLocal(constrainedPoint), figure);
                            if (newConnector != null && figure.canConnect(ff, newConnector)) {
                                newConnectedFigure = ff;
                                break SearchLoop;
                            }
                        }
                    }
                }
            }
        }
        figure.set(SimpleLineConnectionFigure.START_CONNECTOR, newConnector);
        figure.set(SimpleLineConnectionFigure.START_TARGET, newConnectedFigure);

        dm.addChildTo(figure, layer);
        event.consume();
    }

    @Override
    protected void handleMouseReleased(@Nonnull MouseEvent event, @Nonnull DrawingView view) {
        if (figure != null) {
            handleMouseDragged(event, view);
            view.getSelectedFigures().clear();
            view.getSelectedFigures().add(figure);
            figure = null;
        }
        fireToolDone();
    }

    @Override
    protected void stopEditing() {
        figure = null;
    }

    @Override
    public String getHelpText() {
        return "ConnectionTool"
                + "\n  Press the mouse on a figure in the drawing view, and drag the mouse to another figure."
                + " The tool will create a new figure which connects the two figures."
                ;
    }

}
