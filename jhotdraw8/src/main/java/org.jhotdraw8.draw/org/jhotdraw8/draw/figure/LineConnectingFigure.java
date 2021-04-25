/*
 * @(#)LineConnectingFigure.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.draw.figure;

import javafx.geometry.Point2D;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.SimpleNullableKey;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.key.CssPoint2DStyleableMapAccessor;
import org.jhotdraw8.draw.key.CssSizeStyleableKey;

/**
 * LineConnectingFigure.
 *
 * @author Werner Randelshofer
 */
public interface LineConnectingFigure extends ConnectingFigure {
    /**
     * The end position of the line.
     */
    @NonNull
    CssPoint2DStyleableMapAccessor END = LineFigure.END;
    /**
     * The end connector.
     */
    @NonNull
    SimpleNullableKey<Connector> END_CONNECTOR = new SimpleNullableKey<>("endConnector", Connector.class, null);
    /**
     * The end target.
     */
    @NonNull
    SimpleNullableKey<Figure> END_TARGET = new SimpleNullableKey<>("endTarget", Figure.class, null);
    /**
     * The end position of the line.
     */
    @NonNull
    CssSizeStyleableKey END_X = LineFigure.END_X;
    /**
     * The end position of the line.
     */
    @NonNull
    CssSizeStyleableKey END_Y = LineFigure.END_Y;
    /**
     * The start position of the line.
     */
    @NonNull
    CssPoint2DStyleableMapAccessor START = LineFigure.START;
    /**
     * The start connector.
     */
    @NonNull
    SimpleNullableKey<Connector> START_CONNECTOR = new SimpleNullableKey<>("startConnector", Connector.class, null);
    /**
     * The start target.
     */
    @NonNull
    SimpleNullableKey<Figure> START_TARGET = new SimpleNullableKey<>("startTarget", Figure.class, null);
    /**
     * The start position of the line.
     */
    @NonNull CssSizeStyleableKey START_X = LineFigure.START_X;
    /**
     * The start position of the line.
     */
    @NonNull CssSizeStyleableKey START_Y = LineFigure.START_Y;

    default boolean isStartConnected() {
        return get(START_CONNECTOR) != null && get(START_TARGET) != null;
    }

    default boolean isEndConnected() {
        return get(END_CONNECTOR) != null && get(END_TARGET) != null;
    }

    default @Nullable Point2D getStartTargetPoint() {
        Connector connector = get(START_CONNECTOR);
        Figure target = get(START_TARGET);
        if (connector != null && target != null) {
            return worldToLocal(connector.getPointAndTangentInWorld(this, target).getPoint(Point2D::new));
        } else {
            return getNonNull(START).getConvertedValue();
        }
    }

    default @Nullable Point2D getEndTargetPoint() {
        Connector connector = get(END_CONNECTOR);
        Figure target = get(END_TARGET);
        if (connector != null && target != null) {
            return worldToLocal(connector.getPointAndTangentInWorld(this, target).getPoint(Point2D::new));
        } else {
            return getNonNull(END).getConvertedValue();
        }
    }
}
