/*
 * @(#)RegionFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.Path;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.connector.PathConnector;
import org.jhotdraw8.draw.locator.BoundsLocator;
import org.jhotdraw8.draw.render.RenderContext;

public class RegionFigure extends AbstractRegionFigure
        implements FillableFigure, StrokableFigure, CompositableFigure,
        StyleableFigure, TransformableFigure, HideableFigure,
        ConnectableFigure, LockableFigure, ResizableFigure {

    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public final static String TYPE_SELECTOR = "Region";

    @Nullable
    @Override
    public Connector findConnector(@NonNull Point2D pointInLocal, Figure connectingFigure) {
        return new PathConnector(new BoundsLocator(getLayoutBounds(), pointInLocal));
    }

    @Override
    public void updateNode(@NonNull RenderContext ctx, @NonNull Node node) {
        super.updateNode(ctx, node);
        applyHideableFigureProperties(ctx, node);
    }

    @Override
    protected void updatePathNode(@NonNull RenderContext ctx, @NonNull Path path) {
        super.updatePathNode(ctx, path);
        applyFillableFigureProperties(ctx, path);
        applyStrokableFigureProperties(ctx, path);
        applyCompositableFigureProperties(ctx, path);
        applyTransformableFigureProperties(ctx, path);
    }

    @NonNull
    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }
}
