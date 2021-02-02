/*
 * @(#)AbstractCssConverter.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.io.IdResolver;
import org.jhotdraw8.io.IdSupplier;

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
    public final T parse(@NonNull CssTokenizer tt, @Nullable IdResolver idResolver) throws ParseException, IOException {
        if (isNullable()) {
            if (tt.nextIsIdentNone()) {
                return null;
            }
            tt.pushBack();
        }
        return parseNonNull(tt, idResolver);
    }

    @Override
    public final <TT extends T> void produceTokens(@Nullable TT value, @Nullable IdSupplier idSupplier, @NonNull Consumer<CssToken> out) throws IOException {
        if (value == null) {
            out.accept(new CssToken(CssTokenType.TT_IDENT, CssTokenType.IDENT_NONE));
        } else {
            produceTokensNonNull(value, idSupplier, out);
        }
    }

    @NonNull
    public abstract T parseNonNull(@NonNull CssTokenizer tt, @Nullable IdResolver idResolver) throws ParseException, IOException;

    protected abstract <TT extends T> void produceTokensNonNull(@NonNull TT value, @Nullable IdSupplier idSupplier, @NonNull Consumer<CssToken> out) throws IOException;

    @Nullable
    @Override
    public T getDefaultValue() {
        return null;
    }

    public boolean isNullable() {
        return nullable;
    }
}
