/* @(#)Shapes.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom;

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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Bounds;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.Ellipse;
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
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jhotdraw8.io.StreamPosTokenizer;
import org.jhotdraw8.svg.SvgPath2D;
import org.jhotdraw8.xml.text.XmlNumberConverter;

/**
 * Shapes.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class Shapes {

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
     * Converts a Java Path iterator to a JavaFX shape.
     *
     * @param fx A JavaFX shape
     * @return AWT Shape
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
            return awtShapeFromFXSvgPath((SVGPath) fx);
        } else if (fx instanceof Text) {
            return awtShapeFromFXText((Text) fx);
        } else {
            throw new UnsupportedOperationException("unsupported shape " + fx);
        }
    }

    @Nonnull
    private static Shape awtShapeFromFXArc(Arc node) {
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

    private static Shape awtShapeFromFXCircle(Circle node) {
        double x = node.getCenterX();
        double y = node.getCenterY();
        double r = node.getRadius();
        return new Ellipse2D.Double(x - r, y - r, r * 2, r * 2);
    }

    @Nonnull
    private static Shape awtShapeFromFXCubicCurve(CubicCurve e) {
        Path2D.Double p = new Path2D.Double();
        p.moveTo(e.getStartX(), e.getStartY());
        p.curveTo(e.getControlX1(), e.getControlY1(), e.getControlX2(), e.getControlY2(), e.getEndX(), e.getEndY());
        return p;
    }

    private static Shape awtShapeFromFXEllipse(Ellipse node) {
        double x = node.getCenterX();
        double y = node.getCenterY();
        double rx = node.getRadiusX();
        double ry = node.getRadiusY();
        return new Ellipse2D.Double(x - rx, y - ry, rx * 2, ry * 2);
    }

    @Nonnull
    private static Shape awtShapeFromFXLine(Line node) {
        Line2D.Double p = new Line2D.Double(node.getStartX(), node.getStartY(), node.getEndX(), node.getEndY());
        return p;
    }

    @Nonnull
    private static Shape awtShapeFromFXPath(Path node) {
        return awtShapeFromFXPathElements(node.getElements());
    }

    @Nonnull
    public static Shape awtShapeFromFXPathElements(List<PathElement> pathElements) {
        SvgPath2D p = new SvgPath2D();
        double x = 0;
        double y = 0;
        for (PathElement pe : pathElements) {
            if (pe instanceof MoveTo) {
                MoveTo e = (MoveTo) pe;
                x = e.getX();
                y = e.getY();
                p.moveTo(x, y);
            } else if (pe instanceof LineTo) {
                LineTo e = (LineTo) pe;
                x = e.getX();
                y = e.getY();
                p.lineTo(x, y);
            } else if (pe instanceof CubicCurveTo) {
                CubicCurveTo e = (CubicCurveTo) pe;
                x = e.getX();
                y = e.getY();
                p.curveTo(e.getControlX1(), e.getControlY1(), e.getControlX2(), e.getControlY2(), x, y);
            } else if (pe instanceof QuadCurveTo) {
                QuadCurveTo e = (QuadCurveTo) pe;
                x = e.getX();
                y = e.getY();
                p.quadTo(e.getControlX(), e.getControlY(), x, y);
            } else if (pe instanceof ArcTo) {
                ArcTo e = (ArcTo) pe;
                x = e.getX();
                y = e.getY();
                p.arcTo(e.getRadiusX(), e.getRadiusY(), e.getXAxisRotation(), e.isLargeArcFlag(), e.isSweepFlag(), x, y);
            } else if (pe instanceof HLineTo) {
                HLineTo e = (HLineTo) pe;
                x = e.getX();
                p.lineTo(x, y);
            } else if (pe instanceof VLineTo) {
                VLineTo e = (VLineTo) pe;
                y = e.getY();
                p.lineTo(x, y);
            } else if (pe instanceof ClosePath) {
                p.closePath();
            }
        }
        return p;
    }

    @Nonnull
    private static Shape awtShapeFromFXPolygon(Polygon node) {
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

    @Nonnull
    private static Shape awtShapeFromFXPolyline(Polyline node) {
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

    @Nonnull
    private static Shape awtShapeFromFXQuadCurve(QuadCurve node) {
        Path2D.Double p = new Path2D.Double();
        p.moveTo(node.getStartX(), node.getStartY());
        p.quadTo(node.getControlX(), node.getControlY(), node.getEndX(), node.getEndY());
        return p;
    }

    public static Shape awtShapeFromFXRectangle(Rectangle node) {
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

    private static Shape awtShapeFromFXSvgPath(SVGPath node) {
        AWTPathBuilder b = new AWTPathBuilder();
        try {
            buildFromSvgString(b, node.getContent());
        } catch (IOException ex) {
            // suppress error
        }
        return b.build();
    }

    @Nonnull
    private static Shape awtShapeFromFXText(@Nonnull Text node) {
        Path path = (Path) javafx.scene.shape.Shape.subtract(node, new Rectangle());
        return awtShapeFromFXPath(path);
    }

    /**
     * Returns a value as a SvgPath2D.
     *
     * Also supports elliptical arc commands 'a' and 'A' as specified in
     * http://www.w3.org/TR/SVG/paths.html#PathDataEllipticalArcCommands
     *
     * @param str the SVG path
     * @return the SvgPath2D
     * @throws java.io.IOException if the String is not a valid path
     */
    public static Path2D.Double awtShapeFromSvgString(@Nonnull String str) throws IOException {
        AWTPathBuilder b = new AWTPathBuilder();
        buildFromSvgString(b, str);
        return (Path2D.Double) b.build();
    }

    /**
     * Converts a Java Path iterator to a JavaFX shape.
     *
     * @param fxT A JavaFX Transform.
     * @return An AWT Transform.
     */
    @Nullable
    public static AffineTransform awtTransformFromFX(javafx.scene.transform.Transform fxT) {
        if (fxT == null) {
            return null;
        }

        double[] m = fxT.toArray(MatrixType.MT_2D_2x3);
        return fxT == null ? null : new AffineTransform(m[0], m[3], m[1], m[4], m[2], m[5]);
    }

    @Nonnull
    public static <T extends PathBuilder> T buildFromFXPathElements(@Nonnull T builder, List<PathElement> pathElements) {
        double x = 0;
        double y = 0;
        for (PathElement pe : pathElements) {
            if (pe instanceof MoveTo) {
                MoveTo e = (MoveTo) pe;
                x = e.getX();
                y = e.getY();
                builder.moveTo(x, y);
            } else if (pe instanceof LineTo) {
                LineTo e = (LineTo) pe;
                x = e.getX();
                y = e.getY();
                builder.lineTo(x, y);
            } else if (pe instanceof CubicCurveTo) {
                CubicCurveTo e = (CubicCurveTo) pe;
                x = e.getX();
                y = e.getY();
                builder.curveTo(e.getControlX1(), e.getControlY1(), e.getControlX2(), e.getControlY2(), x, y);
            } else if (pe instanceof QuadCurveTo) {
                QuadCurveTo e = (QuadCurveTo) pe;
                x = e.getX();
                y = e.getY();
                builder.quadTo(e.getControlX(), e.getControlY(), x, y);
            } else if (pe instanceof ArcTo) {
                ArcTo e = (ArcTo) pe;
                x = e.getX();
                y = e.getY();
                builder.arcTo(e.getRadiusX(), e.getRadiusY(), e.getXAxisRotation(), x, y, e.isLargeArcFlag(), e.isSweepFlag());
            } else if (pe instanceof HLineTo) {
                HLineTo e = (HLineTo) pe;
                x = e.getX();
                builder.lineTo(x, y);
            } else if (pe instanceof VLineTo) {
                VLineTo e = (VLineTo) pe;
                y = e.getY();
                builder.lineTo(x, y);
            } else if (pe instanceof ClosePath) {
                builder.closePath();
            }
        }
        builder.pathDone();
        return builder;
    }

    @Nonnull
    public static <T extends PathBuilder> T buildFromPathIterator(@Nonnull T builder, PathIterator iter) {
        double[] coords = new double[6];
        for (; !iter.isDone(); iter.next()) {
            switch (iter.currentSegment(coords)) {
                case PathIterator.SEG_CLOSE:
                    builder.closePath();
                    break;

                case PathIterator.SEG_CUBICTO:
                    builder.curveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
                    break;
                case PathIterator.SEG_LINETO:
                    builder.lineTo(coords[0], coords[1]);
                    break;
                case PathIterator.SEG_QUADTO:
                    builder.quadTo(coords[0], coords[1], coords[2], coords[3]);
                    break;
                case PathIterator.SEG_MOVETO:
                    builder.moveTo(coords[0], coords[1]);
                    break;
                default:
                    throw new InternalError("unsupported segment type:" + iter.currentSegment(coords));
            }
        }
        builder.pathDone();
        return builder;
    }

    /**
     * Returns a value as a SvgPath2D.
     *
     * Also supports elliptical arc commands 'a' and 'A' as specified in
     * http://www.w3.org/TR/SVG/paths.html#PathDataEllipticalArcCommands
     *
     * @param builder the builder
     * @param str the SVG path
     * @return the path builder
     * @throws java.io.IOException if the String is not a valid path
     */
    @Nonnull
    public static PathBuilder buildFromSvgString(@Nonnull PathBuilder builder, @Nonnull String str) throws IOException {
        try {
            Point2D.Double p = new Point2D.Double();
            Point2D.Double c1 = new Point2D.Double();
            Point2D.Double c2 = new Point2D.Double();

            StreamPosTokenizer tt = new StreamPosTokenizer(new StringReader(str));
            tt.resetSyntax();
            tt.parseNumbers();
            tt.parseExponents();
            tt.parsePlusAsNumber();
            tt.whitespaceChars(0, ' ');
            tt.whitespaceChars(',', ',');

            char nextCommand = 'M';
            char command = 'M';
            Commands:
            while (tt.nextToken() != StreamPosTokenizer.TT_EOF) {
                if (tt.ttype > 0) {
                    command = (char) tt.ttype;
                } else {
                    command = nextCommand;
                    tt.pushBack();
                }

                switch (command) {
                    case 'M':

                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("x coordinate missing for 'M' at position " + tt.getStartPosition() + " in " + str);
                        }
                        p.x = tt.nval;
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("y coordinate missing for 'M' at position " + tt.getStartPosition() + " in " + str);
                        }
                        p.y = tt.nval;
                        builder.moveTo(p.x, p.y);
                        nextCommand = 'L';
                        break;
                    case 'm':
                        // relative-moveto dx dy
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("dx coordinate missing for 'm' at position " + tt.getStartPosition() + " in " + str);
                        }
                        p.x += tt.nval;
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("dy coordinate missing for 'm' at position " + tt.getStartPosition() + " in " + str);
                        }
                        p.y += tt.nval;
                        builder.moveTo(p.x, p.y);
                        nextCommand = 'l';

                        break;
                    case 'Z':
                    case 'z':
                        // close path
                        builder.closePath();

                        break;
                    case 'L':
                        // absolute-lineto x y
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("x coordinate missing for 'L' at position " + tt.getStartPosition() + " in " + str);
                        }
                        p.x = tt.nval;
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("y coordinate missing for 'L' at position " + tt.getStartPosition() + " in " + str);
                        }
                        p.y = tt.nval;
                        builder.lineTo(p.x, p.y);
                        nextCommand = 'L';

                        break;
                    case 'l':
                        // relative-lineto dx dy
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("dx coordinate missing for 'l' at position " + tt.getStartPosition() + " in " + str);
                        }
                        p.x += tt.nval;
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("dy coordinate missing for 'l' at position " + tt.getStartPosition() + " in " + str);
                        }
                        p.y += tt.nval;
                        builder.lineTo(p.x, p.y);
                        nextCommand = 'l';

                        break;
                    case 'H':
                        // absolute-horizontal-lineto x
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("x coordinate missing for 'H' at position " + tt.getStartPosition() + " in " + str);
                        }
                        p.x = tt.nval;
                        builder.lineTo(p.x, p.y);
                        nextCommand = 'H';

                        break;
                    case 'h':
                        // relative-horizontal-lineto dx
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("dx coordinate missing for 'h' at position " + tt.getStartPosition() + " in " + str);
                        }
                        p.x += tt.nval;
                        builder.lineTo(p.x, p.y);
                        nextCommand = 'h';

                        break;
                    case 'V':
                        // absolute-vertical-lineto y
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("y coordinate missing for 'V' at position " + tt.getStartPosition() + " in " + str);
                        }
                        p.y = tt.nval;
                        builder.lineTo(p.x, p.y);
                        nextCommand = 'V';

                        break;
                    case 'v':
                        // relative-vertical-lineto dy
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("dy coordinate missing for 'v' at position " + tt.getStartPosition() + " in " + str);
                        }
                        p.y += tt.nval;
                        builder.lineTo(p.x, p.y);
                        nextCommand = 'v';

                        break;
                    case 'C':
                        // absolute-curveto x1 y1 x2 y2 x y
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("x1 coordinate missing for 'C' at position " + tt.getStartPosition() + " in " + str);
                        }
                        c1.x = tt.nval;
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("y1 coordinate missing for 'C' at position " + tt.getStartPosition() + " in " + str);
                        }
                        c1.y = tt.nval;
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("x2 coordinate missing for 'C' at position " + tt.getStartPosition() + " in " + str);
                        }
                        c2.x = tt.nval;
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("y2 coordinate missing for 'C' at position " + tt.getStartPosition() + " in " + str);
                        }
                        c2.y = tt.nval;
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("x coordinate missing for 'C' at position " + tt.getStartPosition() + " in " + str);
                        }
                        p.x = tt.nval;
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("y coordinate missing for 'C' at position " + tt.getStartPosition() + " in " + str);
                        }
                        p.y = tt.nval;
                        builder.curveTo(c1.x, c1.y, c2.x, c2.y, p.x, p.y);
                        nextCommand = 'C';
                        break;

                    case 'c':
                        // relative-curveto dx1 dy1 dx2 dy2 dx dy
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("dx1 coordinate missing for 'c' at position " + tt.getStartPosition() + " in " + str);
                        }
                        c1.x = p.x + tt.nval;
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("dy1 coordinate missing for 'c' at position " + tt.getStartPosition() + " in " + str);
                        }
                        c1.y = p.y + tt.nval;
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("dx2 coordinate missing for 'c' at position " + tt.getStartPosition() + " in " + str);
                        }
                        c2.x = p.x + tt.nval;
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("dy2 coordinate missing for 'c' at position " + tt.getStartPosition() + " in " + str);
                        }
                        c2.y = p.y + tt.nval;
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("dx coordinate missing for 'c' at position " + tt.getStartPosition() + " in " + str);
                        }
                        p.x += tt.nval;
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("dy coordinate missing for 'c' at position " + tt.getStartPosition() + " in " + str);
                        }
                        p.y += tt.nval;
                        builder.curveTo(c1.x, c1.y, c2.x, c2.y, p.x, p.y);
                        nextCommand = 'c';
                        break;

                    case 'S':
                        // absolute-shorthand-curveto x2 y2 x y
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("x2 coordinate missing for 'S' at position " + tt.getStartPosition() + " in " + str);
                        }
                        c2.x = tt.nval;
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("y2 coordinate missing for 'S' at position " + tt.getStartPosition() + " in " + str);
                        }
                        c2.y = tt.nval;
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("x coordinate missing for 'S' at position " + tt.getStartPosition() + " in " + str);
                        }
                        p.x = tt.nval;
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("y coordinate missing for 'S' at position " + tt.getStartPosition() + " in " + str);
                        }
                        p.y = tt.nval;
                        builder.smoothCurveTo(c2.x, c2.y, p.x, p.y);
                        nextCommand = 'S';
                        break;

                    case 's':
                        // relative-shorthand-curveto dx2 dy2 dx dy
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("dx2 coordinate missing for 's' at position " + tt.getStartPosition() + " in " + str);
                        }
                        c2.x = p.x + tt.nval;
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("dy2 coordinate missing for 's' at position " + tt.getStartPosition() + " in " + str);
                        }
                        c2.y = p.y + tt.nval;
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("dx coordinate missing for 's' at position " + tt.getStartPosition() + " in " + str);
                        }
                        p.x += tt.nval;
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("dy coordinate missing for 's' at position " + tt.getStartPosition() + " in " + str);
                        }
                        p.y += tt.nval;
                        builder.smoothCurveTo(c2.x, c2.y, p.x, p.y);
                        nextCommand = 's';
                        break;

                    case 'Q':
                        // absolute-quadto x1 y1 x y
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("x1 coordinate missing for 'Q' at position " + tt.getStartPosition() + " in " + str);
                        }
                        c1.x = tt.nval;
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("y1 coordinate missing for 'Q' at position " + tt.getStartPosition() + " in " + str);
                        }
                        c1.y = tt.nval;
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("x coordinate missing for 'Q' at position " + tt.getStartPosition() + " in " + str);
                        }
                        p.x = tt.nval;
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("y coordinate missing for 'Q' at position " + tt.getStartPosition() + " in " + str);
                        }
                        p.y = tt.nval;
                        builder.quadTo(c1.x, c1.y, p.x, p.y);
                        nextCommand = 'Q';

                        break;

                    case 'q':
                        // relative-quadto dx1 dy1 dx dy
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("dx1 coordinate missing for 'q' at position " + tt.getStartPosition() + " in " + str);
                        }
                        c1.x = p.x + tt.nval;
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("dy1 coordinate missing for 'q' at position " + tt.getStartPosition() + " in " + str);
                        }
                        c1.y = p.y + tt.nval;
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("dx coordinate missing for 'q' at position " + tt.getStartPosition() + " in " + str);
                        }
                        p.x += tt.nval;
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("dy coordinate missing for 'q' at position " + tt.getStartPosition() + " in " + str);
                        }
                        p.y += tt.nval;
                        builder.quadTo(c1.x, c1.y, p.x, p.y);
                        nextCommand = 'q';

                        break;
                    case 'T':
                        // absolute-shorthand-quadto x y
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("x coordinate missing for 'T' at position " + tt.getStartPosition() + " in " + str);
                        }
                        p.x = tt.nval;
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("y coordinate missing for 'T' at position " + tt.getStartPosition() + " in " + str);
                        }
                        p.y = tt.nval;
                        builder.smoothQuadTo(p.x, p.y);
                        nextCommand = 'T';

                        break;

                    case 't':
                        // relative-shorthand-quadto dx dy
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("dx coordinate missing for 't' at position " + tt.getStartPosition() + " in " + str);
                        }
                        p.x += tt.nval;
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("dy coordinate missing for 't' at position " + tt.getStartPosition() + " in " + str);
                        }
                        p.y += tt.nval;
                        builder.smoothQuadTo(p.x, p.y);
                        nextCommand = 's';

                        break;

                    case 'A': {
                        // absolute-elliptical-arc rx ry x-axis-rotation large-arc-flag sweep-flag x y
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("rx coordinate missing for 'A' at position " + tt.getStartPosition() + " in " + str);
                        }
                        // If rX or rY have negative signs, these are dropped;
                        // the absolute value is used instead.
                        double rx = tt.nval;
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("ry coordinate missing for 'A' at position " + tt.getStartPosition() + " in " + str);
                        }
                        double ry = tt.nval;
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("x-axis-rotation missing for 'A' at position " + tt.getStartPosition() + " in " + str);
                        }
                        double xAxisRotation = tt.nval;
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("large-arc-flag missing for 'A' at position " + tt.getStartPosition() + " in " + str);
                        }
                        boolean largeArcFlag = tt.nval != 0;
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("sweep-flag missing for 'A' at position " + tt.getStartPosition() + " in " + str);
                        }
                        boolean sweepFlag = tt.nval != 0;
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("x coordinate missing for 'A' at position " + tt.getStartPosition() + " in " + str);
                        }
                        double x = tt.nval;
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("y coordinate missing for 'A' at position " + tt.getStartPosition() + " in " + str);
                        }
                        double y = tt.nval;

                        builder.arcTo(rx, ry, xAxisRotation, x, y, largeArcFlag, sweepFlag);
                        p.x = x;
                        p.y = y;
                        nextCommand = 'A';
                        break;
                    }
                    case 'a': {
                        // relative-elliptical-arc rx ry x-axis-rotation large-arc-flag sweep-flag x y
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("rx coordinate missing for 'A' at position " + tt.getStartPosition() + " in " + str);
                        }
                        // If rX or rY have negative signs, these are dropped;
                        // the absolute value is used instead.
                        double rx = tt.nval;
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("ry coordinate missing for 'A' at position " + tt.getStartPosition() + " in " + str);
                        }
                        double ry = tt.nval;
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("x-axis-rotation missing for 'A' at position " + tt.getStartPosition() + " in " + str);
                        }
                        double xAxisRotation = tt.nval;
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("large-arc-flag missing for 'A' at position " + tt.getStartPosition() + " in " + str);
                        }
                        boolean largeArcFlag = tt.nval != 0;
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("sweep-flag missing for 'A' at position " + tt.getStartPosition() + " in " + str);
                        }
                        boolean sweepFlag = tt.nval != 0;
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("x coordinate missing for 'A' at position " + tt.getStartPosition() + " in " + str);
                        }
                        double x = p.x + tt.nval;
                        if (tt.nextToken() != StreamPosTokenizer.TT_NUMBER) {
                            throw new IOException("y coordinate missing for 'A' at position " + tt.getStartPosition() + " in " + str);
                        }
                        double y = p.y + tt.nval;
                        builder.arcTo(rx, ry, xAxisRotation, x, y, largeArcFlag, sweepFlag);
                        p.x = x;
                        p.y = y;

                        nextCommand = 'a';
                        break;
                    }
                    default:

                        throw new IOException("Illegal command: " + command);
                }
            }
        } catch (IllegalPathStateException e) {
            throw new IOException(e);
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
    public static String doubleSvgStringFromAWT(Shape shape) {
        return Shapes.doubleSvgStringFromAWT(shape.getPathIterator(null));
    }

    /**
     * Converts a Java AWT Shape iterator to a JavaFX Shape.
     *
     * @param shape AWT Shape
     * @param at Optional transformation which is applied to the shape
     * @return SVG Path
     */
    public static String doubleSvgStringFromAWT(Shape shape, AffineTransform at) {
        return Shapes.doubleSvgStringFromAWT(shape.getPathIterator(at));
    }

    /**
     * Converts a Java Path iterator to a SVG path with double precision.
     *
     * @param iter AWT Path Iterator
     * @return SVG Path
     */
    public static String doubleSvgStringFromAWT(PathIterator iter) {
        XmlNumberConverter nb = new XmlNumberConverter();
        StringBuilder buf = new StringBuilder();
        double[] coords = new double[6];
        boolean first = true;
        for (; !iter.isDone(); iter.next()) {
            if (first) {
                first = false;
            } else {
                buf.append(' ');
            }
            switch (iter.currentSegment(coords)) {
                case PathIterator.SEG_CLOSE:
                    buf.append('Z');
                    break;
                case PathIterator.SEG_CUBICTO:
                    buf.append('C');
                    for (int i = 0; i < 6; i++) {
                        if (i % 2 == 1) {
                            buf.append(',');
                        } else {
                            buf.append(' ');
                        }
                        buf.append(nb.toString(coords[i]));
                    }
                    break;
                case PathIterator.SEG_LINETO:
                    buf.append('L');
                    for (int i = 0; i < 2; i++) {
                        if (i % 2 == 1) {
                            buf.append(',');
                        } else {
                            buf.append(' ');
                        }
                        buf.append(nb.toString(coords[i]));
                    }
                    break;
                case PathIterator.SEG_MOVETO:
                    buf.append('M');
                    for (int i = 0; i < 2; i++) {
                        if (i % 2 == 1) {
                            buf.append(',');
                        } else {
                            buf.append(' ');
                        }
                        buf.append(nb.toString(coords[i]));
                    }
                    break;
                case PathIterator.SEG_QUADTO:
                    buf.append('Q');
                    for (int i = 0; i < 4; i++) {
                        if (i % 2 == 1) {
                            buf.append(',');
                        } else {
                            buf.append(' ');
                        }
                        buf.append(nb.toString(coords[i]));
                    }
                    break;
            }
        }
        return buf.toString();
    }

    public static String doubleSvgStringFromElements(@Nonnull List<PathElement> elements) {
        return doubleSvgStringFromAWT(awtShapeFromFXPathElements(elements));
    }

    /**
     * Converts a Java Path iterator to a SVG path with float precision.
     *
     * @param iter AWT Path Iterator
     * @return SVG Path
     */
    public static String floatSvgStringFromAWT(PathIterator iter) {
        XmlNumberConverter nb = new XmlNumberConverter();
        StringBuilder buf = new StringBuilder();
        float[] coords = new float[6];
        boolean first = true;
        for (; !iter.isDone(); iter.next()) {
            if (first) {
                first = false;
            } else {
                buf.append(' ');
            }
            switch (iter.currentSegment(coords)) {
                case PathIterator.SEG_CLOSE:
                    buf.append('Z');
                    break;
                case PathIterator.SEG_CUBICTO:
                    buf.append('C');
                    for (int i = 0; i < 6; i++) {
                        if (i != 0) {
                            buf.append(',');
                        }
                        buf.append(nb.toString(coords[i]));
                    }
                    break;
                case PathIterator.SEG_LINETO:
                    buf.append('L');
                    for (int i = 0; i < 2; i++) {
                        if (i != 0) {
                            buf.append(',');
                        }
                        buf.append(nb.toString(coords[i]));
                    }
                    break;
                case PathIterator.SEG_MOVETO:
                    buf.append('M');
                    for (int i = 0; i < 2; i++) {
                        if (i != 0) {
                            buf.append(',');
                        }
                        buf.append(nb.toString(coords[i]));
                    }
                    break;
                case PathIterator.SEG_QUADTO:
                    buf.append('Q');
                    for (int i = 0; i < 4; i++) {
                        if (i != 0) {
                            buf.append(',');
                        }
                        buf.append(nb.toString(coords[i]));
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
    @Nonnull
    public static List<PathElement> fxPathElementsFromAWT(PathIterator iter) {
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

    public static List<PathElement> fxPathElementsFromFXSVGPath(SVGPath path) {
        return fxPathElementsFromSvgString(path.getContent());
    }

    public static List<PathElement> fxPathElementsFromSvgString(@Nonnull String content) {
        FXPathBuilder b = new FXPathBuilder();
        try {
            buildFromSvgString(b, content);
        } catch (IOException ex) {
            // suppress error, XXX really?
        }
        return b.build().getElements();
    }

    /**
     * Converts a Java AWT Shape iterator to a JavaFX Shape.
     *
     * @param shape AWT Shape
     * @param fxT Optional transformation which is applied to the shape
     * @return JavaFX Shape
     */
    public static javafx.scene.shape.Path fxShapeFromAWT(Shape shape, javafx.scene.transform.Transform fxT) {
        return fxShapeFromAWT(shape.getPathIterator(awtTransformFromFX(fxT)));
    }

    /**
     * Converts a Java AWT Shape iterator to a JavaFX Shape.
     *
     * @param shape AWT Shape
     * @param at Optional transformation which is applied to the shape
     * @return JavaFX Shape
     */
    public static javafx.scene.shape.Path fxShapeFromAWT(Shape shape, AffineTransform at) {
        return fxShapeFromAWT(shape.getPathIterator(at));
    }

    /**
     * Converts a Java AWT Shape iterator to a JavaFX Shape.
     *
     * @param shape AWT Shape
     * @return JavaFX Shape
     */
    public static javafx.scene.shape.Path fxShapeFromAWT(Shape shape) {
        return fxShapeFromAWT(shape.getPathIterator(null));
    }

    /**
     * Converts a Java Path iterator to a JavaFX shape.
     *
     * @param iter AWT Path Iterator
     * @return JavaFX Shape
     */
    public static javafx.scene.shape.Path fxShapeFromAWT(PathIterator iter) {
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
     * @param shape The shape.
     * @param p The point to be tested.
     * @param tolerance The tolerance for the test.
     * @return true if contained within tolerance
     */
    public static boolean outlineContains(Shape shape, Point2D.Double p, double tolerance) {
        double[] coords = new double[6];
        double prevX = 0, prevY = 0;
        double moveX = 0, moveY = 0;
        for (PathIterator i = new FlatteningPathIterator(shape.getPathIterator(new AffineTransform(), tolerance), Math.abs(tolerance + 0.1e-4)); !i.isDone(); i.next()) {
            switch (i.currentSegment(coords)) {
                case PathIterator.SEG_CLOSE:
                    if (Geom.lineContainsPoint(
                            prevX, prevY, moveX, moveY,
                            p.x, p.y, tolerance)) {
                        return true;
                    }
                    break;
                case PathIterator.SEG_CUBICTO:
                    break;
                case PathIterator.SEG_LINETO:
                    if (Geom.lineContainsPoint(
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

    public static PathIterator pathIteratorFromPoints(@Nonnull List<javafx.geometry.Point2D> points, boolean closed, int windingRule, @Nullable AffineTransform tx) {
        return new PathIterator() {
            int index = 0;
            @Nonnull
            float[] srcf = new float[2];
            @Nonnull
            double[] srcd = new double[2];

            @Override
            public int currentSegment(float[] coords) {
                if (index < points.size()) {
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
                } else if (index == points.size() && closed) {
                    return PathIterator.SEG_CLOSE;
                } else {
                    throw new IndexOutOfBoundsException();
                }
            }

            @Override
            public int currentSegment(double[] coords) {
                if (index < points.size()) {
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
                } else if (index == points.size() && closed) {
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
                return index >= points.size() + (closed ? 1 : 0);
            }

            @Override
            public void next() {
                if (index < Integer.MAX_VALUE) {
                    index++;
                }
            }

        };
    }

    /**
     * Fits the specified SVGPath into the given bounds.
     * <p>
     * FIXME If the pathstr is null, builds a rectangle. That's too much magic.
     *
     * @param pathstr an SVGPath String
     * @param b the desired bounds
     * @param builder the builder into which the path is output
     */
    public static void reshape(@Nullable String pathstr, @Nonnull Bounds b, @Nonnull PathBuilder builder) {
        if (pathstr != null) {
            try {
                Shape shape = Shapes.awtShapeFromSvgString(pathstr);
                java.awt.geom.Rectangle2D r2d = shape.getBounds2D();
                Transform tx = Transforms.createReshapeTransform(
                        r2d.getX(), r2d.getY(), r2d.getWidth(), r2d.getHeight(),
                        b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight()
                );
                buildFromPathIterator(builder, shape.getPathIterator(Transforms.toAWT(tx)));
            } catch (IOException ex) {
                pathstr = null;
                Logger.getLogger(Shape.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (pathstr == null) {
            builder.moveTo(b.getMinX(), b.getMinY());
            builder.lineTo(b.getMaxX(), b.getMinY());
            builder.lineTo(b.getMaxX(), b.getMaxY());
            builder.lineTo(b.getMinX(), b.getMaxY());
            builder.closePath();
        }
    }

    /**
     * Fits the specified SVGPath into the given bounds.
     *
     * @param pathstr an SVGPath String
     * @param b the desired bounds
     * @param elems on output contains the reshaped path elements
     */
    public static void reshapePathElements(String pathstr, @Nonnull Bounds b, List<PathElement> elems) {
        FXPathBuilder builder = new FXPathBuilder(elems);
        reshape(pathstr, b, builder);
        builder.pathDone();
    }

    @Nonnull
    public static List<PathElement> transformFXPathElements(@Nonnull List<PathElement> elements, javafx.scene.transform.Transform fxT) {
        ArrayList<PathElement> result = new ArrayList<>();
        awtShapeFromFXPathElements(elements);
        return result;
    }

    public static Shape awtShapeFromFxBounds(Bounds node) {
            return new Rectangle2D.Double(
                    node.getMinX(),
                    node.getMinY(),
                    node.getWidth(),
                    node.getHeight()
            );
    }
}
