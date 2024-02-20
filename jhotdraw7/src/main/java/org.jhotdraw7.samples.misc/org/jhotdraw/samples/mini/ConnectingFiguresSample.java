/* @(#)ConnectingFiguresSample.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.samples.mini;

import org.jhotdraw.draw.*;
import org.jhotdraw.draw.liner.ElbowLiner;
import org.jhotdraw.geom.Geom;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.geom.Point2D;

/**
 * Example showing how to connect two text areas with an elbow connection.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ConnectingFiguresSample {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                
                // Create the two text areas
                TextAreaFigure ta = new TextAreaFigure();
                ta.setBounds(new Point2D.Double(10,10),new Point2D.Double(100,100));
                
                TextAreaFigure tb = new TextAreaFigure();
                tb.setBounds(new Point2D.Double(210,110),new Point2D.Double(300,200));
                
                // Create an elbow connection
                ConnectionFigure cf = new LineConnectionFigure();
                cf.setLiner(new ElbowLiner());
                
                // Connect the figures
                cf.setStartConnector(ta.findConnector(Geom.center(ta.getBounds()), cf));
                cf.setEndConnector(tb.findConnector(Geom.center(tb.getBounds()), cf));
                
                // Add all figures to a drawing
                Drawing drawing = new DefaultDrawing();
                drawing.add(ta);
                drawing.add(tb);
                drawing.add(cf);
                
                // Show the drawing
                JFrame f = new JFrame("My Drawing");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.setSize(400,300);
                
                DrawingView view = new DefaultDrawingView();
                view.setDrawing(drawing);
                f.getContentPane().add(view.getComponent());
                
                f.setVisible(true);
            }
        });
    }
}
