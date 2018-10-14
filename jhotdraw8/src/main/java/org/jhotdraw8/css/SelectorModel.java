/* @(#)SelectorModel.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css;

import java.util.Set;

import javafx.css.StyleOrigin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
     * This keyword is used to reset a property.
     *
     * <a href="https://www.w3.org/TR/css3-cascade/#initial">Resetting a
     * Property: the 'initial' keyword.</a>
     */
    String INITIAL_VALUE_KEYWORD = "initial";

    /**
     * Returns true if the element has an attribute with the specified name and
     * the value contains the specified substring.
     *
     * @param element       An element of the document
     * @param attributeName an attribute name
     * @param substring     the substring
     * @return true if the element has an attribute with the specified name and
     * the value contains the specified substring.
     */
    default boolean attributeValueContains(@Nonnull T element, @Nonnull String attributeName, @Nonnull String substring) {
        String actualValue = getAttribute(element, attributeName);
        return actualValue != null && (actualValue.contains(substring));
    }

    /**
     * Returns true if the element has an attribute with the specified name and
     * the value is a list of words which contains the specified word.
     *
     * @param element       An element of the document
     * @param attributeName an attribute name
     * @param word          the word
     * @return true if the element has an attribute with the specified name and
     * the value contains the specified word.
     */
    default boolean attributeValueContainsWord(@Nonnull T element, @Nonnull String attributeName, @Nonnull String word) {
        String value = getAttribute(element, attributeName);
        if (value != null) {
            String[] words = value.split("\\s+");
            for (int i = 0; i < words.length; i++) {
                if (word.equals(words[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if the element has an attribute with the specified name and
     * the attribute value ends with the specified substring.
     *
     * @param element       An element of the document
     * @param attributeName an attribute name
     * @param substring     the substring
     * @return true if the element has an attribute with the specified name and
     * the value ends with the specified substring.
     */
    default boolean attributeValueEndsWith(@Nonnull T element, @Nonnull String attributeName, @Nonnull String substring) {
        String actualValue = getAttribute(element, attributeName);
        return actualValue != null && (actualValue.endsWith(substring));
    }

    /**
     * Returns true if the element has an attribute with the specified name and
     * value.
     *
     * @param element        An element of the document
     * @param attributeName  an attribute name
     * @param attributeValue the attribute value
     * @return true if the element has an attribute with the specified name and
     * value
     */
    default boolean attributeValueEquals(@Nonnull T element, @Nonnull String attributeName, @Nonnull String attributeValue) {
        String actualValue = getAttribute(element, attributeName);
        return actualValue != null && actualValue.equals(attributeValue);
    }

    /**
     * Returns true if the element has an attribute with the specified name and
     * the attribute value starts with the specified substring.
     *
     * @param element       An element of the document
     * @param attributeName an attribute name
     * @param substring     the substring
     * @return true if the element has an attribute with the specified name and
     * the value starts with the specified substring.
     */
    default boolean attributeValueStartsWith(@Nonnull T element, @Nonnull String attributeName, @Nonnull String substring) {
        String actualValue = getAttribute(element, attributeName);
        return actualValue != null && (actualValue.startsWith(substring));
    }

    /**
     * Returns the attribute value with the given name.
     *
     * @param element The element
     * @param name    The attribute name
     * @return The attribute value. Returns null if the element does not have an
     * attribute with this name.
     */
    @Nullable
    String getAttribute(@Nonnull T element, @Nonnull String name);

    @Nullable
    public String getAttribute(@Nonnull T element, @Nullable StyleOrigin origin, @Nonnull String name);

    /**
     * Returns all styleable attributes of the element.
     *
     * @param element An element of the document
     * @return a set of styleable attributes.
     */
    Set<String> getAttributeNames(@Nonnull T element);

    /**
     * Returns all non-decomposed styleable attributes of the element.
     * <p>
     * If an attribute can be decomposed, only the composite attribute is
     * returned.
     *
     * @param element An element of the document
     * @return a set of styleable attributes.
     */
    Set<String> getComposedAttributeNames(@Nonnull T element);

    /**
     * Returns all decomposed styleable attributes of the element.
     * <p>
     * If an attribute can be composed, only the decomposed attributes are
     * returned.
     *
     * @param element An element of the document
     * @return a set of styleable attributes.
     */
    Set<String> getDecomposedAttributeNames(@Nonnull T element);

    /**
     * Returns the id of the element.
     *
     * @param element the element
     * @return the id or null if the element does not have an id.
     */
    @Nullable
    String getId(@Nonnull T element);

    /**
     * Gets the parent of the element.
     *
     * @param element An element of the document
     * @return The parent element. Returns null if the element has no parent.
     */
    @Nullable
    T getParent(@Nonnull T element);

    /**
     * Gets the previous sibling of the element.
     *
     * @param element An element of the document
     * @return The previous sibling. Returns null if the element has no previous
     * sibling.
     */
    @Nullable
    T getPreviousSibling(@Nonnull T element);

    /**
     * Returns the style classes of the element.
     *
     * @param element the element
     * @return the style classes or an empty set.
     */
    Set<String> getStyleClasses(@Nonnull T element);

    /**
     * Returns the style type of the element.
     *
     * @param element the element
     * @return the style type of the element.
     */
    String getType(@Nonnull T element);

    /**
     * Returns true if the element has the specified attribute.
     *
     * @param element       An element of the document
     * @param attributeName an attribute name
     * @return true if the element has an attribute with the specified name
     */
    boolean hasAttribute(@Nonnull T element, @Nonnull String attributeName);

    /**
     * Returns true if the element has the specified id.
     *
     * @param element An element of the document
     * @param id      an id
     * @return true if the element has the id
     */
    boolean hasId(@Nonnull T element, @Nonnull String id);

    /**
     * Returns true if the element has the specified pseudo class.
     *
     * @param element     An element of the document
     * @param pseudoClass a pseudo class
     * @return true if the element has the id
     */
    boolean hasPseudoClass(@Nonnull T element, @Nonnull String pseudoClass);

    /**
     * Returns true if the element has the specified style class.
     *
     * @param element An element of the document
     * @param clazz   a style class
     * @return true if the element has the id
     */
    boolean hasStyleClass(@Nonnull T element, @Nonnull String clazz);

    /**
     * Returns true if the element has the specified type.
     *
     * @param element An element of the document
     * @param type    an id
     * @return true if the element has the id
     */
    boolean hasType(@Nonnull T element, @Nonnull String type);

    /**
     * Sets an attribute value.
     *
     * @param element The element
     * @param origin  The style origin
     * @param name    The attribute name
     * @param value   The attribute value. Null removes the attribute from the
     *                element.
     */
    void setAttribute(@Nonnull T element, @Nonnull StyleOrigin origin, @Nonnull String name, @Nullable String value);
}
