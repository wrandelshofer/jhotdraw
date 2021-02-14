/*
 * @(#)SvgLinearGradientConverter.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.svg.text;

import javafx.scene.paint.CycleMethod;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.text.AbstractCssConverter;
import org.jhotdraw8.css.text.CssColorConverter;
import org.jhotdraw8.css.text.CssStop;
import org.jhotdraw8.io.IdResolver;
import org.jhotdraw8.io.IdSupplier;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * SvgLinearGradientConverter.
 * <p>
 * Parses the following EBNF from the
 * <a href="https://docs.oracle.com/javafx/2/api/javafx/scene/doc-files/cssref.html">JavaFX
 * CSS Reference Guide</a>.
 * </p>
 * <pre>
 * Paint := (Color|LinearGradient|RadialGradient|ImagePattern RepeatingImagePattern) ;
 * LinearGradient := "linear-gradient(", LinearGradientParameters, [ Cycle ], ColorStop,{"," ColorStop} ")"
 * LinearGradientParameters := [ PointToPoint | SideOrCorner] ]) ;
 * PointToPoint = "from", Point, "to", Point
 * SideOrCorner = "to" ["left" | "right"] , ["top" | "bottom"]
 * Cycle = ( "repeat" | "reflect" )
 * Point = (Number|CssSize|Percentage), (Number|CssSize|Percentage) ;
 * ColorStop = Color, [" ", Offset] ;
 *
 * </pre>
 *
 * @author Werner Randelshofer
 */
public class SvgCssLinearGradientConverter extends AbstractCssConverter<SvgLinearGradient> {
    @NonNull
    private final static CssColorConverter colorConverter = new CssColorConverter(false);
    public static final String LINEAR_GRADIENT_FUNCTION = "linear-gradient";

    public SvgCssLinearGradientConverter(boolean nullable) {
        super(nullable);
    }

    public SvgCssLinearGradientConverter() {
        this(false);
    }

