package org.jhotdraw8.samples.mini;

import org.jhotdraw8.draw.connector.RectangleConnector;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Layer;
import org.jhotdraw8.draw.figure.SimpleLineConnectionFigure;
import org.jhotdraw8.draw.figure.SimpleRectangleFigure;
import org.jhotdraw8.draw.figure.SimpleDrawing;
import org.jhotdraw8.draw.figure.SimpleLayer;

public class ConnectingFiguresSample  {

    public Drawing createDrawing() {
        SimpleRectangleFigure a = new SimpleRectangleFigure(100, 80, 150, 100);  // 1
        SimpleRectangleFigure b = new SimpleRectangleFigure(300, 230, 150, 100);
        SimpleLineConnectionFigure c = new SimpleLineConnectionFigure();
        
        c.setStartConnection(a, new RectangleConnector()); // 2
        c.setEndConnection(b, new RectangleConnector());
        
        c.layout(); // 3
        
        Drawing drawing = new SimpleDrawing(600, 400); // 4
        Layer layer = new SimpleLayer();
        drawing.getChildren().addAll(layer);
        layer.getChildren().addAll(a, b, c);
        
        return drawing;
    }
}
