/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhotdraw8.geom;

/**
 * DoubleSvgPathBuilder.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class DoubleSvgPathBuilder implements PathBuilder {

    private StringBuilder buf = new StringBuilder();

    @Override
    public void arcTo(double radiusX, double radiusY, double xAxisRotation, double x, double y, boolean largeArcFlag, boolean sweepFlag) {
        buf.append('A')
                .append(Double.toString(radiusX))
                .append(',')
                .append(Double.toString(radiusY))
                .append(' ')
                .append(Double.toString(xAxisRotation))
                .append(',')
                .append(largeArcFlag ? '1' : '0')
                .append(' ')
                .append(sweepFlag ? '1' : '0')
                .append(' ')
                .append(Double.toString(x))
                .append(',')
                .append(Double.toString(y));
    }

    @Override
    public void closePath() {
        buf.append('Z');
    }

    @Override
    public void curveTo(double x1, double y1, double x2, double y2, double x, double y) {
        buf.append('C')
                .append(Double.toString(x1))
                .append(',')
                .append(Double.toString(y1))
                .append(' ')
                .append(Double.toString(x2))
                .append(',')
                .append(Double.toString(y2))
                .append(' ')
                .append(Double.toString(x))
                .append(',')
                .append(Double.toString(y));
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
                .append(Double.toString(x))
                .append(',')
                .append(Double.toString(y));
    }

    @Override
    public void moveTo(double x, double y) {
        buf.append('M')
                .append(Double.toString(x))
                .append(',')
                .append(Double.toString(y));
    }

    @Override
    public void quadTo(double x1, double y1, double x, double y) {
        buf.append('Q')
                .append(Double.toString(x1))
                .append(',')
                .append(Double.toString(y1))
                .append(' ')
                .append(Double.toString(x))
                .append(',')
                .append(Double.toString(y));

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
