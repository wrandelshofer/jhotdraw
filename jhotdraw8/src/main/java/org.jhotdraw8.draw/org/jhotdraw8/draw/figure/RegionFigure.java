/*
 * @(#)RegionFigure.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
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
    public static final String TYPE_SELECTOR = "Region";

    @Override
    public @Nullable Connector findConnector(@NonNull Point2D pointInLocal, Figure connectingFigure, double tolerance) {
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

    @Override
    public @NonNull String getTypeSelector() {
        return TYPE_SELECTOR;
    }
}
