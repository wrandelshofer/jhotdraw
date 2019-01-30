package org.jhotdraw8.samples.mini;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.draw.connector.RectangleConnector;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Layer;
import org.jhotdraw8.draw.figure.SimpleLineConnectionFigure;
import org.jhotdraw8.draw.figure.SimpleRectangleFigure;
import org.jhotdraw8.draw.figure.SimpleDrawing;
import org.jhotdraw8.draw.figure.SimpleLayer;
import org.jhotdraw8.draw.render.SimpleRenderContext;

public class ConnectingFiguresSample  {

    @Nonnull
    public Drawing createDrawing() {
        SimpleRectangleFigure a = new SimpleRectangleFigure(100, 80, 150, 100);  // 1
        SimpleRectangleFigure b = new SimpleRectangleFigure(300, 230, 150, 100);
        SimpleLineConnectionFigure c = new SimpleLineConnectionFigure();
        
        c.setStartConnection(a, new RectangleConnector()); // 2
        c.setEndConnection(b, new RectangleConnector());

        SimpleRenderContext ctx = new SimpleRenderContext();
        c.layout(ctx); // 3
        
        Drawing drawing = new SimpleDrawing(600, 400); // 4
        Layer layer = new SimpleLayer();
        drawing.getChildren().add(layer);
        layer.getChildren().add(a);
        layer.getChildren().add(b);
        layer.getChildren().add(c);

        return drawing;
    }
}
