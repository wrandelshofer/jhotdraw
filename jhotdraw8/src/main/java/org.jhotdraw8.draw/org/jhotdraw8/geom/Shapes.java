/*
 * @(#)Shapes.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom;

import javafx.geometry.Bounds;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.VLineTo;
import javafx.scene.text.Text;
import javafx.scene.transform.MatrixType;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.geom.intersect.IntersectLinePoint;
import org.jhotdraw8.io.StreamPosTokenizer;
import org.jhotdraw8.svg.SvgPath2D;
import org.jhotdraw8.text.NumberConverter;
import org.jhotdraw8.xml.text.XmlNumberConverter;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.IllegalPathStateException;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Shapes.
 *
 * @author Werner Randelshofer
 */
public class Shapes {


    private static final Logger LOGGER = Logger.getLogger(Shapes.class.getName());

    /**
     * Creates a new instance.
     */
    private Shapes() {
    }

    public static int awtCapFromFX(@Nullable StrokeLineCap cap) {
        if (cap == null) {
            return BasicStroke.CAP_BUTT;
        }
        switch (cap) {
            case BUTT:
            default:
                return BasicStroke.CAP_BUTT;
            case ROUND:
                return BasicStroke.CAP_ROUND;
            case SQUARE:
                return BasicStroke.CAP_SQUARE;
        }
    }

    public static int awtJoinFromFX(@Nullable StrokeLineJoin join) {
        if (join == null) {
            return BasicStroke.JOIN_BEVEL;
        }
        switch (join) {
            default:
            case BEVEL:
                return BasicStroke.JOIN_BEVEL;
            case MITER:
                return BasicStroke.JOIN_MITER;
            case ROUND:
                return BasicStroke.JOIN_ROUND;
        }
    }

