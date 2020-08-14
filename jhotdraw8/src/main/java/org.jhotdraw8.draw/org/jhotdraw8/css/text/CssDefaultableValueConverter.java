/*
 * @(#)CssDefaultableValueConverter.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.css.text;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssDefaultableValue;
import org.jhotdraw8.css.CssDefaulting;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.io.IdResolver;
import org.jhotdraw8.io.IdSupplier;

import java.io.IOException;
import java.text.ParseException;
import java.util.function.Consumer;

public class CssDefaultableValueConverter<T> implements CssConverter<CssDefaultableValue<T>> {


    private final CssConverter<T> valueConverter;

    public CssDefaultableValueConverter(CssConverter<T> valueConverter) {

        this.valueConverter = valueConverter;
    }


    @Override
    public @Nullable CssDefaultableValue<T> parse(@NonNull CssTokenizer tt, @Nullable IdResolver idResolver) throws ParseException, IOException {
        if (tt.next() == CssTokenType.TT_IDENT) {
            switch (tt.currentStringNonNull()) {
            case CssTokenType.IDENT_INHERIT:
                return new CssDefaultableValue<>(CssDefaulting.INHERIT, null);
            case CssTokenType.IDENT_REVERT:
                return new CssDefaultableValue<>(CssDefaulting.REVERT, null);
            case CssTokenType.IDENT_INITIAL:
                return new CssDefaultableValue<>(CssDefaulting.INITIAL, null);
            case CssTokenType.IDENT_UNSET:
                return new CssDefaultableValue<>(CssDefaulting.UNSET, null);
            }
        }
        tt.pushBack();
        return new CssDefaultableValue<>(null, valueConverter.parse(tt, idResolver));
    }

    @Override
    public <TT extends CssDefaultableValue<T>> void produceTokens(@Nullable TT value, @Nullable IdSupplier idSupplier, @NonNull Consumer<CssToken> out) throws IOException {
        if (value == null) {
            out.accept(new CssToken(CssTokenType.TT_IDENT, CssTokenType.IDENT_NONE));
            return;
        }
        CssDefaulting defaulting = value.getDefaulting();
        if (defaulting != null) {
            switch (defaulting) {
            case INITIAL:
                out.accept(new CssToken(CssTokenType.TT_IDENT, CssTokenType.IDENT_INITIAL));
                break;
            case INHERIT:
                out.accept(new CssToken(CssTokenType.TT_IDENT, CssTokenType.IDENT_INHERIT));
                break;
            case UNSET:
                out.accept(new CssToken(CssTokenType.TT_IDENT, CssTokenType.IDENT_UNSET));
                break;
            case REVERT:
                out.accept(new CssToken(CssTokenType.TT_IDENT, CssTokenType.IDENT_REVERT));
                break;
            }
            return;
        }
        valueConverter.produceTokens(value.getValue(), idSupplier, out);
    }

    @Override
    public @Nullable CssDefaultableValue<T> getDefaultValue() {
        return new CssDefaultableValue<>(CssDefaulting.INHERIT, null);
    }

    @Override
    public @Nullable String getHelpText() {
        return "Format of ⟨DefaultableValue⟩: ⟨Value⟩｜" + CssTokenType.IDENT_INHERIT + "｜" + CssTokenType.IDENT_INITIAL + "｜" + CssTokenType.IDENT_UNSET + "｜" + CssTokenType.IDENT_REVERT + "｜" + "\n"
                + "With ⟨Value⟩:\n  " + valueConverter.getHelpText();
    }

    @Override
    public boolean isNullable() {
        return valueConverter.isNullable();
    }
}
