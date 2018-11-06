package org.jhotdraw8.css;

import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import org.jhotdraw8.collection.ImmutableList;

public class CssStroke {
    private final CssSize width;
    private final Paintable paint;
    private final CssSize dashOffset;
    private final ImmutableList<CssSize> dashArray;
    private final StrokeType type;
    private final StrokeLineJoin lineJoin;
    private final StrokeLineCap lineCap;
    private final CssSize miterLimit;

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
}
