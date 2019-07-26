/*
 * @(#)ConnectingFiguresSample.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.mini;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.draw.connector.RectangleConnector;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.DrawingFigure;
import org.jhotdraw8.draw.figure.Layer;
import org.jhotdraw8.draw.figure.LayerFigure;
import org.jhotdraw8.draw.figure.LineConnectionFigure;
import org.jhotdraw8.draw.figure.RectangleFigure;
import org.jhotdraw8.draw.render.SimpleRenderContext;

public class ConnectingFiguresSample {

    @Nonnull
    public Drawing createDrawing() {
        RectangleFigure a = new RectangleFigure(100, 80, 150, 100);  // 1
        RectangleFigure b = new RectangleFigure(300, 230, 150, 100);
        LineConnectionFigure c = new LineConnectionFigure();

        c.setStartConnection(a, new RectangleConnector()); // 2
        c.setEndConnection(b, new RectangleConnector());

        SimpleRenderContext ctx = new SimpleRenderContext();
        c.layout(ctx); // 3

        Drawing drawing = new DrawingFigure(600, 400); // 4
        Layer layer = new LayerFigure();
        drawing.getChildren().add(layer);
        layer.getChildren().add(a);
        layer.getChildren().add(b);
        layer.getChildren().add(c);

        return drawing;
    }
}
