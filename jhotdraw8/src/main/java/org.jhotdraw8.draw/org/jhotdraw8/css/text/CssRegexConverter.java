/*
 * @(#)CssRegexConverter.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.text.RegexReplace;

import java.io.IOException;
import java.text.ParseException;
import java.util.function.Consumer;

/**
 * CssRegexConverter.
 * <p>
 * Parses the following EBNF:
 * <pre>
 * RegexReplace := "none" | "replace(" Find  ","   [ Replace ] ")" ;
 * Find := TT_STRING;
 * Replace := TT_STRING;
 * </pre>
 *
 * @author Werner Randelshofer
 */
public class CssRegexConverter extends AbstractCssConverter<RegexReplace> {
    public CssRegexConverter(final boolean nullable) {
        super(nullable);
    }

    @NonNull
    @Override
    public RegexReplace parseNonNull(@NonNull CssTokenizer tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        tt.requireNextToken(CssTokenType.TT_FUNCTION, "⟨replace⟩: function expected.");
        if (!"replace".equals(tt.currentStringNonNull())) {
            throw tt.createParseException("⟨replace⟩: replace() function expected.");
        }
        tt.requireNextToken(CssTokenType.TT_STRING, "⟨replace⟩: find string expected.");
        String find = tt.currentStringNonNull();
        String replace;
        if (tt.next() != CssTokenType.TT_COMMA) {
            tt.pushBack();
        }
        if (tt.next() == CssTokenType.TT_STRING) {
            replace = tt.currentStringNonNull();
        } else {
            tt.pushBack();
            replace = null;
        }
        tt.requireNextToken(CssTokenType.TT_RIGHT_BRACKET, "⟨replace⟩: right bracket expected.");
        return new RegexReplace(find, replace);
    }

    @Override
    public @Nullable String getHelpText() {
        return "Format of ⟨replace⟩: none | replace(⟨Match⟩, ⟨Replace⟩)"
                + "\nFormat of ⟨Match⟩: \"match\""
                + "\nFormat of ⟨Replace⟩: \"replacement\"";
    }

    @Override
    protected <TT extends RegexReplace> void produceTokensNonNull(@NonNull TT value, @Nullable IdFactory idFactory, @NonNull Consumer<CssToken> out) {
        out.accept(new CssToken(CssTokenType.TT_FUNCTION, "replace"));
        String find = value.getFind();
        out.accept(new CssToken(CssTokenType.TT_STRING, find == null ? "" : find));
        out.accept(new CssToken(CssTokenType.TT_COMMA));
        String replace = value.getReplace();
        out.accept(new CssToken(CssTokenType.TT_STRING, replace == null ? "" : replace));
        out.accept(new CssToken(CssTokenType.TT_RIGHT_BRACKET));
    }

}
