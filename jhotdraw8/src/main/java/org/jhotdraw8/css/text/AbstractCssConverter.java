/* @(#)AbstractCssConverter.java
 * Copyright © 2018 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizerAPI;
import org.jhotdraw8.css.ast.Token;
import org.jhotdraw8.io.IdFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.text.ParseException;
import java.util.function.Consumer;

public abstract class AbstractCssConverter<T> implements CssConverter<T> {
    private final boolean nullable;

    public AbstractCssConverter(boolean nullable) {
        this.nullable = nullable;
    }


    @Nullable
    @Override
    public final T parse(@Nonnull CssTokenizerAPI tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        tt.skipWhitespace();
        if (tt.nextTokenIsIdentNone()) {
            return null;
        }
        tt.pushBack();
        return parseNonnull(tt, idFactory);
    }

    @Override
    public final <TT extends T> void produceTokens(@Nullable TT value, @Nullable IdFactory idFactory, @Nonnull Consumer<Token> out) {
        if (value == null) {
            out.accept(new Token(CssTokenType.TT_IDENT, CssTokenType.IDENT_NONE));
        } else {
            produceTokensNonnull(value, idFactory, out);
        }
    }

    @Nonnull
    public abstract T parseNonnull(@Nonnull CssTokenizerAPI tt, @Nullable IdFactory idFactory) throws ParseException, IOException;

    protected abstract <TT extends T> void produceTokensNonnull(@Nonnull TT value, @Nullable IdFactory idFactory, @Nonnull Consumer<Token> out);

    @Nullable
    @Override
    public T getDefaultValue() {
        return null;
    }
}
