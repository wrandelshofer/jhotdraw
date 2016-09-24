/* @(#)DashMatchSelector.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.css.ast;

import org.jhotdraw.css.SelectorModel;

/**
 * A "dash match selector" {@code |=} matches an element if the element has an
 * attribute with the specified name and its value is either exactly the
 * specified substring or its value begins with the specified substring immediately
 * followed by a dash '-' character. This is primarily intended to allow
 * language subcode matches.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DashMatchSelector extends AbstractAttributeSelector {

    private final String attributeName;
    private final String substring;

    public DashMatchSelector(String attributeName, String substring) {
        this.attributeName = attributeName;
        this.substring = substring;
    }

    @Override
    protected <T> T match(SelectorModel<T> model, T element) {
        return (model.attributeValueEquals(element, attributeName, substring) //
                || model.attributeValueStartsWith(element, attributeName, substring + '-'))//
                        ? element : null;
    }
}
