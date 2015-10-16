/*
 * @(#)SelectorModel.java
 * Copyright (c) 2014 Supercomputing Systems AG, Schweiz.
 * Alle Rechte vorbehalten. 
 */
package org.jhotdraw.xml.css;

/**
 * This is a model on which a {@code CssAST.SelectorGroup} can perform a match
 * operation.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public interface SelectorModel<T> {

    /**
     * Returns true if the element has the specified id.
     *
     * @param element An element of the document
     * @param id an id
     * @return true if the element has the id
     */
    boolean hasId(T element, String id);

    /**
     * Returns true if the element has the specified type.
     *
     * @param element An element of the document
     * @param type an id
     * @return true if the element has the id
     */
    boolean hasType(T element, String type);

    /**
     * Returns true if the element has the specified class.
     *
     * @param element An element of the document
     * @param clazz an id
     * @return true if the element has the id
     */
    boolean hasStyleClass(T element, String clazz);

    /**
     * Returns true if the element has the specified attribute.
     *
     * @param element An element of the document
     * @param attributeName an attribute name
     * @return true if the element has an attribute with the specified name
     */
    boolean hasAttribute(T element, String attributeName);

    /**
     * Returns true if the element has an attribute with the specified name and
     * value.
     *
     * @param element An element of the document
     * @param attributeName an attribute name
     * @param attributeValue the attribute value
     * @return true if the element has an attribute with the specified name and
     * value
     */
    boolean attributeValueEquals(T element, String attributeName, String attributeValue);

    /**
     * Returns true if the element has an attribute with the specified name and
     * the attribute value starts with the specified string followed by the
     * character '-'.
     *
     * @param element An element of the document
     * @param attributeName an attribute name
     * @param string the start string
     * @return true if the element has an attribute with the specified name and
     * the value starts with the specified string followed by the character '-'.
     */
    boolean attributeValueStartsWith(T element, String attributeName, String string);

    /**
     * Returns true if the element has an attribute with the specified name and
     * the value contains the specified word.
     *
     * @param element An element of the document
     * @param attributeName an attribute name
     * @param word the word
     * @return true if the element has an attribute with the specified name and
     * the value contains the specified word.
     */
    boolean attributeValueContainsWord(T element, String attributeName, String word);

    /**
     * Returns true if the element has the specified pseudo class.
     *
     * @param element An element of the document
     * @param pseudoClass an id
     * @return true if the element has the id
     */
    boolean hasPseudoClass(T element, String pseudoClass);

    /**
     * Gets the parent of the element.
     *
     * @param element An element of the document
     * @return The parent element. Returns null if the element has no parent.
     */
    T getParent(T element);

    /**
     * Gets the previous sibling of the element.
     *
     * @param element An element of the document
     * @return The previous sibling. Returns null if the element has no previous
     * sibling.
     */
    T getPreviousSibling(T element);
}
