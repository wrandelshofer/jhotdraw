/* @(#)CssRadialGradientConverter.java
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
 * CssRadialGradientConverter.
 * <p>
 * Parses the following EBNF from the
 * <a href="https://docs.oracle.com/javafx/2/api/javafx/scene/doc-files/cssref.html">JavaFX
 * CSS Reference Guide</a>.
 * </p>
 * <pre>
 * Paint := (Color|RadialGradient|RadialGradient|ImagePattern RepeatingImagePattern) ;
 * RadialGradient := "radial-gradient(", RadialGradientParameters, ")"
 * RadialGradientParameters := [ FocusAngle "," ], [ FocusDistance "," ], [ Center "," ], Radius, 
 *                   [ ( "repeat" | "reflect" ),"," ] ColorStop,{"," ColorStop}) ;
 * FocusAngle = "focus-angle", Dimension"deg"; 
 * FocusDistance = "focus-distance", Percentage ;
 * Center = "center", Point ;
 * Radius = "radius", ( Length | Percentage ) ;
 * ColorStop = Color, [" ", Offset] ;
 * Point = (Number|Dimension|Percentage), (Number|Dimension|Percentage) ;
 *
 * </pre>
 *
 * @author Werner Randelshofer
 */
public class CssRadialGradientConverter implements Converter<CssRadialGradient> {

    private CssColorConverter colorConverter = new CssColorConverter();
    private CssSizeConverter doubleConverter = new CssSizeConverter();

