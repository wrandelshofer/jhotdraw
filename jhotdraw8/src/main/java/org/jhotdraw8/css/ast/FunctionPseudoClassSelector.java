/* @(#)FunctionPseudoClassSelector.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

import java.util.Collections;
import java.util.List;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.SelectorModel;
import org.jhotdraw8.css.CssToken;

/**
 * A "class selector" matches an element based on the value of its "pseudo
 * class" attribute.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class FunctionPseudoClassSelector extends PseudoClassSelector {

    private final String functionIdentifier;
    @Nonnull
    private final List<CssToken> terms;

    public FunctionPseudoClassSelector(String functionIdentifier, @Nonnull List<CssToken> terms) {
        this.functionIdentifier = functionIdentifier;
        this.terms = Collections.unmodifiableList(terms);
    }

    @Nonnull
    @Override
    public String toString() {
        return "FunctionPseudoClass:" + functionIdentifier;
    }

    @Nullable
    @Override
    public <T> T match(@Nonnull SelectorModel<T> model, @Nullable T element) {
        return (element != null && model.hasPseudoClass(element, functionIdentifier)) //
                ? element : null;
    }
}
