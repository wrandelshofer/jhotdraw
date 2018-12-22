/* @(#)Point2DConverter.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import java.io.IOException;
import java.text.ParseException;
import java.util.function.Consumer;

import javafx.geometry.Point3D;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;

import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.io.IdFactory;

/**
 * Converts a {@code javafx.geometry.Point3D} into a {@code String} and vice
 * versa.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class Point3DConverter extends AbstractCssConverter<Point3D> {

    public Point3DConverter(boolean nullable) {
        super(nullable);
    }

    @Nonnull
    @Override
    public String getHelpText() {
        return "Format of ⟨Point3D⟩: ⟨x⟩, ⟨y⟩, ⟨z⟩";
    }

    @Nonnull
    @Override
    public Point3D parseNonnull(@Nonnull CssTokenizer tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        final double x, y, z;
        tt.requireNextToken(CssTokenType.TT_NUMBER, " ⟨Point3D⟩: ⟨x⟩ expected.");
        x = tt.currentNumberNonnull().doubleValue();
        tt.skipIfPresent(CssTokenType.TT_COMMA);
        tt.requireNextToken(CssTokenType.TT_NUMBER, " ⟨Point3D⟩: ⟨y⟩ expected.");
        y = tt.currentNumberNonnull().doubleValue();
        tt.skipIfPresent(CssTokenType.TT_COMMA);
        if (tt.next() == CssTokenType.TT_NUMBER) {
            z = tt.currentNumberNonnull().doubleValue();
        } else {
            tt.pushBack();
            z = 0;
        }

        return new Point3D(x, y, z);
    }

    @Override
    protected <TT extends Point3D> void produceTokensNonnull(@Nonnull TT value, @Nullable IdFactory idFactory, @Nonnull Consumer<CssToken> out) {
        out.accept(new CssToken(CssTokenType.TT_NUMBER, value.getX()));
        out.accept(new CssToken(CssTokenType.TT_COMMA));
        out.accept(new CssToken(CssTokenType.TT_S, " "));
        out.accept(new CssToken(CssTokenType.TT_NUMBER, value.getY()));
        double z = value.getZ();
        if (z!=0.0) {
            out.accept(new CssToken(CssTokenType.TT_COMMA));
            out.accept(new CssToken(CssTokenType.TT_S, " "));
            out.accept(new CssToken(CssTokenType.TT_NUMBER, z));
        }
    }
}
