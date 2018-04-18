/* @(#)SvgFloatPathBuilder.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom;

/**
 * DoubleSvgPathBuilder.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SvgFloatPathBuilder implements PathBuilder {

    private StringBuilder buf = new StringBuilder();

    @Override
    public void arcTo(double radiusX, double radiusY, double xAxisRotation, double x, double y, boolean largeArcFlag, boolean sweepFlag) {
        buf.append('A')
                .append(Float.toString((float) radiusX))
                .append(',')
                .append(Float.toString((float) radiusY))
                .append(' ')
                .append(Float.toString((float) xAxisRotation))
                .append(',')
                .append(largeArcFlag ? '1' : '0')
                .append(' ')
                .append(sweepFlag ? '1' : '0')
                .append(' ')
                .append(Float.toString((float) x))
                .append(',')
                .append(Float.toString((float) y));
    }

    @Override
    public void closePath() {
        buf.append('Z');
    }

    @Override
    public void curveTo(double x1, double y1, double x2, double y2, double x, double y) {
        buf.append('C')
                .append(Float.toString((float) x1))
                .append(',')
                .append(Float.toString((float) y1))
                .append(' ')
                .append(Float.toString((float) x2))
                .append(',')
                .append(Float.toString((float) y2))
                .append(' ')
                .append(Float.toString((float) x))
                .append(',')
                .append(Float.toString((float) y));
    }

    @Override
    public double getLastCX() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getLastCY() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getLastX() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getLastY() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void lineTo(double x, double y) {
        buf.append('L')
                .append(Float.toString((float) x))
                .append(',')
                .append(Float.toString((float) y));
    }

    @Override
    public void moveTo(double x, double y) {
        buf.append('M')
                .append(Float.toString((float) x))
                .append(',')
                .append(Float.toString((float) y));
    }

    @Override
    public void quadTo(double x1, double y1, double x, double y) {
        buf.append('Q')
                .append(Float.toString((float) x1))
                .append(',')
                .append(Float.toString((float) y1))
                .append(' ')
                .append(Float.toString((float) x))
                .append(',')
                .append(Float.toString((float) y));

    }

    @Override
    public void smoothCurveTo(double x2, double y2, double x, double y) {
        PathBuilder.super.smoothCurveTo(x2, y2, x, y); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void smoothQuadTo(double x, double y) {
        PathBuilder.super.smoothQuadTo(x, y); //To change body of generated methods, choose Tools | Templates.
    }

    public String build() {
        return buf.toString();
    }
}
