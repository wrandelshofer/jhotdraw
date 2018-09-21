/* @(#)EqualsMatchSelector.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jhotdraw8.css.SelectorModel;

/**
 * An "attribute value selector" matches an element if the element has an
 * attribute with the specified name and value.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class EqualsMatchSelector extends AbstractAttributeSelector {

    private final String attributeName;
    private final String attributeValue;

    public EqualsMatchSelector(String attributeName, String attributeValue) {
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
    }

    @Nullable
    @Override
    protected <T> T match(@Nonnull SelectorModel<T> model, T element) {
        return model.attributeValueEquals(element, attributeName, attributeValue) ? element : null;
    }

    @Nonnull
    @Override
    public String toString() {
        return "[" + attributeName + "=" + attributeValue + ']';
    }
}
