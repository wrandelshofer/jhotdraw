
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Formatter;
import java.util.Locale;
import javafx.geometry.Point2D;
import org.jhotdraw.text.Point2DConverter;
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
        Point2DConverter c = new Point2DConverter();
        String str = c.toString(new Point2D(3e15,3.14519265788947687879));
        System.out.println(str);
        Object value = c.toValue(str);
        System.out.println(value);
        System.exit(0);
    }
    
}
