package org.jhotdraw8.css.text;

import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.css.*;
import org.jhotdraw8.io.IdFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CssStrokeConverter extends AbstractCssConverter<CssStroke> {

    public static final String INSIDE = "inside";
    public static final String OUTSIDE = "outside";
    public static final String CENTERED = "centered";
    public static final String BUTT = "butt";
    public static final String MITER = "miter";
    public static final String ROUND = "round";
    public static final String BEVEL = "bevel";
    public static final String SQUARE = "square";
    public static final String DASH_OFFSET = "dash-offset";
    public static final String DASH_ARRAY = "dash-array";
    public static final String MITER_LIMIT = "miter-limit";

    public CssStrokeConverter(boolean nullable) {
        super(nullable);
    }

    @Nonnull
    @Override
    public CssStroke parseNonnull(@Nonnull CssTokenizer tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        CssSize width = parseSize("width", new CssSize(1.0), tt, idFactory);
        Paintable paint = new CssPaintableConverter(true).parse(tt, idFactory);

        StrokeType type;
        switch (tt.next()) {
            case CssTokenType.TT_IDENT: {
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
                        type = StrokeType.CENTERED;
                        tt.pushBack();
                        break;
                }
                break;
            }
            default:
                type = StrokeType.CENTERED;
                tt.pushBack();
        }

        StrokeLineCap lineCap;
        switch (tt.next()) {
            case CssTokenType.TT_IDENT: {
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
                        lineCap = StrokeLineCap.BUTT;
                        tt.pushBack();
                        break;
                }
                break;
            }
            default:
                lineCap = StrokeLineCap.BUTT;
                tt.pushBack();
        }

        StrokeLineJoin lineJoin;
        switch (tt.next()) {
            case CssTokenType.TT_IDENT: {
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
                        lineJoin = StrokeLineJoin.MITER;
                        tt.pushBack();
                        break;
                }
                break;
            }
            default:
                lineJoin = StrokeLineJoin.MITER;
                tt.pushBack();
        }

        CssSize miterLimit = new CssSize(4);
        CssSize dashOffset = new CssSize(0);
        ImmutableList<CssSize> dashArray = ImmutableLists.emptyList();

        while (tt.next() == CssTokenType.TT_FUNCTION) {
            tt.pushBack();
            switch (tt.currentStringNonnull()) {
                case MITER_LIMIT:
                    miterLimit = parseNumericFunction(MITER_LIMIT, new CssSize(10), tt, idFactory);
                    break;
                case DASH_OFFSET:
                    dashOffset = parseNumericFunction(DASH_OFFSET, new CssSize(0), tt, idFactory);
                    break;
                case DASH_ARRAY:
                    dashArray = parseDashArray(tt, idFactory);
                    break;
                default:
                    throw new ParseException("⟨Stroke⟩: Unsupported function: " + tt.currentStringNonnull(), tt.getStartPosition());
            }
        }

        return new CssStroke(width, paint, type, lineCap, lineJoin, miterLimit, dashOffset, dashArray);
    }

    private ImmutableList<CssSize> parseDashArray(@Nonnull CssTokenizer tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        if (tt.next() == CssTokenType.TT_FUNCTION) {
            if (!DASH_ARRAY.equals(tt.currentStringNonnull())) {
                tt.pushBack();
                return ImmutableLists.emptyList();
            }
        } else {
            tt.pushBack();
            return ImmutableLists.emptyList();
        }
        List<CssSize> list = new ArrayList<>();
        while (tt.next() == CssTokenType.TT_NUMBER || tt.current() == CssTokenType.TT_DIMENSION) {
            tt.pushBack();
            list.add(parseSize(DASH_ARRAY, null, tt, idFactory));
            if (tt.next() != CssTokenType.TT_COMMA) {
                tt.pushBack();
            }
        }
        tt.pushBack();
        tt.requireNextToken(CssTokenType.TT_RIGHT_BRACKET, "⟨Stroke⟩: ⟨" + DASH_ARRAY + "⟩ right bracket expected.");
        return ImmutableLists.ofCollection(list);
    }

    private CssSize parseNumericFunction(String functionName, CssSize defaultValue, @Nonnull CssTokenizer tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        CssSize value;
        if (tt.next() == CssTokenType.TT_FUNCTION) {
            if (functionName.equals(tt.currentStringNonnull())) {
                value = parseSize(functionName, defaultValue, tt, idFactory);
                tt.requireNextToken(CssTokenType.TT_RIGHT_BRACKET, "⟨Stroke⟩: ⟨" + functionName + "⟩ right bracket expected.");
            } else {
                value = defaultValue;
                tt.pushBack();
            }
        } else {
            value = defaultValue;
            tt.pushBack();
        }
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
        return "Format of ⟨Stroke⟩: ［⟨width⟩］⟨Paint⟩［⟨Type⟩］［⟨LineCap⟩］［⟨LineJoin⟩］［⟨MiterLimit⟩］［⟨DashOffset⟩］［⟨DashArray⟩］"
                + "\n  with ⟨width⟩: size"
                + "\n  with ⟨Paint⟩: ⟨Color⟩｜⟨Gradient⟩"
                + "\n  with ⟨Type⟩: inside｜outside｜centered"
                + "\n  with ⟨LineCap⟩: square｜butt｜round"
                + "\n  with ⟨LineJoin⟩: miter｜bevel｜round"
                + "\n  with ⟨MiterLimit⟩: miter-limit(size)"
                + "\n  with ⟨DashOffset⟩: dash-offset(size)"
                + "\n  with ⟨DashArray⟩: dash-array(size...)"
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

        switch (value.getType()) {
            case INSIDE:
                out.accept(new CssToken(CssTokenType.TT_S, " "));
                out.accept(new CssToken(CssTokenType.TT_IDENT, INSIDE));
                break;
            case OUTSIDE:
                out.accept(new CssToken(CssTokenType.TT_S, " "));
                out.accept(new CssToken(CssTokenType.TT_IDENT, OUTSIDE));
                break;
            case CENTERED:
                // this is the default value
                break;
        }

        boolean mustPrintLineCap = value.getLineJoin() != StrokeLineJoin.MITER;
        boolean mustPrintLineJoin = value.getLineCap() != StrokeLineCap.BUTT;

        switch (value.getLineCap()) {
            case BUTT:
                if (mustPrintLineCap) {
                    out.accept(new CssToken(CssTokenType.TT_S, " "));
                    out.accept(new CssToken(CssTokenType.TT_IDENT, BUTT));
                }
                break;
            case ROUND:
                out.accept(new CssToken(CssTokenType.TT_S, " "));
                out.accept(new CssToken(CssTokenType.TT_IDENT, ROUND));
                break;
            case SQUARE:
                out.accept(new CssToken(CssTokenType.TT_S, " "));
                out.accept(new CssToken(CssTokenType.TT_IDENT, SQUARE));
                break;
        }
        switch (value.getLineJoin()) {
            case BEVEL:
                out.accept(new CssToken(CssTokenType.TT_S, " "));
                out.accept(new CssToken(CssTokenType.TT_IDENT, BUTT));
                break;
            case ROUND:
                out.accept(new CssToken(CssTokenType.TT_S, " "));
                out.accept(new CssToken(CssTokenType.TT_IDENT, ROUND));
                break;
            case MITER:
                if (mustPrintLineJoin) {
                    out.accept(new CssToken(CssTokenType.TT_S, " "));
                    out.accept(new CssToken(CssTokenType.TT_IDENT, MITER));
                }
                break;
        }

        CssSize miterLimit = value.getMiterLimit();
        if (!miterLimit.getUnits().equals(UnitConverter.DEFAULT) || miterLimit.getConvertedValue() != 4.0) {
            out.accept(new CssToken(CssTokenType.TT_S, " "));
            out.accept(new CssToken(CssTokenType.TT_FUNCTION, MITER_LIMIT));
            out.accept(new CssToken(CssTokenType.TT_DIMENSION, miterLimit.getValue(), miterLimit.getUnits()));
            out.accept(new CssToken(CssTokenType.TT_RIGHT_BRACKET));
        }

        CssSize dashOffset = value.getDashOffset();
        if (!dashOffset.getUnits().equals(UnitConverter.DEFAULT) || dashOffset.getConvertedValue() != 0.0) {
            out.accept(new CssToken(CssTokenType.TT_S, " "));
            out.accept(new CssToken(CssTokenType.TT_FUNCTION, DASH_OFFSET));
            out.accept(new CssToken(CssTokenType.TT_DIMENSION, dashOffset.getValue(), dashOffset.getUnits()));
            out.accept(new CssToken(CssTokenType.TT_RIGHT_BRACKET));
        }

        ImmutableList<CssSize> dashArray = value.getDashArray();
        if (!dashArray.isEmpty()) {
            out.accept(new CssToken(CssTokenType.TT_S, " "));
            out.accept(new CssToken(CssTokenType.TT_FUNCTION, DASH_ARRAY));
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
