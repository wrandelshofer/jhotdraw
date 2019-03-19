/* @(#)WordListConverter.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableSet;
import org.jhotdraw8.collection.ImmutableSets;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.io.IdFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * CssSetConverter.
 *
 * @author Werner Randelshofer
 * @version $Id$
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
    public ImmutableSet<T> parse(@Nonnull CssTokenizer tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        ArrayList<T> list = new ArrayList<>();
        do {
            T elem = elementConverter.parse(tt, idFactory);
            if (elem != null) {
                list.add(elem);
            }
        } while (tt.next() == CssTokenType.TT_COMMA || tt.current() == CssTokenType.TT_S);
        tt.pushBack();
        return ImmutableSets.ofCollection(list);
    }

    @Override
    public <TT extends ImmutableSet<T>> void produceTokens(TT value, @Nullable IdFactory idFactory, @Nonnull Consumer<CssToken> out) {
        if (value.isEmpty()) {
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
                elementConverter.produceTokens(elem, idFactory, out);
            }
        }
    }

    @Nullable
    @Override
    public ImmutableSet<T> getDefaultValue() {
        return ImmutableSets.emptySet();
    }

    @Override
    public String getHelpText() {
        return "Format of ⟨Set⟩: none | ⟨Item⟩, ⟨Item⟩, ...\n"
                + "With ⟨Item⟩:\n  " + elementConverter.getHelpText();
    }
}
