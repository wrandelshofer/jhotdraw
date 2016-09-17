/* @(#)FunctionPseudoClassSelector.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.css.ast;

import java.util.Collections;
import java.util.List;
import org.jhotdraw.css.SelectorModel;

/**
 * A "class selector" matches an element based on the value of its "pseudo
 * class" attribute.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class FunctionPseudoClassSelector extends PseudoClassSelector {

    private final String functionIdentifier;
    private final List<PreservedToken> terms;

    public FunctionPseudoClassSelector(String functionIdentifier,List<PreservedToken> terms) {
        this.functionIdentifier = functionIdentifier;
        this.terms=Collections.unmodifiableList(terms);
    }

    @Override
    public String toString() {
        return "FunctionPseudoClass:" + functionIdentifier;
    }

    @Override
    public <T> MatchResult<T> match(SelectorModel<T> model, T element) {
        // FIXME implement me
        return (element != null && model.hasPseudoClass(element, functionIdentifier)) //
                ? new MatchResult<>(element, this) : null;
    }
}
