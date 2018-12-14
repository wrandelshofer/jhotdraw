package org.jhotdraw8.draw.figure;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.Path;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.connector.PathConnector;
import org.jhotdraw8.draw.locator.RelativeLocator;
import org.jhotdraw8.draw.render.RenderContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SimpleRegionFigure extends AbstractRegionFigure
        implements FillableFigure, StrokableFigure, CompositableFigure,
        StyleableFigure, TransformableFigure, HideableFigure,
        ConnectableFigure, LockableFigure, ResizableFigure {

    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public final static String TYPE_SELECTOR = "Region";

    @Nullable
    @Override
    public Connector findConnector(Point2D pointInLocal, Figure connectingFigure) {
        return new PathConnector(new RelativeLocator(getBoundsInLocal(), pointInLocal));
    }

    @Override
    public void updateNode(RenderContext ctx, Node node) {
        super.updateNode(ctx, node);
        applyHideableFigureProperties(node);
    }

    @Override
    protected void updatePathNode(RenderContext ctx, @Nonnull Path path) {
        super.updatePathNode(ctx, path);
        applyFillableFigureProperties(path);
        applyStrokableFigureProperties(ctx, path);
        applyCompositableFigureProperties(path);
        applyTransformableFigureProperties(ctx, path);
    }

    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }
}
