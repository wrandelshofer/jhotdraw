/*
 * @(#)FXSvgPaths.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.geom;

import javafx.geometry.Bounds;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.VLineTo;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.io.StreamPosTokenizer;
import org.jhotdraw8.text.NumberConverter;
import org.jhotdraw8.xml.text.XmlNumberConverter;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides methods for parsing and generating SVG path strings from JavaFX paths.
 */
public class FXSvgPaths {
    /**
     * Don't let anyone instantiate this class.
     */
private FXSvgPaths() {

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
     * This parser preserves more of the semantics than {@link SvgPaths#buildFromSvgString(PathBuilder, String)},
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
     * Fits the specified SVGPath into the given bounds.
     *
     * @param pathstr an SVGPath String
     * @param b       the desired bounds
     * @param elems   on output contains the reshaped path elements
     */
    public static void reshapePathElements(String pathstr, @NonNull Bounds b, List<PathElement> elems) {
        FXPathBuilder builder = new FXPathBuilder(elems);
        SvgPaths.reshape(pathstr, b, builder);
        builder.pathDone();
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

    public static @NonNull List<PathElement> transformFXPathElements(@NonNull List<PathElement> elements, FillRule fillRule, javafx.scene.transform.Transform fxT) {
        ArrayList<PathElement> result = new ArrayList<>();
        Shapes.awtShapeFromFXPathElements(elements, fillRule);
        return result;
    }
}
