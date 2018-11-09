/* @(#)TypeSelector.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jhotdraw8.css.SelectorModel;

/**
 * A "class selector" matches an element if the element has a type with the
 * specified value.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class TypeSelector extends SimpleSelector {
    @Nullable
    private final String namespace;
    @Nonnull
    private final String type;

    public TypeSelector(@Nullable String namespace, @Nonnull String type) {
        this.namespace = namespace;
        this.type = type;
    }

    @Nonnull
    @Override
    public String toString() {
        return "Type:" + type;
    }

    @Nullable
    @Override
    public <T> T match(@Nonnull SelectorModel<T> model, @Nullable T element) {
        return (element != null && model.hasType(element, namespace, type)) //
                ? element : null;
    }

    @Override
    public int getSpecificity() {
        return 1;
    }
}
