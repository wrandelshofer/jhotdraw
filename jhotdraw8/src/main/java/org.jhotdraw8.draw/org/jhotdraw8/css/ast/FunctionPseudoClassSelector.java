/*
 * @(#)FunctionPseudoClassSelector.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.SelectorModel;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * A "class selector" matches an element based on the value of its "pseudo
 * class" attribute.
 *
 * @author Werner Randelshofer
 */
public class FunctionPseudoClassSelector extends PseudoClassSelector {

    private final String functionIdentifier;
    @Nonnull
    private final List<CssToken> terms;

    public FunctionPseudoClassSelector(String functionIdentifier, @Nonnull List<CssToken> terms) {
        this.functionIdentifier = functionIdentifier;
        this.terms = Collections.unmodifiableList(terms);
    }

    @Nonnull
    @Override
    public String toString() {
        return "FunctionPseudoClass:" + functionIdentifier;
    }

    @Nullable
    @Override
    public <T> T match(@Nonnull SelectorModel<T> model, @Nullable T element) {
        return (element != null && model.hasPseudoClass(element, functionIdentifier)) //
                ? element : null;
    }

    @Override
    public void produceTokens(Consumer<CssToken> consumer) {
        consumer.accept(new CssToken(CssTokenType.TT_COLON));
        consumer.accept(new CssToken(CssTokenType.TT_FUNCTION, functionIdentifier));
        consumer.accept(new CssToken(CssTokenType.TT_RIGHT_BRACKET));
    }
}
