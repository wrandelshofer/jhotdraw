/*
 * @(#)UniversalSelector.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.SelectorModel;

import java.util.function.Consumer;

/**
 * A "universal selector" matches an element if the element exists.
 *
 * @author Werner Randelshofer
 */
public class UniversalSelector extends SimpleSelector {

    @Nonnull
    @Override
    public String toString() {
        return "Universal:*";
    }

    @Nullable
    @Override
    public <T> T match(SelectorModel<T> model, T element) {
        return element;
    }

    @Override
    public int getSpecificity() {
        return 0;
    }

    @Override
    public void produceTokens(Consumer<CssToken> consumer) {
        consumer.accept(new CssToken(CssTokenType.TT_ASTERISK));
    }
}
