/* @(#)GeneralSiblingCombinator.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

import javax.annotation.Nonnull;
import org.jhotdraw8.css.SelectorModel;

/**
 * An "generarl sibling combinator" matches an element if its first selector
 * matches on a previous sibling of the element and if its second selector
 * matches the element.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class GeneralSiblingCombinator extends Combinator {

    public GeneralSiblingCombinator(SimpleSelector simpleSelector, Selector selector) {
        super(simpleSelector, selector);
    }

    @Nonnull
    @Override
    public String toString() {
        return firstSelector + " ~ " + secondSelector;
    }

    @Override
    public <T> T match(@Nonnull SelectorModel<T> model, T element) {
        T result = secondSelector.match(model, element);
        T siblingElement = result;
        while (siblingElement != null) {
            siblingElement = model.getPreviousSibling(siblingElement);
            result = firstSelector.match(model, siblingElement);
            if (result != null) {
                break;
            }
        }
        return result;
    }

    @Override
    public final int getSpecificity() {
        return firstSelector.getSpecificity() + secondSelector.getSpecificity();
    }
}