    /**
     * Converts a JavaFX shape to a AWT shape.
     * <p>
     * If conversion fails, returns a Rectangle.Double with the layout bounds
     * of the shape.
     *
     * @param fx A JavaFX shape
     * @return AWT Shape or Rectangle
     */
    public static Shape awtShapeFromFX(javafx.scene.shape.Shape fx) {
        if (fx instanceof Arc) {
            return awtShapeFromFXArc((Arc) fx);
        } else if (fx instanceof Circle) {
            return awtShapeFromFXCircle((Circle) fx);
        } else if (fx instanceof CubicCurve) {
            return awtShapeFromFXCubicCurve((CubicCurve) fx);
        } else if (fx instanceof Ellipse) {
            return awtShapeFromFXEllipse((Ellipse) fx);
        } else if (fx instanceof Line) {
            return awtShapeFromFXLine((Line) fx);
        } else if (fx instanceof Path) {
            return awtShapeFromFXPath((Path) fx);
        } else if (fx instanceof Polygon) {
            return awtShapeFromFXPolygon((Polygon) fx);
        } else if (fx instanceof Polyline) {
            return awtShapeFromFXPolyline((Polyline) fx);
        } else if (fx instanceof QuadCurve) {
            return awtShapeFromFXQuadCurve((QuadCurve) fx);
        } else if (fx instanceof Rectangle) {
            return awtShapeFromFXRectangle((Rectangle) fx);
        } else if (fx instanceof SVGPath) {
            try {
                return awtShapeFromFXSvgPath((SVGPath) fx);
            } catch (ParseException e) {
                LOGGER.warning("Illegal path: " + e.getMessage());
                Bounds b = fx.getLayoutBounds();
                return new Rectangle2D.Double(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
            }
        } else if (fx instanceof Text) {
            return awtShapeFromFXText((Text) fx);
        } else {
            LOGGER.warning("Unsupported shape: " + fx);
            Bounds b = fx.getLayoutBounds();
            return new Rectangle2D.Double(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
        }
    }

    private static @NonNull Shape awtShapeFromFXArc(@NonNull Arc node) {
        double centerX = node.getCenterX();
        double centerY = node.getCenterY();
        double radiusX = node.getRadiusX();
        double radiusY = node.getRadiusY();
        double startAngle = Math.toRadians(-node.getStartAngle());
        double endAngle = Math.toRadians(-node.getStartAngle() - node.getLength());
        double length = node.getLength();

        double startX = radiusX * Math.cos(startAngle);
        double startY = radiusY * Math.sin(startAngle);

        double endX = centerX + radiusX * Math.cos(endAngle);
        double endY = centerY + radiusY * Math.sin(endAngle);

        int xAxisRot = 0;
        boolean largeArc = (length > 180);
        boolean sweep = (length < 0);

        SvgPath2D p = new SvgPath2D();
        p.moveTo(centerX, centerY);

        if (ArcType.ROUND == node.getType()) {
            p.lineTo(startX, startY);
        }

        p.arcTo(radiusX, radiusY, xAxisRot, largeArc, sweep, endX, endY);

        if (ArcType.CHORD == node.getType()
                || ArcType.ROUND == node.getType()) {
            p.closePath();
        }
        return p;
    }

    private static @NonNull Shape awtShapeFromFXCircle(@NonNull Circle node) {
        double x = node.getCenterX();
        double y = node.getCenterY();
        double r = node.getRadius();
        return new Ellipse2D.Double(x - r, y - r, r * 2, r * 2);
    }

    private static @NonNull Shape awtShapeFromFXCubicCurve(@NonNull CubicCurve e) {
        Path2D.Double p = new Path2D.Double();
        p.moveTo(e.getStartX(), e.getStartY());
        p.curveTo(e.getControlX1(), e.getControlY1(), e.getControlX2(), e.getControlY2(), e.getEndX(), e.getEndY());
        return p;
    }

    private static @NonNull Shape awtShapeFromFXEllipse(@NonNull Ellipse node) {
        double x = node.getCenterX();
        double y = node.getCenterY();
        double rx = node.getRadiusX();
        double ry = node.getRadiusY();
        return new Ellipse2D.Double(x - rx, y - ry, rx * 2, ry * 2);
    }

    private static @NonNull Shape awtShapeFromFXLine(@NonNull Line node) {
        Line2D.Double p = new Line2D.Double(node.getStartX(), node.getStartY(), node.getEndX(), node.getEndY());
        return p;
    }

    private static @NonNull Shape awtShapeFromFXPath(@NonNull Path node) {
        return awtShapeFromFXPathElements(node.getElements(), node.getFillRule());
    }

    public static @NonNull Shape awtShapeFromFXPathElements(@NonNull List<PathElement> pathElements, FillRule fillRule) {
        SvgPath2D p = new SvgPath2D();
        p.setWindingRule(fillRule == FillRule.NON_ZERO ? PathIterator.WIND_NON_ZERO : PathIterator.WIND_EVEN_ODD);
        double x = 0;
        double y = 0;
        for (PathElement pe : pathElements) {
            if (pe instanceof MoveTo) {
                MoveTo e = (MoveTo) pe;
                if (e.isAbsolute()) {
                    x = e.getX();
                    y = e.getY();
                } else {
                    x += e.getX();
                    y += e.getY();
                }
                p.moveTo(x, y);
            } else if (pe instanceof LineTo) {
                LineTo e = (LineTo) pe;
                if (e.isAbsolute()) {
                    x = e.getX();
                    y = e.getY();
                } else {
                    x += e.getX();
                    y += e.getY();
                }
                p.lineTo(x, y);
            } else if (pe instanceof CubicCurveTo) {
                CubicCurveTo e = (CubicCurveTo) pe;
                final double cx1, cy1, cx2, cy2;
                if (e.isAbsolute()) {
                    cx1 = e.getControlX1();
                    cy1 = e.getControlY1();
                    cx2 = e.getControlX2();
                    cy2 = e.getControlY2();
                    x = e.getX();
                    y = e.getY();
                } else {
                    cx1 = x + e.getControlX1();
                    cy1 = y + e.getControlY1();
                    cx2 = x + e.getControlX2();
                    cy2 = y + e.getControlY2();
                    x += e.getX();
                    y += e.getY();
                }
                p.curveTo(cx1, cy1, cx2, cy2, x, y);
            } else if (pe instanceof QuadCurveTo) {
                QuadCurveTo e = (QuadCurveTo) pe;
                final double cx, cy;
                if (e.isAbsolute()) {
                    cx = e.getControlX();
                    cy = e.getControlY();
                    x = e.getX();
                    y = e.getY();
                } else {
                    cx = x + e.getControlX();
                    cy = y + e.getControlY();
                    x += e.getX();
                    y += e.getY();
                }
                p.quadTo(cx, cy, x, y);
            } else if (pe instanceof ArcTo) {
                ArcTo e = (ArcTo) pe;
                if (e.isAbsolute()) {
                    x = e.getX();
                    y = e.getY();
                } else {
                    x += e.getX();
                    y += e.getY();
                }
                p.arcTo(e.getRadiusX(), e.getRadiusY(), e.getXAxisRotation(), e.isLargeArcFlag(), e.isSweepFlag(), x, y);
            } else if (pe instanceof HLineTo) {
                HLineTo e = (HLineTo) pe;
                if (e.isAbsolute()) {
                    x = e.getX();
                } else {
                    x += e.getX();
                }
                p.lineTo(x, y);
            } else if (pe instanceof VLineTo) {
                VLineTo e = (VLineTo) pe;
                if (e.isAbsolute()) {
                    y = e.getY();
                } else {
                    y += e.getY();
                }
                p.lineTo(x, y);
            } else if (pe instanceof ClosePath) {
                p.closePath();
            }
        }
        return p;
    }

    private static @NonNull Shape awtShapeFromFXPolygon(@NonNull Polygon node) {
        Path2D.Double p = new Path2D.Double();
        List<Double> ps = node.getPoints();
        for (int i = 0, n = ps.size(); i < n; i += 2) {
            if (i == 0) {
                p.moveTo(ps.get(i), ps.get(i + 1));
            } else {
                p.lineTo(ps.get(i), ps.get(i + 1));
            }
        }
        p.closePath();
        return p;
    }

    private static @NonNull Shape awtShapeFromFXPolyline(@NonNull Polyline node) {
        Path2D.Double p = new Path2D.Double();
        List<Double> ps = node.getPoints();
        for (int i = 0, n = ps.size(); i < n; i += 2) {
            if (i == 0) {
                p.moveTo(ps.get(i), ps.get(i + 1));
            } else {
                p.lineTo(ps.get(i), ps.get(i + 1));
            }
        }
        return p;
    }

    private static @NonNull Shape awtShapeFromFXQuadCurve(@NonNull QuadCurve node) {
        Path2D.Double p = new Path2D.Double();
        p.moveTo(node.getStartX(), node.getStartY());
        p.quadTo(node.getControlX(), node.getControlY(), node.getEndX(), node.getEndY());
        return p;
    }

    public static @NonNull Shape awtShapeFromFXRectangle(@NonNull Rectangle node) {
        if (node.getArcHeight() == 0 && node.getArcWidth() == 0) {
            return new Rectangle2D.Double(
                    node.getX(),
                    node.getY(),
                    node.getWidth(),
                    node.getHeight()
            );

        } else {
            return new RoundRectangle2D.Double(
                    node.getX(),
                    node.getY(),
                    node.getWidth(),
                    node.getHeight(),
                    node.getArcWidth(),
                    node.getArcHeight()
            );
        }
    }

    private static Shape awtShapeFromFXSvgPath(@NonNull SVGPath node) throws ParseException {
        AWTPathBuilder b = new AWTPathBuilder();
        buildFromSvgString(b, node.getContent());
        return b.build();
    }

    private static @NonNull Shape awtShapeFromFXText(@NonNull Text node) {
        Path path = (Path) javafx.scene.shape.Shape.subtract(node, new Rectangle());
        return awtShapeFromFXPath(path);
    }

    /**
     * Returns a value as a SvgPath2D.
     * <p>
     * Also supports elliptical arc commands 'a' and 'A' as specified in
     * http://www.w3.org/TR/SVG/paths.html#PathDataEllipticalArcCommands
     *
     * @param str the SVG path
     * @return the SvgPath2D
     * @throws java.io.IOException if the String is not a valid path
     */
    public static @NonNull Path2D.Double awtShapeFromSvgString(@NonNull String str) throws ParseException {
        AWTPathBuilder b = new AWTPathBuilder();
        buildFromSvgString(b, str);
        return b.build();
    }

    /**
     * Converts a Java Path iterator to a JavaFX shape.
     *
     * @param fxT A JavaFX Transform.
     * @return An AWT Transform.
     */
    public static @Nullable AffineTransform awtTransformFromFX(@Nullable javafx.scene.transform.Transform fxT) {
        if (fxT == null) {
            return null;
        }

        double[] m = fxT.toArray(MatrixType.MT_2D_2x3);
        return fxT == null ? null : new AffineTransform(m[0], m[3], m[1], m[4], m[2], m[5]);
    }

    public static @NonNull <T extends PathBuilder> T buildFromFXPathElements(@NonNull T builder, @NonNull List<PathElement> pathElements) {
        double x = 0;
        double y = 0;
        double ix = 0, iy = 0;
        for (PathElement pe : pathElements) {
            if (pe instanceof MoveTo) {
                MoveTo e = (MoveTo) pe;
                if (e.isAbsolute()) {
                    x = e.getX();
                    y = e.getY();
                } else {
                    x += e.getX();
                    y += e.getY();
                }
                ix = x;
                iy = y;
                builder.moveTo(x, y);
            } else if (pe instanceof LineTo) {
                LineTo e = (LineTo) pe;
                if (e.isAbsolute()) {
                    x = e.getX();
                    y = e.getY();
                } else {
                    x += e.getX();
                    y += e.getY();
                }
                builder.lineTo(x, y);
            } else if (pe instanceof CubicCurveTo) {
                CubicCurveTo e = (CubicCurveTo) pe;
                if (e.isAbsolute()) {
                    x = e.getX();
                    y = e.getY();
                    builder.curveTo(e.getControlX1(), e.getControlY1(),
                            e.getControlX2(), e.getControlY2(),
                            x, y);
                } else {
                    builder.curveTo(e.getControlX1() + x, e.getControlY1() + y,
                            e.getControlX2() + x, e.getControlY2() + y,
                            x += e.getX(), y += e.getY());
                }
            } else if (pe instanceof QuadCurveTo) {
                QuadCurveTo e = (QuadCurveTo) pe;
                if (e.isAbsolute()) {
                    x = e.getX();
                    y = e.getY();
                    builder.quadTo(e.getControlX(), e.getControlY(), x, y);
                } else {
                    builder.quadTo(e.getControlX() + x, e.getControlY() + y, x += e.getX(), y += e.getY());
                }
            } else if (pe instanceof ArcTo) {
                ArcTo e = (ArcTo) pe;
                if (e.isAbsolute()) {
                    x = e.getX();
                    y = e.getY();
                    builder.arcTo(e.getRadiusX(), e.getRadiusY(), e.getXAxisRotation(), x, y, e.isLargeArcFlag(), e.isSweepFlag());
                } else {
                    builder.arcTo(e.getRadiusX(), e.getRadiusY(), e.getXAxisRotation(), x += e.getX(), y += e.getY(), e.isLargeArcFlag(), e.isSweepFlag());
                }
            } else if (pe instanceof HLineTo) {
                HLineTo e = (HLineTo) pe;
                if (e.isAbsolute()) {
                    x = e.getX();
                } else {
                    x += e.getX();
                }
                builder.lineTo(x, y);
            } else if (pe instanceof VLineTo) {
                VLineTo e = (VLineTo) pe;
                if (e.isAbsolute()) {
                    y = e.getY();
                } else {
                    y += e.getY();
                }
                builder.lineTo(x, y);
            } else if (pe instanceof ClosePath) {
                builder.closePath();
                x = ix;
                y = iy;
            }
        }
        builder.pathDone();
        return builder;
    }

    public static @NonNull <T extends PathBuilder> T buildFromPathIterator(@NonNull T builder, @NonNull PathIterator iter) {
        return buildFromPathIterator(builder, iter, true);
    }

    public static @NonNull <T extends PathBuilder> T buildFromPathIterator(@NonNull T builder, @NonNull PathIterator iter, boolean doPathDone) {
        double[] coords = new double[6];
        boolean needsMoveTo = true;
        for (; !iter.isDone(); iter.next()) {
            switch (iter.currentSegment(coords)) {
            case PathIterator.SEG_CLOSE:
                builder.closePath();
                needsMoveTo = true;
                break;
            case PathIterator.SEG_CUBICTO:
                if (needsMoveTo) {
                        throw new IllegalStateException("Missing initial moveto in path definition.");
                    }
                    builder.curveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
                    break;
                case PathIterator.SEG_LINETO:
                    if (needsMoveTo) {
                        throw new IllegalStateException("Missing initial moveto in path definition.");
                    }
                    builder.lineTo(coords[0], coords[1]);
                    break;
                case PathIterator.SEG_QUADTO:
                    if (needsMoveTo) {
                        throw new IllegalStateException("Missing initial moveto in path definition.");
                    }
                    builder.quadTo(coords[0], coords[1], coords[2], coords[3]);
                    break;
            case PathIterator.SEG_MOVETO:
                builder.moveTo(coords[0], coords[1]);
                needsMoveTo = false;
                break;
            default:
                throw new IllegalArgumentException("Unsupported segment type:" + iter.currentSegment(coords));
            }
        }
        if (doPathDone) {
            builder.pathDone();
        }
        return builder;
    }

    /**
     * Returns a value as a SvgPath2D.
     * <p>
     * Also supports elliptical arc commands 'a' and 'A' as specified in
     * http://www.w3.org/TR/SVG/paths.html#PathDataEllipticalArcCommands
     *
     * @param builder the builder
     * @param str     the SVG path
     * @return the path builder
     * @throws java.io.IOException if the String is not a valid path
     */
    public static @NonNull PathBuilder buildFromSvgString(@NonNull PathBuilder builder, @NonNull String str) throws ParseException {
        StreamPosTokenizer tt = new StreamPosTokenizer(new StringReader(str));
        try {

            tt.resetSyntax();
            tt.parseNumbers();
            tt.parseExponents();
            tt.parsePlusAsNumber();
            tt.whitespaceChars(0, ' ');
            tt.whitespaceChars(',', ',');

            char next = 'M';
            char command = 'M';
            double x = 0, y = 0; // current point
            double cx1 = 0, cy1 = 0, cx2 = 0, cy2 = 0;// control points
            double ix = 0, iy = 0; // initial point of subpath
            Commands:
            while (tt.nextToken() != StreamPosTokenizer.TT_EOF) {
                if (tt.ttype > 0) {
                    command = (char) tt.ttype;
                } else {
                    command = next;
                    tt.pushBack();
                }

                switch (command) {
                case 'M':
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "x coordinate missing for 'M'");
                    ix = x = tt.nval;
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "y coordinate missing for 'M'");
                    iy = y = tt.nval;
                    builder.moveTo(x, y);
                    next = 'L';
                    break;
                case 'm':
                    // relative-moveto dx dy
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "dx coordinate missing for 'm'");
                    ix = x += tt.nval;
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "dy coordinate missing for 'm'");
                    iy = y += tt.nval;
                    builder.moveTo(x, y);
                    next = 'l';

                    break;
                case 'Z':
                case 'z':
                    // close path
                    builder.closePath();
                    x = ix;
                    y = iy;
                    break;
                case 'L':
                    // absolute-lineto x y
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "x coordinate missing for 'L'");
                    x = tt.nval;
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "y coordinate missing for 'L'");
                    y = tt.nval;
                    builder.lineTo(x, y);
                    next = 'L';

