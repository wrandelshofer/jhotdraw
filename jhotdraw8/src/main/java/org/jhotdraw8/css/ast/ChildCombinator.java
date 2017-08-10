/* @(#)ChildCombinator.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

import org.jhotdraw8.css.SelectorModel;

/**
 * A "child combinator" matches an element if its first selector matches on the
 * parent of the element and if its second selector matches on the element
 * itself.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ChildCombinator extends Combinator {

    public ChildCombinator(SimpleSelector simpleSelector, Selector selector) {
        super(simpleSelector, selector);
    }

    @Override
    public String toString() {
        return "(" + firstSelector + " > " + secondSelector + ")";
    }

    @Override
    public <T> T match(SelectorModel<T> model, T element) {
        T result = secondSelector.match(model, element);
        if (result != null) {
            result = firstSelector.match(model, model.getParent(result));
        }
        return result;
    }

    @Override
    public int getSpecificity() {
        return firstSelector.getSpecificity() + secondSelector.getSpecificity();
    }

}
