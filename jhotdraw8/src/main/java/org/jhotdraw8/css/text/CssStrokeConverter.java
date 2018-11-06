package org.jhotdraw8.css.text;

import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.CssStroke;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.Paintable;
import org.jhotdraw8.io.IdFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CssStrokeConverter extends AbstractCssConverter<CssStroke> {
    public CssStrokeConverter(boolean nullable) {
        super(nullable);
    }

    @Nonnull
    @Override
    public CssStroke parseNonnull(@Nonnull CssTokenizer tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        CssSize width=                parseSize("width",new CssSize(1.0),tt,idFactory);
        Paintable paint = new CssPaintableConverter(true).parse(tt, idFactory);

        StrokeType type;
        switch (tt.next()) {
            case CssTokenType.TT_IDENT: {
                switch (tt.currentStringNonnull()) {
                    case "inside":
                        type = StrokeType.INSIDE;
                        break;
                    case "outside":
                        type = StrokeType.OUTSIDE;
                        break;
                    case "centered":
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
                    case "square":
                        lineCap = StrokeLineCap.SQUARE;
                        break;
                    case "butt":
                        lineCap = StrokeLineCap.BUTT;
                        break;
                    case "round":
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
                    case "miter":
                        lineJoin = StrokeLineJoin.MITER;
                        break;
                    case "bevel":
                        lineJoin = StrokeLineJoin.BEVEL;
                        break;
                    case "round":
                        lineJoin = StrokeLineJoin.ROUND;
                        break;
                    default:
                        lineJoin = StrokeLineJoin.BEVEL;
                        tt.pushBack();
                        break;
                }
                break;
            }
            default:
                lineJoin = StrokeLineJoin.BEVEL;
                tt.pushBack();
        }
        CssSize miterLimit = parseNumericFunction("miter-limit", new CssSize(10), tt, idFactory);
        CssSize dashOffset = parseNumericFunction("dash-offset", new CssSize(10), tt, idFactory);
        ImmutableList<CssSize> dashArray = parseDashArray(tt, idFactory);

        return new CssStroke(width,paint,type, lineCap, lineJoin, miterLimit,dashOffset,dashArray);
    }

    private ImmutableList<CssSize> parseDashArray(@Nonnull CssTokenizer tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        List<CssSize> list = new ArrayList<>();
        return ImmutableList.ofCollection(list);
    }

    private CssSize parseNumericFunction(String functionName, CssSize defaultValue, @Nonnull CssTokenizer tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        CssSize value;
        if (tt.next() == CssTokenType.TT_FUNCTION) {
            if (functionName.equals(tt.currentStringNonnull())) {
                value=parseSize(functionName,defaultValue,tt,idFactory);
                tt.requireNextToken(CssTokenType.TT_RIGHT_BRACKET, "right bracket expected.");
            } else {
                value = defaultValue;
                tt.pushBack();
            }
        } else {
            value=defaultValue;
            tt.pushBack();
        }
        return value;
    }

    private CssSize parseSize(String name, CssSize defaultValue,CssTokenizer tt, IdFactory idFactory)  throws ParseException, IOException{
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
       CssSize width=value.getWidth();
       if (width.getConvertedValue()!=1.0) {
           out.accept(new CssToken(CssTokenType.TT_DIMENSION,width.getUnits(),width.getValue()));
       }
       new CssPaintableConverter(true).produceTokens(value.getPaint(),idFactory,out);
       // FIXME implement the rest
    }
}
