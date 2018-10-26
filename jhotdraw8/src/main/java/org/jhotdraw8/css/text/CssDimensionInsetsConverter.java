/* @(#)CssPoint2DConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.io.IdFactory;

/**
 * Converts a {@link CssDimensionInsets} into a {@code String} and vice versa.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssDimensionInsetsConverter extends AbstractCssConverter<CssDimensionInsets> {
    public CssDimensionInsetsConverter(boolean nullable) {
        super(nullable);
    }

    @Nonnull
    @Override
    public CssDimensionInsets parseNonnull(@Nonnull CssTokenizer tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        List<CssDimension> list=new ArrayList<>(4);
        for (int i=0;i<4;i++) {
            switch (tt.next()) {
                case CssTokenType.TT_NUMBER:
                    list.add(new CssDimension(tt.currentNumberNonnull().doubleValue(),null));
                    break;
                case CssTokenType.TT_DIMENSION:
                    list.add(new CssDimension(tt.currentNumberNonnull().doubleValue(),tt.currentString()));
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
                CssDimension trbl=list.get(0);
                return new CssDimensionInsets(trbl,trbl,trbl,trbl);
            case 2:
                CssDimension tb=list.get(0);
                CssDimension rl=list.get(1);
                return new CssDimensionInsets(tb,rl,tb,rl);
            case 4:
                CssDimension t=list.get(0);
                CssDimension r=list.get(1);
                CssDimension b=list.get(2);
                CssDimension l=list.get(3);
                return new CssDimensionInsets(t,r,b,l);
            default:
                throw new ParseException("⟨DimensionInsets⟩: ⟨top-right-bottom-left⟩ ｜ ⟨top-bottom⟩,⟨left-right⟩ ｜ ⟨top⟩,⟨right⟩,⟨bottom⟩,⟨left⟩ expected.",tt.getStartPosition());

        }
    }
    @Nonnull
    @Override
    public String getHelpText() {
        return "Format of ⟨DimensionInsets⟩: ⟨top-right-bottom-left⟩ ｜ ⟨top-bottom⟩ ⟨left-right⟩ ｜ ⟨top⟩ ⟨right⟩ ⟨bottom⟩ ⟨left⟩";
    }

    @Override
    protected <TT extends CssDimensionInsets> void produceTokensNonnull(@Nonnull TT value, @Nullable IdFactory idFactory, @Nonnull Consumer<CssToken> out) {
        CssDimension top = value.getTop();
        CssDimension right = value.getRight();
        CssDimension bottom = value.getBottom();
        CssDimension left = value.getLeft();
        if (right == left) {
            if (top == bottom) {
                if (top == left) {
                    out.accept(new CssToken(CssTokenType.TT_DIMENSION,top.getUnits(), top.getValue()));
                    return;
                } else {
                    out.accept(new CssToken(CssTokenType.TT_DIMENSION, top.getUnits(),top.getValue()));
                    out.accept(new CssToken(CssTokenType.TT_S," "));
                    out.accept(new CssToken(CssTokenType.TT_DIMENSION, right.getUnits(),right.getValue()));
                    return;
                }
            }
        }
        out.accept(new CssToken(CssTokenType.TT_DIMENSION, top.getUnits(),top.getValue()));
        out.accept(new CssToken(CssTokenType.TT_S," "));
        out.accept(new CssToken(CssTokenType.TT_DIMENSION, right.getUnits(),right.getValue()));
        out.accept(new CssToken(CssTokenType.TT_S," "));
        out.accept(new CssToken(CssTokenType.TT_DIMENSION, bottom.getUnits(),bottom.getValue()));
        out.accept(new CssToken(CssTokenType.TT_S," "));
        out.accept(new CssToken(CssTokenType.TT_DIMENSION, left.getUnits(),left.getValue()));
    }
}



