/*
 * @(#)CssInsetsConverter.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssInsets;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.io.IdResolver;
import org.jhotdraw8.io.IdSupplier;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.jhotdraw8.css.text.CssSizeConverter.parseSize;

/**
 * Converts a {@link CssInsets} object into a {@code String} and vice
 * versa.
 * <p>
 * List of four sizes in the sequence top, right, bottom, left. If left is
 * omitted, it is the same as right. If bottom is omitted, it is the same as
 * top. If right is omitted it is the same as top.
 * <pre>
 * insets       = top-right-bottom-left ;
 * insets       = top-bottom, right-left ;
 * insets       = top , right , bottom, left ;
 * </pre> *
 *
 * @author Werner Randelshofer
 */
public class CssInsetsConverter extends AbstractCssConverter<CssInsets> {
    public CssInsetsConverter(boolean nullable) {
        super(nullable);
    }

    @Override
    public @NonNull CssInsets parseNonNull(@NonNull CssTokenizer tt, @Nullable IdResolver idResolver) throws ParseException, IOException {
        List<CssSize> list = new ArrayList<>(4);
        for (int i = 0; i < 4; i++) {
            switch (tt.next()) {
            case CssTokenType.TT_NUMBER:
            case CssTokenType.TT_DIMENSION:
                tt.pushBack();
                list.add(parseSize(tt, i + ""));
                break;
            case CssTokenType.TT_COMMA:
                break;
            default:
                tt.pushBack();
                break;
            }
        }
        switch (list.size()) {
            case 1:
                CssSize trbl = list.get(0);
                return new CssInsets(trbl, trbl, trbl, trbl);
            case 2:
                CssSize tb = list.get(0);
                CssSize rl = list.get(1);
                return new CssInsets(tb, rl, tb, rl);
            case 4:
                CssSize t = list.get(0);
                CssSize r = list.get(1);
                CssSize b = list.get(2);
                CssSize l = list.get(3);
                return new CssInsets(t, r, b, l);
            default:
                throw new ParseException("⟨DimensionInsets⟩: ⟨top-right-bottom-left⟩ ｜ ⟨top-bottom⟩,⟨left-right⟩ ｜ ⟨top⟩,⟨right⟩,⟨bottom⟩,⟨left⟩ expected.", tt.getStartPosition());

        }
    }

    @Override
    public @NonNull String getHelpText() {
        return "Format of ⟨DimensionInsets⟩: ⟨top-right-bottom-left⟩ ｜ ⟨top-bottom⟩ ⟨left-right⟩ ｜ ⟨top⟩ ⟨right⟩ ⟨bottom⟩ ⟨left⟩";
    }

    @Override
    protected <TT extends CssInsets> void produceTokensNonNull(@NonNull TT value, @Nullable IdSupplier idSupplier, @NonNull Consumer<CssToken> out) {
        CssSize top = value.getTop();
        CssSize right = value.getRight();
        CssSize bottom = value.getBottom();
        CssSize left = value.getLeft();
        if (right == left) {
            if (top == bottom) {
                if (top == left) {
                    out.accept(new CssToken(CssTokenType.TT_DIMENSION, top.getValue(), top.getUnits()));
                    return;
                } else {
                    out.accept(new CssToken(CssTokenType.TT_DIMENSION, top.getValue(), top.getUnits()));
                    out.accept(new CssToken(CssTokenType.TT_S, " "));
                    out.accept(new CssToken(CssTokenType.TT_DIMENSION, right.getValue(), right.getUnits()));
                    return;
                }
            }
        }
        out.accept(new CssToken(CssTokenType.TT_DIMENSION, top.getValue(), top.getUnits()));
        out.accept(new CssToken(CssTokenType.TT_S, " "));
        out.accept(new CssToken(CssTokenType.TT_DIMENSION, right.getValue(), right.getUnits()));
        out.accept(new CssToken(CssTokenType.TT_S, " "));
        out.accept(new CssToken(CssTokenType.TT_DIMENSION, bottom.getValue(), bottom.getUnits()));
        out.accept(new CssToken(CssTokenType.TT_S, " "));
        out.accept(new CssToken(CssTokenType.TT_DIMENSION, left.getValue(), left.getUnits()));
    }
}



