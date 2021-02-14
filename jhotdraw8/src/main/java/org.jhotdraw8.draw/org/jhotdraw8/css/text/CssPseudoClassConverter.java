/*
 * @(#)CssPseudoClassConverter.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import javafx.css.PseudoClass;
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

/**
 * WordSetConverter converts an ImmutableObservableSet of Strings into a
 * String.
 * <p>
 * The word list is actually a "set of space separated tokens", as specified in
 * HTML 5 and in XML Schema Part 2.
 * <p>
 * The word list converter coalesces duplicate entries if they have the same
 * Unicode NFD form. The tokens are sorted using their Unicode NFD form.
 * <p>
 * References:
 * <dl>
 *     <dt>HTML 5, Common Microsyntaxes, Space-separated tokens</dt>
 *     <dd><a href="https://dev.w3.org/html5/spec-preview/common-microsyntaxes.html#set-of-space-separated-tokens">w3.org</a></dd>
 *     <dt>XML Schema Part 2, Built-in datatypes, Derived datatypes, CssToken</dt>
 *     <dd><a href="https://www.w3.org/TR/xmlschema-2/#token">w3.org</a></dd>
 * </dl>
 * @author Werner Randelshofer
 */
public class CssPseudoClassConverter extends AbstractCssConverter<PseudoClass> {

    public CssPseudoClassConverter(boolean nullable) {
        super(nullable);
    }

    @Override
    public @NonNull PseudoClass parseNonNull(@NonNull CssTokenizer tt, @Nullable IdResolver idResolver) throws ParseException, IOException {
        tt.requireNextToken(CssTokenType.TT_IDENT, " ⟨PseudoClass⟩: ⟨identifier⟩ expected");
        return PseudoClass.getPseudoClass(tt.currentStringNonNull());
    }


    @Override
    protected <TT extends PseudoClass> void produceTokensNonNull(@NonNull TT value, @Nullable IdSupplier idSupplier, @NonNull Consumer<CssToken> out) {
        out.accept(new CssToken(CssTokenType.TT_IDENT, value.getPseudoClassName()));
    }

    @Override
    public String getHelpText() {
        return "Format of ⟨PseudoClass⟩: ⟨identifier⟩";
    }
}
