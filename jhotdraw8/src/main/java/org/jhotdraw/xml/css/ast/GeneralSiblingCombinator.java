/* @(#)GeneralSiblingCombinator.java
 * Copyright (c) 2014 Supercomputing Systems AG, Schweiz.
 * Alle Rechte vorbehalten. 
 */

package org.jhotdraw.xml.css.ast;

import org.jhotdraw.xml.css.SelectorModel;

/**
 * An "generarl sibling combinator" matches an element if its first selector
 * matches on a previous sibling of the element and if its second selector
 * matches the element.
 * @author Werner Randelshofer
 * @version $$Id$$
 */
    public class GeneralSiblingCombinator extends Combinator {

        public GeneralSiblingCombinator(SimpleSelector simpleSelector, Selector selector) {
            super(simpleSelector, selector);
        }

        @Override
        public String toString() {
            return firstSelector + " ~ " + secondSelector;
        }

        @Override
        public <T> T match(SelectorModel<T> model, T element) {
            T siblingElement = secondSelector.match(model, element);
            T matchingElement = null;
            while (siblingElement != null) {
                siblingElement = model.getPreviousSibling(siblingElement);
                matchingElement = firstSelector.match(model, siblingElement);
                if (matchingElement != null) {
                    break;
                }
            }
            return matchingElement;
        }
    }

