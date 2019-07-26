/*
 * @(#)AbstractAttributeSelector.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

/**
 * An abstract "attribute selector" matches an element based on its attributes.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractAttributeSelector extends SimpleSelector {

    @Override
    public final int getSpecificity() {
        return 10;
    }

}
