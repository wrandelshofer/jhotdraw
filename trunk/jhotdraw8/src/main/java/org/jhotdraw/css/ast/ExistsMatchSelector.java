/* @(#)AbstractAttributeSelector.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.css.ast;

import org.jhotdraw.css.SelectorModel;

/**
 * An "exists match" matches an element if the element has an attribute
 * with the specified name.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ExistsMatchSelector extends AbstractAttributeSelector {

    private final String attributeName;

    public ExistsMatchSelector(String attributeName) {
        this.attributeName = attributeName;
    }

    @Override
    protected <T> T match(SelectorModel<T> model, T element) {
        return model.hasAttribute(element, attributeName) ? element : null;
    }

    @Override
    public String toString() {
        return "[" + attributeName + ']';
    }

}
