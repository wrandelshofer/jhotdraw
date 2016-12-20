/* @(#)ClassSelector.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.css.ast;

import org.jhotdraw8.css.SelectorModel;

/**
 * A "class selector" matches an element if the element has a style class with
 * the specified value.
 *
 * @author Werner Randelshofer
 * @version $Id$
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

    @Override
    public int getSpecificity() {
        return 10;
    }

}
