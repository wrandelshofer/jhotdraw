package org.jhotdraw8.draw.figure;

import javafx.geometry.Point2D;
import javafx.scene.shape.Path;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.connector.PathConnector;
import org.jhotdraw8.draw.locator.RelativeLocator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SimpleRegionFigure extends AbstractRegionFigure
        implements FillableFigure, StrokeableFigure, CompositableFigure,
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
    protected void updatePathNode(@Nonnull Path path) {
        super.updatePathNode(path);
        applyFillableFigureProperties(path);
        applyStrokeableFigureProperties(path);
        applyCompositableFigureProperties(path);
        applyTransformableFigureProperties(path);
    }

    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }
}
