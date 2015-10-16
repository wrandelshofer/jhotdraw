/*
 * @(#)AbstractAttributeSelector.java
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
public class AttributeValueContainsWordSelector extends AbstractAttributeSelector {

    private final String attributeName;
    private final String word;

    public AttributeValueContainsWordSelector(String attributeName, String word) {
        this.attributeName = attributeName;
        this.word = word;
    }

    @Override
    protected <T> T match(SelectorModel<T> model, T element) {
        return model.attributeValueContainsWord(element, attributeName, word) ? element : null;
    }

}
