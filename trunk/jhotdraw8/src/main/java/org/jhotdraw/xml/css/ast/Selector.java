/* @(#)Selector.java
 * Copyright (c) 2014 Supercomputing Systems AG, Schweiz.
 * Alle Rechte vorbehalten. 
 */
package org.jhotdraw.xml.css.ast;

import org.jhotdraw.xml.css.SelectorModel;

/**
 * A "selector" is a tree of "combinator"s.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public abstract class Selector extends AST {

    /**
     * Selects the element.
     *
     * @param element the element
     * @return true if the combinator tree match the element.
     */
    public boolean select(Object element) {
        return false;
    }

    /**
     * Returns the matching element.
     *
     * @param <T> element type
     * @param model The helper is used to access properties of the element and
     * parent or sibling elements in the document.
     * @param element the element
     * @return the matching element or null
     */
    protected abstract <T> T match(SelectorModel<T> model, T element);
}
