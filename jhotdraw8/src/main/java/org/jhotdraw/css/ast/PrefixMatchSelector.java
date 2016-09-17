/* @(#)PrefixMatchSelector.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.css.ast;

import org.jhotdraw.css.SelectorModel;

/**
 * A "prefix match selector" {@code ^=} matches an element if the element has an
 * attribute with the specified name and its value starts with the specified 
 * substring.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class PrefixMatchSelector extends AbstractAttributeSelector {

    private final String attributeName;
    private final String substring;

    public PrefixMatchSelector(String attributeName, String substring) {
        this.attributeName = attributeName;
        this.substring = substring;
    }

    @Override
    protected <T> MatchResult<T> match(SelectorModel<T> model, T element) {
        return (model.attributeValueStartsWith(element, attributeName, substring))//
                        ? new MatchResult<>(element,this) : null;
    }
    @Override
    public String toString() {
        return "[" + attributeName + "^=" + substring + ']';
    }
}
