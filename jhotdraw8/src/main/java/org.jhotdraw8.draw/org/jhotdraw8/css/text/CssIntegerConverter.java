/*
 * @(#)CssIntegerConverter.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import org.jhotdraw8.annotation.NonNull;
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
 */
public class CssIntegerConverter extends AbstractCssConverter<Integer> {

    public CssIntegerConverter(boolean nullable) {
        super(nullable);
    }

    @NonNull
    @Override
    public Integer parseNonNull(@NonNull CssTokenizer tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        switch (tt.next()) {
            case CssTokenType.TT_NUMBER:
                return tt.currentNumberNonNull().intValue();
            default:
                throw new ParseException("⟨Integer⟩: integer expected.", tt.getStartPosition());
        }
    }

    @Override
    public <TT extends Integer> void produceTokensNonNull(@NonNull TT value, @Nullable IdFactory idFactory, @NonNull Consumer<CssToken> out) {
        out.accept(new CssToken(CssTokenType.TT_NUMBER, value));
    }

    @NonNull
    @Override
    public String getHelpText() {
        return "Format of ⟨Integer⟩: ⟨integer⟩";
    }

}
