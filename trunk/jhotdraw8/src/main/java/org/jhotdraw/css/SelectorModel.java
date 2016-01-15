/* @(#)SelectorModel.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.css;

import java.util.Set;

/**
 * This is a model on which a {@code CssAST.SelectorGroup} can perform a match
 * operation.
 *
 * @param <T> the element type
 * @author Werner Randelshofer
 * @version $Id$
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
     * Returns all styleable attributes of the element.
     *
     * @param element An element of the document
     * @return a set of styleable attributes.
     */
    Set<String> getAttributeNames(T element);

    /**
     * Returns all styleable attributes of the element.
     * <p>
     * If an attribute can be decomposed, only the composite attribute
     * is returned.
     * 
     *
     * @param element An element of the document
     * @return a set of styleable attributes.
     */
    Set<String> getNonDecomposedAttributeNames(T element);

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
     * the attribute value starts with the specified substring.
     *
     * @param element An element of the document
     * @param attributeName an attribute name
     * @param substring the substring
     * @return true if the element has an attribute with the specified name and
     * the value starts with the specified substring.
     */
    boolean attributeValueStartsWith(T element, String attributeName, String substring);

    /**
     * Returns true if the element has an attribute with the specified name and
     * the attribute value ends with the specified substring.
     *
     * @param element An element of the document
     * @param attributeName an attribute name
     * @param substring the substring
     * @return true if the element has an attribute with the specified name and
     * the value ends with the specified substring.
     */
    boolean attributeValueEndsWith(T element, String attributeName, String substring);
    /**
     * Returns true if the element has an attribute with the specified name and
     * the value contains the specified substring.
     *
     * @param element An element of the document
     * @param attributeName an attribute name
     * @param substring the substring
     * @return true if the element has an attribute with the specified name and
     * the value contains the specified substring.
     */
    boolean attributeValueContains(T element, String attributeName, String substring);
    /**
     * Returns true if the element has an attribute with the specified name and
     * the value is a list of words which contains the specified word.
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

    /** Returns the attribute value with the given name.
     * 
     * @param element The element
     * @param name The attribute name
     * @return The attribute value. Returns null if the element does not have
     * an attribute with this value.
     */
    public String getAttributeValue(T element, String name);

    
    /** Returns the id of the element.
     * 
     * @param element the element
     * @return the id or null if the element does not have an id.
     */
    public String getId(T element);
    /** Returns the style type of the element.
     * 
     * @param element the element
     * @return the style type of the element.
     */
    public String getType(T element);
    
    /** Returns the style classes of the element.
     * 
     * @param element the element
     * @return the style classes or an empty set.
     */
    public Set<String> getStyleClasses(T element);
}
