/* @(#)AbstractAttributeSelector.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.SelectorModel;

/**
 * An "exists match" matches an element if the element has an attribute with the
 * specified name.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ExistsMatchSelector extends AbstractAttributeSelector {
    @Nullable
    private final String attributeNamespace;
    @Nonnull
    private final String attributeName;

    public ExistsMatchSelector(@Nullable String attributeNamespace, @Nonnull String attributeName) {
        this.attributeNamespace=attributeNamespace;
        this.attributeName = attributeName;
    }

    @Nullable
    @Override
    protected <T> T match(@Nonnull SelectorModel<T> model, T element) {
        return model.hasAttribute(element,attributeNamespace, attributeName) ? element : null;
    }

    @Nonnull
    @Override
    public String toString() {
        return "[" + attributeNamespace+":"+attributeName + ']';
    }
}
