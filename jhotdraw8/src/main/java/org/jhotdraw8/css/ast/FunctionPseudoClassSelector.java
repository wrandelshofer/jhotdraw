/* @(#)FunctionPseudoClassSelector.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jhotdraw8.css.SelectorModel;

/**
 * A "class selector" matches an element based on the value of its "pseudo
 * class" attribute.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class FunctionPseudoClassSelector extends PseudoClassSelector {

    private final String functionIdentifier;
    @NonNull
    private final List<PreservedToken> terms;

    public FunctionPseudoClassSelector(String functionIdentifier, @NonNull List<PreservedToken> terms) {
        this.functionIdentifier = functionIdentifier;
        this.terms = Collections.unmodifiableList(terms);
    }

    @NonNull
    @Override
    public String toString() {
        return "FunctionPseudoClass:" + functionIdentifier;
    }

    @Nullable
    @Override
    public <T> T match(@NonNull SelectorModel<T> model, @Nullable T element) {
        return (element != null && model.hasPseudoClass(element, functionIdentifier)) //
                ? element : null;
    }
}
