/*
 * @(#)CssStrokeStyle.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css;

import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableLists;

import java.util.Objects;

public class CssStrokeStyle {
    private final CssSize dashOffset;
    private final ImmutableList<CssSize> dashArray;
    private final StrokeType type;
    private final StrokeLineJoin lineJoin;
    private final StrokeLineCap lineCap;
    private final CssSize miterLimit;

    public CssStrokeStyle() {
        this(StrokeType.CENTERED, StrokeLineCap.BUTT, StrokeLineJoin.MITER, new CssSize(4.0),
                CssSize.ZERO, ImmutableLists.emptyList());
    }

    public CssStrokeStyle(StrokeType type, StrokeLineCap lineCap, StrokeLineJoin lineJoin, CssSize miterLimit,
                          CssSize dashOffset,
                          ImmutableList<CssSize> dashArray) {
        this.dashOffset = dashOffset;
        this.dashArray = dashArray;
        this.type = type;
        this.lineJoin = lineJoin;
        this.lineCap = lineCap;
        this.miterLimit = miterLimit;
    }

    public CssSize getDashOffset() {
        return dashOffset;
    }

    public ImmutableList<CssSize> getDashArray() {
        return dashArray;
    }

    public StrokeType getType() {
        return type;
    }

    public StrokeLineJoin getLineJoin() {
        return lineJoin;
    }

    public StrokeLineCap getLineCap() {
        return lineCap;
    }

    public CssSize getMiterLimit() {
        return miterLimit;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CssStrokeStyle cssStroke = (CssStrokeStyle) o;
        return Objects.equals(dashOffset, cssStroke.dashOffset) &&
                Objects.equals(dashArray, cssStroke.dashArray) &&
                type == cssStroke.type &&
                lineJoin == cssStroke.lineJoin &&
                lineCap == cssStroke.lineCap &&
                Objects.equals(miterLimit, cssStroke.miterLimit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dashOffset, dashArray, type, lineJoin, lineCap, miterLimit);
    }

    @Override
    public @NonNull String toString() {
        return "CssStrokeStyle{" +
                ", type=" + type +
                ", lineJoin=" + lineJoin +
                ", lineCap=" + lineCap +
                ", miterLimit=" + miterLimit +
                ", dashOffset=" + dashOffset +
                ", dashArray=" + dashArray +
                '}';
    }
}
