
import java.awt.BasicStroke;
import java.awt.geom.Path2D;
import java.text.ParseException;
import java.awt.Shape;
import java.awt.geom.PathIterator;
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
        Path2D.Double p = new Path2D.Double();
        p.moveTo(10,10);
        p.lineTo(100,100);
        p.curveTo(10, 10, 20, 30, 40, 50);
        System.out.println("hey");
        BasicStroke s = new BasicStroke();
        Shape sh = s.createStrokedShape(p);
        double[] coords=new double[6];
        for (PathIterator i=sh.getPathIterator(null);!i.isDone();) {
            i.next();
            int type=i.currentSegment(coords);
            switch (type) {
                case PathIterator.SEG_CLOSE:
                    System.out.println("close");
                    break;
                case PathIterator.SEG_CUBICTO:
                    System.out.println("cubicto");
                    break;
                case PathIterator.SEG_LINETO:
                    System.out.println("lineto");
                    break;
                case PathIterator.SEG_MOVETO:
                    System.out.println("quadto");
                    break;
                case PathIterator.SEG_QUADTO:
                    System.out.println("quadto");
                    break;
            }
            
        }
       
    }

}
