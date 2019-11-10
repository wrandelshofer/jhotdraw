/*
 * @(#)StyleRule.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

import org.jhotdraw8.annotation.NonNull;

import java.util.Collections;
import java.util.List;

/**
 * A style rule associates a selector list to a list of declarations.
 *
 * @author Werner Randelshofer
 */
public class StyleRule extends Rule {

    private final SelectorGroup selectorList;
    @NonNull
    private final List<Declaration> declarations;

    public StyleRule(SelectorGroup selectorGroup, @NonNull List<Declaration> declarations) {
        this.selectorList = selectorGroup;
        this.declarations = Collections.unmodifiableList(declarations);
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder("StyleRule: ");
        buf.append(selectorList.toString());
        buf.append("{");
        for (Declaration r : declarations) {
            buf.append(r.toString());
            buf.append(';');
        }
        buf.append("}");
        return buf.toString();
    }

    public SelectorGroup getSelectorGroup() {
        return selectorList;
    }

    @NonNull
    public List<Declaration> getDeclarations() {
        return declarations;
    }
}
