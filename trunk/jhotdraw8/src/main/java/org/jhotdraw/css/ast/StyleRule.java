/* @(#)StyleRule.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.css.ast;

import java.util.Collections;
import java.util.List;

/**
 * A qualified rule consists of a "selector list" and a list of "declaration"s.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class StyleRule extends AST {

    private final SelectorGroup selectorList;
    private final List<Declaration> declarations;

    public StyleRule(SelectorGroup selectorGroup, List<Declaration> declarations) {
        this.selectorList = selectorGroup;
        this.declarations = Collections.unmodifiableList(declarations);
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder("QualifiedRule: ");
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

    public List<Declaration> getDeclarations() {
        return declarations;
    }

}
