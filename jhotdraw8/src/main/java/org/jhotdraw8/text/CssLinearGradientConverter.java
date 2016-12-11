/* @(#)CssLinearGradientConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.io.StringReader;
import java.nio.CharBuffer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.paint.CycleMethod;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.CssTokenizerInterface;
import org.jhotdraw8.draw.io.IdFactory;

/**
 * CssLinearGradientConverter.
 * <p>
 * Parses the following EBNF from the
 * <a href="https://docs.oracle.com/javafx/2/api/javafx/scene/doc-files/cssref.html">JavaFX
 * CSS Reference Guide</a>.
 * </p>
 * <pre>
 * Paint := (Color|LinearGradient|RadialGradient|ImagePattern RepeatingImagePattern) ;
 * LinearGradient := "linear-gradient(", LinearGradientParameters, ")"
 * LinearGradientParameters := [ ("from", PointToPoint |  "to", SideOrCorner], "," ],
 *                   [ ( "repeat" | "reflect" ),"," ] ColorStop,{"," ColorStop}) ;
 * PointToPoint = (Point, "to", Point)
 * SideOrCorner = "to" ["left" | "right"] , ["top" | "bottom"]
 * Point = (Number|Dimension|Percentage), (Number|Dimension|Percentage) ;
 * ColorStop = Color, [" ", Offset] ;
 *
 * </pre>
 *
 * @author Werner Randelshofer
 */
public class CssLinearGradientConverter implements Converter<CssLinearGradient> {

    private CssColorConverter colorConverter = new CssColorConverter();
    private CssSizeConverter doubleConverter = new CssSizeConverter();

