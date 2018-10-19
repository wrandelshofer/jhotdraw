/* @(#)WordListConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;
import org.jhotdraw8.collection.ImmutableSet;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizerAPI;
import org.jhotdraw8.css.ast.Token;
import org.jhotdraw8.css.text.CssConverter;
import org.jhotdraw8.io.IdFactory;

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
    public ImmutableSet<T> parse(@NotNull CssTokenizerAPI tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        ArrayList<T> list = new ArrayList<>();
        do {
            tt.skipWhitespace();
            T elem = elementConverter.parse(tt, idFactory);
            if (elem != null) {
                list.add(elem);
            }
            tt.setSkipWhitespaces(false);
        } while (tt.nextToken() == CssTokenType.TT_COMMA || tt.currentToken() == CssTokenType.TT_S);
        tt.pushBack();
        return ImmutableSet.ofCollection(list);
    }

    @Override
    public <TT extends ImmutableSet<T>> void produceTokens(TT value, @Nullable IdFactory idFactory, @NotNull Consumer<Token> out) {
        if (value.isEmpty()) {
            out.accept(new Token(CssTokenType.TT_IDENT, CssTokenType.IDENT_NONE));
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
                        out.accept(new Token(CssTokenType.TT_COMMA));
                    }
                    out.accept(new Token(CssTokenType.TT_S, " "));
                }
                elementConverter.produceTokens(elem, idFactory, out);
            }
        }
    }

    @Nullable
    @Override
    public ImmutableSet<T> getDefaultValue() {
        return ImmutableSet.emptySet();
    }

    @Override
    public String getHelpText() {
        return "Format of ⟨Set⟩: none | ⟨Item⟩, ⟨Item⟩, ...\n"
                + "With ⟨Item⟩:\n  " + elementConverter.getHelpText();
    }
}
