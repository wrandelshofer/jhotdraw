/*
 * @(#)LineCreationTool.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.tool;

import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.AnchorableFigure;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.Layer;
import org.jhotdraw8.draw.figure.LayerFigure;
import org.jhotdraw8.draw.figure.LineFigure;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.util.Resources;

import java.util.function.Supplier;

public class LineCreationTool extends CreationTool {
    private final MapAccessor<CssPoint2D> p1;
    private final MapAccessor<CssPoint2D> p2;

    public LineCreationTool(String name, Resources rsrc, Supplier<? extends Figure> factory) {
        this(name, rsrc, factory, LayerFigure::new, LineFigure.START, LineFigure.END);
    }

    public LineCreationTool(String name, Resources rsrc, Supplier<? extends Figure> factory, MapAccessor<CssPoint2D> p1, MapAccessor<CssPoint2D> p2) {
        this(name, rsrc, factory, LayerFigure::new, p1, p2);
    }

    public LineCreationTool(String name, Resources rsrc, Supplier<? extends Figure> figureFactory, Supplier<Layer> layerFactory) {
        this(name, rsrc, figureFactory, layerFactory, LineFigure.START, LineFigure.END);
    }

    public LineCreationTool(String name, Resources rsrc, Supplier<? extends Figure> figureFactory, Supplier<Layer> layerFactory,
                            MapAccessor<CssPoint2D> p1, MapAccessor<CssPoint2D> p2) {
        super(name, rsrc, figureFactory, layerFactory);
        this.p1 = p1;
        this.p2 = p2;
    }

    @Override
    protected void handleMousePressed(@NonNull MouseEvent event, @NonNull DrawingView view) {
        x1 = event.getX();
        y1 = event.getY();
        x2 = x1;
        y2 = y1;
        createdFigure = createFigure();

        double anchorX = Geom.clamp(createdFigure.getNonNull(AnchorableFigure.ANCHOR_X), 0, 1);
        double anchorY = Geom.clamp(createdFigure.getNonNull(AnchorableFigure.ANCHOR_Y), 0, 1);


        CssPoint2D c = view.getConstrainer().constrainPoint(createdFigure, new CssPoint2D(view.viewToWorld(new Point2D(x1, y1))));
        DrawingModel dm = view.getModel();
        createdFigure.set(p1, c);
        createdFigure.set(p2, c);
        Drawing drawing = dm.getDrawing();

        Layer layer = getOrCreateLayer(view, createdFigure);
        view.setActiveLayer(layer);

        dm.addChildTo(createdFigure, layer);
        event.consume();
    }

    @Override
    protected void handleMouseDragged(@NonNull MouseEvent event, @NonNull DrawingView dv) {
        if (createdFigure != null) {
            x2 = event.getX();
            y2 = event.getY();
            CssPoint2D c1 = dv.getConstrainer().constrainPoint(createdFigure, new CssPoint2D(dv.viewToWorld(x1, y1)));
            CssPoint2D c2 = dv.getConstrainer().constrainPoint(createdFigure, new CssPoint2D(dv.viewToWorld(x2, y2)));
            DrawingModel dm = dv.getModel();
            dm.set(createdFigure, p1, c1);
            dm.set(createdFigure, p2, c2);
        }
        event.consume();
    }

    @Override
    protected void reshapeInLocal(@NonNull Figure figure, CssPoint2D c1, CssPoint2D c2, @NonNull DrawingModel dm) {
        dm.set(figure, p1, c1);
        dm.set(figure, p2, c2);
    }
}
