/*
 * @(#)ConnectingFiguresSample.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.mini;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.draw.connector.RectangleConnector;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.LineConnectionFigure;
import org.jhotdraw8.draw.figure.RectangleFigure;
import org.jhotdraw8.draw.figure.SimpleDrawing;
import org.jhotdraw8.draw.render.SimpleRenderContext;

public class ConnectingFiguresSample {

    public @NonNull Drawing createDrawing() {
        RectangleFigure a = new RectangleFigure(100, 80, 150, 100);  // 1
        RectangleFigure b = new RectangleFigure(300, 230, 150, 100);
        LineConnectionFigure c = new LineConnectionFigure();

        c.setStartConnection(a, new RectangleConnector()); // 2
        c.setEndConnection(b, new RectangleConnector());

        SimpleRenderContext ctx = new SimpleRenderContext();
        c.layout(ctx); // 3

        Drawing drawing = new SimpleDrawing(600, 400); // 4
        drawing.getChildren().add(a);
        drawing.getChildren().add(b);
        drawing.getChildren().add(c);

        return drawing;
    }
}
