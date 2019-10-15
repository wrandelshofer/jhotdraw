/*
 * @(#)CssStrokeStyleConverter.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.CssStroke;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.Paintable;
import org.jhotdraw8.css.UnitConverter;
import org.jhotdraw8.io.IdFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Allows to set all stroke properties at once.
 * <pre>
 *     StrokeStyle := [width], [ Paint, {Options} ] ;
 *     width := Size ;
 *     Paint := Color | Gradient ;
 *     Options = ( Type | Linecap | Linejoin | Miterlimit | Dashoffset | Dasharray );
 *     Type = "type(" , ("inside"|"outside"|"centered"), ")";
 *     Linecap = "linecap(",("square"|"butt"|"round"),")";
 *     Linejoin = "linecap(",("miter"|"bevel"|"round"),")";
 *     Miterlimit = "miterlimit(",Size,")";
 *     Dashoffset = "dashoffset(",Size,")";
 *     Dasharray = "dasharray(",Size,{Size},")";
 * </pre>
 */
public class CssStrokeStyleConverter extends AbstractCssConverter<CssStroke> {

    public static final String INSIDE = "inside";
    public static final String OUTSIDE = "outside";
    public static final String CENTERED = "centered";
    public static final String BUTT = "butt";
    public static final String MITER = "miter";
    public static final String ROUND = "round";
    public static final String BEVEL = "bevel";
    public static final String SQUARE = "square";
    public static final String TYPE = "type";
    public static final String LINEJOIN = "linejoin";
    public static final String LINECAP = "linecap";
    public static final String DASHOFFSET = "dashoffset";
    public static final String DASHARRAY = "dasharray";
    public static final String MITERLIMIT = "miterlimit";
    private boolean printAllValues = true;

    public CssStrokeStyleConverter(boolean nullable) {
        super(nullable);
    }

    @Nonnull
    @Override
    public CssStroke parseNonnull(@Nonnull CssTokenizer tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        CssSize width = parseSize("width", new CssSize(1.0), tt, idFactory);
        if (tt.next() == CssTokenType.TT_EOF) {
            return new CssStroke(width, CssColor.BLACK);
        } else {
            tt.pushBack();
        }
        Paintable paint = new CssPaintableConverter(true).parse(tt, idFactory);

        StrokeType type = StrokeType.CENTERED;
        StrokeLineCap lineCap = StrokeLineCap.BUTT;
        StrokeLineJoin lineJoin = StrokeLineJoin.MITER;
        CssSize miterLimit = new CssSize(4);
        CssSize dashOffset = new CssSize(0);
        ImmutableList<CssSize> dashArray = ImmutableLists.emptyList();

        while (tt.next() == CssTokenType.TT_FUNCTION) {
            tt.pushBack();
            switch (tt.currentStringNonnull()) {
                case TYPE:
                    type = parseStrokeType(tt);
                    break;
                case LINECAP:
                    lineCap = parseLineCap(tt);
                    break;
                case LINEJOIN:
                    lineJoin = parseLineJoin(tt);
                    break;
                case MITERLIMIT:
                    miterLimit = parseNumericFunction(MITERLIMIT, new CssSize(10), tt, idFactory);
                    break;
                case DASHOFFSET:
                    dashOffset = parseNumericFunction(DASHOFFSET, new CssSize(0), tt, idFactory);
                    break;
                case DASHARRAY:
                    dashArray = parseDashArray(tt, idFactory);
                    break;
                default:
                    throw new ParseException("⟨StrokeStyle⟩:: Unsupported function: " + tt.currentStringNonnull(), tt.getStartPosition());
            }
        }

        return new CssStroke(width, paint, type, lineCap, lineJoin, miterLimit, dashOffset, dashArray);
    }