    public void toString(Appendable out, IdFactory idFactory, CssLinearGradient value) throws IOException {
        if (value == null) {
            out.append("none");
        } else {
            out.append("linear-gradient(");
            CssLinearGradient lg = value;
            final boolean proportional = lg.isProportional();
            final double startX = lg.getStartX();
            final double startY = lg.getStartY();
            final double endX = lg.getEndX();
            final double endY = lg.getEndY();
            final boolean needsFromTo;
            boolean needsSpace = false;
            boolean needsComma = false;
            if (proportional) {
                if (startX == 0.0 && startY == 0.0 & endX == 0.0 && endY == 0.0) {// invalid
                    needsFromTo = true;
                } else if (startX == 0.0 && startY == 0.0 & endX == 0.0 && endY == 1.0) {
                    //  the gradient direction defaults to 'to bottom'.
                    needsFromTo = false;
                } else if (startX == 0.0 && startY == 0.0 & endX == 1.0 && endY == 0.0) {
                    out.append("to right");
                    needsComma = true;
                    needsFromTo = false;
                } else if (startX == 0.0 && startY == 0.0 & endX == 1.0 && endY == 1.0) {
                    out.append("to right bottom");
                    needsComma = true;
                    needsFromTo = false;
                } else if (startX == 0.0 && startY == 1.0 & endX == 0.0 && endY == 0.0) {
                    out.append("to top");
                    needsComma = true;
                    needsFromTo = false;
                } else if (startX == 0.0 && startY == 1. & endX == 0.0 && endY == 1.0) {// invalid
                    needsFromTo = true;
                } else if (startX == 0.0 && startY == 1.0 & endX == 1.0 && endY == 0.0) {
                    out.append("to right top");
                    needsComma = true;
                    needsFromTo = false;
                } else if (startX == 0.0 && startY == 1.0 & endX == 1.0 && endY == 1.0) {
                    out.append("to right");
                    needsComma = true;
                    needsFromTo = false;
                } else if (startX == 1.0 && startY == 0.0 & endX == 0.0 && endY == 0.0) {
                    out.append("to left");
                    needsComma = true;
                    needsFromTo = false;
                } else if (startX == 1.0 && startY == 0.0 & endX == 0.0 && endY == 1.0) {
                    out.append("to left bottom");
                    needsComma = true;
                    needsFromTo = false;
                } else if (startX == 1.0 && startY == 0.0 & endX == 1.0 && endY == 0.0) {//invalid
                    needsFromTo = true;
                } else if (startX == 1.0 && startY == 0.0 & endX == 1.0 && endY == 1.0) {
                    //  the gradient direction defaults to 'to bottom'.
                    needsFromTo = false;
                } else if (startX == 1.0 && startY == 1.0 & endX == 0.0 && endY == 0.0) {
                    out.append("to left top");
                    needsComma = true;
                    needsFromTo = false;
                } else if (startX == 1.0 && startY == 1.0 & endX == 0.0 && endY == 1.0) {
                    out.append("to left");
                    needsComma = true;
                    needsFromTo = false;
                } else if (startX == 1.0 && startY == 1.0 & endX == 1.0 && endY == 0.0) {
                    out.append("to top");
                    needsComma = true;
                    needsFromTo = false;
                } else if (startX == 1.0 && startY == 1.0 & endX == 1.0 && endY == 1.0) {//invalid
                    needsFromTo = true;
                } else {
                    needsFromTo = true;
                }
            } else {
                needsFromTo = true;
            }
            if (needsFromTo) {
                final String unit = proportional ? "%" : "";
                {
                    out.append("from ")
                            .append(doubleConverter.toString(proportional ? startX * 100.0 : startX))
                            .append(unit)
                            .append(' ')
                            .append(doubleConverter.toString(proportional ? startY * 100.0 : startY))
                            .append(unit);
                    needsSpace = true;
                    needsComma = true;
                }
                {
                    if (needsSpace) {
                        out.append(' ');
                    }
                    out.append("to ")
                            .append(doubleConverter.toString(proportional ? endX * 100.0 : endX))
                            .append(unit)
                            .append(' ')
                            .append(doubleConverter.toString(proportional ? endY * 100.0 : endY))
                            .append(unit);
                    needsSpace = true;
                    needsComma = true;
                }
            }
            {
                switch (lg.getCycleMethod()) {
                    case NO_CYCLE:
                        break;
                    case REPEAT:
                        if (needsComma) {
                            out.append(", ");
                        }
                        out.append("repeat");
                        needsComma = true;
                        break;
                    case REFLECT:
                        if (needsComma) {
                            out.append(", ");
                        }
                        out.append("reflect");
                        needsComma = true;
                        break;
                    default:
                        throw new UnsupportedOperationException("not yet implemented");
                }
                for (CssStop stop : lg.getStops()) {
                    if (needsComma) {
                        out.append(", ");
                    }
                    colorConverter.toString(out, idFactory, stop.getColor());
                    if (stop.getOffset() != null) {
                        out.append(' ');
                        doubleConverter.toString(out, idFactory, stop.getOffset() * 100.0);
                        out.append('%');
                    }
                    needsComma = true;
                }
            }
            out.append(")");
        }
    }

