/* @(#)CssLinearGradientConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.io.StringReader;
import java.nio.CharBuffer;
import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Map;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import org.jhotdraw8.css.CssTokenizer;
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
 * LinearGradientParameters := [ ("from", Point, "to", Point) |  "to", SideOrCorner], "," ],
 *                   [ ( "repeat" | "reflect" ),"," ] ColorStop,{"," ColorStop}) ;
 * ColorStop = Color, [" ", Offset] ;

 * </pre>
 * <p>
 * FIXME currently only parses the Color production
 * </p>
 *
 * @author Werner Randelshofer
 */
public class CssCLinearGradientConverter implements Converter<CLinearGradient> {

    private CssColorConverter colorConverter = new CssColorConverter();
    private XmlNumberConverter doubleConverter = new XmlNumberConverter();

    public void toString(Appendable out, IdFactory idFactory, CLinearGradient value) throws IOException {
        if (value == null) {
            out.append("none");
        } else {
            LinearGradient lg = value.getLinearGradient();
            final boolean proportional = lg.isProportional();
            final String unit = proportional ? "%" : "px";
            out.append("linear-gradient(");
            boolean needsSpace = false;
            boolean needsComma = false;
            {
                final double startX = lg.getStartX();
                final double startY = lg.getStartY();
                if (startX != 0.0 || startY != 0.0) {
                    out.append("from ")
                            .append(doubleConverter.toString(startX))
                            .append(unit)
                            .append(' ')
                            .append(doubleConverter.toString(startY))
                            .append(unit);
                    needsSpace = true;
                    needsComma = true;
                }
            }
            {
                final double endX = lg.getEndX();
                final double endY = lg.getEndY();
                if (endX != 0.0 || endY != 0.0) {
                    if (needsSpace) {
                        out.append(' ');
                    }
                    out.append("to ")
                            .append(doubleConverter.toString(endX))
                            .append(unit)
                            .append(' ')
                            .append(doubleConverter.toString(endY))
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
                for (Stop stop : lg.getStops()) {
                    if (needsComma) {
                        out.append(", ");
                    }
                    colorConverter.toString(out, idFactory, stop.getColor());
                    out.append(' ');
                    if (proportional) {
                        doubleConverter.toString(out, idFactory, stop.getOffset() * 100.0);
                        out.append('%');
                    } else {
                        doubleConverter.toString(out, idFactory, stop.getOffset());
                    }
                    needsComma = true;
                }
            }
            out.append(")");
        }
    }

    @Override
    public CLinearGradient fromString(CharBuffer in, IdFactory idFactory) throws ParseException, IOException {
        CssTokenizer tt = new CssTokenizer(new StringReader(in.toString()));
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
                throw new ParseException("CSS LinearGradient: \"<linear-gradient>(\"  expected, found: "+tt.currentStringValue(), tt.getPosition());
        }
        tt.skipWhitespace();
        boolean needComma = false;
        double startX = 0.0;
        double startY = 0.0;
        double endX = 1.0;
        double endY = 0.0;
        if (tt.nextToken() == CssTokenizer.TT_IDENT && "from".equals(tt.currentStringValue())) {
        tt.skipWhitespace();
            if (tt.nextToken()!=CssTokenizer.TT_NUMBER) {
                                throw new ParseException("CSS LinearGradient: start-x expected, found: "+tt.currentStringValue(), tt.getPosition());
            }
            startX=tt.currentNumericValue().doubleValue();
        tt.skipWhitespace();
            if (tt.nextToken()!=CssTokenizer.TT_NUMBER) {
                                throw new ParseException("CSS LinearGradient: start-y expected, found: "+tt.currentStringValue(), tt.getPosition());
            }
            startY=tt.currentNumericValue().doubleValue();
            needComma = true;
        } else {
            tt.pushBack();
        }
        tt.skipWhitespace();
        if (tt.nextToken() == CssTokenizer.TT_IDENT && "to".equals(tt.currentStringValue())) {
        tt.skipWhitespace();
            if (tt.nextToken()!=CssTokenizer.TT_NUMBER) {
                                throw new ParseException("CSS LinearGradient: start-x expected, found: "+tt.currentStringValue(), tt.getPosition());
            }
            startX=tt.currentNumericValue().doubleValue();
        tt.skipWhitespace();
            if (tt.nextToken()!=CssTokenizer.TT_NUMBER) {
                                throw new ParseException("CSS LinearGradient: start-y expected, found: "+tt.currentStringValue(), tt.getPosition());
            }
            startY=tt.currentNumericValue().doubleValue();
            needComma = true;
        } else {
            tt.pushBack();
        }
        tt.skipWhitespace();
        if (needComma) {
            if (tt.nextToken() != ',') {
                throw new ParseException("CSS LinearGradient: ','  expected, found: "+tt.currentStringValue(), tt.getPosition());
            }
            needComma = false;
        }
        tt.skipWhitespace();
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
        tt.skipWhitespace();
        if (needComma) {
            if (tt.nextToken() != ',') {
                throw new ParseException("CSS LinearGradient: ','  expected, found: "+tt.currentStringValue(), tt.getPosition());
            }
            needComma = false;
        }
        tt.skipWhitespace();
        if (tt.nextToken() != ')') {
            throw new ParseException("CSS LinearGradient: ')'  expected, found: "+tt.currentStringValue(), tt.getPosition());
        }

        return null;
    }

    @Override
    public CLinearGradient getDefaultValue() {
        return null;
    }
}
