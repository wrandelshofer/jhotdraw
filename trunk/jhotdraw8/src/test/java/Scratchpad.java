/* @(#)Scratchpad.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Scale;


/**
 *
 * @author werni
 */
public class Scratchpad {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ParseException {
        Scale s = new Scale(0,2);
        
        System.out.println("s:"+s);
        try {
            System.out.println("invs:"+s.createInverse());
        } catch (NonInvertibleTransformException ex) {
            System.out.println(ex);
        }
    }

}
