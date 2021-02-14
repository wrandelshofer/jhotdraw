/*
 * @(#)SimplePseudoClassSelector.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.SelectorModel;

import java.util.function.Consumer;

/**
 * A "simple class selector" matches an element based on the value of its
 * "pseudo class" attribute.
 *
 * @author Werner Randelshofer
 */
public class SimplePseudoClassSelector extends PseudoClassSelector {

    private final String pseudoClass;

    public SimplePseudoClassSelector(String pseudoClass) {
        this.pseudoClass = pseudoClass;
    }

    @Override
    public @NonNull String toString() {
        return "PseudoClass:" + pseudoClass;
    }

    @Override
    public @Nullable <T> T match(@NonNull SelectorModel<T> model, @Nullable T element) {
        return (element != null && model.hasPseudoClass(element, pseudoClass)) //
                ? element : null;
    }

    @Override
    public void produceTokens(@NonNull Consumer<CssToken> consumer) {
        consumer.accept(new CssToken(CssTokenType.TT_COLON));
        consumer.accept(new CssToken(CssTokenType.TT_IDENT, pseudoClass));
    }

}
