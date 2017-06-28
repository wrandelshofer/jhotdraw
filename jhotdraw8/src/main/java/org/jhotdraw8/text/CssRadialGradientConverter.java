/* @(#)CssRadialGradientConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.text;

import org.jhotdraw8.draw.key.CssRadialGradient;
import org.jhotdraw8.draw.key.CssColor;
import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.paint.CycleMethod;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.CssTokenizerInterface;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.io.CharBufferReader;

/**
 * CssRadialGradientConverter.
 * <p>
 * Parses the following EBNF from the
 * <a href="https://docs.oracle.com/javafx/2/api/javafx/scene/doc-files/cssref.html">JavaFX
 * CSS Reference Guide</a>.
 * </p>
 * <pre>
 * Paint := (Color|RadialGradient|RadialGradient|ImagePattern RepeatingImagePattern) ;
 * RadialGradient := "radial-gradient(", RadialGradientParameters,  Cycle, ColorStop,{"," ColorStop}")"
 * RadialGradientParameters := [ FocusAngle "," ], [ FocusDistance "," ], [ Center "," ], Radius ;
 * FocusAngle = "focus-angle", Dimension"deg";
 * FocusDistance = "focus-distance", Percentage ;
 * Center = "center", Point ;
 * Cycle = ( "repeat" | "reflect" )
 * Radius = "radius", ( Length | Percentage ) ;
 * ColorStop = Color, [" ", Offset] ;
 * Point = (Number|Dimension|Percentage), (Number|Dimension|Percentage) ;
 *
 * </pre>
 *
 * @author Werner Randelshofer
 */
public class CssRadialGradientConverter implements Converter<CssRadialGradient> {

    private CssColorConverter colorConverter = new CssColorConverter(false);
    private CssDoubleConverter doubleConverter = new CssDoubleConverter();

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
            boolean needsComma = false;
            if (focusAngle != 0.0) {
                out.append("focus-angle ");
                out.append(doubleConverter.toString(focusAngle));
                out.append("deg");
                needsComma = true;
            }
            if (focusDistance != 0.0) {
                if (needsComma) {
                    out.append(", ");
                }
                out.append("focus-distance ");
                out.append(doubleConverter.toString(focusDistance * 100));
                out.append("%");
                needsComma = true;
            }
            if (centerX != 0.5 || centerY != 0.5) {
                if (needsComma) {
                    out.append(", ");
                }
                out.append("center ");
                if (proportional) {
                    out.append(doubleConverter.toString(centerX * 100));
                    out.append("% ");
                    out.append(doubleConverter.toString(centerY * 100));
                    out.append("%");
                } else {
                    out.append(doubleConverter.toString(centerX));
                    out.append(" ");
                    out.append(doubleConverter.toString(centerY));
                }
                needsComma = true;
            }
            if (needsComma) {
                out.append(", ");
            }
            out.append("radius ");
            if (proportional) {
                out.append(doubleConverter.toString(radius * 100));
                out.append("%");
            } else {
                out.append(doubleConverter.toString(radius));
            }
            needsComma = true;

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
        CssTokenizerInterface tt = new CssTokenizer(new CharBufferReader(in));
        tt.setSkipWhitespaces(true);
        if (tt.nextToken() == CssTokenizer.TT_IDENT) {
            if ("none".equals(tt.currentStringValue())) {
                in.position(in.limit());
                return null;
            } else {
                throw new ParseException("CSS RadialGradient: \"<none>\" or \"<radial-gradient>(\"  expected", tt.getStartPosition());
            }
        }
        if (tt.currentToken() != CssTokenizer.TT_FUNCTION) {
            throw new ParseException("CSS RadialGradient: \"<radial-gradient>(\"  expected", tt.getStartPosition());
        }

        switch (tt.currentStringValue()) {
            case "radial-gradient":
                break;
            default:
                throw new ParseException("CSS RadialGradient: \"<radial-gradient>(\"  expected, found: " + tt.currentStringValue(), tt.getStartPosition());
        }

