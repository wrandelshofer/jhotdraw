/*
 * @(#)Selector.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.SelectorModel;


/**
 * A "selector" is a tree of "combinator"s.
 *
 * @author Werner Randelshofer
 */
public abstract class Selector extends AST {


    /**
     * Returns the specificity of this selector.
     * <p>
     * A selector's specificity is calculated as follows:
     * <ul>
     * <li>count the number of ID selectors in the selector (= a)</li>
     * <li>count the number of class selectors, attributes selectors, and
     * pseudo-classes in the selector (= b)</li>
     * <li>count the number of type selectors and pseudo-elements in the
     * selector (= c)</li>
     * <li>ignore the universal selector</li>
     * </ul>
     * <p>
     * Selectors inside the negation pseudo-class are counted like any other,
     * but the negation itself does not count as a pseudo-class.
     * <p>
     * Concatenating the three numbers a-b-c (in a number system with a large
     * base) gives the specificity.
     * <p>
     * In this implementation we compute specificity with
     * {@code specificity=100*a+10*b+c}.
     * <p>
     * References:
     * <ul>
     * <li><a href="https://www.w3.org/TR/2011/REC-css3-selectors-20110929/">CSS
     * Syntax Selectors Level 3, Chapter 9. Calculating a selector's
     * specificity</a></li>
     * </ul>
     *
     * @return the specificity
     */
    public abstract int getSpecificity();

    /**
     * Returns true if the selector matches the element.
     *
     * @param <T>     the element type
     * @param model   The helper is used to access properties of the element and
     *                parent or sibling elements in the document.
     * @param element the element
     * @return true on match
     */
    public <T> boolean matches(SelectorModel<T> model, T element) {
        return match(model, element) != null;
    }

    /**
     * Returns the matching element.
     *
     * @param <T>     element type
     * @param model   The helper is used to access properties of the element and
     *                parent or sibling elements in the document.
     * @param element the element
     * @return the matching element or null
     */
    @Nullable
    protected abstract <T> T match(SelectorModel<T> model, T element);


}
