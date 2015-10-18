/*
 * @(#)UniversalSelector.java
 * Copyright (c) 2014 Supercomputing Systems AG, Schweiz.
 * Alle Rechte vorbehalten. 
 */
package org.jhotdraw.css.ast;

import org.jhotdraw.css.SelectorModel;

/**
 * A "universal selector" matches an element if the element exists.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class UniversalSelector extends SimpleSelector {

    @Override
    public String toString() {
        return "Universal:*";
    }

    @Override
    public <T> T match(SelectorModel<T> model, T element) {
        return element;
    }
}
