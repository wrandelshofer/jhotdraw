/*
 * @(#)PseudoClassSelector.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

/**
 * A "pseudo class selector" matches an element based on criteria which are not
 * directly encoded in the element.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class PseudoClassSelector extends SimpleSelector {

    @Override
    public final int getSpecificity() {
        return 10;
    }

}
