/* @(#)SimplePseudoClassSelector.java
 * Copyright (c) 2014 Supercomputing Systems AG, Schweiz.
 * Alle Rechte vorbehalten. 
 */
package org.jhotdraw.css.ast;

import org.jhotdraw.css.SelectorModel;

/**
 * A "simple class selector" matches an element based on the value of its "pseudo
 * class" attribute.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class SimplePseudoClassSelector extends PseudoClassSelector {

    private final String pseudoClass;

    public SimplePseudoClassSelector(String pseudoClass) {
        this.pseudoClass = pseudoClass;
    }

    @Override
    public String toString() {
        return "PseudoClass:" + pseudoClass;
    }

    @Override
    public <T> T match(SelectorModel<T> model, T element) {
        return (element != null && model.hasPseudoClass(element, pseudoClass)) //
                ? element : null;
    }
}
