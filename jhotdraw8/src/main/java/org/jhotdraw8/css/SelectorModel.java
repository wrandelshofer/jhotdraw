/* @(#)SelectorModel.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javafx.css.StyleOrigin;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ReadableList;

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
     * Returns true if the element has an attribute with the specified name and
     * the value contains the specified substring.
     *
     * @param element       An element of the document
     * @param namespace an optional namespace, null means any namespace
     * @param attributeName an attribute name
     * @param substring     the substring
     * @return true if the element has an attribute with the specified name and
     * the value contains the specified substring.
     */
    default boolean attributeValueContains(@Nonnull T element, @Nullable String namespace, @Nonnull String attributeName, @Nonnull String substring) {
        String actualValue = getAttributeAsString(element, namespace, attributeName);
        return actualValue != null && (actualValue.contains(substring));
    }

    /**
     * Returns true if the element has an attribute with the specified name and
     * the value is a list of words which contains the specified word.
     *
     * @param element       An element of the document
     * @param namespace an optional namespace, null means any namespace
     * @param attributeName an attribute name
     * @param word          the word
     * @return true if the element has an attribute with the specified name and
     * the value contains the specified word.
     */
    default boolean attributeValueContainsWord(@Nonnull T element, @Nullable String namespace, @Nonnull String attributeName, @Nonnull String word) {
        String value = getAttributeAsString(element,namespace, attributeName);
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
    default boolean attributeValueEndsWith(@Nonnull T element, @Nullable String namespace, @Nonnull String attributeName, @Nonnull String substring) {
        String actualValue = getAttributeAsString(element, namespace,attributeName);
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
    default boolean attributeValueEquals(@Nonnull T element, @Nullable String namespace, @Nonnull String attributeName, @Nonnull String attributeValue) {
        String actualValue = getAttributeAsString(element,namespace, attributeName);
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
    default boolean attributeValueStartsWith(@Nonnull T element, @Nullable String namespace, @Nonnull String attributeName, @Nonnull String substring) {
        String actualValue = getAttributeAsString(element, namespace,attributeName);
        return actualValue != null && (actualValue.startsWith(substring));
    }

    /**
     * Returns the attribute value with the given name from the USER style origin.
     *
     * @param element The element
     * @param name    The attribute name
     * @return The attribute value. Returns "initial" if the element does not have an
     * attribute with this name.
     */
    @Nullable
    default String getAttributeAsString(@Nonnull T element, @Nullable String namespace, @Nonnull String name) {
        return getAttributeAsString(element, StyleOrigin.USER, namespace, name);
    }

    @Nullable
    default String getAttributeAsString(@Nonnull T element, @Nullable StyleOrigin origin, @Nullable String namespace, @Nonnull String name) {
        List<CssToken> list = getAttribute(element, origin, namespace, name);
        if (list == null) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        for (CssToken t : list) buf.append(t.fromToken());
        return buf.toString();
    }

    @Nullable
    List<CssToken> getAttribute(@Nonnull T element, @Nullable StyleOrigin origin, @Nullable String namespace, @Nonnull String name);

    /**
     * Returns all styleable attributes of the element.
     *
     * @param element An element of the document
     * @return a set of styleable attributes.
     */
    Set<QualifiedName> getAttributeNames(@Nonnull T element);

    /**
     * Returns all non-decomposed styleable attributes of the element.
     * <p>
     * If an attribute can be decomposed, only the composite attribute is
     * returned.
     *
     * @param element An element of the document
     * @return a set of styleable attributes.
     */
    Set<QualifiedName> getComposedAttributeNames(@Nonnull T element);

    /**
     * Returns all decomposed styleable attributes of the element.
     * <p>
     * If an attribute can be composed, only the decomposed attributes are
     * returned.
     *
     * @param element An element of the document
     * @return a set of styleable attributes.
     */
    Set<QualifiedName> getDecomposedAttributeNames(@Nonnull T element);

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
     * @param namespace     an optional namespace (null means any namespace)
     * @param attributeName an attribute name
     * @return true if the element has an attribute with the specified name
     */
    boolean hasAttribute(@Nonnull T element, @Nullable String namespace, @Nonnull String attributeName);

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
     * @param namespace    an optional namespace (null means any namespace)
     * @param type    an id
     * @return true if the element has the id
     */
    boolean hasType(@Nonnull T element, @Nullable String namespace, @Nonnull String type);

    /**
     * Sets an attribute value.
     *
     * @param element The element
     * @param origin  The style origin
     * @param namespace    an optional namespace (null means any namespace)
     * @param name    The attribute name
     * @param value   The attribute value. Null removes the attribute from the
     *                element.
     */
    default void setAttributeAsString(@Nonnull T element, @Nonnull StyleOrigin origin, @Nullable String namespace, @Nonnull String name, @Nullable String value) {
        if (value == null) {
            setAttribute(element, origin, namespace, name, null);
        } else {
            List<CssToken> list = new ArrayList<>();
            StreamCssTokenizer tt = new StreamCssTokenizer(value);
            try {
                while (tt.nextNoSkip() != CssTokenType.TT_EOF) list.add(tt.getToken());
            } catch (IOException e) {
                throw new RuntimeException("unexpected exception", e);
            }
            setAttribute(element, origin, namespace, name, ImmutableList.ofCollection(list));
        }
    }

    /**
     * Sets an attribute value.
     *
     * @param element The element
     * @param origin  The style origin
     * @param namespace    an optional namespace (null means any namespace)
     * @param name    The attribute name
     * @param value   The attribute value. Null removes the attribute from the
     *                element.
     */
    void setAttribute(@Nonnull T element, @Nonnull StyleOrigin origin, @Nullable String namespace, @Nonnull String name, @Nullable ReadableList<CssToken> value);
}
