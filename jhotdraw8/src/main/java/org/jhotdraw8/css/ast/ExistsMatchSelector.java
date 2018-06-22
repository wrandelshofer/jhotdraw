/* @(#)AbstractAttributeSelector.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jhotdraw8.css.SelectorModel;

/**
 * An "exists match" matches an element if the element has an attribute with the
 * specified name.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ExistsMatchSelector extends AbstractAttributeSelector {

    private final String attributeName;

    public ExistsMatchSelector(String attributeName) {
        this.attributeName = attributeName;
    }

    @Nullable
    @Override
    protected <T> T match(@NonNull SelectorModel<T> model, T element) {
        return model.hasAttribute(element, attributeName) ? element : null;
    }

    @NonNull
    @Override
    public String toString() {
        return "[" + attributeName + ']';
    }
}
