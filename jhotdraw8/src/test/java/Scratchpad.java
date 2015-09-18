
import java.text.ParseException;
import java.util.List;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.shape.Rectangle;
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
        Group root = new Group();
        Scene scene = new Scene(root);
scene.getStylesheets().add("test.css"); 

        Rectangle r = new Rectangle();
        System.out.println("r.scene:"+r.getScene());
        System.out.println("r.fill:"+r.getFill());
        
        root.getChildren().add(r);
        System.out.println("r.scene:"+r.getScene());
        System.out.println("r.fill:"+r.getFill());
        
        
        for (CssMetaData<? extends Styleable, ?> md : r.getCssMetaData()) {
System.out.println(md);
        }
        
        
    }

}
