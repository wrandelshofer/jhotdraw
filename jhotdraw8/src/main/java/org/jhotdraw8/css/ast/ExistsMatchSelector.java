/* @(#)AbstractAttributeSelector.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
    protected <T> T match(@Nonnull SelectorModel<T> model, T element) {
        return model.hasAttribute(element, attributeName) ? element : null;
    }

    @Nonnull
    @Override
    public String toString() {
        return "[" + attributeName + ']';
    }
}
