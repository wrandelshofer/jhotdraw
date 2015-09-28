
import java.text.ParseException;
import java.util.List;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.shape.Rectangle;
import org.jhotdraw.beans.ClampedDoubleProperty;
import org.jhotdraw.beans.NonnullProperty;
import static org.jhotdraw.draw.DrawingView.DRAWING_MODEL_PROPERTY;
import org.jhotdraw.draw.model.ConnectionsNoLayoutDrawingModel;
import org.jhotdraw.draw.model.DrawingModel;
/* @(#)Scratchpad.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
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
        ClampedDoubleProperty p1=new ClampedDoubleProperty(null,null,0.5,0,1);
        DoubleProperty p2=new SimpleDoubleProperty(8);
        p1.addListener((o,oldv,newv)->System.out.println("oldv:"+oldv+" newv:"+newv));
        p1.set(-7);
        p1.bind(p2);
        System.out.println(p1);
    }

}
