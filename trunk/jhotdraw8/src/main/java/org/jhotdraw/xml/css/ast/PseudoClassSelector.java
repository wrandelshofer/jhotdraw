/*
 * @(#)PseudoClassSelector.java
 * Copyright (c) 2014 Supercomputing Systems AG, Schweiz.
 * Alle Rechte vorbehalten. 
 */
package org.jhotdraw.xml.css.ast;

import org.jhotdraw.xml.css.SelectorModel;

/**
 * A "class selector" matches an element based on the value of its "pseudo
 * class" attribute.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class PseudoClassSelector extends SimpleSelector {

    private final String pseudoClass;

    public PseudoClassSelector(String pseudoClass) {
        this.pseudoClass = pseudoClass;
    }

    @Override
    public String toString() {
        return "PseudoClass:" + pseudoClass;
    }

    @Override
    public <T> T match(SelectorModel<T> model, T element) {
        return (element != null && model.hasStylePseudoClass(element, pseudoClass)) //
                ? element : null;
    }
}
