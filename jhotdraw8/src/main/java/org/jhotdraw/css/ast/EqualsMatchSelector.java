/* @(#)EqualsMatchSelector.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.css.ast;

import org.jhotdraw.css.SelectorModel;

/**
 * An "attribute value selector" matches an element if the element has an
 * attribute with the specified name and value.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class EqualsMatchSelector extends AbstractAttributeSelector {

    private final String attributeName;
    private final String attributeValue;

    public EqualsMatchSelector(String attributeName, String attributeValue) {
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
    }

    @Override
    protected <T> T match(SelectorModel<T> model, T element) {
        return model.attributeValueEquals(element, attributeName, attributeValue) ? element : null;
    }

    @Override
    public String toString() {
        return "[" + attributeName + "=" + attributeValue + ']';
    }
}
