/* @(#)EqualsMatchSelector.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.SelectorModel;

/**
 * An "attribute value selector" matches an element if the element has an
 * attribute with the specified name and value.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class EqualsMatchSelector extends AbstractAttributeSelector {
    @Nullable
    private final String namespace;
    @Nonnull
    private final String attributeName;
    @Nonnull
    private final String attributeValue;

    public EqualsMatchSelector(@Nullable String namespace, @Nonnull String attributeName, @Nonnull String attributeValue) {
        this.namespace = namespace;
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
    }

    @Nullable
    @Override
    protected <T> T match(@Nonnull SelectorModel<T> model, T element) {
        return model.attributeValueEquals(element, namespace, attributeName, attributeValue) ? element : null;
    }

    @Nonnull
    @Override
    public String toString() {
        return "[" + attributeName + "=" + attributeValue + ']';
    }
}