        double focusAngle = 0;
        double focusDistance = 0;
        double centerX = 0.5;
        double centerY = 0.5;
        double radius = 1;
        Boolean isProportional = null;
        while (tt.nextToken() == CssTokenizer.TT_IDENT) {
            if ("focus-angle".equals(tt.currentStringValue())) {
                switch (tt.nextToken()) {
                    case CssTokenizer.TT_DIMENSION:
                        if (!"deg".equals(tt.currentStringValue())) {
                            throw new ParseException("CSS RadialGradient: expected focus-angle given in degrees with unit  \"deg\", found: " + tt.currentStringValue(), tt.getStartPosition());
                        }
                        focusAngle = tt.currentNumericValue().doubleValue();
                        break;
                    case CssTokenizer.TT_NUMBER:
                        focusAngle = tt.currentNumericValue().doubleValue();
                        break;
                    default:
                        throw new ParseException("CSS RadialGradient: focus-angle  expected, found: " + tt.currentStringValue(), tt.getStartPosition());
                }

            } else if ("focus-distance".equals(tt.currentStringValue())) {
                switch (tt.nextToken()) {
                    case CssTokenizer.TT_PERCENTAGE:
                        focusDistance = tt.currentNumericValue().doubleValue() / 100;
                        break;
                    case CssTokenizer.TT_NUMBER:
                        focusDistance = tt.currentNumericValue().doubleValue();
                        break;
                    default:
                        throw new ParseException("CSS RadialGradient: focus-distance  expected, found: " + tt.currentStringValue(), tt.getStartPosition());
                }

            } else if ("center".equals(tt.currentStringValue())) {
                switch (tt.nextToken()) {
                    case CssTokenizer.TT_PERCENTAGE:
                        if (isProportional == null) {
                            isProportional = true;
                        }
                        if (!isProportional) {
                            throw new ParseException("CSS RadialGradient: absolute value  expected, found: " + tt.currentStringValue(), tt.getStartPosition());
                        }
                        centerX = tt.currentNumericValue().doubleValue() / 100;
                        break;
                    case CssTokenizer.TT_NUMBER:
                        if (isProportional == null) {
                            isProportional = false;
                        }
                        if (isProportional) {
                            throw new ParseException("CSS RadialGradient: percentage  expected, found: " + tt.currentStringValue(), tt.getStartPosition());
                        }
                        centerX = tt.currentNumericValue().doubleValue();
                        break;
                    default:
                        throw new ParseException("CSS RadialGradient: center x-value  expected, found: " + tt.currentStringValue(), tt.getStartPosition());
                }
                switch (tt.nextToken()) {
                    case CssTokenizer.TT_PERCENTAGE:
                        if (isProportional == null) {
                            isProportional = true;
                        }
                        if (!isProportional) {
                            throw new ParseException("CSS RadialGradient: absolute value  expected, found: " + tt.currentStringValue(), tt.getStartPosition());
                        }
                        centerY = tt.currentNumericValue().doubleValue() / 100;
                        break;
                    case CssTokenizer.TT_NUMBER:
                        if (isProportional == null) {
                            isProportional = false;
                        }
                        if (isProportional) {
                            throw new ParseException("CSS RadialGradient: percentage  expected, found: " + tt.currentStringValue(), tt.getStartPosition());
                        }
                        centerY = tt.currentNumericValue().doubleValue();
                        break;
                    default:
                        throw new ParseException("CSS RadialGradient: center y-value  expected, found: " + tt.currentStringValue(), tt.getStartPosition());
                }
            } else if ("radius".equals(tt.currentStringValue())) {
                switch (tt.nextToken()) {
                    case CssTokenizer.TT_PERCENTAGE:
                        if (isProportional == null) {
                            isProportional = true;
                        }
                        if (!isProportional) {
                            throw new ParseException("CSS RadialGradient: absolute value expected, found: " + tt.currentStringValue(), tt.getStartPosition());
                        }
                        radius = tt.currentNumericValue().doubleValue() / 100;
                        break;
                    case CssTokenizer.TT_NUMBER:
                        if (isProportional == null) {
                            isProportional = false;
                        }
                        if (isProportional) {
                            throw new ParseException("CSS RadialGradient: percentage  expected, found: " + tt.currentStringValue(), tt.getStartPosition());
                        }
                        radius = tt.currentNumericValue().doubleValue();
                        break;
                    default:
                        throw new ParseException("CSS RadialGradient: center x-value  expected, found: " + tt.currentStringValue(), tt.getStartPosition());
                }
            } else {
                tt.pushBack();
                break;
            }

            if (tt.nextToken() != ',') {
                tt.pushBack();
            }
        }
        CycleMethod cycleMethod = CycleMethod.NO_CYCLE;
        if (tt.nextToken() == CssTokenizer.TT_IDENT) {
            if ("repeat".equals(tt.currentStringValue())) {
                cycleMethod = CycleMethod.REPEAT;

            } else if ("reflect".equals(tt.currentStringValue())) {
                cycleMethod = CycleMethod.REFLECT;
            } else {
                tt.pushBack();
            }
        } else {
            tt.pushBack();
        }

        if (tt.nextToken() != ',') {
            tt.pushBack();
        }
        List<CssStop> stops = new ArrayList<>();
        while (tt.nextToken() != ')' && tt.currentToken() != CssTokenizer.TT_EOF) {
            tt.pushBack();
            stops.add(parseColorStop(tt));
            if (tt.nextToken() != ',') {
                tt.pushBack();
            }
        }

        if (tt.currentToken() != ')') {
            throw new ParseException("CSS RadialGradient: ')'  expected, found: " + tt.currentStringValue(), tt.getStartPosition());
        }
        tt.skipWhitespace();
        if (isProportional == null) {
            isProportional = true;
        }

        return new CssRadialGradient(focusAngle, focusDistance, centerX, centerY, radius, isProportional, cycleMethod, stops.toArray(new CssStop[stops.size()]));
    }

    @Override
    public CssRadialGradient getDefaultValue() {
        return null;
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

    @Override
    public String getHelpText() {
        return "Format of ⟨RadialGradient⟩: radial-gradient(［⟨RadialGradientParameters⟩］［,⟨Cycle⟩］,⟨ColorStop⟩｛,⟨ColorStop⟩｝)"
                + "\nFormat of ⟨RadialGradientParameters⟩: ［⟨FocusAngle⟩］［, ⟨FocusDistance⟩］［, ⟨Center⟩］, ⟨Radius⟩"
                + "\nFormat of ⟨FocusAngle⟩: focus-angle ⟨angle⟩deg"
                + "\nFormat of ⟨FocusDistance⟩: focus-distance ⟨percentage⟩%"
                + "\nFormat of ⟨Center⟩: center ⟨cx⟩,⟨cy⟩｜center ⟨cx⟩%,⟨cy⟩%"
                + "\nFormat of ⟨Radius⟩: ⟨radius⟩｜⟨percentage⟩%"
                + "\nFormat of ⟨Cycle⟩: repeat｜reflect"
                + "\nFormat of ⟨ColorStop⟩: ⟨Color⟩ ⟨percentage⟩%"
                + "\n" + colorConverter.getHelpText();
    }
}
