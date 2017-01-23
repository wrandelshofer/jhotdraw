package org.jhotdraw8.samples.mini;

import org.jhotdraw8.draw.connector.ChopRectangleConnector;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Layer;
import org.jhotdraw8.draw.figure.LineConnectionFigure;
import org.jhotdraw8.draw.figure.RectangleFigure;
import org.jhotdraw8.draw.figure.SimpleDrawing;
import org.jhotdraw8.draw.figure.SimpleLayer;

public class ConnectingFiguresSample  {

    public Drawing createDrawing() {
        RectangleFigure a = new RectangleFigure(100, 80, 150, 100);  // 1
        RectangleFigure b = new RectangleFigure(300, 230, 150, 100);
        LineConnectionFigure c = new LineConnectionFigure();
        
        c.setStartConnection(a, new ChopRectangleConnector()); // 2
        c.setEndConnection(b, new ChopRectangleConnector());
        
        c.layout(); // 3
        
        Drawing drawing = new SimpleDrawing(600, 400); // 4
        Layer layer = new SimpleLayer();
        drawing.getChildren().addAll(layer);
        layer.getChildren().addAll(a, b, c);
        
        return drawing;
    }
}
