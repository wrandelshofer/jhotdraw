/*
 * @(#)AttributeValueEqualsSelector.java
 * Copyright (c) 2014 Supercomputing Systems AG, Schweiz.
 * Alle Rechte vorbehalten. 
 */
package org.jhotdraw.css.ast;

import org.jhotdraw.css.SelectorModel;

/**
 * An "attribute value selector" matches an element if the element has an
 * attribute with the specified name and value.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class AttributeValueEqualsSelector extends AbstractAttributeSelector {

    private final String attributeName;
    private final String attributeValue;

    public AttributeValueEqualsSelector(String attributeName, String attributeValue) {
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
    }

    @Override
    protected <T> T match(SelectorModel<T> model, T element) {
        return model.attributeValueEquals(element, attributeName, attributeValue) ? element : null;
    }
}
