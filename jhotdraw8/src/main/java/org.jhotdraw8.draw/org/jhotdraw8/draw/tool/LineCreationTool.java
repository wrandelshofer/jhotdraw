package org.jhotdraw8.draw.tool;

import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.Layer;
import org.jhotdraw8.draw.figure.LayerFigure;
import org.jhotdraw8.draw.figure.LineFigure;
import org.jhotdraw8.draw.model.DrawingModel;
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
    protected void reshapeInLocal(Figure figure, CssPoint2D c1, CssPoint2D c2, DrawingModel dm) {
        dm.set(figure, p1, c1);
        dm.set(figure, p2, c2);
    }
}
