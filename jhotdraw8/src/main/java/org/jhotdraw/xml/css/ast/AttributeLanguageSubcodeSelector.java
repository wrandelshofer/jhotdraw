/*
 * @(#)AttributeSelector.java
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
public class AttributeLanguageSubcodeSelector extends AttributeSelector {

    private final String attributeName;
    private final String attributeValue;

    public AttributeLanguageSubcodeSelector(String attributeName, String attributeValue) {
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
    }

    @Override
    protected <T> T match(SelectorModel<T> model, T element) {
        String value = model.getAttribute(element, attributeName);
         return (value != null && (attributeValue.equals(value)
                || value.startsWith(attributeValue + "-")))
                        ? element : null;
    }

}
