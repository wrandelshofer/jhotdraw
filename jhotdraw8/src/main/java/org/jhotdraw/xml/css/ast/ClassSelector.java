/*
 * @(#)ClassSelector.java
 * Copyright (c) 2014 Supercomputing Systems AG, Schweiz.
 * Alle Rechte vorbehalten. 
 */
package org.jhotdraw.xml.css.ast;

import org.jhotdraw.xml.css.SelectorModel;

/**
 * A "class selector" matches an element based on the value of its "class"
 * attribute.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class ClassSelector extends SimpleSelector {

    private final String clazz;

    public ClassSelector(String clazz) {
        this.clazz = clazz;
    }

    @Override
    public String toString() {
        return "Class:" + clazz;
    }

    @Override
    public <T> T match(SelectorModel<T> model, T element) {
        return (element != null && model.hasStyleClass(element, clazz)) //
                ? element : null;
    }
}
