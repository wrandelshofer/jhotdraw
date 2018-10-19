/* @(#)WordListConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import java.io.IOException;
import java.text.ParseException;
import java.util.function.Consumer;

import javafx.css.PseudoClass;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizerAPI;
import org.jhotdraw8.css.ast.Token;
import org.jhotdraw8.css.text.AbstractCssConverter;
import org.jhotdraw8.io.IdFactory;

/**
 * WordSetConverter converts an ImmutableObservableSet of Strings into a
 * String.
 * <p>
 The word list is actually a "set of space separated tokens", as specified in
 HTML 5 and in XML Schema Part 2.
 <p>
 * The word list converter coalesces duplicate entries if they have the same
 * Unicode NFD form. The tokens are sorted using their Unicode NFD form.
 * <p>
 * References:
 * <ul>
 * <li><a href="https://dev.w3.org/html5/spec-preview/common-microsyntaxes.html#set-of-space-separated-tokens">
 * HTML 5, Common Microsyntaxes, Space-separated tokens
 * </a></li>
 * <li><a href="https://www.w3.org/TR/xmlschema-2/#token">
 * XML Schema Part 2, Built-in datatypes, Derived datatypes, Token
 * </a></li>
 * </ul>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssPseudoClassConverter extends AbstractCssConverter<PseudoClass> {

    public CssPseudoClassConverter(boolean nullable) {
        super(nullable);
    }

    @Nonnull
    @Override
    public PseudoClass parseNonnull(@Nonnull CssTokenizerAPI tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        tt.requireNextToken(CssTokenType.TT_IDENT," ⟨PseudoClass⟩: ⟨identifier⟩ expected");
        return PseudoClass.getPseudoClass(tt.currentStringNonnull());
    }



    @Override
    protected <TT extends PseudoClass> void produceTokensNonnull(@Nonnull TT value, @Nullable IdFactory idFactory, @Nonnull Consumer<Token> out) {
        out.accept(new Token(CssTokenType.TT_IDENT,value.getPseudoClassName()));
    }

    @Override
    public String getHelpText() {
        return "Format of ⟨PseudoClass⟩: ⟨identifier⟩";
    }
}
