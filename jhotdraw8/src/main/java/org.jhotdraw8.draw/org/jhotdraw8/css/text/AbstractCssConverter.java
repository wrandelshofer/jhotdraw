/* @(#)AbstractCssConverter.java
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

public abstract class AbstractCssConverter<T> implements CssConverter<T> {
    private final boolean nullable;

    public AbstractCssConverter(boolean nullable) {
        this.nullable = nullable;
    }


    @Nullable
    @Override
    public final T parse(@Nonnull CssTokenizer tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        if (tt.nextIsIdentNone()) {
            return null;
        }
        tt.pushBack();
        return parseNonnull(tt, idFactory);
    }

    @Override
    public final <TT extends T> void produceTokens(@Nullable TT value, @Nullable IdFactory idFactory, @Nonnull Consumer<CssToken> out) {
        if (value == null) {
            out.accept(new CssToken(CssTokenType.TT_IDENT, CssTokenType.IDENT_NONE));
        } else {
            produceTokensNonnull(value, idFactory, out);
        }
    }

    @Nonnull
    public abstract T parseNonnull(@Nonnull CssTokenizer tt, @Nullable IdFactory idFactory) throws ParseException, IOException;

    protected abstract <TT extends T> void produceTokensNonnull(@Nonnull TT value, @Nullable IdFactory idFactory, @Nonnull Consumer<CssToken> out);

    @Nullable
    @Override
    public T getDefaultValue() {
        return null;
    }

    public boolean isNullable() {
        return nullable;
    }
}
