/*
 * @(#)AttributeSelector.java
 * Copyright (c) 2014 Supercomputing Systems AG, Schweiz.
 * Alle Rechte vorbehalten. 
 */
package org.jhotdraw.xml.css.ast;

import org.jhotdraw.xml.css.SelectorModel;

/**
 * An "attribute word list selector" matches an element if the element has an
 * attribute with the specified name and the attribute value contains a word
 * list with the specified word.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class AttributeWordListItemSelector extends AttributeSelector {

    private final String attributeName;
    private final String attributeValue;

    public AttributeWordListItemSelector(String attributeName, String attributeValue) {
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
    }

    @Override
    protected <T> T match(SelectorModel<T> model, T element) {
        String value = model.getAttribute(element, attributeName);
        if (value != null) {
            String[] words = value.split("\\s+");
            for (int i = 0; i < words.length; i++) {
                if (attributeValue.equals(words[i])) {
                    return element;
                }
            }
        }
        return null;
    }

}
