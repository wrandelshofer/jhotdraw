/* @(#)SVGFigureFactory.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.samples.svg.io;

import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.CompositeFigure;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.geom.BezierPath;
import org.jhotdraw.samples.svg.Gradient;

import org.jhotdraw.annotation.Nullable;
import javax.swing.text.StyledDocument;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Map;

/**
 * Creates Figures for SVG elements.
 * <p>
 * Design pattern:<br>
 * Name: Abstract Factory.<br>
 * Role: Abstract Factory.<br>
 * Partners: {@link SVGInputFormat} as Client. 
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface SVGFigureFactory {
    public Figure createRect(
            double x, double y, double width, double height, double rx, double ry, 
            Map<AttributeKey<?>,Object> attributes);
    
    public Figure createCircle(
            double cx, double cy, double r, 
            Map<AttributeKey<?>,Object> attributes);
    
    public Figure createEllipse(
            double cx, double cy, double rx, double ry, 
            Map<AttributeKey<?>,Object> attributes);

    public Figure createLine(
            double x1, double y1, double x2, double y2, 
            Map<AttributeKey<?>,Object> attributes);

    public Figure createPolyline(
            Point2D.Double[] points, 
            Map<AttributeKey<?>,Object> attributes);
    
    public Figure createPolygon(
            Point2D.Double[] points, 
            Map<AttributeKey<?>,Object> attributes);

    public Figure createPath(
            BezierPath[] beziers, 
            Map<AttributeKey<?>,Object> attributes);

    public CompositeFigure createG(Map<AttributeKey<?>,Object> attributes);
    
    public Figure createText(
            Point2D.Double[] coordinates, double[] rotate,
            StyledDocument text,  
            Map<AttributeKey<?>,Object> attributes);
    
    public Figure createTextArea(double x, double y, double w, double h, 
            StyledDocument doc, Map<AttributeKey<?>,Object> attributes);

    /**
     * Creates a Figure from an image element.
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @param width The width.
     * @param height The height.
     * @param imageData Holds the image data. Can be null, if the buffered image
     * has not been created from a file.
     * @param bufferedImage Holds the buffered image. Can be null, if the 
     * image data has not been interpreted.
     * @param attributes Figure attributes.
     */
    public Figure createImage(double x, double y, double width, double height, 
           @Nullable byte[] imageData, @Nullable BufferedImage bufferedImage, Map<AttributeKey<?>,Object> attributes);


    public Gradient createLinearGradient(
            double x1, double y1, double x2, double y2, 
            double[] stopOffsets, Color[] stopColors, double[] stopOpacities,
            boolean isRelativeToFigureBounds, AffineTransform tx);
    
    public Gradient createRadialGradient(
            double cx, double cy, double fx, double fy, double r, 
            double[] stopOffsets, Color[] stopColors, double[] stopOpacities,
            boolean isRelativeToFigureBounds, AffineTransform tx);

}
