/*
 * @(#)SvgDefaultablePaintConverter.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.svg.css.text;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.Paintable;
import org.jhotdraw8.css.text.CssConverter;
import org.jhotdraw8.io.IdResolver;
import org.jhotdraw8.io.IdSupplier;
import org.jhotdraw8.svg.css.SvgDefaultablePaint;
import org.jhotdraw8.svg.css.SvgPaintDefaulting;

import java.io.IOException;
import java.text.ParseException;
import java.util.function.Consumer;

public class SvgDefaultablePaintConverter<T extends Paintable> implements CssConverter<SvgDefaultablePaint<T>> {


    public static final String CURRENT_COLOR = "currentColor";
    private final CssConverter<T> valueConverter;

    public SvgDefaultablePaintConverter(CssConverter<T> valueConverter) {

        this.valueConverter = valueConverter;
    }


    @Override
    public @Nullable SvgDefaultablePaint<T> parse(@NonNull CssTokenizer tt, @Nullable IdResolver idResolver) throws ParseException, IOException {
        if (tt.next() == CssTokenType.TT_IDENT) {
            switch (tt.currentStringNonNull()) {
            case CssTokenType.IDENT_INHERIT:
                return new SvgDefaultablePaint<>(SvgPaintDefaulting.INHERIT, null);
            case CURRENT_COLOR:
                return new SvgDefaultablePaint<>(SvgPaintDefaulting.CURRENT_COLOR, null);
            }
        }
        tt.pushBack();
        return new SvgDefaultablePaint<>(null, valueConverter.parse(tt, idResolver));
    }

    @Override
    public <TT extends SvgDefaultablePaint<T>> void produceTokens(@Nullable TT value, @Nullable IdSupplier idSupplier, @NonNull Consumer<CssToken> out) throws IOException {
        if (value == null) {
            out.accept(new CssToken(CssTokenType.TT_IDENT, CssTokenType.IDENT_NONE));
            return;
        }
        SvgPaintDefaulting defaulting = value.getDefaulting();
        if (defaulting != null) {
            switch (defaulting) {
            case CURRENT_COLOR:
                out.accept(new CssToken(CssTokenType.TT_IDENT, CURRENT_COLOR));
                break;
            case INHERIT:
                out.accept(new CssToken(CssTokenType.TT_IDENT, CssTokenType.IDENT_INHERIT));
                break;
            }
            return;
        }
        valueConverter.produceTokens(value.getValue(), idSupplier, out);
    }

    @Override
    public @Nullable SvgDefaultablePaint<T> getDefaultValue() {
        return new SvgDefaultablePaint<>(SvgPaintDefaulting.INHERIT, null);
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
