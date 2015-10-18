/*
 * @(#)IdSelector.java
 * Copyright (c) 2014 Supercomputing Systems AG, Schweiz.
 * Alle Rechte vorbehalten. 
 */
package org.jhotdraw.css.ast;

import org.jhotdraw.css.SelectorModel;

/**
 * An "id selector" matches an element if the element has an id with the 
 * specified value.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class IdSelector extends SimpleSelector {

    private final String id;

    public IdSelector(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Id:" + id;
    }

    @Override
    public <T> T match(SelectorModel<T> model, T element) {
        return (element != null && model.hasId(element, id)) //
                ? element : null;
    }
}