    @Override
    public CssLinearGradient fromString(CharBuffer in, IdFactory idFactory) throws ParseException, IOException {
        CssTokenizerInterface tt = new CssTokenizer(new StringReader(in.toString()));
        tt.setSkipWhitespaces(true);
        if (tt.nextToken() == CssTokenizer.TT_IDENT) {
            if ("none".equals(tt.currentStringValue())) {
                in.position(in.limit());
                return null;
            } else {
                throw new ParseException("CSS LinearGradient: \"<none>\" or \"<linear-gradient>(\"  expected", tt.getPosition());
            }
        }
        if (tt.currentToken() != CssTokenizer.TT_FUNCTION) {
            throw new ParseException("CSS LinearGradient: \"<linear-gradient>(\"  expected", tt.getPosition());
        }

        boolean isLinear = false;
        String func;
        switch (tt.currentStringValue()) {
            case "linear-gradient":
                isLinear = true;
                break;
            default:
                throw new ParseException("CSS LinearGradient: \"<linear-gradient>(\"  expected, found: " + tt.currentStringValue(), tt.getPosition());
        }
        boolean needComma = false;
        PointToPoint fromTo = null;

        // parse [from point to point] | [to sideOrCorner]
        if (tt.nextToken() == CssTokenizer.TT_IDENT && "from".equals(tt.currentStringValue())) {
            fromTo = parsePointToPoint(tt);

            needComma = true;
        } else if (tt.currentToken() == CssTokenizer.TT_IDENT && "to".equals(tt.currentStringValue())) {
            fromTo = parseSideOrCorner(tt);
            needComma = true;
        } else {
            fromTo = new PointToPoint(0.0, 0.0, 0.0, 1.0, true);
            tt.pushBack();
        }
        if (needComma) {
            if (tt.nextToken() != ',') {
                throw new ParseException("CSS LinearGradient: ','  expected, found: " + tt.currentStringValue(), tt.getPosition());
            }
            needComma = false;
        }
        CycleMethod cycleMethod = CycleMethod.NO_CYCLE;
        if (tt.nextToken() == CssTokenizer.TT_IDENT) {
            if ("repeat".equals(tt.currentStringValue())) {
                cycleMethod = CycleMethod.REPEAT;
                needComma = true;

            } else if ("reflect".equals(tt.currentStringValue())) {
                cycleMethod = CycleMethod.REFLECT;
                needComma = true;
            } else {
                tt.pushBack();
            }
        } else {
            tt.pushBack();
        }

        if (needComma) {
            if (tt.nextToken() != ',') {
                throw new ParseException("CSS LinearGradient: ','  expected, found: " + tt.currentStringValue(), tt.getPosition());
            }
            needComma = false;
        }
        List<CssStop> stops = new ArrayList<>();
        do {
            stops.add(parseColorStop(tt));
        } while (tt.nextToken() == ',');

        if (tt.currentToken() != ')') {
            throw new ParseException("CSS LinearGradient: ')'  expected, found: " + tt.currentStringValue(), tt.getPosition());
        }
        tt.skipWhitespace();
        in.position(tt.getPosition());
        return new CssLinearGradient(fromTo.startX, fromTo.startY, fromTo.endX, fromTo.endY, fromTo.isProportional, cycleMethod, stops.toArray(new CssStop[stops.size()]));
    }

    @Override
    public CssLinearGradient getDefaultValue() {
        return null;
    }

    private static class PointToPoint {

        public double startX, startY, endX, endY;
        public boolean isProportional;

