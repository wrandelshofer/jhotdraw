/*
 * @(#)SvgPaths.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.geom;

import javafx.geometry.Bounds;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.geom.intersect.IntersectLinePoint;
import org.jhotdraw8.io.StreamPosTokenizer;
import org.jhotdraw8.text.NumberConverter;
import org.jhotdraw8.xml.text.XmlNumberConverter;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.IllegalPathStateException;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Provides methods for parsing and generating SVG path strings from AWT paths.
 */
public class SvgPaths {
    private static final Logger LOGGER = Logger.getLogger(SvgPaths.class.getName());
    /**
     * Returns a value as a SvgPath2D.
     * <p>
     * Also supports elliptical arc commands 'a' and 'A' as specified in
     * http://www.w3.org/TR/SVG/paths.html#PathDataEllipticalArcCommands
     *
     * @param builder the builder
     * @param str     the SVG path
     * @return the path builder
     * @throws ParseException if the String is not a valid path
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
    public static @NonNull String doubleSvgStringFromAwt(@NonNull Shape shape) {
        return doubleSvgStringFromAwt(shape.getPathIterator(null));
    }

    /**
     * Converts a Java AWT Shape iterator to a JavaFX Shape.
     *
     * @param shape AWT Shape
     * @param at    Optional transformation which is applied to the shape
     * @return SVG Path
     */
    public static @NonNull String doubleSvgStringFromAwt(@NonNull Shape shape, AffineTransform at) {
        return doubleSvgStringFromAwt(shape.getPathIterator(at));
    }

    /**
     * Converts a Java Path iterator to a SVG path with double precision.
     *
     * @param iter AWT Path Iterator
     * @return SVG Path
     */
    public static @NonNull String doubleSvgStringFromAwt(@NonNull PathIterator iter) {
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
     * Returns a value as a SvgPath2D.
     * <p>
     * Also supports elliptical arc commands 'a' and 'A' as specified in
     * http://www.w3.org/TR/SVG/paths.html#PathDataEllipticalArcCommands
     *
     * @param str the SVG path
     * @return the SvgPath2D
     * @throws ParseException if the String is not a valid path
     */
    public static @NonNull Path2D.Double awtShapeFromSvgString(@NonNull String str) throws ParseException {
        AwtPathBuilder b = new AwtPathBuilder();
        buildFromSvgString(b, str);
        return b.build();
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
                shape = awtShapeFromSvgString(pathstr);
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
     * Returns true, if the outline of this shape contains the specified point.
     *
     * @param shape     The shape.
     * @param p         The point to be tested.
     * @param tolerance The tolerance for the test.
     * @return true if contained within tolerance
     */
    public static boolean outlineContains(@NonNull Shape shape, @NonNull Point2D.Double p, double tolerance) {
        AwtPathBuilder b = new AwtPathBuilder();

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

    public static @NonNull PathIterator pathIteratorFromPointCoords(@NonNull List<Double> coordsList, boolean closed, int windingRule, @Nullable AffineTransform tx) {
        return new PathIterator() {
            private final int size = coordsList.size();
            int index = 0;
            final float[] srcf = tx == null ? null : new float[2];
            final double[] srcd = tx == null ? null : new double[2];

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
}
