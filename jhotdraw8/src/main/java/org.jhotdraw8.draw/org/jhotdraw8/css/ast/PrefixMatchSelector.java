/* @(#)PrefixMatchSelector.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.SelectorModel;

/**
 * A "prefix match selector" {@code ^=} matches an element if the element has an
 * attribute with the specified name and its value starts with the specified
 * substring.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class PrefixMatchSelector extends AbstractAttributeSelector {
    @Nullable
    private final String namespace;
    @Nonnull
    private final String attributeName;
    @Nonnull
    private final String substring;

    public PrefixMatchSelector(@Nullable String namespace, @Nonnull String attributeName, @Nonnull String substring) {
        this.namespace = namespace;
        this.attributeName = attributeName;
        this.substring = substring;
    }

    @Nullable
    @Override
    protected <T> T match(@Nonnull SelectorModel<T> model, T element) {
        return (model.attributeValueStartsWith(element, namespace, attributeName, substring))//
                ? element : null;
    }

    @Nonnull
    @Override
    public String toString() {
        return "[" + attributeName + "^=" + substring + ']';
    }
}
