/* @(#)FunctionPseudoClassSelector.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.css.ast;

import java.util.Collections;
import java.util.List;
import org.jhotdraw8.css.SelectorModel;

/**
 * A "class selector" matches an element based on the value of its "pseudo
 * class" attribute.
 *
 * @author Werner Randelshofer
 * @version $Id: FunctionPseudoClassSelector.java 1149 2016-11-18 11:00:10Z
 * rawcoder $
 */
public class FunctionPseudoClassSelector extends PseudoClassSelector {

    private final String functionIdentifier;
    private final List<PreservedToken> terms;

    public FunctionPseudoClassSelector(String functionIdentifier, List<PreservedToken> terms) {
        this.functionIdentifier = functionIdentifier;
        this.terms = Collections.unmodifiableList(terms);
    }

    @Override
    public String toString() {
        return "FunctionPseudoClass:" + functionIdentifier;
    }

    @Override
    public <T> T match(SelectorModel<T> model, T element) {
        return (element != null && model.hasPseudoClass(element, functionIdentifier)) //
                ? element : null;
    }
}
