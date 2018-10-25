/* @(#)CssScale3DConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import java.util.function.Consumer;

import javafx.geometry.Point3D;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.PatternConverter;

/**
 * Converts a {@code javafx.geometry.Point2D} into a {@code String} and vice
 * versa.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssScale3DConverter extends AbstractCssConverter<Point3D> {

    public CssScale3DConverter(boolean nullable) {
        super(nullable);
    }


    @Nonnull
    @Override
    public Point3D parseNonnull(@Nonnull CssTokenizer tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        final double x, y, z;
        tt.requireNextToken(CssTokenType.TT_NUMBER, " ⟨Translate3D⟩: ⟨x⟩ expected.");
        x = tt.currentNumberNonnull().doubleValue();
        tt.skipIfPresent(CssTokenType.TT_COMMA);
        if (tt.next() == CssTokenType.TT_NUMBER) {
            y = tt.currentNumberNonnull().doubleValue();
            tt.skipIfPresent(CssTokenType.TT_COMMA);
            if (tt.next() == CssTokenType.TT_NUMBER) {
                z = tt.currentNumberNonnull().doubleValue();
            } else {
                tt.pushBack();
                z = 1;
            }
        } else {
            tt.pushBack();
            y = x;
            z = x;
        }
        return new Point3D(x, y, z);
    }

    @Override
    protected <TT extends Point3D> void produceTokensNonnull(@Nonnull TT value, @Nullable IdFactory idFactory, @Nonnull Consumer<CssToken> out) {
        if (value.getZ() == 1.0) {
            if (value.getX() == value.getY()) {
                out.accept(new CssToken(CssTokenType.TT_NUMBER, value.getX()));
            } else {
                out.accept(new CssToken(CssTokenType.TT_NUMBER, value.getX()));
                out.accept(new CssToken(CssTokenType.TT_S, " "));
                out.accept(new CssToken(CssTokenType.TT_NUMBER, value.getY()));
            }
        } else {
            out.accept(new CssToken(CssTokenType.TT_NUMBER, value.getX()));
            out.accept(new CssToken(CssTokenType.TT_S, " "));
            out.accept(new CssToken(CssTokenType.TT_NUMBER, value.getY()));
            out.accept(new CssToken(CssTokenType.TT_S, " "));
            out.accept(new CssToken(CssTokenType.TT_NUMBER, value.getZ()));
        }
    }

    @Nonnull
    @Override
    public Point3D getDefaultValue() {
        return new Point3D(1, 1, 1);
    }

    @Nonnull
    @Override
    public String getHelpText() {
        return "Format of ⟨Scale3D⟩: ⟨s⟩ ｜ ⟨xs⟩ ⟨ys⟩ ｜ ⟨xs⟩ ⟨ys⟩ ⟨zs⟩";
    }

}
