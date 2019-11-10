/*
 * @(#)CssStroke.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
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

public class CssStroke {
    private final CssSize width;
    private final Paintable paint;
    private final CssSize dashOffset;
    private final ImmutableList<CssSize> dashArray;
    private final StrokeType type;
    private final StrokeLineJoin lineJoin;
    private final StrokeLineCap lineCap;
    private final CssSize miterLimit;

    public CssStroke(Paintable paint) {
        this(CssSize.ONE, paint, StrokeType.CENTERED, StrokeLineCap.BUTT, StrokeLineJoin.MITER, new CssSize(4.0),
                CssSize.ZERO, ImmutableLists.emptyList());
    }

    public CssStroke(CssSize width, Paintable paint) {
        this(width, paint, StrokeType.CENTERED, StrokeLineCap.BUTT, StrokeLineJoin.MITER, new CssSize(4.0),
                CssSize.ZERO, ImmutableLists.emptyList());
    }

    public CssStroke(CssSize width, Paintable paint, StrokeType type, StrokeLineCap lineCap, StrokeLineJoin lineJoin, CssSize miterLimit,
                     CssSize dashOffset,
                     ImmutableList<CssSize> dashArray) {
        this.width = width;
        this.paint = paint;
        this.dashOffset = dashOffset;
        this.dashArray = dashArray;
        this.type = type;
        this.lineJoin = lineJoin;
        this.lineCap = lineCap;
        this.miterLimit = miterLimit;
    }

    public CssSize getWidth() {
        return width;
    }

    public Paintable getPaint() {
        return paint;
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
        CssStroke cssStroke = (CssStroke) o;
        return Objects.equals(width, cssStroke.width) &&
                Objects.equals(paint, cssStroke.paint) &&
                Objects.equals(dashOffset, cssStroke.dashOffset) &&
                Objects.equals(dashArray, cssStroke.dashArray) &&
                type == cssStroke.type &&
                lineJoin == cssStroke.lineJoin &&
                lineCap == cssStroke.lineCap &&
                Objects.equals(miterLimit, cssStroke.miterLimit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, paint, dashOffset, dashArray, type, lineJoin, lineCap, miterLimit);
    }

    @NonNull
    @Override
    public String toString() {
        return "CssStroke{" +
                "width=" + width +
                ", paint=" + paint +
                ", type=" + type +
                ", lineJoin=" + lineJoin +
                ", lineCap=" + lineCap +
                ", miterLimit=" + miterLimit +
                ", dashOffset=" + dashOffset +
                ", dashArray=" + dashArray +
                '}';
    }
}
