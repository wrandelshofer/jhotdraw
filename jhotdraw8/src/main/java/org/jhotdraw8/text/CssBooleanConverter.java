/* @(#)CssBooleanConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizerAPI;
import org.jhotdraw8.css.ast.Token;
import org.jhotdraw8.io.IdFactory;

/**
 * Converts a {@code Boolean} into the CSS String representation.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssBooleanConverter implements CssConverter<Boolean> {

    private static final long serialVersionUID = 1L;

    private final String trueString = "true";
    private final String falseString = "false";

    /**
     * Creates a new instance.
     */
    public CssBooleanConverter() {
    }

    @Nullable
    @Override
    public Boolean parse(@Nonnull CssTokenizerAPI tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        tt.requireNextToken(CssTokenType.TT_IDENT,"⟨Boolean⟩ identifier expected.");
        String s = tt.currentStringValue();
        if (s==null)s=CssTokenType.IDENT_NONE;
        switch (s) {
            case CssTokenType.IDENT_NONE:
                return null;
            case trueString:
                return Boolean.TRUE;
            case falseString:
                return Boolean.FALSE;
            default:
                throw new ParseException("⟨Boolean⟩ "+trueString+" or "+falseString+" expected.",tt.getStartPosition());
        }
    }

    @Override
    public void produceTokens(@Nullable Boolean value, @Nullable IdFactory idFactory, @Nonnull Consumer<Token> consumer) {
        if (value ==null) {
            consumer.accept(new Token(CssTokenType.TT_IDENT,CssTokenType.IDENT_NONE));
        }else
            consumer.accept(new Token(CssTokenType.TT_IDENT,value?trueString:falseString));
    }

    @Nonnull
    @Override
    public String getHelpText() {
        return "Format of ⟨Boolean⟩: true｜false";
    }

}
