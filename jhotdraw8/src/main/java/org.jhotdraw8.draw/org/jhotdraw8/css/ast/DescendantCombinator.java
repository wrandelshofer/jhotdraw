/*
 * @(#)DescendantCombinator.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.SelectorModel;

import java.util.function.Consumer;

/**
 * A "descendant combinator" matches an element if its first selector matches on
 * an ancestor of the element and if its second selector matches on the element
 * itself.
 *
 * @author Werner Randelshofer
 */
public class DescendantCombinator extends Combinator {

    public DescendantCombinator(SimpleSelector simpleSelector, Selector selector) {
        super(simpleSelector, selector);
    }

    @NonNull
    @Override
    public String toString() {
        return firstSelector + ".isAncestorOf(" + secondSelector + ")";
    }

    @Nullable
    @Override
    public <T> T match(@NonNull SelectorModel<T> model, T element) {
        T result = secondSelector.match(model, element);
        T siblingElement = result == null ? null : result;
        while (siblingElement != null) {
            siblingElement = model.getParent(siblingElement);
            result = firstSelector.match(model, siblingElement);
            if (result != null) {
                break;
            }
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
        consumer.accept(new CssToken(CssTokenType.TT_S, " "));
        secondSelector.produceTokens(consumer);
    }
}
