/* @(#)CssRectangle2DConverterOLD.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.io.IdFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.text.ParseException;
import java.util.function.Consumer;

/**
 * Converts a {@code javafx.geometry.CssPoint2D} into a {@code String} and vice
 * versa.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssPoint2DConverter extends AbstractCssConverter<CssPoint2D> {
    private final boolean withSpace;
    private final boolean withComma;

    public CssPoint2DConverter(boolean nullable) {
        this(nullable, true, false);
    }

    public CssPoint2DConverter(boolean nullable, boolean withSpace, boolean withComma) {
        super(nullable);
        this.withSpace = withSpace || !withComma;
        this.withComma = withComma;
    }

    @Nonnull
    @Override
    public CssPoint2D parseNonnull(@Nonnull CssTokenizer tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        final CssSize x, y;
        x = parseDimension(tt,"x");
        tt.skipIfPresent(CssTokenType.TT_COMMA);
        y = parseDimension(tt,"y");

        return new CssPoint2D(x, y);
    }

    private CssSize parseDimension(CssTokenizer tt, String variable) throws ParseException, IOException {
        switch (tt.next()) {
            case CssTokenType.TT_NUMBER:
                return new CssSize(tt.currentNumber().doubleValue(),null);
            case CssTokenType.TT_DIMENSION:
                return new CssSize(tt.currentNumber().doubleValue(),tt.currentString());
            default:
                throw new ParseException(" ⟨CssPoint2D⟩: ⟨"+variable+"⟩ expected.",tt.getStartPosition());
        }
    }

    @Override
    protected <TT extends CssPoint2D> void produceTokensNonnull(@Nonnull TT value, @Nullable IdFactory idFactory, @Nonnull Consumer<CssToken> out) {
        CssSize x = value.getX();
        out.accept(new CssToken(CssTokenType.TT_DIMENSION, x.getUnits(),x.getValue()));
        if (withComma) {
            out.accept(new CssToken(CssTokenType.TT_COMMA));
        }
        if (withSpace) {
            out.accept(new CssToken(CssTokenType.TT_S, " "));
        }
        CssSize y = value.getY();
        out.accept(new CssToken(CssTokenType.TT_DIMENSION, y.getUnits(),y.getValue()));
    }

    @Override
    public String getHelpText() {
        return "Format of ⟨CssPoint2D⟩: ⟨x⟩ ⟨y⟩";
    }
}
