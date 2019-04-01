/* @(#)LineConnectingFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.draw.figure;

import javafx.geometry.Point2D;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.key.CssPoint2DStyleableMapAccessor;
import org.jhotdraw8.draw.key.CssSizeStyleableKey;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.NullableObjectKey;

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
    CssPoint2DStyleableMapAccessor END = LineFigure.END;
    /**
     * The end connector.
     */
    @Nonnull
    NullableObjectKey<Connector> END_CONNECTOR = new NullableObjectKey<>("endConnector", Connector.class, DirtyMask.of(DirtyBits.STATE, DirtyBits.LAYOUT_SUBJECT, DirtyBits.LAYOUT, DirtyBits.LAYOUT_OBSERVERS, DirtyBits.TRANSFORM), null);
    /**
     * The end target.
     */
    @Nonnull
    NullableObjectKey<Figure> END_TARGET = new NullableObjectKey<>("endTarget", Figure.class, DirtyMask.of(DirtyBits.STATE, DirtyBits.LAYOUT_SUBJECT, DirtyBits.LAYOUT, DirtyBits.LAYOUT_OBSERVERS, DirtyBits.TRANSFORM), null);
    /**
     * The end position of the line.
     */
    @Nonnull
    CssSizeStyleableKey END_X = LineFigure.END_X;
    /**
     * The end position of the line.
     */
    @Nonnull
    CssSizeStyleableKey END_Y = LineFigure.END_Y;
    /**
     * The start position of the line.
     */
    @Nonnull
    CssPoint2DStyleableMapAccessor START = LineFigure.START;
    /**
     * The start connector.
     */
    @Nonnull
    NullableObjectKey<Connector> START_CONNECTOR = new NullableObjectKey<>("startConnector", Connector.class, DirtyMask.of(DirtyBits.STATE, DirtyBits.LAYOUT_SUBJECT, DirtyBits.LAYOUT, DirtyBits.LAYOUT_OBSERVERS, DirtyBits.TRANSFORM), null);
    /**
     * The start target.
     */
    @Nonnull
    NullableObjectKey<Figure> START_TARGET = new NullableObjectKey<>("startTarget", Figure.class, DirtyMask.of(DirtyBits.STATE, DirtyBits.LAYOUT_SUBJECT, DirtyBits.LAYOUT, DirtyBits.LAYOUT_OBSERVERS, DirtyBits.TRANSFORM), null);
    /**
     * The start position of the line.
     */
    CssSizeStyleableKey START_X = LineFigure.START_X;
    /**
     * The start position of the line.
     */
    CssSizeStyleableKey START_Y = LineFigure.START_Y;

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
