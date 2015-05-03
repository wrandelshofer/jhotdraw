
import java.text.ParseException;
import javafx.collections.MapChangeListener;
import javafx.geometry.Rectangle2D;
import org.jhotdraw.collection.Key;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingModelEvent;
import org.jhotdraw.draw.SimpleDrawing;
import org.jhotdraw.draw.SimpleDrawingModel;
import org.jhotdraw.draw.shape.RectangleFigure;
import org.jhotdraw.event.Listener;
/* @(#)Scratchpad.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */

/**
 *
 * @author werni
 */
public class Scratchpad {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ParseException {
        Drawing d = new SimpleDrawing();
        RectangleFigure r = new RectangleFigure();
                r.properties().addListener(new MapChangeListener<Key<?>,Object>() {

            @Override
            public void onChanged(MapChangeListener.Change<? extends Key<?>, ? extends Object> change) {
                System.out.println("Map change:"+change);
            }
        });

        SimpleDrawingModel m=new SimpleDrawingModel();
        m.setRoot(d);
        m.addDrawingModelListener(new Listener<DrawingModelEvent>() {

            @Override
            public void handle(DrawingModelEvent mutation) {
                System.out.println(mutation);
            }
        });
        d=new SimpleDrawing();
        m.setRoot(d);
        d.add(r);
        r.set(RectangleFigure.RECTANGLE, new Rectangle2D(1,2,3,4));
        r.reshape(6,7,8,9);
        r.reshape(1,1,2,4);
        System.exit(0);
    }
    
}
