/*
 * @(#)CssIntegerConverter.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.io.IdFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.function.Consumer;

/**
 * CssIntegerConverter.
 * <p>
 * Parses an attribute value of type integer.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssIntegerConverter extends AbstractCssConverter<Integer> {

    public CssIntegerConverter(boolean nullable) {
        super(nullable);
    }

    @Nonnull
    @Override
    public Integer parseNonnull(@Nonnull CssTokenizer tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        switch (tt.next()) {
            case CssTokenType.TT_NUMBER:
                return tt.currentNumberNonnull().intValue();
            default:
                throw new ParseException("⟨Integer⟩: integer expected.", tt.getStartPosition());
        }
    }

    @Override
    public <TT extends Integer> void produceTokensNonnull(@Nonnull TT value, @Nullable IdFactory idFactory, @Nonnull Consumer<CssToken> out) {
        out.accept(new CssToken(CssTokenType.TT_NUMBER, value));
    }

    @Nonnull
    @Override
    public String getHelpText() {
        return "Format of ⟨Integer⟩: ⟨integer⟩";
    }

}