    @Nonnull
    protected StrokeLineJoin parseLineJoin(@Nonnull CssTokenizer tt) throws ParseException, IOException {
        if (tt.next() != CssTokenType.TT_FUNCTION || !LINEJOIN.equals(tt.currentStringNonnull())) {
            throw new ParseException("⟨StrokeStyle⟩:: Function " + LINEJOIN + "() expected.", tt.getStartPosition());
        }
        StrokeLineJoin lineJoin;
        tt.requireNextToken(CssTokenType.TT_IDENT, "⟨StrokeStyle⟩: One of " + MITER + ", " + BEVEL + ", " + ROUND + " expected.");
        switch (tt.currentStringNonnull()) {
            case MITER:
                lineJoin = StrokeLineJoin.MITER;
                break;
            case BEVEL:
                lineJoin = StrokeLineJoin.BEVEL;
                break;
            case ROUND:
                lineJoin = StrokeLineJoin.ROUND;
                break;
            default:
                throw new ParseException("⟨StrokeStyle⟩:One of " + MITER + ", " + BEVEL + ", " + ROUND + " expected.", tt.getStartPosition());
        }
        tt.requireNextToken(CssTokenType.TT_RIGHT_BRACKET, "⟨StrokeStyle⟩:: ⟨" + LINEJOIN + "⟩ right bracket expected.");
        return lineJoin;
    }

    @Nonnull
    protected StrokeLineCap parseLineCap(@Nonnull CssTokenizer tt) throws ParseException, IOException {
        if (tt.next() != CssTokenType.TT_FUNCTION || !LINECAP.equals(tt.currentStringNonnull())) {
            throw new ParseException("⟨StrokeStyle⟩:: Function " + LINECAP + "() expected.", tt.getStartPosition());
        }
        StrokeLineCap lineCap;
        tt.requireNextToken(CssTokenType.TT_IDENT, "⟨StrokeStyle⟩: One of " + SQUARE + ", " + BUTT + ", " + ROUND + " expected.");
        switch (tt.currentStringNonnull()) {
            case SQUARE:
                lineCap = StrokeLineCap.SQUARE;
                break;
            case BUTT:
                lineCap = StrokeLineCap.BUTT;
                break;
            case ROUND:
                lineCap = StrokeLineCap.ROUND;
                break;
            default:
                throw new ParseException("⟨StrokeStyle⟩: One of " + SQUARE + ", " + BUTT + ", " + ROUND + " expected.", tt.getStartPosition());
        }
        tt.requireNextToken(CssTokenType.TT_RIGHT_BRACKET, "⟨StrokeStyle⟩:: ⟨" + LINECAP + "⟩ right bracket expected.");
        return lineCap;
    }

    @Nonnull
    protected StrokeType parseStrokeType(@Nonnull CssTokenizer tt) throws ParseException, IOException {
        if (tt.next() != CssTokenType.TT_FUNCTION || !TYPE.equals(tt.currentStringNonnull())) {
            throw new ParseException("⟨StrokeStyle⟩:: Function " + TYPE + "() expected.", tt.getStartPosition());
        }
        StrokeType type;
        tt.requireNextToken(CssTokenType.TT_IDENT, "One of " + INSIDE + ", " + OUTSIDE + ", " + CENTERED + " expected.");
        switch (tt.currentStringNonnull()) {
            case INSIDE:
                type = StrokeType.INSIDE;
                break;
            case OUTSIDE:
                type = StrokeType.OUTSIDE;
                break;
            case CENTERED:
                type = StrokeType.CENTERED;
                break;
            default:
                throw new ParseException("One of " + INSIDE + ", " + OUTSIDE + ", " + CENTERED + " expected.", tt.getStartPosition());
        }
        tt.requireNextToken(CssTokenType.TT_RIGHT_BRACKET, "⟨StrokeStyle⟩:: ⟨" + TYPE + "⟩ right bracket expected.");
        return type;
    }

