/* @(#)SubstringMatchSelector.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.css.ast;

import org.jhotdraw.css.SelectorModel;

/**
 * A "substring match selector" {@code *=} matches an element if the element has
 * an attribute with the specified name and its value contains the specified
 * substring.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class SubstringMatchSelector extends AbstractAttributeSelector {

    private final String attributeName;
    private final String substring;

    public SubstringMatchSelector(String attributeName, String substring) {
        this.attributeName = attributeName;
        this.substring = substring;
    }

    @Override
    protected <T> T match(SelectorModel<T> model, T element) {
        return (model.attributeValueContains(element, attributeName, substring))//
                ? element : null;
    }
    @Override
    public String toString() {
        return "[" + attributeName + "*=" + substring + ']';
    }

}