    @Override
    protected <TT extends SvgLinearGradient> void produceTokensNonNull(@NonNull TT lg, @Nullable IdSupplier idSupplier, @NonNull Consumer<CssToken> out) {
        out.accept(new CssToken(CssTokenType.TT_FUNCTION, LINEAR_GRADIENT_FUNCTION));
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
            } else if (startX == 0.0 && endX == 1.0 && startY == endY) {
                out.accept(new CssToken(CssTokenType.TT_IDENT, "to"));
                out.accept(new CssToken(CssTokenType.TT_S, " "));
                out.accept(new CssToken(CssTokenType.TT_IDENT, "right"));
                needsComma = true;
                needsFromTo = false;
            } else if (startX == 0.0 && startY == 0.0 & endX == 1.0 && endY == 1.0) {
                out.accept(new CssToken(CssTokenType.TT_IDENT, "to"));
                out.accept(new CssToken(CssTokenType.TT_S, " "));
                out.accept(new CssToken(CssTokenType.TT_IDENT, "right"));
                out.accept(new CssToken(CssTokenType.TT_S, " "));
                out.accept(new CssToken(CssTokenType.TT_IDENT, "bottom"));
                needsComma = true;
                needsFromTo = false;
            } else if (startX == endX && startY == 1.0 && endY == 0.0) {
                out.accept(new CssToken(CssTokenType.TT_IDENT, "to"));
                out.accept(new CssToken(CssTokenType.TT_S, " "));
                out.accept(new CssToken(CssTokenType.TT_IDENT, "top"));
                needsComma = true;
                needsFromTo = false;
            } else if (startX == 0.0 && startY == 1. && endX == 0.0 && endY == 1.0) {// invalid
                needsFromTo = true;
            } else if (startX == 0.0 && startY == 1.0 && endX == 1.0 && endY == 0.0) {
                out.accept(new CssToken(CssTokenType.TT_IDENT, "to"));
                out.accept(new CssToken(CssTokenType.TT_S, " "));
                out.accept(new CssToken(CssTokenType.TT_IDENT, "right"));
                out.accept(new CssToken(CssTokenType.TT_S, " "));
                out.accept(new CssToken(CssTokenType.TT_IDENT, "top"));
                needsComma = true;
                needsFromTo = false;
            } else if (startX == 1.0 && endX == 0.0 && endY == startY) {
                out.accept(new CssToken(CssTokenType.TT_IDENT, "to"));
                out.accept(new CssToken(CssTokenType.TT_S, " "));
                out.accept(new CssToken(CssTokenType.TT_IDENT, "left"));
                needsComma = true;
                needsFromTo = false;
            } else if (startX == 1.0 && startY == 0.0 && endX == 0.0 && endY == 1.0) {
                out.accept(new CssToken(CssTokenType.TT_IDENT, "to"));
                out.accept(new CssToken(CssTokenType.TT_S, " "));
                out.accept(new CssToken(CssTokenType.TT_IDENT, "left"));
                out.accept(new CssToken(CssTokenType.TT_S, " "));
                out.accept(new CssToken(CssTokenType.TT_IDENT, "bottom"));
                needsComma = true;
                needsFromTo = false;
            } else if (startX == 1.0 && startY == 0.0 && endX == 1.0 && endY == 0.0) {//invalid
                needsFromTo = true;
            } else if (startX == 1.0 && startY == 0.0 && endX == 1.0 && endY == 1.0) {
                //  the gradient direction defaults to 'to bottom'.
                needsFromTo = false;
            } else if (startX == 1.0 && startY == 1.0 && endX == 0.0 && endY == 0.0) {
                out.accept(new CssToken(CssTokenType.TT_IDENT, "to"));
                out.accept(new CssToken(CssTokenType.TT_S, " "));
                out.accept(new CssToken(CssTokenType.TT_IDENT, "left"));
                out.accept(new CssToken(CssTokenType.TT_S, " "));
                out.accept(new CssToken(CssTokenType.TT_IDENT, "top"));
                needsComma = true;
                needsFromTo = false;
            } else if (startX == 1.0 && startY == 1.0 && endX == 1.0 && endY == 1.0) {//invalid
                needsFromTo = true;
            } else {
                needsFromTo = true;
            }
        } else {
            needsFromTo = true;
        }
        if (needsFromTo) {
            {
                out.accept(new CssToken(CssTokenType.TT_IDENT, "from"));
                out.accept(new CssToken(CssTokenType.TT_S, " "));
                if (proportional) {
                    out.accept(new CssToken(CssTokenType.TT_PERCENTAGE, startX * 100));
                    out.accept(new CssToken(CssTokenType.TT_S, " "));
                    out.accept(new CssToken(CssTokenType.TT_PERCENTAGE, startY * 100));
                    out.accept(new CssToken(CssTokenType.TT_S, " "));
                } else {
                    out.accept(new CssToken(CssTokenType.TT_NUMBER, startX));
                    out.accept(new CssToken(CssTokenType.TT_S, " "));
                    out.accept(new CssToken(CssTokenType.TT_NUMBER, startY));
                }
                needsSpace = true;
                needsComma = true;
            }
            {
                if (needsSpace) {
                    out.accept(new CssToken(CssTokenType.TT_S, " "));
                }
                out.accept(new CssToken(CssTokenType.TT_IDENT, "to"));
                out.accept(new CssToken(CssTokenType.TT_S, " "));
                if (proportional) {
                    out.accept(new CssToken(CssTokenType.TT_PERCENTAGE, endX * 100));
                    out.accept(new CssToken(CssTokenType.TT_S, " "));
                    out.accept(new CssToken(CssTokenType.TT_PERCENTAGE, endY * 100));
                    out.accept(new CssToken(CssTokenType.TT_S, " "));
                } else {
                    out.accept(new CssToken(CssTokenType.TT_NUMBER, endX));
                    out.accept(new CssToken(CssTokenType.TT_S, " "));
                    out.accept(new CssToken(CssTokenType.TT_NUMBER, endY));
                }
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
                        out.accept(new CssToken(CssTokenType.TT_COMMA));
                        out.accept(new CssToken(CssTokenType.TT_S, " "));
                    }
                    out.accept(new CssToken(CssTokenType.TT_IDENT, "repeat"));
                    needsComma = true;
                    break;
                case REFLECT:
                    if (needsComma) {
                        out.accept(new CssToken(CssTokenType.TT_COMMA));
                        out.accept(new CssToken(CssTokenType.TT_S, " "));
                    }
                    out.accept(new CssToken(CssTokenType.TT_IDENT, "reflect"));
                    needsComma = true;
                    break;
                default:
                    throw new UnsupportedOperationException("not yet implemented");
            }
            for (CssStop stop : lg.getStops()) {
                if (needsComma) {
                    out.accept(new CssToken(CssTokenType.TT_COMMA));
                    out.accept(new CssToken(CssTokenType.TT_S, " "));
                }
                colorConverter.produceTokens(stop.getColor(), idSupplier, out);
                if (stop.getOffset() != null) {
                    out.accept(new CssToken(CssTokenType.TT_S, " "));
                    out.accept(new CssToken(CssTokenType.TT_PERCENTAGE, stop.getOffset() * 100.0));
                }
                needsComma = true;
            }
        }
        out.accept(new CssToken(CssTokenType.TT_RIGHT_BRACKET));
    }

    @NonNull
    @Override
    public SvgLinearGradient parseNonNull(@NonNull CssTokenizer tt, @Nullable IdResolver idResolver) throws ParseException, IOException {
        tt.requireNextToken(CssTokenType.TT_FUNCTION, "⟨LinearGradient⟩: \"linear-gradient(\"  expected");
        switch (tt.currentStringNonNull()) {
        case LINEAR_GRADIENT_FUNCTION:
            break;
        default:
            throw new ParseException("⟨LinearGradient⟩: \"linear-gradient\" expected, found: " + tt.currentString(), tt.getStartPosition());
        }

        PointToPoint fromTo;

        // parse [from point to point] | [to sideOrCorner]
        if (tt.next() == CssTokenType.TT_IDENT && "from".equals(tt.currentString())) {
            fromTo = parsePointToPoint(tt);
        } else if (tt.current() == CssTokenType.TT_IDENT && "to".equals(tt.currentString())) {
            fromTo = parseSideOrCorner(tt);
        } else {
            fromTo = new PointToPoint(0.0, 0.0, 0.0, 1.0, true);
            tt.pushBack();
        }
        if (tt.next() != ',') {
            tt.pushBack();
        }

        CycleMethod cycleMethod = CycleMethod.NO_CYCLE;
        if (tt.next() == CssTokenType.TT_IDENT) {
            if ("repeat".equals(tt.currentString())) {
                cycleMethod = CycleMethod.REPEAT;
            } else if ("reflect".equals(tt.currentString())) {
                cycleMethod = CycleMethod.REFLECT;
            } else {
                tt.pushBack();
            }
        } else {
            tt.pushBack();
        }

        if (tt.next() != ',') {
            tt.pushBack();
        }
        List<CssStop> stops = new ArrayList<>();
        while (tt.next() != ')' && tt.current() != CssTokenType.TT_EOF) {
            tt.pushBack();
            stops.add(parseColorStop(tt));
            if (tt.next() != ',') {
                tt.pushBack();
            }
        }

        if (tt.current() != ')') {
            throw new ParseException("CSS LinearGradient: ')'  expected, found: " + tt.currentString(), tt.getStartPosition());
        }

        return new SvgLinearGradient(fromTo.startX, fromTo.startY, fromTo.endX, fromTo.endY, fromTo.isProportional, cycleMethod, stops.toArray(new CssStop[stops.size()]));
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

    @NonNull
    private PointToPoint parsePointToPoint(@NonNull CssTokenizer tt) throws IOException, ParseException {
        double startX, startY, endX, endY;
        Boolean isProportional = null;
        switch (tt.next()) {
            case CssTokenType.TT_NUMBER:
                startX = tt.currentNumberNonNull().doubleValue();
                isProportional = false;
                break;
            case CssTokenType.TT_PERCENTAGE:
                isProportional = true;
                startX = tt.currentNumberNonNull().doubleValue() / 100.0;
                break;
            case CssTokenType.TT_DIMENSION:
                isProportional = false;
                if (!"px".equals(tt.currentString())) {
                    throw new ParseException("CSS LinearGradient: start-x given in pixels or percentage expected, found: " + tt.currentString() + " ttype:" + tt.current(), tt.getStartPosition());
                }
                startX = tt.currentNumberNonNull().doubleValue();
                break;
            default:
                throw new ParseException("CSS LinearGradient: start-x expected, found: " + tt.currentString() + " ttype:" + tt.current(), tt.getStartPosition());
        }
        switch (tt.next()) {
            case CssTokenType.TT_NUMBER:
                if (isProportional) {
                    throw new ParseException("CSS LinearGradient: start-y as percentage value expected, found: " + tt.currentString() + " ttype:" + tt.current(), tt.getStartPosition());
                }
                startY = tt.currentNumberNonNull().doubleValue();
                break;
            case CssTokenType.TT_PERCENTAGE:
                if (!isProportional) {
                    throw new ParseException("CSS LinearGradient: start-y as absolute value expected, found: " + tt.currentString() + " ttype:" + tt.current(), tt.getStartPosition());
                }
                startY = tt.currentNumberNonNull().doubleValue() / 100.0;
                break;
            case CssTokenType.TT_DIMENSION:
                if (isProportional) {
                    throw new ParseException("CSS LinearGradient: start-y as percentage value expected, found: " + tt.currentString() + " ttype:" + tt.current(), tt.getStartPosition());
                }
                if (!"px".equals(tt.currentString())) {
                    throw new ParseException("CSS LinearGradient: start-y given in pixels or percentage expected, found: " + tt.currentString() + " ttype:" + tt.current(), tt.getStartPosition());
                }
                startY = tt.currentNumberNonNull().doubleValue();
                break;
            default:
                throw new ParseException("CSS LinearGradient: start-y expected, found: " + tt.currentString() + " ttype:" + tt.current(), tt.getStartPosition());
        }
        if (tt.next() == CssTokenType.TT_IDENT && "to".equals(tt.currentString())) {
            switch (tt.next()) {
                case CssTokenType.TT_NUMBER:
                    if (isProportional) {
                        throw new ParseException("CSS LinearGradient: end-x as percentage value expected, found: " + tt.currentString() + " ttype:" + tt.current(), tt.getStartPosition());
                    }
                    endX = tt.currentNumberNonNull().doubleValue();
                    break;
                case CssTokenType.TT_PERCENTAGE:
                    if (!isProportional) {
                        throw new ParseException("CSS LinearGradient: end-x as absolute value expected, found: " + tt.currentString() + " ttype:" + tt.current(), tt.getStartPosition());
                    }
                    endX = tt.currentNumberNonNull().doubleValue() / 100.0;
                    break;
                case CssTokenType.TT_DIMENSION:
                    if (isProportional) {
                        throw new ParseException("CSS LinearGradient: end-x as percentage value expected, found: " + tt.currentString() + " ttype:" + tt.current(), tt.getStartPosition());
                    }
                    if (!"px".equals(tt.currentString())) {
                        throw new ParseException("CSS LinearGradient: end-x given in pixels or percentage expected, found: " + tt.currentString() + " ttype:" + tt.current(), tt.getStartPosition());
                    }
                    endX = tt.currentNumberNonNull().doubleValue();
                    break;
                default:
                    throw new ParseException("CSS LinearGradient: end-x expected, found: " + tt.currentString() + " ttype:" + tt.current(), tt.getStartPosition());
            }
            switch (tt.next()) {
                case CssTokenType.TT_NUMBER:
                    if (isProportional) {
                        throw new ParseException("CSS LinearGradient: end-y as proportional value expected, found: " + tt.currentString() + " ttype:" + tt.current(), tt.getStartPosition());
                    }
                    endY = tt.currentNumberNonNull().doubleValue();
                    break;
                case CssTokenType.TT_PERCENTAGE:
                    if (!isProportional) {
                        throw new ParseException("CSS LinearGradient: end-y as absolute value expected, found: " + tt.currentString() + " ttype:" + tt.current(), tt.getStartPosition());
                    }
                    endY = tt.currentNumberNonNull().doubleValue() / 100.0;
                    break;
                case CssTokenType.TT_DIMENSION:
                    if (isProportional) {
                        throw new ParseException("CSS LinearGradient: end-y as proportional value expected, found: " + tt.currentString() + " ttype:" + tt.current(), tt.getStartPosition());
                    }
                    if (!"px".equals(tt.currentString())) {
                        throw new ParseException("CSS LinearGradient: end-y given in pixels or percent expected, found: " + tt.currentString() + " ttype:" + tt.current(), tt.getStartPosition());
                    }
                    endY = tt.currentNumberNonNull().doubleValue();
                    break;
                default:
                    throw new ParseException("CSS LinearGradient: end-y expected, found: " + tt.currentString() + " ttype:" + tt.current(), tt.getStartPosition());
            }
        } else {
            throw new ParseException("CSS LinearGradient: 'to' expected, found: " + tt.currentString() + " ttype:" + tt.current(), tt.getStartPosition());

        }
        return new PointToPoint(startX, startY, endX, endY, isProportional);
    }

    @NonNull
    private PointToPoint parseSideOrCorner(@NonNull CssTokenizer tt) throws IOException, ParseException {
        double startX = 0.0, startY = 0.0, endX = 0.0, endY = 1.0;
        Boolean isProportional = true;
        String h = null;
        String v = null;
        while (tt.next() == CssTokenType.TT_IDENT) {
            switch (tt.currentString()) {
                case "top":
                    if (v != null) {
                        throw new ParseException("CSS LinearGradient: you already specified '" + v + "', found: " + tt.currentString() + " ttype:" + tt.current(), tt.getStartPosition());
                    }
                    v = tt.currentString();
                    break;
                case "bottom":
                    if (v != null) {
                        throw new ParseException("CSS LinearGradient: you already specified '" + v + "', found: " + tt.currentString() + " ttype:" + tt.current(), tt.getStartPosition());
                    }
                    v = tt.currentString();
                    break;
                case "left":
                    if (h != null) {
                        throw new ParseException("CSS LinearGradient: you already specified '" + h + "', found: " + tt.currentString() + " ttype:" + tt.current(), tt.getStartPosition());
                    }
                    h = tt.currentString();
                    break;
                case "right":
                    if (h != null) {
                        throw new ParseException("CSS LinearGradient: you already specified '" + h + "', found: " + tt.currentString() + " ttype:" + tt.current(), tt.getStartPosition());
                    }
                    h = tt.currentString();
                    break;
                default:
                    throw new ParseException("CSS LinearGradient: 'top', 'bottom', 'left' or 'right' expected, found: " + tt.currentString() + " ttype:" + tt.current(), tt.getStartPosition());
            }
        }
        tt.pushBack();
        switch (h + " " + v) {
            case "null null":
                throw new ParseException("CSS LinearGradient: 'top', 'bottom', 'left' or 'right' expected after 'to'.", tt.getStartPosition());
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

    @NonNull
    private CssStop parseColorStop(@NonNull CssTokenizer tt) throws IOException, ParseException {
        CssColor color = colorConverter.parse(tt, null);
        Double offset = null;
        switch (tt.next()) {
            case CssTokenType.TT_NUMBER:
                offset = tt.currentNumberNonNull().doubleValue();
                break;
            case CssTokenType.TT_PERCENTAGE:
                offset = tt.currentNumberNonNull().doubleValue() / 100.0;
                break;
            default:
                tt.pushBack();
        }
        return new CssStop(offset, color);
    }

    @NonNull
    @Override
    public String getHelpText() {
        return "Format of ⟨LinearGradient⟩: linear-gradient(［⟨LinearGradientParameters⟩］［,⟨Cycle⟩］,⟨ColorStop⟩｛,⟨ColorStop⟩｝)"
                + "\nFormat of ⟨LinearGradientParameters⟩: ⟨PointToPoint⟩｜⟨SideOrCorners⟩"
                + "\nFormat of ⟨PointToPoint⟩: from ⟨x1⟩,⟨y1⟩ to ⟨x2⟩,⟨y2⟩｜from ⟨x1⟩%,⟨y1⟩% to ⟨x2⟩%,⟨y2⟩%"
                + "\nFormat of ⟨SideOrCorners⟩: to（left｜right｜top｜bottom）｜to（left｜right）（top｜bottom）"
                + "\nFormat of ⟨Cycle⟩: repeat｜reflect"
                + "\nFormat of ⟨ColorStop⟩: ⟨Color⟩ ⟨percentage⟩%"
                + "\n" + colorConverter.getHelpText();
    }
}
