/*
 * @(#)AdjacentSiblingCombinator.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.SelectorModel;

import java.util.function.Consumer;

/**
 * An "adjacent sibling combinator" matches an element if its first selector
 * matches on the adjacent sibling of the element and if its second selector
 * matches the element.
 *
 * @author Werner Randelshofer
 */
public class AdjacentSiblingCombinator extends Combinator {

    public AdjacentSiblingCombinator(SimpleSelector firstSelector, Selector secondSelector) {
        super(firstSelector, secondSelector);
    }

    @NonNull
    @Override
    public String toString() {
        return firstSelector + " + " + secondSelector;
    }

    @Nullable
    @Override
    public <T> T match(@NonNull SelectorModel<T> model, T element) {
        T result = secondSelector.match(model, element);
        if (result != null) {
            result = firstSelector.match(model, model.getPreviousSibling(result));
        }
        return result;
    }

    @Override
    public int getSpecificity() {
        return firstSelector.getSpecificity() + secondSelector.getSpecificity();
    }

    @Override
    public void produceTokens(@NonNull Consumer<CssToken> consumer) {
        firstSelector.produceTokens(consumer);
        consumer.accept(new CssToken(CssTokenType.TT_PLUS));
        secondSelector.produceTokens(consumer);
    }
}