        public PointToPoint(double startX, double startY, double endX, double endY, boolean isProportional) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
            this.isProportional = isProportional;
        }

    }

    private PointToPoint parsePointToPoint(CssTokenizerInterface tt) throws IOException, ParseException {
        double startX = 0.0, startY = 0.0, endX = 0.0, endY = 1.0;
        Boolean isProportional = null;
        switch (tt.nextToken()) {
            case CssTokenizer.TT_NUMBER:
                startX = tt.currentNumericValue().doubleValue();
                isProportional = false;
                break;
            case CssTokenizer.TT_PERCENTAGE:
                isProportional = true;
                startX = tt.currentNumericValue().doubleValue() / 100.0;
                break;
            case CssTokenizer.TT_DIMENSION:
                isProportional = false;
                if (!"px".equals(tt.currentUnitValue())) {
                    throw new ParseException("CSS LinearGradient: start-x given in pixels or percentage expected, found: " + tt.currentStringValue() + " ttype:" + tt.currentToken(), tt.getPosition());
                }
                startX = tt.currentNumericValue().doubleValue();
                break;
            default:
                throw new ParseException("CSS LinearGradient: start-x expected, found: " + tt.currentStringValue() + " ttype:" + tt.currentToken(), tt.getPosition());
        }
        switch (tt.nextToken()) {
            case CssTokenizer.TT_NUMBER:
                if (isProportional) {
                    throw new ParseException("CSS LinearGradient: start-y as percentage value expected, found: " + tt.currentStringValue() + " ttype:" + tt.currentToken(), tt.getPosition());
                }
                startY = tt.currentNumericValue().doubleValue();
                break;
            case CssTokenizer.TT_PERCENTAGE:
                if (!isProportional) {
                    throw new ParseException("CSS LinearGradient: start-y as absolute value expected, found: " + tt.currentStringValue() + " ttype:" + tt.currentToken(), tt.getPosition());
                }
                startY = tt.currentNumericValue().doubleValue() / 100.0;
                break;
            case CssTokenizer.TT_DIMENSION:
                if (isProportional) {
                    throw new ParseException("CSS LinearGradient: start-y as percentage value expected, found: " + tt.currentStringValue() + " ttype:" + tt.currentToken(), tt.getPosition());
                }
                if (!"px".equals(tt.currentUnitValue())) {
                    throw new ParseException("CSS LinearGradient: start-y given in pixels or percentage expected, found: " + tt.currentStringValue() + " ttype:" + tt.currentToken(), tt.getPosition());
                }
                startY = tt.currentNumericValue().doubleValue();
                break;
            default:
                throw new ParseException("CSS LinearGradient: start-y expected, found: " + tt.currentStringValue() + " ttype:" + tt.currentToken(), tt.getPosition());
        }
        if (tt.nextToken() == CssTokenizer.TT_IDENT && "to".equals(tt.currentStringValue())) {
            switch (tt.nextToken()) {
                case CssTokenizer.TT_NUMBER:
                    if (isProportional) {
                        throw new ParseException("CSS LinearGradient: end-x as percentage value expected, found: " + tt.currentStringValue() + " ttype:" + tt.currentToken(), tt.getPosition());
                    }
                    endX = tt.currentNumericValue().doubleValue();
                    break;
                case CssTokenizer.TT_PERCENTAGE:
                    if (!isProportional) {
                        throw new ParseException("CSS LinearGradient: end-x as absolute value expected, found: " + tt.currentStringValue() + " ttype:" + tt.currentToken(), tt.getPosition());
                    }
                    endX = tt.currentNumericValue().doubleValue() / 100.0;
                    break;
                case CssTokenizer.TT_DIMENSION:
                    if (isProportional) {
                        throw new ParseException("CSS LinearGradient: end-x as percentage value expected, found: " + tt.currentStringValue() + " ttype:" + tt.currentToken(), tt.getPosition());
                    }
                    if (!"px".equals(tt.currentUnitValue())) {
                        throw new ParseException("CSS LinearGradient: end-x given in pixels or percentage expected, found: " + tt.currentStringValue() + " ttype:" + tt.currentToken(), tt.getPosition());
                    }
                    endX = tt.currentNumericValue().doubleValue();
                    break;
                default:
                    throw new ParseException("CSS LinearGradient: end-x expected, found: " + tt.currentStringValue() + " ttype:" + tt.currentToken(), tt.getPosition());
            }
            switch (tt.nextToken()) {
                case CssTokenizer.TT_NUMBER:
                    if (isProportional) {
                        throw new ParseException("CSS LinearGradient: end-y as proportional value expected, found: " + tt.currentStringValue() + " ttype:" + tt.currentToken(), tt.getPosition());
                    }
                    endY = tt.currentNumericValue().doubleValue();
                    break;
                case CssTokenizer.TT_PERCENTAGE:
                    if (!isProportional) {
                        throw new ParseException("CSS LinearGradient: end-y as absolute value expected, found: " + tt.currentStringValue() + " ttype:" + tt.currentToken(), tt.getPosition());
                    }
                    endY = tt.currentNumericValue().doubleValue() / 100.0;
                    break;
                case CssTokenizer.TT_DIMENSION:
                    if (isProportional) {
                        throw new ParseException("CSS LinearGradient: end-y as proportional value expected, found: " + tt.currentStringValue() + " ttype:" + tt.currentToken(), tt.getPosition());
                    }
                    if (!"px".equals(tt.currentUnitValue())) {
                        throw new ParseException("CSS LinearGradient: end-y given in pixels or percent expected, found: " + tt.currentStringValue() + " ttype:" + tt.currentToken(), tt.getPosition());
                    }
                    endY = tt.currentNumericValue().doubleValue();
                    break;
                default:
                    throw new ParseException("CSS LinearGradient: end-y expected, found: " + tt.currentStringValue() + " ttype:" + tt.currentToken(), tt.getPosition());
            }
        } else {
            throw new ParseException("CSS LinearGradient: 'to' expected, found: " + tt.currentStringValue() + " ttype:" + tt.currentToken(), tt.getPosition());

        }
        return new PointToPoint(startX, startY, endX, endY, isProportional);
    }

    private PointToPoint parseSideOrCorner(CssTokenizerInterface tt) throws IOException, ParseException {
        double startX = 0.0, startY = 0.0, endX = 0.0, endY = 1.0;
        Boolean isProportional = true;
        String h = null;
        String v = null;
        while (tt.nextToken() == CssTokenizer.TT_IDENT) {
            switch (tt.currentStringValue()) {
                case "top":
                    if (v != null) {
                        throw new ParseException("CSS LinearGradient: you already specified '" + v + "', found: " + tt.currentStringValue() + " ttype:" + tt.currentToken(), tt.getPosition());
                    }
                    v = tt.currentStringValue();
                    break;
                case "bottom":
                    if (v != null) {
                        throw new ParseException("CSS LinearGradient: you already specified '" + v + "', found: " + tt.currentStringValue() + " ttype:" + tt.currentToken(), tt.getPosition());
                    }
                    v = tt.currentStringValue();
                    break;
                case "left":
                    if (h != null) {
                        throw new ParseException("CSS LinearGradient: you already specified '" + h + "', found: " + tt.currentStringValue() + " ttype:" + tt.currentToken(), tt.getPosition());
                    }
                    h = tt.currentStringValue();
                    break;
                case "right":
                    if (h != null) {
                        throw new ParseException("CSS LinearGradient: you already specified '" + h + "', found: " + tt.currentStringValue() + " ttype:" + tt.currentToken(), tt.getPosition());
                    }
                    h = tt.currentStringValue();
                    break;
                default:
                    throw new ParseException("CSS LinearGradient: 'top', 'bottom', 'left' or 'right' expected, found: " + tt.currentStringValue() + " ttype:" + tt.currentToken(), tt.getPosition());
            }
        }
        tt.pushBack();
        switch (h + " " + v) {
            case "null null":
                throw new ParseException("CSS LinearGradient: 'top', 'bottom', 'left' or 'right' expected after 'to'.", tt.getPosition());
            case "left null":
                startX = 1.0;
                startY = endX = endY = 0.0;
                break;
            case "right null":
                startX = startY = endY = 0.0;
                endX = 1.0;
                break;
            case "null top":
                startX = endX = endY = 0.0;
                startY = 1.0;
                break;
            case "null bottom":
                startX = startY = endX = 0.0;
                endY = 1.0;
                break;
            case "left top":
                startX = startY = 1.0;
                endX = endY = 0.0;
                break;
            case "left bottom":
                startX = endY = 1.0;
                startY = endX = 0.0;

                break;
            case "right top":
                startX = endY = 0.0;
                startY = endX = 1.0;
                break;
            case "right bottom":
                startX = startY = 0.0;
                endX = endY = 1.0;
                break;
        }
        return new PointToPoint(startX, startY, endX, endY, isProportional);
    }

    private CssStop parseColorStop(CssTokenizerInterface tt) throws IOException, ParseException {
        CssColor color = colorConverter.parseColor(tt);
        Double offset = null;
        switch (tt.nextToken()) {
            case CssTokenizerInterface.TT_NUMBER:
                offset = tt.currentNumericValue().doubleValue();
                break;
            case CssTokenizerInterface.TT_PERCENTAGE:
                offset = tt.currentNumericValue().doubleValue() / 100.0;
                break;
            default:
                tt.pushBack();
        }
        return new CssStop(offset, color);
    }
}
