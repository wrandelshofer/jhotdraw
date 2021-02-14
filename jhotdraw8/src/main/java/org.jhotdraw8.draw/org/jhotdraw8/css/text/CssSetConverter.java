/*
 * @(#)CssSetConverter.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableSet;
import org.jhotdraw8.collection.ImmutableSets;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.io.IdResolver;
import org.jhotdraw8.io.IdSupplier;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * CssSetConverter.
 *
 * @author Werner Randelshofer
 */
public class CssSetConverter<T> implements CssConverter<ImmutableSet<T>> {
    private final CssConverter<T> elementConverter;
    private final boolean withComma;

    public CssSetConverter(CssConverter<T> elementConverter) {
        this(elementConverter, true);
    }

    public CssSetConverter(CssConverter<T> elementConverter, boolean withComma) {
        this.elementConverter = elementConverter;
        this.withComma = withComma;
    }


    @Override
    public ImmutableSet<T> parse(@NonNull CssTokenizer tt, @Nullable IdResolver idResolver) throws ParseException, IOException {
        ArrayList<T> list = new ArrayList<>();
        do {
            T elem = elementConverter.parse(tt, idResolver);
            if (elem != null) {
                list.add(elem);
            }
        } while (tt.next() == CssTokenType.TT_COMMA || tt.current() == CssTokenType.TT_S);
        tt.pushBack();
        return ImmutableSets.ofCollection(list);
    }

    @Override
    public <TT extends ImmutableSet<T>> void produceTokens(@Nullable TT value, @Nullable IdSupplier idSupplier, @NonNull Consumer<CssToken> out) throws IOException {
        if (value == null || value.isEmpty()) {
            out.accept(new CssToken(CssTokenType.TT_IDENT, CssTokenType.IDENT_NONE));
        } else {
            boolean first = true;
            for (T elem : value) {
                if (elem == null) {
                    continue;
                }
                if (first) {
                    first = false;
                } else {
                    if (withComma) {
                        out.accept(new CssToken(CssTokenType.TT_COMMA));
                    }
                    out.accept(new CssToken(CssTokenType.TT_S, " "));
                }
                elementConverter.produceTokens(elem, idSupplier, out);
            }
        }
    }

    @Override
    public @Nullable ImmutableSet<T> getDefaultValue() {
        return ImmutableSets.emptySet();
    }

    @Override
    public boolean isNullable() {
        return true;
    }

    @Override
    public String getHelpText() {
        return "Format of ⟨Set⟩: none | ⟨Item⟩, ⟨Item⟩, ...\n"
                + "With ⟨Item⟩:\n  " + elementConverter.getHelpText();
    }
}