    private ImmutableList<CssSize> parseDashArray(@Nonnull CssTokenizer tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        if (tt.next() != CssTokenType.TT_FUNCTION || !DASHARRAY.equals(tt.currentStringNonnull())) {
            throw new ParseException("⟨StrokeStyle⟩: Function " + DASHARRAY + "() expected.", tt.getStartPosition());
        }

        List<CssSize> list = new ArrayList<>();
        while (tt.next() == CssTokenType.TT_NUMBER || tt.current() == CssTokenType.TT_DIMENSION) {
            tt.pushBack();
            list.add(parseSize(DASHARRAY, null, tt, idFactory));
            if (tt.next() != CssTokenType.TT_COMMA) {
                tt.pushBack();
            }
        }
        tt.pushBack();
        tt.requireNextToken(CssTokenType.TT_RIGHT_BRACKET, "⟨StrokeStyle⟩: ⟨" + DASHARRAY + "⟩ right bracket expected.");
        return ImmutableLists.ofCollection(list);
    }

    private CssSize parseNumericFunction(String functionName, CssSize defaultValue, @Nonnull CssTokenizer tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        if (tt.next() != CssTokenType.TT_FUNCTION || !functionName.equals(tt.currentStringNonnull())) {
            throw new ParseException("Function " + functionName + "() expected.", tt.getStartPosition());
        }

        CssSize value;
        value = parseSize(functionName, defaultValue, tt, idFactory);
        tt.requireNextToken(CssTokenType.TT_RIGHT_BRACKET, "⟨StrokeStyle⟩: ⟨" + functionName + "⟩ right bracket expected.");
        return value;
    }

    private CssSize parseSize(String name, CssSize defaultValue, CssTokenizer tt, IdFactory idFactory) throws ParseException, IOException {
        CssSize value;
        switch (tt.next()) {
            case CssTokenType.TT_NUMBER:
                value = new CssSize(tt.currentNumberNonnull().doubleValue());
                break;
            case CssTokenType.TT_DIMENSION:
                value = new CssSize(tt.currentNumberNonnull().doubleValue(), tt.currentStringNonnull());
                break;
            default:
                value = defaultValue;
                tt.pushBack();
        }
        return value;
    }

    @Override
    public String getHelpText() {
        return "Format of ⟨StrokeStyle⟩: ［⟨width⟩］⟨Paint⟩［⟨Type⟩］［⟨Linecap⟩］［⟨Linejoin⟩］［⟨Miterlimit⟩］［⟨Dashoffset⟩］［⟨Dasharray⟩］"
                + "\n  with ⟨width⟩: size"
                + "\n  with ⟨Paint⟩: ⟨Color⟩｜⟨Gradient⟩"
                + "\n  with ⟨Type⟩: " + TYPE + "(inside｜outside｜centered)"
                + "\n  with ⟨Linecap⟩: " + LINECAP + "(square｜butt｜round)"
                + "\n  with ⟨Linejoin⟩: " + LINEJOIN + "(miter｜bevel｜round)"
                + "\n  with ⟨Miterlimit⟩: " + MITERLIMIT + "(size)"
                + "\n  with ⟨Dashoffset⟩: " + DASHOFFSET + "(size)"
                + "\n  with ⟨Dasharray⟩: " + DASHARRAY + "(size...)"
                ;
    }


