/*
 * @(#)FunctionPseudoClassSelector.java
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
 * A "class selector" matches an element based on the value of its "pseudo
 * class" attribute.
 *
 * @author Werner Randelshofer
 */
public class FunctionPseudoClassSelector extends PseudoClassSelector {

    private final String functionIdentifier;

    public FunctionPseudoClassSelector(String functionIdentifier) {
        this.functionIdentifier = functionIdentifier;
    }

    @NonNull
    @Override
    public String toString() {
        return "FunctionPseudoClass:" + functionIdentifier + "(" + ")";
    }

    @Nullable
    @Override
    public <T> T match(@NonNull SelectorModel<T> model, @Nullable T element) {
        return (element != null && model.hasPseudoClass(element, functionIdentifier)) //
                ? element : null;
    }

    public String getFunctionIdentifier() {
        return functionIdentifier;
    }

    @Override
    public void produceTokens(@NonNull Consumer<CssToken> consumer) {
        consumer.accept(new CssToken(CssTokenType.TT_COLON));
        consumer.accept(new CssToken(CssTokenType.TT_FUNCTION, functionIdentifier));
        consumer.accept(new CssToken(CssTokenType.TT_RIGHT_BRACKET));
    }
}
