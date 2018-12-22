/* @(#)LineConnectingFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.draw.figure;

import javafx.geometry.Point2D;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.key.CssPoint2DStyleableMapAccessor;
import org.jhotdraw8.draw.key.CssSizeStyleableFigureKey;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.NullableObjectFigureKey;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;

/**
 * LineConnectingFigure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface LineConnectingFigure extends ConnectingFigure {
    /**
     * The end position of the line.
     */
    @Nonnull
    CssPoint2DStyleableMapAccessor END = SimpleLineFigure.END;
    /**
     * The end connector.
     */
    @Nonnull
    NullableObjectFigureKey<Connector> END_CONNECTOR = new NullableObjectFigureKey<>("endConnector", Connector.class, DirtyMask.of(DirtyBits.STATE, DirtyBits.LAYOUT_SUBJECT, DirtyBits.LAYOUT, DirtyBits.LAYOUT_OBSERVERS, DirtyBits.TRANSFORM), null);
    /**
     * The end target.
     */
    @Nonnull
    NullableObjectFigureKey<Figure> END_TARGET = new NullableObjectFigureKey<>("endTarget", Figure.class, DirtyMask.of(DirtyBits.STATE, DirtyBits.LAYOUT_SUBJECT, DirtyBits.LAYOUT, DirtyBits.LAYOUT_OBSERVERS, DirtyBits.TRANSFORM), null);
    /**
     * The end position of the line.
     */
    @Nonnull
    CssSizeStyleableFigureKey END_X = SimpleLineFigure.END_X;
    /**
     * The end position of the line.
     */
    @Nonnull
    CssSizeStyleableFigureKey END_Y = SimpleLineFigure.END_Y;
    /**
     * The start position of the line.
     */
    @Nonnull
    CssPoint2DStyleableMapAccessor START = SimpleLineFigure.START;
    /**
     * The start connector.
     */
    @Nonnull
    NullableObjectFigureKey<Connector> START_CONNECTOR = new NullableObjectFigureKey<>("startConnector", Connector.class, DirtyMask.of(DirtyBits.STATE, DirtyBits.LAYOUT_SUBJECT, DirtyBits.LAYOUT, DirtyBits.LAYOUT_OBSERVERS, DirtyBits.TRANSFORM), null);
    /**
     * The start target.
     */
    @Nonnull
    NullableObjectFigureKey<Figure> START_TARGET = new NullableObjectFigureKey<>("startTarget", Figure.class, DirtyMask.of(DirtyBits.STATE, DirtyBits.LAYOUT_SUBJECT, DirtyBits.LAYOUT, DirtyBits.LAYOUT_OBSERVERS, DirtyBits.TRANSFORM), null);
    /**
     * The start position of the line.
     */
    CssSizeStyleableFigureKey START_X = SimpleLineFigure.START_X;
    /**
     * The start position of the line.
     */
    CssSizeStyleableFigureKey START_Y = SimpleLineFigure.START_Y;

    default boolean isStartConnected() {
        return get(START_CONNECTOR) != null && get(START_TARGET) != null;
    }

    default boolean isEndConnected() {
        return get(END_CONNECTOR) != null && get(END_TARGET) != null;
    }

    @Nullable
    default Point2D getStartTargetPoint() {
        Connector connector = get(START_CONNECTOR);
        Figure target = get(START_TARGET);
        if (connector != null && target != null) {
            return worldToLocal(connector.getPositionInWorld(this, target));
        } else {
            return getNonnull(START).getConvertedValue();
        }
    }

    @Nullable
    default Point2D getEndTargetPoint() {
        Connector connector = get(END_CONNECTOR);
        Figure target = get(END_TARGET);
        if (connector != null && target != null) {
            return worldToLocal(connector.getPositionInWorld(this, target));
        } else {
            return getNonnull(END).getConvertedValue();
        }
    }
}
