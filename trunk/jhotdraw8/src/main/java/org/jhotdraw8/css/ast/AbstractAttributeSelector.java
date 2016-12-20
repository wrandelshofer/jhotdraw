/* @(#)AbstractAttributeSelector.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.css.ast;

/**
 * An abstract "attribute selector" matches an element based on its attributes.
 *
 * @author Werner Randelshofer
 * @version $Id: AbstractAttributeSelector.java 1149 2016-11-18 11:00:10Z
 * rawcoder $
 */
public abstract class AbstractAttributeSelector extends SimpleSelector {

    @Override
    public final int getSpecificity() {
        return 10;
    }

}