    @Override
    protected <TT extends CssStroke> void produceTokensNonnull(@Nonnull TT value, @Nullable IdFactory idFactory, @Nonnull Consumer<CssToken> out) {
        if (value.getPaint() == null) {
            out.accept(new CssToken(CssTokenType.TT_IDENT, CssTokenType.IDENT_NONE));
            return;
        }

        CssSize width = value.getWidth();
        out.accept(new CssToken(CssTokenType.TT_DIMENSION, width.getValue(), width.getUnits()));
        out.accept(new CssToken(CssTokenType.TT_S, " "));
        new CssPaintableConverter(true).produceTokens(value.getPaint(), idFactory, out);

        final StrokeType type = value.getType();
        if (printAllValues || type != StrokeType.CENTERED) {
            out.accept(new CssToken(CssTokenType.TT_S, " "));
            out.accept(new CssToken(CssTokenType.TT_FUNCTION, TYPE));
            switch (type) {
                case INSIDE:
                    out.accept(new CssToken(CssTokenType.TT_IDENT, INSIDE));
                    break;
                case OUTSIDE:
                    out.accept(new CssToken(CssTokenType.TT_IDENT, OUTSIDE));
                    break;
                case CENTERED:
                    out.accept(new CssToken(CssTokenType.TT_IDENT, CENTERED));
                    break;
            }
            out.accept(new CssToken(CssTokenType.TT_RIGHT_BRACKET));
        }

        final StrokeLineCap lineCap = value.getLineCap();

        if (printAllValues || lineCap != StrokeLineCap.BUTT) {
            out.accept(new CssToken(CssTokenType.TT_S, " "));
            out.accept(new CssToken(CssTokenType.TT_FUNCTION, LINECAP));
            switch (lineCap) {
                case BUTT:
                    out.accept(new CssToken(CssTokenType.TT_IDENT, BUTT));
                    break;
                case ROUND:
                    out.accept(new CssToken(CssTokenType.TT_IDENT, ROUND));
                    break;
                case SQUARE:
                    out.accept(new CssToken(CssTokenType.TT_IDENT, SQUARE));
                    break;
            }
            out.accept(new CssToken(CssTokenType.TT_RIGHT_BRACKET));
        }
        final StrokeLineJoin lineJoin = value.getLineJoin();
        if (printAllValues || lineJoin != StrokeLineJoin.MITER) {
            out.accept(new CssToken(CssTokenType.TT_S, " "));
            out.accept(new CssToken(CssTokenType.TT_FUNCTION, LINEJOIN));
            switch (lineJoin) {
                case BEVEL:
                    out.accept(new CssToken(CssTokenType.TT_IDENT, BUTT));
                    break;
                case ROUND:
                    out.accept(new CssToken(CssTokenType.TT_IDENT, ROUND));
                    break;
                case MITER:
                    out.accept(new CssToken(CssTokenType.TT_IDENT, MITER));
                    break;
            }
            out.accept(new CssToken(CssTokenType.TT_RIGHT_BRACKET));
        }

        CssSize miterLimit = value.getMiterLimit();
        if (printAllValues || !miterLimit.getUnits().equals(UnitConverter.DEFAULT) || miterLimit.getConvertedValue() != 4.0) {
            out.accept(new CssToken(CssTokenType.TT_S, " "));
            out.accept(new CssToken(CssTokenType.TT_FUNCTION, MITERLIMIT));
            out.accept(new CssToken(CssTokenType.TT_DIMENSION, miterLimit.getValue(), miterLimit.getUnits()));
            out.accept(new CssToken(CssTokenType.TT_RIGHT_BRACKET));
        }

        CssSize dashOffset = value.getDashOffset();
        if (printAllValues || !dashOffset.getUnits().equals(UnitConverter.DEFAULT) || dashOffset.getConvertedValue() != 0.0) {
            out.accept(new CssToken(CssTokenType.TT_S, " "));
            out.accept(new CssToken(CssTokenType.TT_FUNCTION, DASHOFFSET));
            out.accept(new CssToken(CssTokenType.TT_DIMENSION, dashOffset.getValue(), dashOffset.getUnits()));
            out.accept(new CssToken(CssTokenType.TT_RIGHT_BRACKET));
        }

        ImmutableList<CssSize> dashArray = value.getDashArray();
        if (printAllValues || !dashArray.isEmpty()) {
            out.accept(new CssToken(CssTokenType.TT_S, " "));
            out.accept(new CssToken(CssTokenType.TT_FUNCTION, DASHARRAY));
            for (int i = 0, n = dashArray.size(); i < n; i++) {
                if (i != 0) {
                    out.accept(new CssToken(CssTokenType.TT_S, " "));
                }
                CssSize dash = dashArray.get(i);
                out.accept(new CssToken(CssTokenType.TT_DIMENSION, dash.getValue(), dash.getUnits()));
            }
            out.accept(new CssToken(CssTokenType.TT_RIGHT_BRACKET));
        }
    }
}
