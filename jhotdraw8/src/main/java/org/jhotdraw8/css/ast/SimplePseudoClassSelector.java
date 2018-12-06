/* @(#)SimplePseudoClassSelector.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jhotdraw8.css.SelectorModel;

/**
 * A "simple class selector" matches an element based on the value of its
 * "pseudo class" attribute.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimplePseudoClassSelector extends PseudoClassSelector {

    private final String pseudoClass;

    public SimplePseudoClassSelector(String pseudoClass) {
        this.pseudoClass = pseudoClass;
    }

    @Nonnull
    @Override
    public String toString() {
        return "PseudoClass:" + pseudoClass;
    }

    @Nullable
    @Override
    public <T> T match(@Nonnull SelectorModel<T> model, @Nullable T element) {
        return (element != null && model.hasPseudoClass(element, pseudoClass)) //
                ? element : null;
    }
}
