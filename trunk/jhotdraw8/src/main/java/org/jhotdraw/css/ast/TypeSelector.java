/* @(#)TypeSelector.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.css.ast;

import org.jhotdraw.css.SelectorModel;

/**
 * A "class selector" matches an element if the element has a type with the
 * specified value.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class TypeSelector extends SimpleSelector {

    private final String type;

    public TypeSelector(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Type:" + type;
    }

    @Override
    public <T> T match(SelectorModel<T> model, T element) {
        return (element != null && model.hasType(element, type)) //
                ? element : null;
    }
}
