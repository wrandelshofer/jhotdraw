/* @(#)Combinator.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.css.ast;

/**
 * Abstract superclass for "combinator"s.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public abstract class Combinator extends Selector {

    protected final SimpleSelector firstSelector;
    protected final Selector secondSelector;

    public Combinator(SimpleSelector firstSelector, Selector secondSelector) {
        this.firstSelector = firstSelector;
        this.secondSelector = secondSelector;

    }

    /**
     * Selects the element.
     *
     * @param element the element
     * @return true if the combinator tree match the element.
     */
    @Override
    public boolean select(Object element) {
        return false;
    }

    @Override
    public String toString() {
        return "Combinator{" + "simpleSelector=" + firstSelector + ", selector=" + secondSelector + '}';
    }

}