                    break;
                case 'l':
                    // relative-lineto dx dy
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "dx coordinate missing for 'l'");
                    x += tt.nval;
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "dy coordinate missing for 'l'");
                    y += tt.nval;
                    builder.lineTo(x, y);
                    next = 'l';

                    break;
                case 'H':
                    // absolute-horizontal-lineto x
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "x coordinate missing for 'H'");
                    x = tt.nval;
                    builder.lineTo(x, y);
                    next = 'H';

                    break;
                case 'h':
                    // relative-horizontal-lineto dx
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "dx coordinate missing for 'h'");
                    x += tt.nval;
                    builder.lineTo(x, y);
                    next = 'h';

                    break;
                case 'V':
                    // absolute-vertical-lineto y
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "y coordinate missing for 'V'");
                    y = tt.nval;
                    builder.lineTo(x, y);
                    next = 'V';

                    break;
                case 'v':
                    // relative-vertical-lineto dy
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "dy coordinate missing for 'v'");
                    y += tt.nval;
                    builder.lineTo(x, y);
                    next = 'v';

                    break;
                case 'C':
                    // absolute-curveto x1 y1 x2 y2 x y
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "x1 coordinate missing for 'C'");
                    cx1 = tt.nval;
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "y1 coordinate missing for 'C'");
                    cy1 = tt.nval;
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "x2 coordinate missing for 'C'");
                    cx2 = tt.nval;
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "y2 coordinate missing for 'C'");
                    cy2 = tt.nval;
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "x coordinate missing for 'C'");
                    x = tt.nval;
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "y coordinate missing for 'C'");
                    y = tt.nval;
                    builder.curveTo(cx1, cy1, cx2, cy2, x, y);
                    next = 'C';
                    break;

                case 'c':
                    // relative-curveto dx1 dy1 dx2 dy2 dx dy
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "dx1 coordinate missing for 'c'");
                    cx1 = x + tt.nval;
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "dy1 coordinate missing for 'c'");
                    cy1 = y + tt.nval;
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "dx2 coordinate missing for 'c'");
                    cx2 = x + tt.nval;
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "dy2 coordinate missing for 'c'");
                    cy2 = y + tt.nval;
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "dx coordinate missing for 'c'");
                    x += tt.nval;
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "dy coordinate missing for 'c'");
                    y += tt.nval;
                    builder.curveTo(cx1, cy1, cx2, cy2, x, y);
                    next = 'c';
                    break;

                case 'S':
                    // absolute-shorthand-curveto x2 y2 x y
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "x2 coordinate missing for 'S'");
                    cx2 = tt.nval;
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "y2 coordinate missing for 'S'");
                    cy2 = tt.nval;
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "x coordinate missing for 'S'");
                    x = tt.nval;
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "y coordinate missing for 'S'");
                    y = tt.nval;
                    builder.smoothCurveTo(cx2, cy2, x, y);
                    next = 'S';
                    break;

                case 's':
                    // relative-shorthand-curveto dx2 dy2 dx dy
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "dx2 coordinate missing for 's'");
                    cx2 = x + tt.nval;
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "dy2 coordinate missing for 's'");
                    cy2 = y + tt.nval;
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "dx coordinate missing for 's'");
                    x += tt.nval;
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "dy coordinate missing for 's'");
                    y += tt.nval;
                    builder.smoothCurveTo(cx2, cy2, x, y);
                    next = 's';
                    break;

                case 'Q':
                    // absolute-quadto x1 y1 x y
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "x1 coordinate missing for 'Q'");
                    cx1 = tt.nval;
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "y1 coordinate missing for 'Q'");
                    cy1 = tt.nval;
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "x coordinate missing for 'Q'");
                    x = tt.nval;
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "y coordinate missing for 'Q'");
                    y = tt.nval;
                    builder.quadTo(cx1, cy1, x, y);
                    next = 'Q';

                    break;

                case 'q':
                    // relative-quadto dx1 dy1 dx dy
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "dx1 coordinate missing for 'q'");
                    cx1 = x + tt.nval;
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "dy1 coordinate missing for 'q'");
                    cy1 = y + tt.nval;
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "dx coordinate missing for 'q'");
                    x += tt.nval;
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "dy coordinate missing for 'q'");
                    y += tt.nval;
                    builder.quadTo(cx1, cy1, x, y);
                    next = 'q';

                    break;
                case 'T':
                    // absolute-shorthand-quadto x y
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "x coordinate missing for 'T'");
                    x = tt.nval;
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "y coordinate missing for 'T'");
                    y = tt.nval;
                    builder.smoothQuadTo(x, y);
                    next = 'T';

                    break;

                case 't':
                    // relative-shorthand-quadto dx dy
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "dx coordinate missing for 't'");
                    x += tt.nval;
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "dy coordinate missing for 't'");
                    y += tt.nval;
                    builder.smoothQuadTo(x, y);
                    next = 's';

                    break;

                case 'A': {
                    // absolute-elliptical-arc rx ry x-axis-rotation large-arc-flag sweep-flag x y
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "rx coordinate missing for 'A'");
                    // If rX or rY have negative signs, these are dropped;
                    // the absolute value is used instead.
                    double rx = tt.nval;
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "ry coordinate missing for 'A'");
                    double ry = tt.nval;
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "x-axis-rotation missing for 'A'");
                    double xAxisRotation = tt.nval;
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "large-arc-flag missing for 'A'");
                    boolean largeArcFlag = tt.nval != 0;
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "sweep-flag missing for 'A'");
                    boolean sweepFlag = tt.nval != 0;
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "x coordinate missing for 'A'");
                    x = tt.nval;
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "y coordinate missing for 'A'");
                    y = tt.nval;

                    builder.arcTo(rx, ry, xAxisRotation, x, y, largeArcFlag, sweepFlag);
                    next = 'A';
                    break;
                }
                case 'a': {
                    // relative-elliptical-arc rx ry x-axis-rotation large-arc-flag sweep-flag x y
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "rx coordinate missing for 'A'");
                    // If rX or rY have negative signs, these are dropped;
                    // the absolute value is used instead.
                    double rx = tt.nval;
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "ry coordinate missing for 'A'");
                    double ry = tt.nval;
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "x-axis-rotation missing for 'A'");
                    double xAxisRotation = tt.nval;
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "large-arc-flag missing for 'A'");
                    boolean largeArcFlag = tt.nval != 0;
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "sweep-flag missing for 'A'");
                    boolean sweepFlag = tt.nval != 0;
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "x coordinate missing for 'A'");
                    x = x + tt.nval;
                    tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "y coordinate missing for 'A'");
                    y = y + tt.nval;
                    builder.arcTo(rx, ry, xAxisRotation, x, y, largeArcFlag, sweepFlag);

                    next = 'a';
                    break;
                }
                default:
                    throw new ParseException("Illegal command: " + command + ".", tt.getStartPosition());
                }
            }
        } catch (ParseException e) {
            // We must build the path up to the illegal path element!
            // https://www.w3.org/TR/SVG/paths.html#PathDataErrorHandling
        } catch (IllegalPathStateException | IOException e) {
            throw new ParseException(e.getMessage(), tt.getStartPosition());
        }

        builder.pathDone();
        return builder;
    }

    /**
     * Converts a Java AWT Shape iterator to a JavaFX Shape.
     *
     * @param shape AWT Shape
     * @return SVG Path
     */
    public static @NonNull String doubleSvgStringFromAWT(@NonNull Shape shape) {
        return Shapes.doubleSvgStringFromAWT(shape.getPathIterator(null));
    }

    /**
     * Converts a Java AWT Shape iterator to a JavaFX Shape.
     *
     * @param shape AWT Shape
     * @param at    Optional transformation which is applied to the shape
     * @return SVG Path
     */
    public static @NonNull String doubleSvgStringFromAWT(@NonNull Shape shape, AffineTransform at) {
        return Shapes.doubleSvgStringFromAWT(shape.getPathIterator(at));
    }

    /**
     * Converts a Java Path iterator to a SVG path with double precision.
     *
     * @param iter AWT Path Iterator
     * @return SVG Path
     */
    public static @NonNull String doubleSvgStringFromAWT(@NonNull PathIterator iter) {
        XmlNumberConverter nb = new XmlNumberConverter();
        StringBuilder buf = new StringBuilder();
        double[] coords = new double[6];
        char next = 'Z'; // next instruction
        for (; !iter.isDone(); iter.next()) {
            if (buf.length() != 0) {
                buf.append(' ');
            }
            switch (iter.currentSegment(coords)) {
            case PathIterator.SEG_MOVETO:
                    buf.append('M');
                    next = 'L'; // move implies line
                    buf.append(nb.toString(coords[0]))
                            .append(',')
                            .append(nb.toString(coords[1]));
                    break;
                case PathIterator.SEG_LINETO:
                    if (next != 'L') {
                        buf.append(next = 'L');
                    }
                    buf.append(nb.toString(coords[0]))
                            .append(',')
                            .append(nb.toString(coords[1]));
                    break;
                case PathIterator.SEG_QUADTO:
                    if (next != 'Q') {
                        buf.append(next = 'Q');
                    }
                    buf.append(nb.toString(coords[0]))
                            .append(',')
                            .append(nb.toString(coords[1]))
                            .append(',')
                            .append(nb.toString(coords[2]))
                            .append(',')
                            .append(nb.toString(coords[3]));
                    break;
                case PathIterator.SEG_CUBICTO:
                    if (next != 'C') {
                        buf.append(next = 'C');
                    }
                    buf.append(nb.toString(coords[0]))
                            .append(',')
                            .append(nb.toString(coords[1]))
                            .append(',')
                            .append(nb.toString(coords[2]))
                            .append(',')
                            .append(nb.toString(coords[3]))
                            .append(',')
                            .append(nb.toString(coords[4]))
                            .append(',')
                            .append(nb.toString(coords[5]));
                    break;
                case PathIterator.SEG_CLOSE:
                    if (next != 'Z') {
                        buf.append(next = 'Z');
                    }
                    break;
            }
        }
        return buf.toString();
    }

    /**
     * Converts a Java Path iterator to a SVG path with double precision.
     *
     * @param iter AWT Path Iterator
     * @return SVG Path
     */
    public static @NonNull String doubleRelativeSvgStringFromAWT(@NonNull PathIterator iter) {
        XmlNumberConverter nb = new XmlNumberConverter();
        StringBuilder buf = new StringBuilder();
        double[] coords = new double[6];
        double x = 0, y = 0;// current point
        double ix = 0, iy = 0;// initial point of a subpath
        char next = 'z'; // next instruction
        for (; !iter.isDone(); iter.next()) {
            double px = x, py = y;// previous point
            if (buf.length() != 0) {
                buf.append(' ');
            }
            switch (iter.currentSegment(coords)) {
                case PathIterator.SEG_MOVETO:
                    buf.append('m');
                    next = 'l'; // move implies line
                    buf.append(nb.toString((ix = x = coords[0]) - px))
                            .append(',')
                            .append(nb.toString((iy = y = coords[1]) - py));
                    break;
                case PathIterator.SEG_LINETO:
                    if (next != 'l') {
                        buf.append(next = 'l');
                    }
                    buf.append(nb.toString((x = coords[0]) - px))
                            .append(',')
                            .append(nb.toString((y = coords[1]) - py));
                    break;
                case PathIterator.SEG_QUADTO:
                    if (next != 'q') {
                        buf.append(next = 'q');
                    }
                    buf.append(nb.toString(coords[0] - px))
                            .append(',')
                            .append(nb.toString(coords[1] - py))
                            .append(',')
                            .append(nb.toString((x = coords[2]) - px))
                            .append(',')
                            .append(nb.toString((y = coords[3]) - py));
                    break;
                case PathIterator.SEG_CUBICTO:
                    if (next != 'c') {
                        buf.append(next = 'c');
                    }
                    buf.append(nb.toString(coords[0] - px))
                            .append(',')
                            .append(nb.toString(coords[1] - py))
                            .append(',')
                            .append(nb.toString(coords[2] - px))
                            .append(',')
                            .append(nb.toString(coords[3] - py))
                            .append(',')
                            .append(nb.toString((x = coords[4]) - px))
                            .append(',')
                            .append(nb.toString((y = coords[5]) - py));
                    break;
                case PathIterator.SEG_CLOSE:
                    if (next != 'z') {
                        buf.append(next = 'z');
                    }
                    x = ix;
                    y = iy;
                    break;
            }
        }
        return buf.toString();
    }

    /**
     * Converts a Java Path iterator to a SVG path with double precision.
     *
     * @param iter AWT Path Iterator
     * @return SVG Path
     */
    public static @NonNull String floatRelativeSvgStringFromAWT(@NonNull PathIterator iter) {
        XmlNumberConverter nb = new XmlNumberConverter();
        StringBuilder buf = new StringBuilder();
        float[] coords = new float[6];
        float x = 0, y = 0;// current point
        float ix = 0, iy = 0;// initial point of a subpath
        char next = 'z'; // next instruction
        for (; !iter.isDone(); iter.next()) {
            float px = x, py = y;// previous point
            if (buf.length() != 0) {
                buf.append(' ');
            }
            switch (iter.currentSegment(coords)) {
                case PathIterator.SEG_MOVETO:
                    buf.append('m');
                    next = 'l'; // move implies line
                    buf.append(nb.toString((ix = x = coords[0]) - px))
                            .append(',')
                            .append(nb.toString((iy = y = coords[1]) - py));
                    break;
                case PathIterator.SEG_LINETO:
                    if (next != 'l') {
                        buf.append(next = 'l');
                    }
                    buf.append(nb.toString((x = coords[0]) - px))
                            .append(',')
                            .append(nb.toString((y = coords[1]) - py));
                    break;
                case PathIterator.SEG_QUADTO:
                    if (next != 'q') {
                        buf.append(next = 'q');
                    }
                    buf.append(nb.toString(coords[0] - px))
                            .append(',')
                            .append(nb.toString(coords[1] - py))
                            .append(',')
                            .append(nb.toString((x = coords[2]) - px))
                            .append(',')
                            .append(nb.toString((y = coords[3]) - py));
                    break;
                case PathIterator.SEG_CUBICTO:
                    if (next != 'c') {
                        buf.append(next = 'c');
                    }
                    buf.append(nb.toString(coords[0] - px))
                            .append(',')
                            .append(nb.toString(coords[1] - py))
                            .append(',')
                            .append(nb.toString(coords[2] - px))
                            .append(',')
                            .append(nb.toString(coords[3] - py))
                            .append(',')
                            .append(nb.toString((x = coords[4]) - px))
                            .append(',')
                            .append(nb.toString((y = coords[5]) - py));
                    break;
                case PathIterator.SEG_CLOSE:
                    if (next != 'z') {
                        buf.append(next = 'z');
                    }
                    x = ix;
                    y = iy;
                    break;
            }
        }
        return buf.toString();
    }

    public static @NonNull String doubleSvgStringFromElements(@NonNull List<PathElement> elements) {
        XmlNumberConverter nb = new XmlNumberConverter();
        return svgStringFromElements(elements, nb);
    }

    public static @NonNull String floatSvgStringFromElements(@NonNull List<PathElement> elements) {
        NumberConverter nb = new NumberConverter(Float.class);
        return svgStringFromElements(elements, nb);
    }


    public static @NonNull String svgStringFromElements(@NonNull List<PathElement> elements, NumberConverter nb) {
        StringBuilder buf = new StringBuilder();
        char next = 'Z'; // next instruction
        double x = 0, y = 0;// current point
        double ix = 0, iy = 0;// initial point of a subpath
        for (PathElement pe : elements) {
            if (buf.length() != 0) {
                buf.append(' ');
            }
            if (pe instanceof MoveTo) {
                MoveTo e = (MoveTo) pe;
                if (e.isAbsolute()) {
                    buf.append('M');
                    next = 'L'; // move implies line
                    buf.append(nb.toString(ix = x = e.getX()))
                            .append(',')
                            .append(nb.toString(iy = y = e.getY()));
                } else {
                    buf.append('m');
                    next = 'l'; // move implies line
                    buf.append(nb.toString(e.getX()))
                            .append(',')
                            .append(nb.toString(e.getY()));
                    ix = x += e.getX();
                    iy = y += e.getY();
                }
            } else if (pe instanceof LineTo) {
                LineTo e = (LineTo) pe;
                if (e.isAbsolute()) {
                    if (next != 'L') {
                        buf.append(next = 'L');
                    }
                    buf.append(nb.toString(ix = x = e.getX()))
                            .append(',')
                            .append(nb.toString(iy = y = e.getY()));
                } else {
                    if (next != 'l') {
                        buf.append(next = 'l');
                    }
                    buf.append(nb.toString(e.getX()))
                            .append(',')
                            .append(nb.toString(e.getY()));
                    ix = x += e.getX();
                    iy = y += e.getY();
                }
            } else if (pe instanceof CubicCurveTo) {
                CubicCurveTo e = (CubicCurveTo) pe;
                if (e.isAbsolute()) {
                    if (next != 'C') {
                        buf.append(next = 'C');
                    }
                    buf.append(nb.toString(e.getControlX1()))
                            .append(',')
                            .append(nb.toString(e.getControlY1()))
                            .append(',')
                            .append(nb.toString(e.getControlX2()))
                            .append(',')
                            .append(nb.toString(e.getControlY2()))
                            .append(',')
                            .append(nb.toString((x = e.getX())))
                            .append(',')
                            .append(nb.toString((y = e.getY())));
                } else {
                    if (next != 'c') {
                        buf.append(next = 'c');
                    }
                    buf.append(nb.toString(e.getControlX1()))
                            .append(',')
                            .append(nb.toString(e.getControlY1()))
                            .append(',')
                            .append(nb.toString(e.getControlX2()))
                            .append(',')
                            .append(nb.toString(e.getControlY2()))
                            .append(',')
                            .append(nb.toString(e.getX()))
                            .append(',')
                            .append(nb.toString(e.getY()));
                    x += e.getX();
                    y += e.getY();
                }
            } else if (pe instanceof QuadCurveTo) {
                QuadCurveTo e = (QuadCurveTo) pe;
                if (e.isAbsolute()) {
                    if (next != 'Q') {
                        buf.append(next = 'Q');
                    }
                    buf.append(nb.toString(e.getControlX()))
                            .append(',')
                            .append(nb.toString(e.getControlY()))
                            .append(',')
                            .append(nb.toString((x = e.getX())))
                            .append(',')
                            .append(nb.toString((y = e.getY())));
                } else {
                    if (next != 'q') {
                        buf.append(next = 'q');
                    }
                    buf.append(nb.toString(e.getControlX()))
                            .append(',')
                            .append(nb.toString(e.getControlY()))
                            .append(',')
                            .append(nb.toString(e.getX()))
                            .append(',')
                            .append(nb.toString(e.getY()));
                    x += e.getX();
                    y += e.getY();
                }
            } else if (pe instanceof ArcTo) {
                ArcTo e = (ArcTo) pe;
                if (e.isAbsolute()) {
                    if (next != 'A') {
                        buf.append(next = 'A');
                    }
                    buf.append(nb.toString(e.getRadiusX()))
                            .append(',')
                            .append(nb.toString(e.getRadiusY()))
                            .append(' ')
                            .append(nb.toString(e.getXAxisRotation()))
                            .append(' ')
                            .append(e.isLargeArcFlag() ? '1' : '0')
                            .append(',')
                            .append(e.isSweepFlag() ? '1' : '0')
                            .append(' ')
                            .append(nb.toString(x = e.getX()))
                            .append(',')
                            .append(nb.toString(y = e.getY()));
                } else {
                    if (next != 'a') {
                        buf.append(next = 'a');
                    }
                    buf.append(nb.toString(e.getRadiusX()))
                            .append(',')
                            .append(nb.toString(e.getRadiusY()))
                            .append(' ')
                            .append(nb.toString(e.getXAxisRotation()))
                            .append(' ')
                            .append(e.isLargeArcFlag() ? '1' : '0')
                            .append(',')
                            .append(e.isSweepFlag() ? '1' : '0')
                            .append(' ')
                            .append(nb.toString(e.getX()))
                            .append(',')
                            .append(nb.toString(e.getY()));
                    x += e.getX();
                    y += e.getY();
                }
            } else if (pe instanceof HLineTo) {
                HLineTo e = (HLineTo) pe;
                if (e.isAbsolute()) {
                    if (next != 'H') {
                        buf.append(next = 'H');
                    }
                    buf.append(nb.toString(x = e.getX()));
                } else {
                    if (next != 'h') {
                        buf.append(next = 'h');
                    }
                    buf.append(nb.toString(e.getX()));
                    x += e.getX();
                }
            } else if (pe instanceof VLineTo) {
                VLineTo e = (VLineTo) pe;
                if (e.isAbsolute()) {
                    if (next != 'V') {
                        buf.append(next = 'V');
                    }
                    buf.append(nb.toString(y = e.getY()));
                } else {
                    if (next != 'v') {
                        buf.append(next = 'v');
                    }
                    buf.append(nb.toString(e.getY()));
                    y += e.getY();
                }
            } else if (pe instanceof ClosePath) {
                ClosePath e = (ClosePath) pe;
                if (e.isAbsolute()) {
                    if (next != 'Z') {
                        buf.append(next = 'Z');
                    }
                } else {
                    if (next != 'z') {
                        buf.append(next = 'z');
                    }
                }
                x = ix;
                y = iy;
            }
        }
        return buf.toString();
    }

    /**
     * Converts a Java Path iterator to a SVG path with float precision.
     *
     * @param iter AWT Path Iterator
     * @return SVG Path
     */
    public static @NonNull String floatSvgStringFromAWT(@NonNull PathIterator iter) {
        NumberConverter nb = new NumberConverter(Float.class);
        StringBuilder buf = new StringBuilder();
        float[] coords = new float[6];
        char next = 'Z'; // next instruction
        for (; !iter.isDone(); iter.next()) {
            if (buf.length() != 0) {
                buf.append(' ');
            }
            switch (iter.currentSegment(coords)) {
                case PathIterator.SEG_MOVETO:
                    buf.append('M');
                    next = 'L'; // move implies line
                    buf.append(nb.toString(coords[0]))
                            .append(',')
                            .append(nb.toString(coords[1]));
                    break;
                case PathIterator.SEG_LINETO:
                    if (next != 'L') {
                        buf.append(next = 'L');
                    }
                    buf.append(nb.toString(coords[0]))
                            .append(',')
                            .append(nb.toString(coords[1]));
                    break;
                case PathIterator.SEG_QUADTO:
                    if (next != 'Q') {
                        buf.append(next = 'Q');
                    }
                    buf.append(nb.toString(coords[0]))
                            .append(',')
                            .append(nb.toString(coords[1]))
                            .append(',')
                            .append(nb.toString(coords[2]))
                            .append(',')
                            .append(nb.toString(coords[3]));
                    break;
                case PathIterator.SEG_CUBICTO:
                    if (next != 'C') {
                        buf.append(next = 'C');
                    }
                    buf.append(nb.toString(coords[0]))
                            .append(',')
                            .append(nb.toString(coords[1]))
                            .append(',')
                            .append(nb.toString(coords[2]))
                            .append(',')
                            .append(nb.toString(coords[3]))
                            .append(',')
                            .append(nb.toString(coords[4]))
                            .append(',')
                            .append(nb.toString(coords[5]));
                    break;
                case PathIterator.SEG_CLOSE:
                    if (next != 'Z') {
                        buf.append(next = 'Z');
                    }
                    break;
            }
        }
        return buf.toString();
    }

    /**
     * Converts a Java Path iterator to a JavaFX shape.
     *
     * @param iter AWT Path Iterator
     * @return JavaFX Shape
     */
    public static @NonNull List<PathElement> fxPathElementsFromAWT(@NonNull PathIterator iter) {
        List<PathElement> fxelem = new ArrayList<>();
        double[] coords = new double[6];
        for (; !iter.isDone(); iter.next()) {
            switch (iter.currentSegment(coords)) {
            case PathIterator.SEG_CLOSE:
                fxelem.add(new ClosePath());
                break;
            case PathIterator.SEG_CUBICTO:
                fxelem.add(new CubicCurveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]));
                break;
                case PathIterator.SEG_LINETO:
                    fxelem.add(new LineTo(coords[0], coords[1]));
                    break;
                case PathIterator.SEG_MOVETO:
                    fxelem.add(new MoveTo(coords[0], coords[1]));
                    break;
            case PathIterator.SEG_QUADTO:
                fxelem.add(new QuadCurveTo(coords[0], coords[1], coords[2], coords[3]));
                break;
            }
        }
        return fxelem;
    }

    /**
     * This parser preserves more of the semantics than {@link #buildFromSvgString(PathBuilder, String)},
     * because {@link PathBuilder} does not understand relative path commands
     * and horizontal and vertical lineto commands.
     */
    public static @NonNull List<PathElement> fxPathElementsFromSvgString(@NonNull String str) throws ParseException {
        List<PathElement> builder = new ArrayList<>();
        try {

            StreamPosTokenizer tt = new StreamPosTokenizer(new StringReader(str));
            tt.resetSyntax();
            tt.parseNumbers();
            tt.parseExponents();
            tt.parsePlusAsNumber();
            tt.whitespaceChars(0, ' ');
            tt.whitespaceChars(',', ',');

            char next = 'M';
            char command = 'M';
            double x = 0, y = 0; // current point
            double cx1 = 0, cy1 = 0, cx2 = 0, cy2 = 0;// control points
            double ix = 0, iy = 0; // initial point of subpath
            Commands:
            while (tt.nextToken() != StreamPosTokenizer.TT_EOF) {
                double px = x, py = y; // previous points
                if (tt.ttype > 0) {
                    command = (char) tt.ttype;
                } else {
                    command = next;
                    tt.pushBack();
                }

                switch (command) {
                    case 'M':
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "x coordinate missing for 'M'");
                        x = tt.nval;
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "y coordinate missing for 'M'");
                        y = tt.nval;
                        builder.add(new MoveTo(x, y));
                        next = 'L';
                        ix = cx2 = cx1 = x;
                        iy = cy2 = cy1 = y;
                        break;
                    case 'm':
                        // relative-moveto dx dy
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "dx coordinate missing for 'm'");
                        x = tt.nval;
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "dy coordinate missing for 'm'");
                        y = tt.nval;
                        MoveTo moveTo = new MoveTo(x, y);
                        moveTo.setAbsolute(false);
                        builder.add(moveTo);
                        next = 'l';
                        ix = cx2 = cx1 = x += px;
                        iy = cy2 = cy1 = y += px;

                        break;
                    case 'Z':
                        // close path
                        builder.add(new ClosePath());
                        next = 'Z';
                        cx2 = cx1 = x = ix;
                        cy2 = cy1 = y = iy;
                        break;
                    case 'z':
                        // close path
                        ClosePath closePath = new ClosePath();
                        closePath.setAbsolute(false);
                        builder.add(closePath);
                        next = 'z';
                        cx2 = cx1 = x = ix;
                        cy2 = cy1 = y = iy;
                        break;
                    case 'L':
                        // absolute-lineto x y
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "x coordinate missing for 'L'");
                        x = tt.nval;
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "y coordinate missing for 'L'");
                        y = tt.nval;
                        builder.add(new LineTo(x, y));
                        next = 'L';
                        cx2 = cx1 = x;
                        cy2 = cy1 = y;
                        break;
                    case 'l':
                        // relative-lineto dx dy
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "dx coordinate missing for 'l'");
                        x = tt.nval;
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "dy coordinate missing for 'l'");
                        y = tt.nval;
                        LineTo lineTo = new LineTo(x, y);
                        lineTo.setAbsolute(false);
                        builder.add(lineTo);
                        next = 'l';
                        cx2 = cx1 = x += px;
                        cy2 = cy1 = y += px;

                        break;
                    case 'H':
                        // absolute-horizontal-lineto x
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "x coordinate missing for 'H'");
                        x = tt.nval;
                        builder.add(new HLineTo(x));
                        next = 'H';
                        cx2 = cx1 = x;
                        cy2 = cy1 = y;
                        break;
                    case 'h':
                        // relative-horizontal-lineto dx
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "dx coordinate missing for 'h'");
                        x = tt.nval;
                        HLineTo hLineTo = new HLineTo(x);
                        hLineTo.setAbsolute(false);
                        builder.add(hLineTo);
                        next = 'h';
                        cx2 = cy1 = x += px;
                        cy2 = cy1 = y;
                        break;
                    case 'V':
                        // absolute-vertical-lineto y
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "y coordinate missing for 'V'");
                        y = tt.nval;
                        builder.add(new VLineTo(y));
                        next = 'V';
                        cx2 = cx1 = x;
                        cy2 = cy1 = y;
                        break;
                    case 'v':
                        // relative-vertical-lineto dy
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "dy coordinate missing for 'v'");
                        y = tt.nval;
                        VLineTo vLineTo = new VLineTo(y);
                        vLineTo.setAbsolute(false);
                        builder.add(vLineTo);
                        next = 'v';
                        cx2 = cy1 = x;
                        cy2 += cy1 = y += py;
                        break;
                    case 'C':
                        // absolute-curveto x1 y1 x2 y2 x y
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "x1 coordinate missing for 'C'");
                        cx1 = tt.nval;
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "y1 coordinate missing for 'C'");
                        cy1 = tt.nval;
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "x2 coordinate missing for 'C'");
                        cx2 = tt.nval;
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "y2 coordinate missing for 'C'");
                        cy2 = tt.nval;
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "x coordinate missing for 'C'");
                        x = tt.nval;
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "y coordinate missing for 'C'");
                        y = tt.nval;
                        builder.add(new CubicCurveTo(cx1, cy1, cx2, cy2, x, y));
                        next = 'C';
                        break;

                    case 'c':
                        // relative-curveto dx1 dy1 dx2 dy2 dx dy
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "dx1 coordinate missing for 'c'");
                        cx1 = tt.nval;
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "dy1 coordinate missing for 'c'");
                        cy1 = tt.nval;
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "dx2 coordinate missing for 'c'");
                        cx2 = tt.nval;
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "dy2 coordinate missing for 'c'");
                        cy2 = tt.nval;
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "dx coordinate missing for 'c'");
                        x = tt.nval;
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "dy coordinate missing for 'c'");
                        y = tt.nval;
                        CubicCurveTo cubi = new CubicCurveTo(cx1, cy1, cx2, cy2, x, y);
                        cubi.setAbsolute(false);
                        builder.add(cubi);
                        next = 'c';
                        cx1 += px;
                        cy1 += py;
                        cx2 += px;
                        cy2 += py;
                        x += py;
                        y += py;
                        break;

                    case 'S':
                        // absolute-shorthand-curveto x2 y2 x y
                        cx1 = x - cx2 + x;
                        cy1 = x - cy2 + y;
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "x2 coordinate missing for 'S'");
                        cx2 = tt.nval;
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "y2 coordinate missing for 'S'");
                        cy2 = tt.nval;
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "x coordinate missing for 'S'");
                        x = tt.nval;
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "y coordinate missing for 'S'");
                        y = tt.nval;
                        builder.add(new CubicCurveTo(cx1, cy1, cx2, cy2, x, y));
                        next = 'S';
                        break;

                    case 's':
                        // relative-shorthand-curveto dx2 dy2 dx dy
                        cx1 = x - cx2;
                        cy1 = x - cy2;
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "dx2 coordinate missing for 's'");
                        cx2 = tt.nval;
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "dy2 coordinate missing for 's'");
                        cy2 = tt.nval;
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "dx coordinate missing for 's'");
                        x += tt.nval;
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "dy coordinate missing for 's'");
                        y += tt.nval;
                        CubicCurveTo smoothCurveTo = new CubicCurveTo(cx1, cy1, cx2, cy2, x, y);
                        smoothCurveTo.setAbsolute(false);
                        builder.add(smoothCurveTo);
                        next = 's';
                        cx1 += px;
                        cy1 += px;
                        cx2 += px;
                        cy2 += px;
                        x += px;
                        y += py;
                        break;

                    case 'Q':
                        // absolute-quadto x1 y1 x y
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "x1 coordinate missing for 'Q'");
                        cx1 = tt.nval;
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "y1 coordinate missing for 'Q'");
                        cy1 = tt.nval;
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "x coordinate missing for 'Q'");
                        x = tt.nval;
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "y coordinate missing for 'Q'");
                        y = tt.nval;
                        builder.add(new QuadCurveTo(cx1, cy1, x, y));
                        next = 'Q';
                        cx2 = x;
                        cy2 = y;
                        break;

                    case 'q':
                        // relative-quadto dx1 dy1 dx dy
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "dx1 coordinate missing for 'q'");
                        cx1 = x + tt.nval;
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "dy1 coordinate missing for 'q'");
                        cy1 = y + tt.nval;
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "dx coordinate missing for 'q'");
                        x = tt.nval;
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "dy coordinate missing for 'q'");
                        y = tt.nval;
                        QuadCurveTo quadCurveTo = new QuadCurveTo(cx1, cy1, x, y);
                        quadCurveTo.setAbsolute(false);
                        builder.add(quadCurveTo);
                        next = 'q';
                        cx2 = x;
                        cy2 = y;
                        break;
                    case 'T':
                        // absolute-shorthand-quadto x y
                        cx1 = x - cx1 + x;
                        cy1 = x - cy1 + y;
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "x coordinate missing for 'T'");
                        x = tt.nval;
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "y coordinate missing for 'T'");
                        y = tt.nval;
                        builder.add(new QuadCurveTo(cx1, cy1, x, y));
                        next = 'T';
                        cx2 = x;
                        cy2 = y;
                        break;

                    case 't':
                        // relative-shorthand-quadto dx dy
                        cx1 = x - cx1;
                        cy1 = x - cy1;
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "dx coordinate missing for 't'");
                        x = tt.nval;
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "dy coordinate missing for 't'");
                        y = tt.nval;
                        QuadCurveTo smoothQuadCurveTo = new QuadCurveTo(cx1, cy1, x, y);
                        smoothQuadCurveTo.setAbsolute(false);
                        builder.add(smoothQuadCurveTo);
                        next = 's';
                        cx1 += px;
                        cy1 += px;
                        cx2 = x += px;
                        cy2 = y += px;
                        break;

                    case 'A': {
                        // absolute-elliptical-arc rx ry x-axis-rotation large-arc-flag sweep-flag x y
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "rx coordinate missing for 'A'");
                        // If rX or rY have negative signs, these are dropped;
                        // the absolute value is used instead.
                        double rx = tt.nval;
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "ry coordinate missing for 'A'");
                        double ry = tt.nval;
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "x-axis-rotation missing for 'A'");
                        double xAxisRotation = tt.nval;
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "large-arc-flag missing for 'A'");
                        boolean largeArcFlag = tt.nval != 0;
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "sweep-flag missing for 'A'");
                        boolean sweepFlag = tt.nval != 0;
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "x coordinate missing for 'A'");
                        x = tt.nval;
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "y coordinate missing for 'A'");
                        y = tt.nval;

                        builder.add(new ArcTo(rx, ry, xAxisRotation, x, y, largeArcFlag, sweepFlag));
                        next = 'A';
                        cx2 = cx1 = x;
                        cy2 = cy1 = y;
                        break;
                    }
                    case 'a': {
                        // relative-elliptical-arc rx ry x-axis-rotation large-arc-flag sweep-flag x y
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "rx coordinate missing for 'A'");
                        // If rX or rY have negative signs, these are dropped;
                        // the absolute value is used instead.
                        double rx = tt.nval;
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "ry coordinate missing for 'A'");
                        double ry = tt.nval;
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "x-axis-rotation missing for 'A'");
                        double xAxisRotation = tt.nval;
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "large-arc-flag missing for 'A'");
                        boolean largeArcFlag = tt.nval != 0;
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "sweep-flag missing for 'A'");
                        boolean sweepFlag = tt.nval != 0;
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "x coordinate missing for 'A'");
                        x = tt.nval;
                        tt.requireNextToken(StreamPosTokenizer.TT_NUMBER, "y coordinate missing for 'A'");
                        y = tt.nval;
                        ArcTo arcTo = new ArcTo(rx, ry, xAxisRotation, x, y, largeArcFlag, sweepFlag);
                        arcTo.setAbsolute(false);
                        builder.add(arcTo);
                        cx2 = cx1 = x += px;
                        cy2 = cy1 = y += py;
                        next = 'a';
                        break;
                    }
                    default:
                        throw tt.createException("Illegal command: " + command);
                }
            }
        } catch (IOException e) {
            // suppress exception
        }

        return builder;
    }

    /**
     * Converts a Java AWT Shape iterator to a JavaFX Shape.
     *
     * @param shape AWT Shape
     * @param fxT   Optional transformation which is applied to the shape
     * @return JavaFX Shape
     */
    public static @NonNull javafx.scene.shape.Path fxShapeFromAWT(@NonNull Shape shape, javafx.scene.transform.Transform fxT) {
        return fxShapeFromAWT(shape.getPathIterator(awtTransformFromFX(fxT)));
    }

    /**
     * Converts a Java AWT Shape iterator to a JavaFX Shape.
     *
     * @param shape AWT Shape
     * @param at    Optional transformation which is applied to the shape
     * @return JavaFX Shape
     */
    public static @NonNull javafx.scene.shape.Path fxShapeFromAWT(@NonNull Shape shape, AffineTransform at) {
        return fxShapeFromAWT(shape.getPathIterator(at));
    }

    /**
     * Converts a Java AWT Shape iterator to a JavaFX Shape.
     *
     * @param shape AWT Shape
     * @return JavaFX Shape
     */
    public static @NonNull javafx.scene.shape.Path fxShapeFromAWT(@NonNull Shape shape) {
        return fxShapeFromAWT(shape.getPathIterator(null));
    }

    /**
     * Converts a Java Path iterator to a JavaFX shape.
     *
     * @param iter AWT Path Iterator
     * @return JavaFX Shape
     */
    public static @NonNull javafx.scene.shape.Path fxShapeFromAWT(@NonNull PathIterator iter) {
        javafx.scene.shape.Path fxpath = new javafx.scene.shape.Path();

        switch (iter.getWindingRule()) {
        case PathIterator.WIND_EVEN_ODD:
            fxpath.setFillRule(javafx.scene.shape.FillRule.EVEN_ODD);
            break;
        case PathIterator.WIND_NON_ZERO:
            fxpath.setFillRule(javafx.scene.shape.FillRule.NON_ZERO);
            break;
        default:
                throw new IllegalArgumentException("illegal winding rule " + iter.getWindingRule());
        }

        fxpath.getElements().addAll(fxPathElementsFromAWT(iter));

        return fxpath;
    }

    /**
     * Returns true, if the outline of this shape contains the specified point.
     *
     * @param shape     The shape.
     * @param p         The point to be tested.
     * @param tolerance The tolerance for the test.
     * @return true if contained within tolerance
     */
    public static boolean outlineContains(@NonNull Shape shape, @NonNull Point2D.Double p, double tolerance) {
        AWTPathBuilder b = new AWTPathBuilder();

        double[] coords = new double[6];
        double prevX = 0, prevY = 0;
        double moveX = 0, moveY = 0;
        for (PathIterator i = new FlatteningPathIterator(shape.getPathIterator(new AffineTransform(), tolerance), Math.abs(tolerance + 0.1e-4)); !i.isDone(); i.next()) {
            switch (i.currentSegment(coords)) {
            case PathIterator.SEG_CLOSE:
                if (IntersectLinePoint.lineContainsPoint(
                        prevX, prevY, moveX, moveY,
                        p.x, p.y, tolerance)) {
                    return true;
                }
                    break;
                case PathIterator.SEG_CUBICTO:
                    break;
                case PathIterator.SEG_LINETO:
                    if (IntersectLinePoint.lineContainsPoint(
                            prevX, prevY, coords[0], coords[1],
                            p.x, p.y, tolerance)) {
                        return true;
                    }
                    break;
                case PathIterator.SEG_MOVETO:
                    moveX = coords[0];
                    moveY = coords[1];
                    break;
                case PathIterator.SEG_QUADTO:
                    break;
                default:
                    break;
            }
            prevX = coords[0];
            prevY = coords[1];
        }
        return false;
    }

    public static @NonNull PathIterator pathIteratorFromPoints(@NonNull List<javafx.geometry.Point2D> points, boolean closed, int windingRule, @Nullable AffineTransform tx) {
        return new PathIterator() {
            private final int size = points.size();
            int index = 0;
            @NonNull
            float[] srcf = new float[2];
            @NonNull
            double[] srcd = new double[2];

            @Override
            public int currentSegment(float[] coords) {
                if (index < size) {
                    javafx.geometry.Point2D p = points.get(index);
                    if (tx == null) {
                        coords[0] = (float) p.getX();
                        coords[1] = (float) p.getY();
                    } else {
                        srcf[0] = (float) p.getX();
                        srcf[1] = (float) p.getY();
                        tx.transform(srcf, 0, coords, 0, 1);
                    }
                    return index == 0 ? PathIterator.SEG_MOVETO : PathIterator.SEG_LINETO;
                } else if (index == size && closed) {
                    return PathIterator.SEG_CLOSE;
                } else {
                    throw new IndexOutOfBoundsException();
                }
            }

            @Override
            public int currentSegment(double[] coords) {
                if (index < size) {
                    javafx.geometry.Point2D p = points.get(index);
                    if (tx == null) {
                        coords[0] = p.getX();
                        coords[1] = p.getY();
                    } else {
                        srcd[0] = p.getX();
                        srcd[1] = p.getY();
                        tx.transform(srcd, 0, coords, 0, 1);
                    }
                    return index == 0 ? PathIterator.SEG_MOVETO : PathIterator.SEG_LINETO;
                } else if (index == size && closed) {
                    return PathIterator.SEG_CLOSE;
                } else {
                    throw new IndexOutOfBoundsException();
                }
            }

            @Override
            public int getWindingRule() {
                return windingRule;
            }

            @Override
            public boolean isDone() {
                return index >= size + (closed ? 1 : 0);
            }

            @Override
            public void next() {
                if (index < size + (closed ? 1 : 0)) {
                    index++;
                }
            }

        };
    }

    public static @NonNull PathIterator pathIteratorFromPointCoords(@NonNull List<Double> coordsList, boolean closed, int windingRule, @Nullable AffineTransform tx) {
        return new PathIterator() {
            private final int size = coordsList.size();
            int index = 0;
            @NonNull
            float[] srcf = new float[2];
            @NonNull
            double[] srcd = new double[2];

            @Override
            public int currentSegment(float[] coords) {
                if (index < size) {
                    double x = coordsList.get(index);
                    double y = coordsList.get(index + 1);
                    if (tx == null) {
                        coords[0] = (float) x;
                        coords[1] = (float) y;
                    } else {
                        srcf[0] = (float) x;
                        srcf[1] = (float) y;
                        tx.transform(srcf, 0, coords, 0, 1);
                    }
                    return index == 0 ? PathIterator.SEG_MOVETO : PathIterator.SEG_LINETO;
                } else if (index == size && closed) {
                    return PathIterator.SEG_CLOSE;
                } else {
                    throw new IndexOutOfBoundsException();
                }
            }

            @Override
            public int currentSegment(double[] coords) {
                if (index < size) {
                    double x = coordsList.get(index);
                    double y = coordsList.get(index + 1);
                    if (tx == null) {
                        coords[0] = x;
                        coords[1] = y;
                    } else {
                        srcd[0] = x;
                        srcd[1] = y;
                        tx.transform(srcd, 0, coords, 0, 1);
                    }
                    return index == 0 ? PathIterator.SEG_MOVETO : PathIterator.SEG_LINETO;
                } else if (index == size && closed) {
                    return PathIterator.SEG_CLOSE;
                } else {
                    throw new IndexOutOfBoundsException();
                }
            }

            @Override
            public int getWindingRule() {
                return windingRule;
            }

            @Override
            public boolean isDone() {
                return index >= size + (closed ? 2 : 0);
            }

            @Override
            public void next() {
                if (index < size + (closed ? 2 : 0)) {
                    index += 2;
                }
            }

        };
    }

    /**
     * Fits the specified SVGPath into the given bounds.
     * <p>
     * If parsing the SVG Path fails, logs a warning message and fits a rectangle
     * into the bounds.
     *
     * @param pathstr an SVGPath String
     * @param b       the desired bounds
     * @param builder the builder into which the path is output
     */
    public static void reshape(@Nullable String pathstr, @NonNull Bounds b, @NonNull PathBuilder builder) {
        if (pathstr != null) {
            Shape shape = null;
            try {
                shape = Shapes.awtShapeFromSvgString(pathstr);
                java.awt.geom.Rectangle2D r2d = shape.getBounds2D();
                Transform tx = FXTransforms.createReshapeTransform(
                        r2d.getX(), r2d.getY(), r2d.getWidth(), r2d.getHeight(),
                        b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight()
                );
                buildFromPathIterator(builder, shape.getPathIterator(FXTransforms.toAWT(tx)));
                return;
            } catch (ParseException e) {
                LOGGER.warning(e.getMessage() + " Path: \"" + pathstr + "\".");
            }
        }

        // We get here if pathstr is null or if we encountered a parse exception

        builder.moveTo(b.getMinX(), b.getMinY());
        builder.lineTo(b.getMaxX(), b.getMinY());
        builder.lineTo(b.getMaxX(), b.getMaxY());
        builder.lineTo(b.getMinX(), b.getMaxY());
        builder.closePath();

    }

    /**
     * Fits the specified SVGPath into the given bounds.
     *
     * @param pathstr an SVGPath String
     * @param b       the desired bounds
     * @param elems   on output contains the reshaped path elements
     */
    public static void reshapePathElements(String pathstr, @NonNull Bounds b, List<PathElement> elems) {
        FXPathBuilder builder = new FXPathBuilder(elems);
        reshape(pathstr, b, builder);
        builder.pathDone();
    }

    public static @NonNull List<PathElement> transformFXPathElements(@NonNull List<PathElement> elements, FillRule fillRule, javafx.scene.transform.Transform fxT) {
        ArrayList<PathElement> result = new ArrayList<>();
        awtShapeFromFXPathElements(elements, fillRule);
        return result;
    }

    public static @NonNull Shape awtShapeFromFxBounds(@NonNull Bounds node) {
        return new Rectangle2D.Double(
                node.getMinX(),
                node.getMinY(),
                node.getWidth(),
                node.getHeight()
        );
    }

    public static @NonNull PathIterator emptyPathIterator() {
        return new PathIterator() {
            @Override
            public int getWindingRule() {
                return PathIterator.WIND_EVEN_ODD;
            }

            @Override
            public boolean isDone() {
                return true;
            }

            @Override
            public void next() {
                // empty
            }

            @Override
            public int currentSegment(float[] coords) {
                return PathIterator.SEG_CLOSE;
            }

            @Override
            public int currentSegment(double[] coords) {
                return PathIterator.SEG_CLOSE;
            }
        };
    }

    public static PathIterator pathIterator(List<PathElement> pathElements, int windingRule, AffineTransform tx) {
        final PathIteratorPathBuilder b = new PathIteratorPathBuilder(windingRule);
        buildFromFXPathElements(b, pathElements);
        return b.build();
    }
}
