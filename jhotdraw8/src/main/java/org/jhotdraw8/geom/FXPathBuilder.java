/* @(#)FXPathBuilder.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw8.geom;

import javafx.scene.shape.ArcTo;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;

/**
 * FXPathBuilder.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class FXPathBuilder implements PathBuilder {

    @Override
    public void arcTo(double x0, double y0, double rx, double ry, double xAxisRotation, double x, double y, boolean largeArcFlag, boolean sweepFlag) {
      path.getElements().add(new  ArcTo( rx, ry, xAxisRotation, x, y, largeArcFlag, sweepFlag));
    }

  private Path path= new Path();
    @Override
    public void closePath() {
        path.getElements().add(new  ClosePath());
    }

    @Override
    public void curveTo(double x, double y, double x0, double y0, double x1, double y1) {
       path.getElements().add(new  CubicCurveTo(x,y, x0, y0, x1, y1));
    }

    @Override
    public void lineTo(double x, double y) {
    path.getElements().add(new LineTo(x, y));
    }

    @Override
    public void moveTo(double x, double y) {
     path.getElements().add(new MoveTo(x, y));        
    }

    @Override
    public void quadTo(double x, double y, double x0, double y0) {
     path.getElements().add(new QuadCurveTo(x, y, x0, y0));
    }

    public Path get() {
        return path;
    }
}