    public void toString(Appendable out, IdFactory idFactory, CssRadialGradient value) throws IOException {
        if (value == null) {
            out.append("none");
        } else {
            out.append("radial-gradient(");
            CssRadialGradient lg = value;
            final boolean proportional = lg.isProportional();
            final double focusAngle = lg.getFocusAngle();
            final double focusDistance = lg.getFocusDistance();
            final double centerX = lg.getCenterX();
            final double centerY = lg.getCenterY();
            final double radius = lg.getRadius();
            boolean needsSpace = false;
            boolean needsComma = false;
                if (focusAngle != 0.0) {
                    out.append("focus-angle ");
                    out.append(doubleConverter.toString(focusAngle));
                    out.append("deg");
                    needsComma=true;
                }
                if (focusDistance != 0.0) {
                    if (needsComma) out.append(", ");
                    out.append("focus-angle ");
                    out.append(doubleConverter.toString(focusDistance*100));
                    out.append("%");
                    needsComma=true;
                }
                if (centerX != 0.0||centerY!=0.0) {
                    if (needsComma) out.append(", ");
                    out.append("center ");
                    if (proportional) {
                    out.append(doubleConverter.toString(centerX*100));
                    out.append("% ");
                    out.append(doubleConverter.toString(centerY*100));
                    out.append("%");
                    }else{
                    out.append(doubleConverter.toString(centerX));
                    out.append(" ");
                    out.append(doubleConverter.toString(centerY));
                    }
                    needsComma=true;
                }
                    if (needsComma) out.append(", ");
                    out.append("radius ");
                    if (proportional) {
                    out.append(doubleConverter.toString(radius*100));
                    out.append("%");
                    }else{
                    out.append(doubleConverter.toString(radius));
                    }
                    needsComma=true;
            
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
    public CssRadialGradient fromString(CharBuffer in, IdFactory idFactory) throws ParseException, IOException {
        CssTokenizerInterface tt = new CssTokenizer(new StringReader(in.toString()));
        tt.setSkipWhitespaces(true);
        if (tt.nextToken() == CssTokenizer.TT_IDENT) {
            if ("none".equals(tt.currentStringValue())) {
                in.position(in.limit());
                return null;
            } else {
                throw new ParseException("CSS RadialGradient: \"<none>\" or \"<radial-gradient>(\"  expected", tt.getPosition());
            }
        }
        if (tt.currentToken() != CssTokenizer.TT_FUNCTION) {
            throw new ParseException("CSS RadialGradient: \"<radial-gradient>(\"  expected", tt.getPosition());
        }

        boolean isRadial = false;
        String func;
        switch (tt.currentStringValue()) {
            case "radial-gradient":
                isRadial = true;
                break;
            default:
                throw new ParseException("CSS RadialGradient: \"<radial-gradient>(\"  expected, found: " + tt.currentStringValue(), tt.getPosition());
        }
        boolean needComma = false;
        PointToPoint fromTo = null;

        // parse [from point to point] | [to sideOrCorner]
        if (tt.nextToken() == CssTokenizer.TT_IDENT && "from".equals(tt.currentStringValue())) {
            fromTo = parsePointToPoint(tt);

            needComma = true;
        } else if (tt.currentToken() == CssTokenizer.TT_IDENT && "to".equals(tt.currentStringValue())) {
            fromTo = null;
            needComma = true;
        } else {
            fromTo = new PointToPoint(0.0, 0.0, 0.0, 1.0, true);
            tt.pushBack();
        }
        if (needComma) {
            if (tt.nextToken() != ',') {
                throw new ParseException("CSS RadialGradient: ','  expected, found: " + tt.currentStringValue(), tt.getPosition());
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
                throw new ParseException("CSS RadialGradient: ','  expected, found: " + tt.currentStringValue(), tt.getPosition());
            }
            needComma = false;
        }
        List<CssStop> stops = new ArrayList<>();
        do {
            stops.add(parseColorStop(tt));
        } while (tt.nextToken() == ',');

        if (tt.currentToken() != ')') {
            throw new ParseException("CSS RadialGradient: ')'  expected, found: " + tt.currentStringValue(), tt.getPosition());
        }
        tt.skipWhitespace();
        in.position(tt.getPosition());
        return null;//new CssRadialGradient(fromTo.focusAngle, fromTo.focusDistance, fromTo.centerX, fromTo.centerY, fromTo.isProportional, cycleMethod, stops.toArray(new CssStop[stops.size()]));
    }

    @Override
    public CssRadialGradient getDefaultValue() {
        return null;
    }

    private static class PointToPoint {

        public double focusAngle, focusDistance, centerX, centerY;
        public boolean isProportional;

        public PointToPoint(double focusAngle, double focusDistance, double centerX, double centerY, boolean isProportional) {
            this.focusAngle = focusAngle;
            this.focusDistance = focusDistance;
            this.centerX = centerX;
            this.centerY = centerY;
            this.isProportional = isProportional;
        }

    }

    private PointToPoint parsePointToPoint(CssTokenizerInterface tt) throws IOException, ParseException {
        double focusAngle = 0.0, focusDistance = 0.0, centerX = 0.0, centerY = 1.0;
        Boolean isProportional = null;
        switch (tt.nextToken()) {
            case CssTokenizer.TT_NUMBER:
                focusAngle = tt.currentNumericValue().doubleValue();
                isProportional = false;
                break;
            case CssTokenizer.TT_PERCENTAGE:
                isProportional = true;
                focusAngle = tt.currentNumericValue().doubleValue() / 100.0;
                break;
            case CssTokenizer.TT_DIMENSION:
                isProportional = false;
                if (!"px".equals(tt.currentUnitValue())) {
                    throw new ParseException("CSS RadialGradient: start-x given in pixels or percentage expected, found: " + tt.currentStringValue() + " ttype:" + tt.currentToken(), tt.getPosition());
                }
                focusAngle = tt.currentNumericValue().doubleValue();
                break;
            default:
                throw new ParseException("CSS RadialGradient: start-x expected, found: " + tt.currentStringValue() + " ttype:" + tt.currentToken(), tt.getPosition());
        }
        switch (tt.nextToken()) {
            case CssTokenizer.TT_NUMBER:
                if (isProportional) {
                    throw new ParseException("CSS RadialGradient: start-y as percentage value expected, found: " + tt.currentStringValue() + " ttype:" + tt.currentToken(), tt.getPosition());
                }
                focusDistance = tt.currentNumericValue().doubleValue();
                break;
            case CssTokenizer.TT_PERCENTAGE:
                if (!isProportional) {
                    throw new ParseException("CSS RadialGradient: start-y as absolute value expected, found: " + tt.currentStringValue() + " ttype:" + tt.currentToken(), tt.getPosition());
                }
                focusDistance = tt.currentNumericValue().doubleValue() / 100.0;
                break;
            case CssTokenizer.TT_DIMENSION:
                if (isProportional) {
                    throw new ParseException("CSS RadialGradient: start-y as percentage value expected, found: " + tt.currentStringValue() + " ttype:" + tt.currentToken(), tt.getPosition());
                }
                if (!"px".equals(tt.currentUnitValue())) {
                    throw new ParseException("CSS RadialGradient: start-y given in pixels or percentage expected, found: " + tt.currentStringValue() + " ttype:" + tt.currentToken(), tt.getPosition());
                }
                focusDistance = tt.currentNumericValue().doubleValue();
                break;
            default:
                throw new ParseException("CSS RadialGradient: start-y expected, found: " + tt.currentStringValue() + " ttype:" + tt.currentToken(), tt.getPosition());
        }
        if (tt.nextToken() == CssTokenizer.TT_IDENT && "to".equals(tt.currentStringValue())) {
            switch (tt.nextToken()) {
                case CssTokenizer.TT_NUMBER:
                    if (isProportional) {
                        throw new ParseException("CSS RadialGradient: end-x as percentage value expected, found: " + tt.currentStringValue() + " ttype:" + tt.currentToken(), tt.getPosition());
                    }
                    centerX = tt.currentNumericValue().doubleValue();
                    break;
                case CssTokenizer.TT_PERCENTAGE:
                    if (!isProportional) {
                        throw new ParseException("CSS RadialGradient: end-x as absolute value expected, found: " + tt.currentStringValue() + " ttype:" + tt.currentToken(), tt.getPosition());
                    }
                    centerX = tt.currentNumericValue().doubleValue() / 100.0;
                    break;
                case CssTokenizer.TT_DIMENSION:
                    if (isProportional) {
                        throw new ParseException("CSS RadialGradient: end-x as percentage value expected, found: " + tt.currentStringValue() + " ttype:" + tt.currentToken(), tt.getPosition());
                    }
                    if (!"px".equals(tt.currentUnitValue())) {
                        throw new ParseException("CSS RadialGradient: end-x given in pixels or percentage expected, found: " + tt.currentStringValue() + " ttype:" + tt.currentToken(), tt.getPosition());
                    }
                    centerX = tt.currentNumericValue().doubleValue();
                    break;
                default:
                    throw new ParseException("CSS RadialGradient: end-x expected, found: " + tt.currentStringValue() + " ttype:" + tt.currentToken(), tt.getPosition());
            }
            switch (tt.nextToken()) {
                case CssTokenizer.TT_NUMBER:
                    if (isProportional) {
                        throw new ParseException("CSS RadialGradient: end-y as proportional value expected, found: " + tt.currentStringValue() + " ttype:" + tt.currentToken(), tt.getPosition());
                    }
                    centerY = tt.currentNumericValue().doubleValue();
                    break;
                case CssTokenizer.TT_PERCENTAGE:
                    if (!isProportional) {
                        throw new ParseException("CSS RadialGradient: end-y as absolute value expected, found: " + tt.currentStringValue() + " ttype:" + tt.currentToken(), tt.getPosition());
                    }
                    centerY = tt.currentNumericValue().doubleValue() / 100.0;
                    break;
                case CssTokenizer.TT_DIMENSION:
                    if (isProportional) {
                        throw new ParseException("CSS RadialGradient: end-y as proportional value expected, found: " + tt.currentStringValue() + " ttype:" + tt.currentToken(), tt.getPosition());
                    }
                    if (!"px".equals(tt.currentUnitValue())) {
                        throw new ParseException("CSS RadialGradient: end-y given in pixels or percent expected, found: " + tt.currentStringValue() + " ttype:" + tt.currentToken(), tt.getPosition());
                    }
                    centerY = tt.currentNumericValue().doubleValue();
                    break;
                default:
                    throw new ParseException("CSS RadialGradient: end-y expected, found: " + tt.currentStringValue() + " ttype:" + tt.currentToken(), tt.getPosition());
            }
        } else {
            throw new ParseException("CSS RadialGradient: 'to' expected, found: " + tt.currentStringValue() + " ttype:" + tt.currentToken(), tt.getPosition());

        }
        return new PointToPoint(focusAngle, focusDistance, centerX, centerY, isProportional);
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
