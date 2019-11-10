/*
 * @(#)CssBooleanConverter.java
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
 * Converts a {@code Boolean} into the CSS String representation.
 *
 * @author Werner Randelshofer
 */
public class CssBooleanConverter extends AbstractCssConverter<Boolean> {

    private static final long serialVersionUID = 1L;

    private final String trueString = "true";
    private final String falseString = "false";

    public CssBooleanConverter(boolean nullable) {
        super(nullable);
    }

    @NonNull
    @Override
    public Boolean parseNonNull(@NonNull CssTokenizer tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        tt.requireNextToken(CssTokenType.TT_IDENT, "⟨Boolean⟩ identifier expected.");
        String s = tt.currentStringNonNull();
        switch (s) {
            case trueString:
                return Boolean.TRUE;
            case falseString:
                return Boolean.FALSE;
            default:
                throw new ParseException("⟨Boolean⟩ " + trueString + " or " + falseString + " expected.", tt.getStartPosition());
        }
    }

    @Override
    public <TT extends Boolean> void produceTokensNonNull(@NonNull TT value, @Nullable IdFactory idFactory, @NonNull Consumer<CssToken> out) {
        out.accept(new CssToken(CssTokenType.TT_IDENT, value ? trueString : falseString));
    }

    @NonNull
    @Override
    public String getHelpText() {
        if (isNullable()) {
            return "Format of ⟨NullableBoolean⟩: none｜true｜false";
        } else {
            return "Format of ⟨Boolean⟩: true｜false";
        }
    }

}
