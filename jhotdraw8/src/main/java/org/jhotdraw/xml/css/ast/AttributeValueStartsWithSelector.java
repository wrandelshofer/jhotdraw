/*
 * @(#)AbstractAttributeSelector.java
 * Copyright (c) 2014 Supercomputing Systems AG, Schweiz.
 * Alle Rechte vorbehalten. 
 */
package org.jhotdraw.xml.css.ast;

import org.jhotdraw.xml.css.SelectorModel;

/**
 * An "attribute value selector" matches an element if the element has an
 * attribute with the specified name and its value is either exactly the
 * specified value or its value begins with the specified value immediately
 * followed by a dash '-' character. This is primarily intended to allow
 * language subcode matches.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class AttributeValueStartsWithSelector extends AbstractAttributeSelector {

    private final String attributeName;
    private final String prefix;

    public AttributeValueStartsWithSelector(String attributeName, String prefix) {
        this.attributeName = attributeName;
        this.prefix = prefix;
    }

    @Override
    protected <T> T match(SelectorModel<T> model, T element) {
        return model.attributeValueStartsWith(element, attributeName, prefix) ? element : null;
    }

}
