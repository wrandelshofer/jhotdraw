/*
 * @(#)Combinator.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

/**
 * Abstract superclass for "combinator"s.
 * <p>
 * A combinator combines the results of two selectors.
 *
 * @author Werner Randelshofer
 */
public abstract class Combinator extends Selector {

    protected final SimpleSelector firstSelector;
    protected final Selector secondSelector;

    public Combinator(SimpleSelector firstSelector, Selector secondSelector) {
        this.firstSelector = firstSelector;
        this.secondSelector = secondSelector;

    }

    @Override
    public String toString() {
        return "Combinator{" + "simpleSelector=" + firstSelector + ", selector=" + secondSelector + '}';
    }

}
