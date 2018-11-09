/* @(#)DashMatchSelector.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jhotdraw8.css.SelectorModel;

/**
 * A "dash match selector" {@code |=} matches an element if the element has an
 * attribute with the specified name and its value is either exactly the
 * specified substring or its value begins with the specified substring
 * immediately followed by a dash '-' character. This is primarily intended to
 * allow language subcode matches.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DashMatchSelector extends AbstractAttributeSelector {
    @Nullable
    private final String namespace;
    @Nonnull
    private final String attributeName;
    @Nonnull
    private final String substring;

    public DashMatchSelector(@Nullable String namespace, @Nonnull String attributeName, @Nonnull String substring) {
        this.namespace=namespace;
        this.attributeName = attributeName;
        this.substring = substring;
    }

    @Nullable
    @Override
    protected <T> T match(@Nonnull SelectorModel<T> model, T element) {
        return (model.attributeValueEquals(element, namespace, attributeName, substring) //
                || model.attributeValueStartsWith(element, namespace,attributeName, substring + '-'))//
                ? element : null;
    }
}
