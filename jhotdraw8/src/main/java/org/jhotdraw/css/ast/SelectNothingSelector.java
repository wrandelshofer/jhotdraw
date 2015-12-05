/*
 * @(#)UniversalSelector.java
 * Copyright (c) 2014 Supercomputing Systems AG, Schweiz.
 * Alle Rechte vorbehalten. 
 */
package org.jhotdraw.css.ast;

import org.jhotdraw.css.SelectorModel;

/**
 * A "select nothing selector" matches nothing.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class SelectNothingSelector extends SimpleSelector {

    @Override
    public String toString() {
        return "SelectNothing";
    }

    @Override
    public <T> T match(SelectorModel<T> model, T element) {
        return null;
    }
}
