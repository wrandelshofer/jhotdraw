/* @(#)LineConnectingFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.draw.figure;

import javafx.geometry.Point2D;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.key.CssPoint2DStyleableMapAccessor;
import org.jhotdraw8.draw.key.CssSizeStyleableFigureKey;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.SimpleFigureKey;

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
    SimpleFigureKey<Connector> END_CONNECTOR = new SimpleFigureKey<>("endConnector", Connector.class, DirtyMask.of(DirtyBits.STATE, DirtyBits.LAYOUT_SUBJECT, DirtyBits.LAYOUT, DirtyBits.LAYOUT_OBSERVERS, DirtyBits.TRANSFORM), null);
    /**
     * The end target.
     */
    @Nonnull
    SimpleFigureKey<Figure> END_TARGET = new SimpleFigureKey<>("endTarget", Figure.class, DirtyMask.of(DirtyBits.STATE, DirtyBits.LAYOUT_SUBJECT, DirtyBits.LAYOUT, DirtyBits.LAYOUT_OBSERVERS, DirtyBits.TRANSFORM), null);
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
    SimpleFigureKey<Connector> START_CONNECTOR = new SimpleFigureKey<>("startConnector", Connector.class, DirtyMask.of(DirtyBits.STATE, DirtyBits.LAYOUT_SUBJECT, DirtyBits.LAYOUT, DirtyBits.LAYOUT_OBSERVERS, DirtyBits.TRANSFORM), null);
    /**
     * The start target.
     */
    @Nonnull
    SimpleFigureKey<Figure> START_TARGET = new SimpleFigureKey<>("startTarget", Figure.class, DirtyMask.of(DirtyBits.STATE, DirtyBits.LAYOUT_SUBJECT, DirtyBits.LAYOUT, DirtyBits.LAYOUT_OBSERVERS, DirtyBits.TRANSFORM), null);
    /**
     * The start position of the line.
     */
    CssSizeStyleableFigureKey START_X = SimpleLineFigure.START_X;
    /**
     * The start position of the line.
     */
    CssSizeStyleableFigureKey START_Y = SimpleLineFigure.START_Y;
        default boolean isStartConnected() {
        return get(START_CONNECTOR)!=null&&get(START_TARGET)!=null;
    }
    default boolean isEndConnected() {
        return get(END_CONNECTOR)!=null&&get(END_TARGET)!=null;
    }
    @Nullable
    default Point2D getStartTargetPoint() {
        if (isStartConnected()) {
        return worldToLocal(getNonnull(START_CONNECTOR).getPositionInWorld(this, getNonnull(START_TARGET)));
        }else{
            return getNonnull(START).getConvertedValue();
        }
    }
    @Nullable
    default Point2D getEndTargetPoint() {
        if (isEndConnected()) {
        return worldToLocal(getNonnull(END_CONNECTOR).getPositionInWorld(this, getNonnull(END_TARGET)));
        }else{
            return getNonnull(END).getConvertedValue();
        }
    }
}
